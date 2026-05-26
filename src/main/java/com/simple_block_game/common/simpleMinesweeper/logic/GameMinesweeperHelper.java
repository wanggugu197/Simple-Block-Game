package com.simple_block_game.common.simpleMinesweeper.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.simpleMinesweeper.block.*;
import com.simple_block_game.common.simpleMinesweeper.data.MinesweeperState;

public class GameMinesweeperHelper {

    private GameMinesweeperHelper() {}

    /**
     * 检查扫雷布局区域是否为空
     */
    public static boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos corePos, int width, int height) {
        for (int xOffset = 0; xOffset <= width + 1; xOffset++) {
            for (int zOffset = 0; zOffset <= height + 1; zOffset++) {
                BlockPos layoutPos = calculateLayoutPos(corePos, xOffset, zOffset);
                if (layoutPos.equals(corePos)) {
                    continue;
                }
                if (!level.isEmptyBlock(layoutPos)) {
                    return false;
                }
            }
        }
        BlockPos refreshPos = calculateRefreshPos(corePos, width, height);
        return level.isEmptyBlock(refreshPos);
    }

    /**
     * 初始化放置扫雷布局方块
     */
    public static void placeMinesweeperBlocks(ServerLevel level, BlockPos corePos, int width, int height) {
        validateLayoutParams(width, height);
        for (int xOffset = 0; xOffset <= width + 1; xOffset++) {
            for (int zOffset = 0; zOffset <= height + 1; zOffset++) {
                BlockPos layoutPos = calculateLayoutPos(corePos, xOffset, zOffset);
                if (layoutPos.equals(corePos)) continue;
                BlockState placeState = getLayoutBlockState(xOffset, zOffset, width, height);
                level.setBlock(layoutPos, placeState, 3);
            }
        }
        BlockPos refreshPos = calculateRefreshPos(corePos, width, height);
        BlockState refreshState = SimpleBlockGameRegistration.BLOCK_MINESWEEPER_REFRESH.get().defaultBlockState();
        level.setBlock(refreshPos, refreshState, 3);
    }

    /**
     * 初始化所有扫雷方块的实体参数
     */
    public static void initMinesweeperEntities(ServerLevel level, BlockPos corePos, int width, int height) {
        if (!level.isLoaded(corePos)) {
            return;
        }
        for (int xOffset = 1; xOffset <= width; xOffset++) {
            for (int zOffset = 1; zOffset <= height; zOffset++) {
                BlockPos displayPos = calculateLayoutPos(corePos, xOffset, zOffset);
                if (!level.isLoaded(displayPos)) {
                    continue;
                }
                BlockEntity be = level.getBlockEntity(displayPos);
                if (be instanceof BlockMinesweeperDisplayEntity displayEntity) {
                    displayEntity.setCorePos(corePos);
                    displayEntity.setChanged();
                }
            }
        }
        BlockPos refreshPos = calculateRefreshPos(corePos, width, height);
        if (level.isLoaded(refreshPos)) {
            BlockEntity refreshBE = level.getBlockEntity(refreshPos);
            if (refreshBE instanceof BlockMinesweeperRefreshEntity refreshEntity) {
                refreshEntity.setCorePos(corePos);
                refreshEntity.setChanged();
            }
        }
    }

    /**
     * 最小化扫雷布局
     */
    public static void minimizeMinesweeperLayout(ServerLevel level, BlockPos corePos, int width, int height) {
        if (corePos == null || !level.isLoaded(corePos)) return;
        for (int xOffset = 0; xOffset <= width + 1; xOffset++) {
            for (int zOffset = 0; zOffset <= height + 1; zOffset++) {
                BlockPos layoutPos = calculateLayoutPos(corePos, xOffset, zOffset);
                BlockState targetState = level.getBlockState(layoutPos);
                Block targetBlock = targetState.getBlock();
                if (targetBlock == SimpleBlockGameRegistration.BLOCK_VERTICAL_FRAME.get() ||
                        targetBlock == SimpleBlockGameRegistration.BLOCK_MINESWEEPER_DISPLAY.get()) {
                    level.removeBlock(layoutPos, false);
                }
            }
        }
        BlockPos refreshPos = calculateRefreshPos(corePos, width, height);
        BlockState refreshState = level.getBlockState(refreshPos);
        if (refreshState.getBlock() == SimpleBlockGameRegistration.BLOCK_MINESWEEPER_REFRESH.get()) {
            level.removeBlock(refreshPos, false);
        }
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof BlockMinesweeperCore) {
            level.setBlock(corePos, coreState.setValue(BlockMinesweeperCore.GAME_STARTED, false), 3);
        }
    }

    /**
     * 关闭扫雷布局
     */
    public static void closeMinesweeperLayout(ServerLevel level, BlockPos corePos, int width, int height) {
        minimizeMinesweeperLayout(level, corePos, width, height);
        if (corePos == null || !level.isLoaded(corePos)) return;
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof BlockMinesweeperCore) {
            ItemStack coreItemStack = new ItemStack(SimpleBlockGameRegistration.BLOCK_MINESWEEPER_CORE.get());
            Vec3 dropPos = Vec3.atCenterOf(corePos);
            ItemEntity itemEntity = new ItemEntity(level, dropPos.x, dropPos.y, dropPos.z, coreItemStack);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
            level.removeBlock(corePos, false);
        }
    }

    /**
     * 重置扫雷布局
     */
    public static void resetMinesweeperLayout(ServerLevel level, BlockPos corePos, int width, int height) {
        if (corePos == null || !level.isLoaded(corePos)) return;
        MinesweeperState[][] initDisplayGrid = GameMinesweeperLogic.initDisplayGrid(width, height);
        writeDisplayGrid(level, corePos, initDisplayGrid);
    }

    /**
     * 从显示方块读取状态数组
     */
    public static MinesweeperState[][] readDisplayGrid(ServerLevel level, BlockPos corePos, int width, int height) {
        MinesweeperState[][] displayGrid = new MinesweeperState[height][width];
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                BlockPos displayPos = calculateDisplayPos(corePos, x, z);
                displayGrid[z][x] = BlockMinesweeperDisplay.getDisplayState(level, displayPos);
            }
        }
        return displayGrid;
    }

    /**
     * 将状态数组写入显示方块
     */
    public static void writeDisplayGrid(ServerLevel level, BlockPos corePos, MinesweeperState[][] displayGrid) {
        GameMinesweeperLogic.validateDisplayGrid(displayGrid);
        int width = displayGrid[0].length;
        int height = displayGrid.length;
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                BlockPos displayPos = calculateDisplayPos(corePos, x, z);
                BlockMinesweeperDisplay.setDisplayState(level, displayPos, displayGrid[z][x]);
            }
        }
    }

    /**
     * 检查数字方块周围插旗数是否匹配目标数
     */
    public static boolean isFlagCountMatched(ServerLevel level, BlockPos corePos, int gridX, int gridZ, int targetCount, int width, int height) {
        int flagCount = 0;
        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dz == 0) continue;
                int newX = gridX + dx;
                int newZ = gridZ + dz;
                if (newX < 0 || newX >= width || newZ < 0 || newZ >= height) continue;
                BlockPos surroundPos = calculateDisplayPos(corePos, newX, newZ);
                MinesweeperState state = BlockMinesweeperDisplay.getDisplayState(level, surroundPos);
                if (state.isFlagged() && !state.isWrongFlag()) {
                    flagCount++;
                }
            }
        }
        return flagCount == targetCount;
    }

    /**
     * 翻开数字方块周围未标记的区域
     */
    public static boolean[] openSurroundingBlocks(ServerLevel level, BlockMinesweeperCoreEntity coreEntity, int gridX, int gridZ) {
        BlockPos corePos = coreEntity.getBlockPos();
        boolean[][] mineGrid = coreEntity.getMineGrid();
        int width = coreEntity.getGridWidth();
        int height = coreEntity.getGridHeight();
        MinesweeperState[][] displayGrid = readDisplayGrid(level, corePos, width, height);
        boolean[] over = new boolean[2];
        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dz == 0) continue;
                int newX = gridX + dx;
                int newZ = gridZ + dz;
                if (newX < 0 || newX >= width || newZ < 0 || newZ >= height) continue;
                if (displayGrid[newZ][newX].isUnopened()) {
                    GameMinesweeperLogic.FlipResult result = GameMinesweeperLogic.processFlip(mineGrid, displayGrid, newX, newZ, coreEntity.getCurrentFlagCount());
                    over[0] = over[0] || result.gameOver();
                    over[1] = over[1] || result.gameWin();
                }
            }
        }
        writeDisplayGrid(level, corePos, displayGrid);
        MinesweeperState[][] newDisplayGrid = readDisplayGrid(level, corePos, width, height);
        over[1] = GameMinesweeperLogic.isGameWin(mineGrid, newDisplayGrid);
        return over;
    }

    /**
     * 在所有雷区位置生成爆炸效果
     */
    public static void generateAllMineExplosions(ServerLevel serverLevel, BlockPos corePos, BlockMinesweeperCoreEntity coreEntity) {
        boolean[][] mineGrid = coreEntity.getMineGrid();

        int gridWidth = coreEntity.getGridWidth();
        int gridHeight = coreEntity.getGridHeight();
        for (int gridZ = 0; gridZ < gridHeight; gridZ++) {
            for (int gridX = 0; gridX < gridWidth; gridX++) {
                if (mineGrid[gridZ][gridX]) {
                    BlockPos mineWorldPos = corePos.offset(
                            gridX + 1,
                            1,
                            gridZ + 1);

                    serverLevel.explode(
                            null,
                            null,
                            null,
                            mineWorldPos.getX(),
                            mineWorldPos.getY(),
                            mineWorldPos.getZ(),
                            5.0f,
                            false,
                            Level.ExplosionInteraction.NONE);
                }
            }
        }
    }

    /**
     * 是否为第一次翻开
     */
    public static boolean isFirstOpen(MinesweeperState[][] displayGrid) {
        for (MinesweeperState[] row : displayGrid) {
            for (MinesweeperState cellState : row) {
                if (cellState != MinesweeperState.UNOPENED) return false;
            }
        }
        return true;
    }

    /**
     * 计算布局方块坐标
     */
    private static BlockPos calculateLayoutPos(BlockPos corePos, int xOffset, int zOffset) {
        return new BlockPos(corePos.getX() + xOffset, corePos.getY(), corePos.getZ() + zOffset);
    }

    /**
     * 计算显示方块坐标
     */
    public static BlockPos calculateDisplayPos(BlockPos corePos, int displayX, int displayZ) {
        return calculateLayoutPos(corePos, displayX + 1, displayZ + 1);
    }

    /**
     * 计算刷新方块坐标
     */
    public static BlockPos calculateRefreshPos(BlockPos corePos, int width, int height) {
        return calculateLayoutPos(corePos, width + 1, height + 1);
    }

    /**
     * 获取该位置的块
     */
    private static BlockState getLayoutBlockState(int xOffset, int zOffset, int width, int height) {
        boolean isDisplayBlock = xOffset >= 1 && xOffset <= width && zOffset >= 1 && zOffset <= height;
        if (isDisplayBlock) {
            return SimpleBlockGameRegistration.BLOCK_MINESWEEPER_DISPLAY.get().defaultBlockState();
        } else {
            return SimpleBlockGameRegistration.BLOCK_VERTICAL_FRAME.get().defaultBlockState();
        }
    }

    /**
     * 校验布局长宽参数
     */
    private static void validateLayoutParams(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("布局长宽必须大于0（width=" + width + ", height=" + height + ")");
        }
        if (width > 256 || height > 256) {
            throw new IllegalArgumentException("布局长宽不能超过256");
        }
    }
}
