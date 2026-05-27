package com.simple_block_game.common.simpleMinesweeper.logic;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCore;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCoreEntity;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperDisplay;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperDisplayEntity;
import com.simple_block_game.common.simpleMinesweeper.data.MinesweeperState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class GameMinesweeperHelper {

    private static final Block FRAME = SimpleBlockGameRegistration.BLOCK_VERTICAL_FRAME.get();
    private static final Block DISPLAY = SimpleBlockGameRegistration.BLOCK_MINESWEEPER_DISPLAY.get();
    private static final Block REFRESH = SimpleBlockGameRegistration.BLOCK_MINESWEEPER_REFRESH.get();
    private static final Block CORE = SimpleBlockGameRegistration.BLOCK_MINESWEEPER_CORE.get();

    private GameMinesweeperHelper() {}

    public static boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos corePos, int width, int height) {
        if (corePos == null || !level.isLoaded(corePos)) return false;

        for (int x = 0; x <= width + 1; x++) {
            for (int z = 0; z <= height + 1; z++) {
                BlockPos pos = corePos.offset(x, 0, z);
                if (!pos.equals(corePos) && !level.isEmptyBlock(pos)) return false;
            }
        }
        return level.isEmptyBlock(corePos.offset(width + 1, 0, height + 1));
    }

    public static void generateLayout(ServerLevel level, BlockPos corePos, int width, int height) {
        Direction coreFacing = level.getBlockState(corePos).getValue(BaseVerticalBlock.FACING);
        BlockState displayState = DISPLAY.defaultBlockState().setValue(BaseVerticalBlock.FACING, coreFacing);
        BlockState frameState = FRAME.defaultBlockState().setValue(BaseVerticalBlock.FACING, coreFacing);
        BlockState refreshState = REFRESH.defaultBlockState().setValue(BaseVerticalBlock.FACING, coreFacing);

        for (int x = 0; x <= width + 1; x++) {
            for (int z = 0; z <= height + 1; z++) {
                BlockPos pos = corePos.offset(x, 0, z);
                if (pos.equals(corePos)) continue;
                boolean isDisplay = x > 0 && x <= width && z > 0 && z <= height;
                level.setBlock(pos, isDisplay ? displayState : frameState, Block.UPDATE_ALL);
                if (isDisplay) {
                    initDisplayEntity(level, pos, corePos);
                }
            }
        }
        BlockPos refreshPos = corePos.offset(width + 1, 0, height + 1);
        level.setBlock(refreshPos, refreshState, Block.UPDATE_ALL);
        initRefreshEntity(level, refreshPos, corePos);
    }

    public static void minimizeLayout(ServerLevel level, BlockPos corePos, int width, int height) {
        if (corePos == null || !level.isLoaded(corePos)) return;
        for (int x = 0; x <= width + 1; x++) {
            for (int z = 0; z <= height + 1; z++) {
                Block block = level.getBlockState(corePos.offset(x, 0, z)).getBlock();
                if (block == FRAME || block == DISPLAY) level.removeBlock(corePos.offset(x, 0, z), false);
            }
        }
        if (level.getBlockState(corePos.offset(width + 1, 0, height + 1)).getBlock() == REFRESH) {
            level.removeBlock(corePos.offset(width + 1, 0, height + 1), false);
        }
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof BlockMinesweeperCore) {
            level.setBlock(corePos, coreState.setValue(IGameCoreBlock.UNFOLDED, false), Block.UPDATE_ALL);
            BlockMinesweeperCore.reset(level, corePos);
        }
    }

    public static void closeLayout(ServerLevel level, BlockPos corePos, int width, int height) {
        minimizeLayout(level, corePos, width, height);
        if (corePos == null || !level.isLoaded(corePos)) return;
        if (level.getBlockState(corePos).getBlock() instanceof BlockMinesweeperCore) {
            Vec3 center = Vec3.atCenterOf(corePos);
            ItemEntity item = new ItemEntity(level, center.x, center.y, center.z, new ItemStack(CORE));
            item.setDefaultPickUpDelay();
            level.addFreshEntity(item);
            level.removeBlock(corePos, false);
        }
    }

    public static void resetLayout(ServerLevel level, BlockPos corePos, int width, int height) {
        if (corePos == null || !level.isLoaded(corePos)) return;
        writeDisplayGrid(level, corePos, GameMinesweeperLogic.initDisplayGrid(width, height));
    }

    public static MinesweeperState[][] readDisplayGrid(ServerLevel level, BlockPos corePos, int width, int height) {
        MinesweeperState[][] grid = new MinesweeperState[height][width];
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                grid[z][x] = BlockMinesweeperDisplay.getDisplayState(level, corePos.offset(x + 1, 0, z + 1));
            }
        }
        return grid;
    }

    public static void writeDisplayGrid(ServerLevel level, BlockPos corePos, MinesweeperState[][] displayGrid) {
        int w = displayGrid[0].length, h = displayGrid.length;
        for (int z = 0; z < h; z++) {
            for (int x = 0; x < w; x++) {
                BlockMinesweeperDisplay.setDisplayState(level, corePos.offset(x + 1, 0, z + 1), displayGrid[z][x]);
            }
        }
    }

    public static boolean isFlagCountMatched(ServerLevel level, BlockPos corePos, int x, int z, int target, int width, int height) {
        int flags = 0;
        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dz == 0) continue;
                int nx = x + dx, nz = z + dz;
                if (nx < 0 || nx >= width || nz < 0 || nz >= height) continue;
                MinesweeperState state = BlockMinesweeperDisplay.getDisplayState(level, corePos.offset(nx + 1, 0, nz + 1));
                if (state.isFlagged() && !state.isWrongFlag()) flags++;
            }
        }
        return flags == target;
    }

    public static boolean[] openSurrounding(ServerLevel level, BlockMinesweeperCoreEntity core, int x, int z) {
        BlockPos corePos = core.getBlockPos();
        boolean[][] mineGrid = core.getMineGrid();
        int w = core.getGridWidth(), h = core.getGridHeight();
        MinesweeperState[][] grid = readDisplayGrid(level, corePos, w, h);

        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dz == 0) continue;
                int nx = x + dx, nz = z + dz;
                if (nx < 0 || nx >= w || nz < 0 || nz >= h) continue;
                if (grid[nz][nx].isUnopened()) {
                    GameMinesweeperLogic.FlipResult result = GameMinesweeperLogic.processFlip(mineGrid, grid, nx, nz, core.getCurrentFlagCount());
                    if (result.gameOver()) {
                        writeDisplayGrid(level, corePos, result.displayGrid());
                        return new boolean[] { true, false };
                    }
                    for (int i = 0; i < h; i++) {
                        System.arraycopy(result.displayGrid()[i], 0, grid[i], 0, w);
                    }
                }
            }
        }
        writeDisplayGrid(level, corePos, grid);
        return new boolean[] { false, GameMinesweeperLogic.isWin(mineGrid, grid) };
    }

    public static void generateExplosions(ServerLevel level, BlockPos corePos, BlockMinesweeperCoreEntity core) {
        boolean[][] mineGrid = core.getMineGrid();
        int w = core.getGridWidth(), h = core.getGridHeight();
        for (int z = 0; z < h; z++) {
            for (int x = 0; x < w; x++) {
                if (mineGrid[z][x]) {
                    BlockPos pos = corePos.offset(x + 1, 1, z + 1);
                    level.explode(null, null, null, pos.getX(), pos.getY(), pos.getZ(), 5.0f, false, Level.ExplosionInteraction.NONE);
                }
            }
        }
    }

    public static boolean isFirstOpen(MinesweeperState[][] displayGrid) {
        for (MinesweeperState[] row : displayGrid) {
            for (MinesweeperState state : row) {
                if (state != MinesweeperState.UNOPENED) return false;
            }
        }
        return true;
    }

    private static void initDisplayEntity(ServerLevel level, BlockPos pos, BlockPos corePos) {
        if (!level.isLoaded(pos)) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockMinesweeperDisplayEntity entity) {
            entity.setCorePos(corePos);
            entity.setDisplayState(MinesweeperState.UNOPENED);
            entity.setChanged();
        }
    }

    private static void initRefreshEntity(ServerLevel level, BlockPos pos, BlockPos corePos) {
        if (!level.isLoaded(pos)) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockRefreshEntity entity) {
            entity.setCorePos(corePos);
            entity.setChanged();
        }
    }
}
