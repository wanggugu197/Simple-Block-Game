package com.simple_block_game.common.simpleSudoku.block;

import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.simpleSudoku.data.SudokuGameState;
import com.simple_block_game.common.simpleSudoku.logic.GameSudokuHelper;
import com.simple_block_game.common.simpleSudoku.logic.GameSudokuLogic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

public class BlockSudokuDisplay extends BaseVerticalBlock {

    public BlockSudokuDisplay(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static final MapCodec<BlockSudokuDisplay> CODEC = simpleCodec(BlockSudokuDisplay::new);

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockSudokuDisplayEntity(pos, state);
    }

    public static void setDisplayValue(ServerLevel level, BlockPos pos, int value) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BlockSudokuDisplayEntity displayEntity)) return;
        displayEntity.setValue(value);
    }

    public static int getDisplayValue(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BlockSudokuDisplayEntity displayEntity)) return 0;
        return displayEntity.getValue();
    }

    public static void setInitial(ServerLevel level, BlockPos pos, boolean initial) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BlockSudokuDisplayEntity displayEntity)) return;
        displayEntity.setInitial(initial);
    }

    public static boolean getInitial(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BlockSudokuDisplayEntity displayEntity)) return false;
        return displayEntity.isInitial();
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                     @NonNull BlockPos pos, @NonNull Player player,
                                                     @NonNull BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        ServerLevel serverLevel = (ServerLevel) level;

        if (hit.getDirection() != Direction.UP) return InteractionResult.PASS;
        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (!(be instanceof BlockSudokuDisplayEntity displayEntity)) return InteractionResult.PASS;
        if (displayEntity.isInitial()) return InteractionResult.PASS;

        if (displayEntity.getValue() == 0) {
            int number = getNumberFromClick(hit, pos);
            displayEntity.setValue(number);
            checkGameComplete(serverLevel, pos, player);
        } else {
            displayEntity.setValue(0);
        }
        return InteractionResult.SUCCESS;
    }

    private void checkGameComplete(ServerLevel level, BlockPos displayPos, Player player) {
        BlockPos corePos = findCorePos(level, displayPos);
        if (corePos == null) return;

        BlockState coreState = level.getBlockState(corePos);
        if (!(coreState.getBlock() instanceof BlockSudokuCore core)) return;

        BlockEntity coreBe = level.getBlockEntity(corePos);
        if (!(coreBe instanceof BlockSudokuCoreEntity coreEntity)) return;
        if (coreEntity.getGameState() == SudokuGameState.COMPLETE) return;

        int[][] board = GameSudokuHelper.readDisplayGrid(level, corePos);
        if (GameSudokuLogic.isBoardValidComplete(board, coreEntity.isDiagonalMode())) {
            core.onComplete(level, corePos, player);
        }
    }

    private BlockPos findCorePos(ServerLevel level, BlockPos displayPos) {
        int maxDist = GameSudokuHelper.GRID_SIZE + 2;
        for (int d = 0; d <= maxDist; d++) {
            for (int dx = -d; dx <= d; dx++) {
                int dz1 = d - Math.abs(dx);
                if (dz1 != 0) {
                    BlockPos pos1 = displayPos.offset(dx, 0, dz1);
                    if (level.isLoaded(pos1) && level.getBlockState(pos1).getBlock() instanceof BlockSudokuCore) {
                        return pos1;
                    }
                    BlockPos pos2 = displayPos.offset(dx, 0, -dz1);
                    if (level.isLoaded(pos2) && level.getBlockState(pos2).getBlock() instanceof BlockSudokuCore) {
                        return pos2;
                    }
                } else {
                    BlockPos pos = displayPos.offset(dx, 0, 0);
                    if (level.isLoaded(pos) && level.getBlockState(pos).getBlock() instanceof BlockSudokuCore) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private static final double THIRD = 16.0 / 3;

    private int getNumberFromClick(BlockHitResult hit, BlockPos pos) {
        Vec3 worldPos = hit.getLocation();
        double u = Mth.clamp((worldPos.x - pos.getX()) * 16, 0, 16);
        double v = Mth.clamp((worldPos.z - pos.getZ()) * 16, 0, 16);

        int col = u < THIRD ? 2 : (u < 2 * THIRD ? 1 : 0);
        int row = v < THIRD ? 0 : (v < 2 * THIRD ? 1 : 2);

        return row + col * 3 + 1;
    }
}
