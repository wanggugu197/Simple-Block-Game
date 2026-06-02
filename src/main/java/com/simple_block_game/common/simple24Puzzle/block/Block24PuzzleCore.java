package com.simple_block_game.common.simple24Puzzle.block;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simple24Puzzle.logic.Game24PuzzleHelper;
import com.simple_block_game.common.simple24Puzzle.logic.Game24PuzzleReward;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

/**
 * 24点游戏核心方块
 */
public class Block24PuzzleCore extends BaseRotatedBlock implements IGameCoreBlock {

    private static final int CENTER_MIN = 4;
    private static final int CENTER_MAX = 12;

    private enum ClickArea {
        CENTER,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        OTHER
    }

    public Block24PuzzleCore(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(UNFOLDED, false));
    }

    public static final MapCodec<Block24PuzzleCore> CODEC = simpleCodec(Block24PuzzleCore::new);

    @Override
    protected @NonNull MapCodec<? extends BaseRotatedBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(@NonNull StateDefinition.Builder<Block, @NonNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UNFOLDED);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new Block24PuzzleCoreEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                     @NonNull BlockPos pos, @NonNull Player player,
                                                     @NonNull BlockHitResult hit) {
        if (!SimpleBlockGameConfig.enable24PuzzleGame.get()) return InteractionResult.PASS;

        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.FAIL;

        ClickArea region = getClickRegion(state, hit);

        if (!state.getValue(UNFOLDED)) {
            if (region != ClickArea.CENTER) return InteractionResult.PASS;
            unfoldGame(serverLevel, pos, state, player);
        } else {
            handleGameClick(serverLevel, pos, state, player, region);
        }
        return InteractionResult.SUCCESS;
    }

    private void handleGameClick(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player, ClickArea region) {
        Block24PuzzleCoreEntity coreEntity = Game24PuzzleHelper.getCoreEntity(serverLevel, pos);
        if (coreEntity == null) return;

        switch (region) {
            case CENTER -> handleCenterClick(serverLevel, pos, state, player, coreEntity);
            case TOP_LEFT -> coreEntity.resetInputToken();
            case TOP_RIGHT -> coreEntity.subtractInputToken();
        }
    }

    private void handleCenterClick(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player, Block24PuzzleCoreEntity coreEntity) {
        if (Game24PuzzleHelper.handleGameInteraction(serverLevel, pos)) {
            long elapsedTicks = coreEntity.getLastSuccessTime() - coreEntity.getStartTime();
            int completedCount = coreEntity.getCompletedCount();
            Game24PuzzleReward.handleReward(serverLevel, player, coreEntity);
            player.sendOverlayMessage(Component.translatable("msg.simple24puzzle.success",
                    completedCount, Game24PuzzleHelper.getFormattedTime(elapsedTicks)));
            startGame(serverLevel, pos, state, player);
        } else {
            player.sendOverlayMessage(Component.translatable("msg.simple24puzzle.failed"));
        }
    }

    private ClickArea getClickRegion(BlockState state, BlockHitResult hit) {
        if (hit.getDirection() != state.getValue(FACING)) return ClickArea.OTHER;
        Vec3 uv = getUV(state.getValue(FACING), hit.getLocation(), hit.getBlockPos());
        int x = (int) uv.x, y = (int) uv.y;
        if (x >= CENTER_MIN && x <= CENTER_MAX && y >= CENTER_MIN && y <= CENTER_MAX) return ClickArea.CENTER;
        boolean left = x > CENTER_MAX, right = x < CENTER_MIN;
        if (y > CENTER_MAX) {
            if (left) return ClickArea.TOP_LEFT;
            else if (right) return ClickArea.TOP_RIGHT;
        } else if (y < CENTER_MIN) {
            if (left) return ClickArea.BOTTOM_LEFT;
            else if (right) return ClickArea.BOTTOM_RIGHT;
        }
        return ClickArea.OTHER;
    }

    private Vec3 getUV(Direction facing, Vec3 worldPos, BlockPos blockPos) {
        double localX = worldPos.x - blockPos.getX(), localY = worldPos.y - blockPos.getY(), localZ = worldPos.z - blockPos.getZ();
        return switch (facing) {
            case NORTH, SOUTH -> new Vec3(Mth.clamp(localX * 16, 0, 16), Mth.clamp(localY * 16, 0, 16), 0);
            case EAST, WEST -> new Vec3(Mth.clamp(localZ * 16, 0, 16), Mth.clamp(localY * 16, 0, 16), 0);
            default -> new Vec3(0, 0, 0);
        };
    }

    @Override
    public boolean checkLayoutAreaIsEmpty(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        return Game24PuzzleHelper.checkLayoutAreaIsEmpty(serverLevel, pos, state.getValue(FACING));
    }

    @Override
    public boolean unfoldGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player) {
        if (!checkLayoutAreaIsEmpty(serverLevel, pos, state)) {
            player.sendOverlayMessage(Component.translatable("msg.common.obstructed"));
            return false;
        }

        Direction facing = state.getValue(FACING);
        Game24PuzzleHelper.generateLayout(serverLevel, pos, facing);
        serverLevel.setBlock(pos, state.setValue(UNFOLDED, true), Block.UPDATE_ALL);
        startGame(serverLevel, pos, state, player);

        return true;
    }

    @Override
    public void startGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player) {
        Game24PuzzleHelper.startGame(serverLevel, pos, state.getValue(FACING));
    }

    @Override
    public void resetGame(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        Game24PuzzleHelper.resetGame(serverLevel, pos, state.getValue(FACING));
    }

    @Override
    public void minimizeGame(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        Game24PuzzleHelper.minimizeLayout(serverLevel, pos, state.getValue(FACING));
    }

    @Override
    public void closeGame(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        Game24PuzzleHelper.closeLayout(serverLevel, pos, state.getValue(FACING));
    }

    @Override
    public BlockEntity getGameCoreEntity(ServerLevel serverLevel, BlockPos pos) {
        return serverLevel.getBlockEntity(pos);
    }
}
