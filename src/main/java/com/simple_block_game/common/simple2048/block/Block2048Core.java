package com.simple_block_game.common.simple2048.block;

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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.simple2048.data.Quadrant;
import com.simple_block_game.common.simple2048.logic.Game2048Helper;
import com.simple_block_game.common.simple2048.logic.Game2048Logic;
import com.simple_block_game.common.simple2048.logic.Game2048Reward;
import lombok.NonNull;

import javax.annotation.Nullable;

public class Block2048Core extends BaseRotatedBlock {

    public static final BooleanProperty UNFOLDED = BooleanProperty.create("unfolded");

    public Block2048Core(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new Block2048CoreEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }

        boolean isUnfolded = state.getValue(UNFOLDED);

        if (!isUnfolded) {
            if (!isClickOnCenter8x8(state, hit)) {
                return InteractionResult.PASS;
            }
            if (!preUnfoldCheck(serverLevel, pos, state, player)) {
                return InteractionResult.SUCCESS;
            }
            Game2048Helper.generate2048Layout(serverLevel, pos, state.getValue(FACING));
            serverLevel.setBlock(pos, state.setValue(UNFOLDED, true), 3);

            int[][] initGrid = Game2048Logic.initGrid();
            Game2048Helper.writeDisplayGrid(serverLevel, pos, state.getValue(FACING), initGrid);

            reset(serverLevel, pos);
            player.sendOverlayMessage(Component.translatable("msg.simple2048.game_started"));

        } else {
            Quadrant clickedQuadrant = getClickedQuadrant(state, hit);
            handleGameMove(serverLevel, pos, state, player, clickedQuadrant);
        }

