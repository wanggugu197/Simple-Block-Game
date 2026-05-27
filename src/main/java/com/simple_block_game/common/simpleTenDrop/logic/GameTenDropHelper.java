package com.simple_block_game.common.simpleTenDrop.logic;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.common.simpleTenDrop.block.BlockTenDropDisplayEntity;
import com.simple_block_game.common.simpleTenDrop.data.DropletLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class GameTenDropHelper {

    private static final Block CORE = SimpleBlockGameRegistration.BLOCK_TEN_DROP_CORE.get();
    private static final Block DISPLAY = SimpleBlockGameRegistration.BLOCK_TEN_DROP_DISPLAY.get();
    private static final Block FRAME = SimpleBlockGameRegistration.BLOCK_VERTICAL_FRAME.get();
    private static final Block REFRESH = SimpleBlockGameRegistration.BLOCK_TEN_DROP_REFRESH.get();

    private GameTenDropHelper() {}

    public static boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos corePos) {
        for (BlockPos pos : getLayoutPositions(corePos)) {
            if (!level.isEmptyBlock(pos)) {
                return false;
            }
        }
        return true;
    }

    public static void generateLayout(ServerLevel level, BlockPos corePos) {
        BlockState displayState = DISPLAY.defaultBlockState();
        BlockState frameState = FRAME.defaultBlockState();
        BlockState refreshState = REFRESH.defaultBlockState();

        for (BlockPos pos : getDisplayPositions(corePos)) {
            if (level.isEmptyBlock(pos)) {
                level.setBlock(pos, displayState, 3);
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof BlockTenDropDisplayEntity entity) {
                    entity.setDropletLevel(DropletLevel.EMPTY);
                }
            }
        }

        for (BlockPos pos : getFramePositions(corePos)) {
            if (level.isEmptyBlock(pos)) {
                level.setBlock(pos, frameState, 3);
            }
        }

        BlockPos refreshPos = getRefreshPos(corePos);
        if (level.isEmptyBlock(refreshPos)) {
            level.setBlock(refreshPos, refreshState, 3);
            if (level.getBlockEntity(refreshPos) instanceof BlockRefreshEntity refreshEntity) {
                refreshEntity.setCorePos(corePos);
            }
        }
    }

    public static void minimizeLayout(ServerLevel level, BlockPos corePos) {
        for (BlockPos pos : getDisplayPositions(corePos)) {
            if (level.getBlockState(pos).getBlock() == DISPLAY) {
                level.removeBlock(pos, false);
            }
        }

        for (BlockPos pos : getFramePositions(corePos)) {
            if (level.getBlockState(pos).getBlock() == FRAME) {
                level.removeBlock(pos, false);
            }
        }

        BlockPos refreshPos = getRefreshPos(corePos);
        if (level.getBlockState(refreshPos).getBlock() == REFRESH) {
            level.removeBlock(refreshPos, false);
        }
    }

    public static void destroyLayout(ServerLevel level, BlockPos corePos) {
        minimizeLayout(level, corePos);
    }

    public static void updateDisplay(ServerLevel level, BlockPos corePos, int[][] grid) {
        for (int row = 0; row < GameTenDropLogic.GRID_SIZE; row++) {
            for (int col = 0; col < GameTenDropLogic.GRID_SIZE; col++) {
                BlockPos displayPos = getDisplayPosition(corePos, col, row);
                if (level.getBlockEntity(displayPos) instanceof BlockTenDropDisplayEntity entity) {
                    entity.setLevelValue(grid[row][col]);
                }
                BlockState state = level.getBlockState(displayPos);
                if (state.hasProperty(com.simple_block_game.common.simpleTenDrop.block.BlockTenDropDisplay.DROPLET_LEVEL)) {
                    level.setBlock(displayPos, state.setValue(
                            com.simple_block_game.common.simpleTenDrop.block.BlockTenDropDisplay.DROPLET_LEVEL,
                            DropletLevel.fromLevel(grid[row][col])), 3);
                }
            }
        }
    }

    private static Iterable<BlockPos> getLayoutPositions(BlockPos corePos) {
        java.util.List<BlockPos> positions = new java.util.ArrayList<>();

        for (BlockPos pos : getDisplayPositions(corePos)) {
            positions.add(pos);
        }
        for (BlockPos pos : getFramePositions(corePos)) {
            positions.add(pos);
        }
        positions.add(getRefreshPos(corePos));

        return positions;
    }

    private static Iterable<BlockPos> getDisplayPositions(BlockPos corePos) {
        java.util.List<BlockPos> positions = new java.util.ArrayList<>();
        for (int row = 0; row < GameTenDropLogic.GRID_SIZE; row++) {
            for (int col = 0; col < GameTenDropLogic.GRID_SIZE; col++) {
                positions.add(corePos.offset(col, 0, row));
            }
        }
        return positions;
    }

    public static BlockPos getDisplayPosition(BlockPos corePos, int col, int row) {
        return corePos.offset(col, 0, row);
    }

    private static Iterable<BlockPos> getFramePositions(BlockPos corePos) {
        java.util.List<BlockPos> positions = new java.util.ArrayList<>();
        int size = GameTenDropLogic.GRID_SIZE;

        positions.add(corePos.offset(-1, 0, -1));
        positions.add(corePos.offset(size, 0, -1));
        positions.add(corePos.offset(-1, 0, size));
        positions.add(corePos.offset(size, 0, size));

        for (int i = 0; i < size; i++) {
            positions.add(corePos.offset(i, 0, -1));
            positions.add(corePos.offset(i, 0, size));
            positions.add(corePos.offset(-1, 0, i));
            positions.add(corePos.offset(size, 0, i));
        }

        return positions;
    }

    private static BlockPos getRefreshPos(BlockPos corePos) {
        return corePos.offset(GameTenDropLogic.GRID_SIZE / 2, 0, GameTenDropLogic.GRID_SIZE + 1);
    }
}
