package com.simple_block_game.common.simpleSudoku.logic;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuCore;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuDisplay;
import com.simple_block_game.common.simpleSudoku.data.SudokuDifficulty;
import com.simple_block_game.common.simpleSudoku.logic.GameSudokuLogic.PuzzleResult;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class GameSudokuHelper {

    public static final int GRID_SIZE = GameSudokuLogic.SIZE;
    private static final int LAYOUT_SIZE = GRID_SIZE + 2;

    private GameSudokuHelper() {}

    public static boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos corePos) {
        if (corePos == null || !level.isLoaded(corePos)) return false;
        for (int x = 0; x < LAYOUT_SIZE; x++) {
            for (int z = 0; z < LAYOUT_SIZE; z++) {
                BlockPos pos = corePos.offset(x, 0, z);
                if (x != 0 || z != 0) {
                    if (!level.isEmptyBlock(pos)) return false;
                }
            }
        }
        return true;
    }

    public static void generateLayout(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        BlockState displayState = SimpleBlockGameRegistration.BLOCK_SUDOKU_DISPLAY.get()
                .defaultBlockState().setValue(BaseVerticalBlock.FACING, coreFacing);
        BlockState refreshState = SimpleBlockGameRegistration.BLOCK_SUDOKU_REFRESH.get()
                .defaultBlockState().setValue(BaseVerticalBlock.FACING, coreFacing);
        BlockState frameState = SimpleBlockGameRegistration.BLOCK_VERTICAL_FRAME.get()
                .defaultBlockState().setValue(BaseVerticalBlock.FACING, coreFacing);
        for (int x = 0; x < LAYOUT_SIZE; x++) {
            for (int z = 0; z < LAYOUT_SIZE; z++) {
                if (x == 0 && z == 0) continue;
                BlockPos pos = corePos.offset(x, 0, z);
                boolean isDisplay = x > 0 && x <= GRID_SIZE && z > 0 && z <= GRID_SIZE;
                level.setBlock(pos, isDisplay ? displayState : frameState, Block.UPDATE_ALL);
            }
        }
        BlockPos refreshPos = corePos.offset(GRID_SIZE + 1, 0, GRID_SIZE + 1);
        level.setBlock(refreshPos, refreshState, Block.UPDATE_ALL);
        if (level.getBlockEntity(refreshPos) instanceof BlockRefreshEntity refreshEntity) {
            refreshEntity.setCorePos(corePos);
        }
    }

    public static void resetLayout(ServerLevel level, BlockPos corePos) {
        if (corePos == null || !level.isLoaded(corePos)) return;
        for (int z = 0; z < GRID_SIZE; z++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                BlockPos pos = corePos.offset(x + 1, 0, z + 1);
                if (!BlockSudokuDisplay.getInitial(level, pos)) {
                    BlockSudokuDisplay.setDisplayValue(level, pos, 0);
                }
            }
        }
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof BlockSudokuCore) {
            BlockSudokuCore.reset(level, corePos);
        }
    }

    public static void minimizeLayout(ServerLevel level, BlockPos corePos) {
        if (corePos == null || !level.isLoaded(corePos)) return;
        for (int x = 0; x < LAYOUT_SIZE; x++) {
            for (int z = 0; z < LAYOUT_SIZE; z++) {
                if (x == 0 && z == 0) continue;
                BlockPos pos = corePos.offset(x, 0, z);
                Block block = level.getBlockState(pos).getBlock();
                if (block == SimpleBlockGameRegistration.BLOCK_VERTICAL_FRAME.get() ||
                        block == SimpleBlockGameRegistration.BLOCK_SUDOKU_DISPLAY.get() ||
                        block == SimpleBlockGameRegistration.BLOCK_SUDOKU_REFRESH.get()) {
                    level.removeBlock(pos, false);
                }
            }
        }
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof BlockSudokuCore) {
            level.setBlock(corePos, coreState.setValue(com.simple_block_game.common.base.block.IGameCoreBlock.UNFOLDED, false), Block.UPDATE_ALL);
            BlockSudokuCore.reset(level, corePos);
        }
    }

    public static void closeLayout(ServerLevel level, BlockPos corePos) {
        if (corePos == null || !level.isLoaded(corePos)) return;
        minimizeLayout(level, corePos);
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof BlockSudokuCore) {
            Vec3 dropPos = Vec3.atCenterOf(corePos);
            level.addFreshEntity(new ItemEntity(level, dropPos.x, dropPos.y, dropPos.z,
                    new ItemStack(SimpleBlockGameRegistration.BLOCK_SUDOKU_CORE.get())));
            level.removeBlock(corePos, false);
        }
    }

    public static void initGame(ServerLevel level, BlockPos corePos, SudokuDifficulty difficulty, boolean diagonalMode) {
        PuzzleResult result = GameSudokuLogic.generatePuzzle(difficulty, diagonalMode);
        writeDisplayGridWithInitial(level, corePos, result.puzzle());
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof BlockSudokuCore core) {
            core.setDiagonalMode(level, corePos, diagonalMode);
            BlockSudokuCore.reset(level, corePos);
        }
    }

    public static void writeDisplayGridWithInitial(ServerLevel level, BlockPos corePos, int[][] puzzle) {
        if (puzzle == null || puzzle.length != GRID_SIZE) return;
        for (int z = 0; z < GRID_SIZE; z++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                BlockPos pos = corePos.offset(x + 1, 0, z + 1);
                int value = puzzle[z][x];
                BlockSudokuDisplay.setDisplayValue(level, pos, value);
                BlockSudokuDisplay.setInitial(level, pos, value != 0);
            }
        }
    }

    public static int[][] readDisplayGrid(ServerLevel level, BlockPos corePos) {
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        for (int z = 0; z < GRID_SIZE; z++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                BlockPos pos = corePos.offset(x + 1, 0, z + 1);
                grid[z][x] = BlockSudokuDisplay.getDisplayValue(level, pos);
            }
        }
        return grid;
    }
}