        return InteractionResult.SUCCESS;
    }

    protected boolean preUnfoldCheck(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player) {
        boolean isAreaEmpty = Game2048Helper.checkLayoutAreaIsEmpty(serverLevel, pos, state.getValue(FACING));
        if (!isAreaEmpty) {
            player.sendOverlayMessage(Component.translatable("msg.simple2048.obstructed"));
            return false;
        }
        return true;
    }

    private boolean isClickOnCenter8x8(BlockState state, BlockHitResult hit) {
        Vec3 worldHitPos = hit.getLocation();
        Direction blockFacing = state.getValue(FACING);
        Direction hitFace = hit.getDirection();
        if (hitFace != blockFacing) {
            return false;
        }
        double relX = Mth.frac(worldHitPos.x);
        double relY = Mth.frac(worldHitPos.y);
        double relZ = Mth.frac(worldHitPos.z);
        double u = 0, v = 0;
        switch (blockFacing) {
            case NORTH, SOUTH -> {
                u = relX * 16;
                v = relY * 16;
            }
            case EAST, WEST -> {
                u = relZ * 16;
                v = relY * 16;
            }
        }
        u = Mth.clamp(u, 0, 16);
        v = Mth.clamp(v, 0, 16);
        return u >= 4 && u <= 12 && v >= 4 && v <= 12;
    }

    private Quadrant getClickedQuadrant(BlockState state, BlockHitResult hit) {
        Vec3 worldHitPos = hit.getLocation();
        Direction blockFacing = state.getValue(FACING);
        Direction hitFace = hit.getDirection();
        if (hitFace != blockFacing) {
            return Quadrant.NULL;
        }
        BlockPos blockPos = hit.getBlockPos();
        double localX = worldHitPos.x - blockPos.getX();
        double localY = worldHitPos.y - blockPos.getY();
        double localZ = worldHitPos.z - blockPos.getZ();
        double u, v;
        switch (blockFacing) {
            case SOUTH:
                u = Mth.clamp(localX * 16, 0, 16);
                v = Mth.clamp(localY * 16, 0, 16);
                boolean southSub = v < (16 - u);
                boolean southMain = v < u;
                if (southSub) {
                    return southMain ? Quadrant.DOWN : Quadrant.LEFT;
                } else {
                    return southMain ? Quadrant.RIGHT : Quadrant.UP;
                }
            case NORTH:
                u = Mth.clamp(localX * 16, 0, 16);
                v = Mth.clamp(localY * 16, 0, 16);
                boolean northSub = v < (16 - u);
                boolean northMain = v < u;
                if (northSub) {
                    return northMain ? Quadrant.DOWN : Quadrant.RIGHT;
                } else {
                    return northMain ? Quadrant.LEFT : Quadrant.UP;
                }
            case EAST:
                u = Mth.clamp(localZ * 16, 0, 16);
                v = Mth.clamp(localY * 16, 0, 16);
                boolean eastSub = v < (16 - u);
                boolean eastMain = v < u;
                if (eastSub) {
                    return eastMain ? Quadrant.DOWN : Quadrant.RIGHT;
                } else {
                    return eastMain ? Quadrant.LEFT : Quadrant.UP;
                }
            case WEST:
                u = Mth.clamp(localZ * 16, 0, 16);
                v = Mth.clamp(localY * 16, 0, 16);
                boolean westSub = v < (16 - u);
                boolean westMain = v < u;
                if (westSub) {
                    return westMain ? Quadrant.DOWN : Quadrant.LEFT;
                } else {
                    return westMain ? Quadrant.RIGHT : Quadrant.UP;
                }
            default:
                return Quadrant.NULL;
        }
    }

    private void handleGameMove(ServerLevel serverLevel, BlockPos corePos, BlockState coreState, Player player, Quadrant direction) {
        if (direction == Quadrant.NULL) {
            return;
        }
        int[][] currentGrid = Game2048Helper.readDisplayGrid(serverLevel, corePos, coreState.getValue(FACING));

        Game2048Logic.MoveResult moveResult = Game2048Logic.processMove(currentGrid, direction);
        int[][] finalGrid = moveResult.newGrid();
        int addedScore = moveResult.score();
        int newMaxNumber = moveResult.maxNumber();

        Game2048Helper.writeDisplayGrid(serverLevel, corePos, coreState.getValue(FACING), finalGrid);

        if (addedScore > 0) {
            int oldTotalScore = getScore(serverLevel, corePos);
            addScore(serverLevel, corePos, addedScore);
            int totalScore = getScore(serverLevel, corePos);
            Game2048Reward.handleScoreReward(serverLevel, player, oldTotalScore, totalScore);
            player.sendOverlayMessage(Component.translatable("msg.simple2048.move_score", addedScore, totalScore));
        }

        int oldMaxNumber = getMaxNumber(serverLevel, corePos);
        if (newMaxNumber != oldMaxNumber) {
            setMaxNumber(serverLevel, corePos, newMaxNumber);
            Game2048Reward.handleMaxNumberReward(serverLevel, player, oldMaxNumber, newMaxNumber);
        }

        if (moveResult.isGameOver()) {
            int totalScore = getScore(serverLevel, corePos);
            int finalMaxNumber = getMaxNumber(serverLevel, corePos);
            player.sendOverlayMessage(Component.translatable("msg.simple2048.unmoveable", finalMaxNumber, totalScore));
        }
    }

    private static int getScore(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof Block2048CoreEntity coreEntity) {
            return coreEntity.getScore();
        }
        return 0;
    }

    private static void addScore(ServerLevel level, BlockPos pos, int add) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof Block2048CoreEntity coreEntity) {
            coreEntity.addScore(add);
            coreEntity.setChanged();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }
    }

    private static int getMaxNumber(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof Block2048CoreEntity coreEntity) {
            return coreEntity.getMaxNumber();
        }
        return 0;
    }

    private static void setMaxNumber(ServerLevel level, BlockPos pos, int newMaxNumber) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof Block2048CoreEntity coreEntity) {
            coreEntity.setMaxNumber(newMaxNumber);
            coreEntity.setChanged();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3); // 同步到客户端
        }
    }

    public static void reset(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof Block2048CoreEntity coreEntity) {
            coreEntity.resetScore();
            coreEntity.resetMaxNumber();
            coreEntity.setChanged();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }
    }
}
