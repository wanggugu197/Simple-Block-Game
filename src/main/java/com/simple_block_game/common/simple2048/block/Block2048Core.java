package com.simple_block_game.common.simple2048.block;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simple2048.data.Quadrant;
import com.simple_block_game.common.simple2048.logic.Game2048Helper;
import com.simple_block_game.common.simple2048.logic.Game2048Logic;
import com.simple_block_game.common.simple2048.logic.Game2048Reward;

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
 * 2048游戏核心方块
 */
public class Block2048Core extends BaseRotatedBlock implements IGameCoreBlock {

    private static final int CENTER_MIN = 4;
    private static final int CENTER_MAX = 12;

    public Block2048Core(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(UNFOLDED, false));
    }

    public static final MapCodec<Block2048Core> CODEC = simpleCodec(Block2048Core::new);

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
        return new Block2048CoreEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                     @NonNull BlockPos pos, @NonNull Player player,
                                                     @NonNull BlockHitResult hit) {
        if (!SimpleBlockGameConfig.enable2048Game.get()) return InteractionResult.PASS;

        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.FAIL;

        if (!state.getValue(UNFOLDED)) {
            if (!isCenterClick(state, hit)) return InteractionResult.PASS;
            unfoldGame(serverLevel, pos, state, player);
        } else {
            handleGameMove(serverLevel, pos, state, player, getQuadrant(state, hit));
        }
        return InteractionResult.SUCCESS;
    }

    private boolean isCenterClick(BlockState state, BlockHitResult hit) {
        if (hit.getDirection() != state.getValue(FACING)) return false;
        Vec3 uv = getUV(state.getValue(FACING), hit.getLocation(), hit.getBlockPos());
        return uv.x >= CENTER_MIN && uv.x <= CENTER_MAX && uv.y >= CENTER_MIN && uv.y <= CENTER_MAX;
    }

    public Quadrant getQuadrant(BlockState state, BlockHitResult hit) {
        Direction facing = state.getValue(FACING);
        if (hit.getDirection() != facing) return Quadrant.NULL;

        Vec3 uv = getUV(facing, hit.getLocation(), hit.getBlockPos());
        boolean swapX = facing == Direction.NORTH || facing == Direction.EAST;
        boolean subDiag = uv.y < (16 - uv.x);
        boolean mainDiag = uv.y < uv.x;

        Quadrant down = Quadrant.DOWN;
        Quadrant left = swapX ? Quadrant.RIGHT : Quadrant.LEFT;
        Quadrant right = swapX ? Quadrant.LEFT : Quadrant.RIGHT;
        Quadrant up = Quadrant.UP;

        return subDiag ? (mainDiag ? down : left) : (mainDiag ? right : up);
    }

    private Vec3 getUV(Direction facing, Vec3 worldPos, BlockPos blockPos) {
        double localX = worldPos.x - blockPos.getX();
        double localY = worldPos.y - blockPos.getY();
        double localZ = worldPos.z - blockPos.getZ();

        return switch (facing) {
            case NORTH, SOUTH -> new Vec3(Mth.clamp(localX * 16, 0, 16), Mth.clamp(localY * 16, 0, 16), 0);
            case EAST, WEST -> new Vec3(Mth.clamp(localZ * 16, 0, 16), Mth.clamp(localY * 16, 0, 16), 0);
            default -> new Vec3(0, 0, 0);
        };
    }

    public void handleGameMove(ServerLevel serverLevel, BlockPos corePos, BlockState coreState,
                               Player player, Quadrant direction) {
        if (direction == Quadrant.NULL) return;

        Direction facing = coreState.getValue(FACING);
        Game2048Logic.MoveResult result = Game2048Logic.processMove(
                Game2048Helper.readDisplayGrid(serverLevel, corePos, facing), direction);

        Block2048CoreEntity coreEntity = getCoreEntity(serverLevel, corePos);
        if (coreEntity == null) return;
        Game2048Helper.writeDisplayGrid(serverLevel, corePos, facing, result.newGrid());

        if (result.score() > 0) {
            int oldScore = coreEntity.getScore();
            coreEntity.addScore(result.score());
            syncEntity(coreEntity);
            Game2048Reward.handleScoreReward(serverLevel, player, oldScore, coreEntity.getScore());
            player.sendOverlayMessage(Component.translatable("msg.simple2048.move_score",
                    result.score(), coreEntity.getScore()));
        }

        if (result.maxNumber() != coreEntity.getMaxNumber()) {
            int oldMax = coreEntity.getMaxNumber();
            coreEntity.setMaxNumber(result.maxNumber());
            syncEntity(coreEntity);
            Game2048Reward.handleMaxNumberReward(serverLevel, player, oldMax, result.maxNumber());
        }

        if (result.gameOver()) {
            player.sendOverlayMessage(Component.translatable("msg.simple2048.unmoveable",
                    coreEntity.getMaxNumber(), coreEntity.getScore()));
        }
    }

    private Block2048CoreEntity getCoreEntity(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof Block2048CoreEntity coreEntity ? coreEntity : null;
    }

    private void syncEntity(Block2048CoreEntity coreEntity) {
        coreEntity.syncToClient();
    }

    public static void reset(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof Block2048CoreEntity coreEntity)) return;
        coreEntity.resetScore();
        coreEntity.resetMaxNumber();
    }

    @Override
    public boolean checkLayoutAreaIsEmpty(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        return Game2048Helper.checkLayoutAreaIsEmpty(serverLevel, pos, state.getValue(FACING));
    }

    @Override
    public boolean unfoldGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player) {
        if (!checkLayoutAreaIsEmpty(serverLevel, pos, state)) {
            player.sendOverlayMessage(Component.translatable("msg.common.obstructed"));
            return false;
        }

        Direction facing = state.getValue(FACING);
        Game2048Helper.generateLayout(serverLevel, pos, facing);
        serverLevel.setBlock(pos, state.setValue(UNFOLDED, true), Block.UPDATE_ALL);
        Game2048Helper.writeDisplayGrid(serverLevel, pos, facing, Game2048Logic.initGrid());
        reset(serverLevel, pos);

        player.sendOverlayMessage(Component.translatable("msg.simple2048.game_started"));
        return true;
    }

    @Override
    public void startGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player) {
        Game2048Helper.writeDisplayGrid(serverLevel, pos, state.getValue(FACING), Game2048Logic.initGrid());
        reset(serverLevel, pos);
        player.sendOverlayMessage(Component.translatable("msg.simple2048.game_started"));
    }

    @Override
    public void resetGame(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        Game2048Helper.resetLayout(serverLevel, pos, state.getValue(FACING));
    }

    @Override
    public void minimizeGame(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        Game2048Helper.minimizeLayout(serverLevel, pos, state.getValue(FACING));
    }

    @Override
    public void closeGame(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        Game2048Helper.closeLayout(serverLevel, pos, state.getValue(FACING));
    }

    @Override
    public BlockEntity getGameCoreEntity(ServerLevel serverLevel, BlockPos pos) {
        return serverLevel.getBlockEntity(pos);
    }
}
