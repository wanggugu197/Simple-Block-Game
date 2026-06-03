package com.simple_block_game.common.simpleJustGet10.logic;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10Core;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10CoreEntity;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10Display;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10DisplayEntity;
import com.simple_block_game.common.simpleJustGet10.data.JustGet10GameState;
import com.simple_block_game.common.simpleJustGet10.data.ValueJustGet10;
import com.simple_block_game.common.simpleJustGet10.simpleJustGet10Registration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class GameJustGet10Helper {

    private static final Block FRAME = SimpleBlockGameRegistration.BLOCK_ROTATED_FRAME.get();
    private static final Block DISPLAY_BLOCK = simpleJustGet10Registration.BLOCK_JUST_GET_10_DISPLAY.get();
    private static final Block REFRESH_BLOCK = simpleJustGet10Registration.BLOCK_JUST_GET_10_REFRESH.get();
    private static final Block CORE_BLOCK = simpleJustGet10Registration.BLOCK_JUST_GET_10_CORE.get();

    private static final int GRID_SIZE = GameJustGet10Logic.SIZE;
    private static final int LAYOUT_SIZE = GRID_SIZE + 2;

    private GameJustGet10Helper() {}

    private static Direction.Axis getAxis(Direction facing) {
        return switch (facing) {
            case NORTH, SOUTH -> Direction.Axis.X;
            case EAST, WEST -> Direction.Axis.Z;
            default -> Direction.Axis.Y;
        };
    }

    private static int getDirFactor(Direction facing) {
        return switch (facing) {
            case NORTH, EAST -> -1;
            case SOUTH, WEST -> +1;
            default -> 1;
        };
    }

    private static BlockPos calcPos(BlockPos corePos, Direction.Axis axis, int dirFactor, int x, int y) {
        int offset = x * dirFactor;
        return switch (axis) {
            case X -> new BlockPos(corePos.getX() + offset, corePos.getY() + y, corePos.getZ());
            case Z -> new BlockPos(corePos.getX(), corePos.getY() + y, corePos.getZ() + offset);
            default -> corePos;
        };
    }

    public static BlockPos getDisplayPosition(BlockPos corePos, int row, int col, Direction facing) {
        return calcPos(corePos, getAxis(facing), getDirFactor(facing), col + 1, row + 1);
    }

    public static BlockPos getGridPosition(BlockPos corePos, BlockPos displayPos, Direction facing) {
        Direction.Axis axis = getAxis(facing);
        int dirFactor = getDirFactor(facing);
        int layoutX = axis == Direction.Axis.X ? (displayPos.getX() - corePos.getX()) * dirFactor : (displayPos.getZ() - corePos.getZ()) * dirFactor;
        int row = (displayPos.getY() - corePos.getY()) - 1;
        int col = layoutX - 1;
        return (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) ? new BlockPos(row, 0, col) : null;
    }

    public static boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos corePos, Direction facing) {
        if (corePos == null || !level.isLoaded(corePos)) return false;
        Direction.Axis axis = getAxis(facing);
        int dirFactor = getDirFactor(facing);
        for (int x = 0; x < LAYOUT_SIZE; x++) {
            for (int y = 0; y < LAYOUT_SIZE; y++) {
                BlockPos pos = calcPos(corePos, axis, dirFactor, x, y);
                if (!pos.equals(corePos) && !level.isEmptyBlock(pos)) return false;
            }
        }
        return true;
    }

    public static void generateLayout(ServerLevel level, BlockPos corePos, Direction facing) {
        Direction.Axis axis = getAxis(facing);
        int dirFactor = getDirFactor(facing);
        BlockState displayState = DISPLAY_BLOCK.defaultBlockState().setValue(BaseRotatedBlock.FACING, facing);
        BlockState frameState = FRAME.defaultBlockState().setValue(BaseRotatedBlock.FACING, facing);
        BlockState refreshState = REFRESH_BLOCK.defaultBlockState().setValue(BaseRotatedBlock.FACING, facing);
        for (int x = 0; x < LAYOUT_SIZE; x++) {
            for (int y = 0; y < LAYOUT_SIZE; y++) {
                BlockPos pos = calcPos(corePos, axis, dirFactor, x, y);
                if (pos.equals(corePos)) continue;
                BlockState state = (x >= 1 && x <= GRID_SIZE && y >= 1 && y <= GRID_SIZE) ? displayState : (x == LAYOUT_SIZE - 1 && y == LAYOUT_SIZE - 1) ? refreshState : frameState;
                if (!level.isEmptyBlock(pos)) continue;
                level.setBlock(pos, state, Block.UPDATE_ALL);
                if (x >= 1 && x <= GRID_SIZE && y >= 1 && y <= GRID_SIZE && level.getBlockEntity(pos) instanceof BlockJustGet10DisplayEntity entity) {
                    entity.setCorePos(corePos);
                }
                if (x == LAYOUT_SIZE - 1 && y == LAYOUT_SIZE - 1 && level.getBlockEntity(pos) instanceof BlockRefreshEntity refreshEntity) {
                    refreshEntity.setCorePos(corePos);
                }
            }
        }
    }

    public static void initBoard(ServerLevel level, BlockPos corePos, Direction facing) {
        writeDisplayGrid(level, corePos, facing, GameJustGet10Logic.createNewBoard());
    }

    public static void resetLayout(ServerLevel level, BlockPos corePos, Direction facing) {
        initBoard(level, corePos, facing);
        if (level.getBlockEntity(corePos) instanceof BlockJustGet10CoreEntity core) {
            core.resetScore();
            core.resetMaxValue();
            core.setGameState(JustGet10GameState.PLAYING);
        }
    }

    public static void minimizeLayout(ServerLevel level, BlockPos corePos, Direction facing) {
        if (corePos == null || !level.isLoaded(corePos)) return;
        Direction.Axis axis = getAxis(facing);
        int dirFactor = getDirFactor(facing);
        for (int x = 0; x < LAYOUT_SIZE; x++) {
            for (int y = 0; y < LAYOUT_SIZE; y++) {
                BlockPos pos = calcPos(corePos, axis, dirFactor, x, y);
                if (pos.equals(corePos)) continue;
                Block block = level.getBlockState(pos).getBlock();
                if (block == FRAME || block == DISPLAY_BLOCK || block == REFRESH_BLOCK) {
                    level.removeBlock(pos, false);
                }
            }
        }
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof BlockJustGet10Core) {
            level.setBlock(corePos, coreState.setValue(IGameCoreBlock.UNFOLDED, false), Block.UPDATE_ALL);
            if (level.getBlockEntity(corePos) instanceof BlockJustGet10CoreEntity core) {
                core.completeReset();
            }
        }
    }

    public static void closeLayout(ServerLevel level, BlockPos corePos, Direction facing) {
        minimizeLayout(level, corePos, facing);
        if (corePos == null || !level.isLoaded(corePos)) return;
        if (level.getBlockState(corePos).getBlock() instanceof BlockJustGet10Core) {
            Vec3 center = Vec3.atCenterOf(corePos);
            ItemEntity item = new ItemEntity(level, center.x, center.y, center.z, new ItemStack(CORE_BLOCK));
            item.setDefaultPickUpDelay();
            level.addFreshEntity(item);
            level.removeBlock(corePos, false);
        }
    }

    public static List<GameJustGet10Logic.Point> getHighlightPositions(ServerLevel level, BlockPos corePos, Direction facing, int row, int col) {
        return GameJustGet10Logic.getHighlightPositions(readDisplayGrid(level, corePos, facing), row, col);
    }

    public static GameJustGet10Logic.MergeResult processMerge(ServerLevel level, BlockPos corePos, Direction facing, int targetRow, int targetCol, int currentScore) {
        ValueJustGet10[][] board = readDisplayGrid(level, corePos, facing);
        var highlight = GameJustGet10Logic.getHighlightPositions(board, targetRow, targetCol);
        var result = GameJustGet10Logic.processMergeAndGravity(board, targetRow, targetCol, highlight, currentScore);
        if (result.success()) writeDisplayGrid(level, corePos, facing, board);
        return result;
    }

    public static boolean checkGameOver(ServerLevel level, BlockPos corePos, Direction facing) {
        return GameJustGet10Logic.checkGameOver(readDisplayGrid(level, corePos, facing));
    }

    public static void clearAllHighlights(ServerLevel level, BlockPos corePos, Direction facing) {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                BlockJustGet10Display.setHighlighted(level, getDisplayPosition(corePos, r, c, facing), false);
            }
        }
    }

    public static void setHighlights(ServerLevel level, BlockPos corePos, Direction facing, List<GameJustGet10Logic.Point> positions) {
        positions.forEach(p -> BlockJustGet10Display.setHighlighted(level, getDisplayPosition(corePos, p.row(), p.col(), facing), true));
    }

    public static ValueJustGet10[][] readDisplayGrid(ServerLevel level, BlockPos corePos, Direction facing) {
        ValueJustGet10[][] board = new ValueJustGet10[GRID_SIZE][GRID_SIZE];
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                ValueJustGet10 v = BlockJustGet10Display.getDisplayValue(level, getDisplayPosition(corePos, r, c, facing));
                board[r][c] = v != null ? v : ValueJustGet10.NUM_1;
            }
        }
        return board;
    }

    public static void writeDisplayGrid(ServerLevel level, BlockPos corePos, Direction facing, ValueJustGet10[][] board) {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                BlockJustGet10Display.setDisplayValue(level, getDisplayPosition(corePos, r, c, facing), board[r][c]);
            }
        }
    }

    public static ValueJustGet10 getDisplayValue(ServerLevel level, BlockPos displayPos) {
        return BlockJustGet10Display.getDisplayValue(level, displayPos);
    }
}
