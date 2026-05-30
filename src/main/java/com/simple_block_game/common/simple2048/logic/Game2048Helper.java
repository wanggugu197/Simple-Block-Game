package com.simple_block_game.common.simple2048.logic;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simple2048.block.Block2048Core;
import com.simple_block_game.common.simple2048.block.Block2048Display;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static com.simple_block_game.common.simple2048.logic.Game2048Logic.GRID_SIZE;

public final class Game2048Helper {

    private static final int LAYOUT_SIZE = GRID_SIZE + 2;
    private static final int REFRESH_OFFSET = GRID_SIZE + 1;

    private Game2048Helper() {}

    public static boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        Direction.Axis axis = getAxis(coreFacing);
        int dir = getDirection(coreFacing);

        for (int i = 0; i < LAYOUT_SIZE; i++) {
            for (int j = 0; j < LAYOUT_SIZE; j++) {
                BlockPos pos = calcPos(corePos, axis, dir, i, j);
                if (!pos.equals(corePos) && !level.isEmptyBlock(pos)) return false;
            }
        }
        return true;
    }

    public static void generateLayout(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        Direction.Axis axis = getAxis(coreFacing);
        int dir = getDirection(coreFacing);

        BlockState displayState = SimpleBlockGameRegistration.BLOCK_2048_DISPLAY.get()
                .defaultBlockState().setValue(BaseRotatedBlock.FACING, coreFacing);
        BlockState refreshState = SimpleBlockGameRegistration.BLOCK_2048_REFRESH.get()
                .defaultBlockState().setValue(BaseRotatedBlock.FACING, coreFacing);
        BlockState frameState = SimpleBlockGameRegistration.BLOCK_ROTATED_FRAME.get()
                .defaultBlockState().setValue(BaseRotatedBlock.FACING, coreFacing);

        BlockPos refreshPos = null;

        for (int i = 0; i < LAYOUT_SIZE; i++) {
            for (int j = 0; j < LAYOUT_SIZE; j++) {
                BlockPos pos = calcPos(corePos, axis, dir, i, j);
                if (pos.equals(corePos)) continue;

                BlockState state = getBlockState(i, j, displayState, refreshState, frameState);

                if (level.isEmptyBlock(pos)) {
                    level.setBlock(pos, state, Block.UPDATE_ALL);
                    if (i == REFRESH_OFFSET && j == REFRESH_OFFSET) refreshPos = pos;
                }
            }
        }

        if (refreshPos != null && level.getBlockEntity(refreshPos) instanceof BlockRefreshEntity refreshEntity) {
            refreshEntity.setCorePos(corePos);
        }
    }

    public static void minimizeLayout(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        if (corePos == null || !level.isLoaded(corePos)) return;

        Direction.Axis axis = getAxis(coreFacing);
        int dir = getDirection(coreFacing);

        for (int i = 0; i < LAYOUT_SIZE; i++) {
            for (int j = 0; j < LAYOUT_SIZE; j++) {
                BlockPos pos = calcPos(corePos, axis, dir, i, j);
                if (pos.equals(corePos)) continue;

                Block block = level.getBlockState(pos).getBlock();
                if (is2048Block(block)) level.removeBlock(pos, false);
            }
        }

        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof Block2048Core) {
            level.setBlock(corePos, coreState.setValue(IGameCoreBlock.UNFOLDED, false), Block.UPDATE_ALL);
            Block2048Core.reset(level, corePos);
        }
    }

    public static void closeLayout(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        minimizeLayout(level, corePos, coreFacing);
        if (corePos == null || !level.isLoaded(corePos)) return;

        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof Block2048Core) {
            Vec3 dropPos = Vec3.atCenterOf(corePos);
            level.addFreshEntity(new ItemEntity(level, dropPos.x, dropPos.y, dropPos.z,
                    new ItemStack(SimpleBlockGameRegistration.BLOCK_2048_CORE.get())));
            level.removeBlock(corePos, false);
        }
    }

    public static void resetLayout(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        if (corePos == null || !level.isLoaded(corePos)) return;
        writeDisplayGrid(level, corePos, coreFacing, Game2048Logic.initGrid());

        if (level.getBlockState(corePos).getBlock() instanceof Block2048Core) {
            Block2048Core.reset(level, corePos);
        }
    }

    public static int[][] readDisplayGrid(ServerLevel level, BlockPos corePos, Direction facing) {
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        Direction.Axis axis = getAxis(facing);
        int dir = getDirection(facing);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                BlockPos pos = calcPos(corePos, axis, dir, col + 1, GRID_SIZE - row);
                grid[row][col] = Block2048Display.getDisplayValue(level, pos);
            }
        }
        return grid;
    }

    public static void writeDisplayGrid(ServerLevel level, BlockPos corePos, Direction facing, int[][] grid) {
        if (Game2048Logic.isInvalidGrid(grid)) return;

        Direction.Axis axis = getAxis(facing);
        int dir = getDirection(facing);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                BlockPos pos = calcPos(corePos, axis, dir, col + 1, GRID_SIZE - row);
                Block2048Display.setDisplayValue(level, pos, grid[row][col]);
            }
        }
    }

    private static Direction.Axis getAxis(Direction facing) {
        return switch (facing) {
            case NORTH, SOUTH -> Direction.Axis.X;
            case EAST, WEST -> Direction.Axis.Z;
            default -> Direction.Axis.Y;
        };
    }

    private static int getDirection(Direction facing) {
        return switch (facing) {
            case NORTH, EAST -> -1;
            case SOUTH, WEST -> +1;
            default -> 1;
        };
    }

    private static BlockPos calcPos(BlockPos corePos, Direction.Axis axis, int dir, int i, int j) {
        int offset = i * dir;
        return switch (axis) {
            case X -> new BlockPos(corePos.getX() + offset, corePos.getY() + j, corePos.getZ());
            case Z -> new BlockPos(corePos.getX(), corePos.getY() + j, corePos.getZ() + offset);
            default -> corePos;
        };
    }

    private static BlockState getBlockState(int i, int j, BlockState display, BlockState refresh, BlockState frame) {
        if (i >= 1 && i <= GRID_SIZE && j >= 1 && j <= GRID_SIZE) return display;
        if (i == REFRESH_OFFSET && j == REFRESH_OFFSET) return refresh;
        return frame;
    }

    private static boolean is2048Block(Block block) {
        return block == SimpleBlockGameRegistration.BLOCK_ROTATED_FRAME.get() ||
                block == SimpleBlockGameRegistration.BLOCK_2048_DISPLAY.get() ||
                block == SimpleBlockGameRegistration.BLOCK_2048_REFRESH.get();
    }
}
