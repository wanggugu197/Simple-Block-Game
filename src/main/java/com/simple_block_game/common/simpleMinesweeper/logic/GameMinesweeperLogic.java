package com.simple_block_game.common.simpleMinesweeper.logic;

import com.simple_block_game.common.simpleMinesweeper.data.MinesweeperState;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 扫雷核心游戏逻辑类
 */
public class GameMinesweeperLogic {

    private static final Random RANDOM = ThreadLocalRandom.current();

    /**
     * 翻牌操作结果
     */
    public record FlipResult(
                             boolean[][] mineGrid,
                             MinesweeperState[][] displayGrid,
                             @Getter boolean gameOver,
                             @Getter boolean gameWin,
                             @Getter int flagCount) {}

    /**
     * 插旗/拔旗操作结果
     */
    public record FlagResult(
                             MinesweeperState[][] displayGrid,
                             @Getter int flagCount,
                             @Getter boolean success,
                             @Getter boolean gameWin) {}

    /**
     * 生成雷分布数组
     */
    public static boolean[][] generateMineGrid(int width, int height, int mineCount, int startX, int startZ) {
        validateCoordinate(width, height, startX, startZ);
        boolean[][] mineGrid = new boolean[height][width];
        int placedMines = 0;
        while (placedMines < mineCount) {
            int randomX = RANDOM.nextInt(width);
            int randomZ = RANDOM.nextInt(height);
            if (isInInitialSafeZone(randomX, randomZ, startX, startZ)) {
                continue;
            }
            if (!mineGrid[randomZ][randomX]) {
                mineGrid[randomZ][randomX] = true;
                placedMines++;
            }
        }
        return mineGrid;
    }

    /**
     * 初始化显示状态数组
     */
    public static MinesweeperState[][] initDisplayGrid(int width, int height) {
        MinesweeperState[][] displayGrid = new MinesweeperState[height][width];
        for (int z = 0; z < height; z++) {
            Arrays.fill(displayGrid[z], MinesweeperState.UNOPENED);
        }
        return displayGrid;
    }

    /**
     * 处理方块翻牌操作
     */
    public static FlipResult processFlip(boolean[][] mineGrid, MinesweeperState[][] displayGrid, int x, int z, int currentFlagCount) {
        validateGridConsistency(mineGrid, displayGrid);
        validateCoordinate(mineGrid[0].length, mineGrid.length, x, z);
        MinesweeperState currentState = displayGrid[z][x];
        if (currentState != MinesweeperState.UNOPENED) {
            return new FlipResult(deepCopyMineGrid(mineGrid), deepCopyDisplayGrid(displayGrid), false, isGameWin(mineGrid, displayGrid), currentFlagCount);
        }
        if (mineGrid[z][x]) {
            revealAllMines(displayGrid, mineGrid, x, z);
            return new FlipResult(deepCopyMineGrid(mineGrid), displayGrid, true, isGameWin(mineGrid, displayGrid), currentFlagCount);
        }
        int adjacentMineCount = countAdjacentMines(mineGrid, x, z);
        if (adjacentMineCount == 0) {
            displayGrid[z][x] = MinesweeperState.OPEN_EMPTY;
            chainFlipEmptyBlocks(mineGrid, displayGrid, x, z);
        } else {
            displayGrid[z][x] = MinesweeperState.fromInt(adjacentMineCount);
        }
        return new FlipResult(deepCopyMineGrid(mineGrid), deepCopyDisplayGrid(displayGrid), false, isGameWin(mineGrid, displayGrid), currentFlagCount);
    }

    /**
     * 处理方块插旗/拔旗操作
     */
    public static FlagResult processFlag(boolean[][] mineGrid, MinesweeperState[][] displayGrid, int x, int z, int currentFlagCount, int totalMineCount) {
        validateGridConsistency(mineGrid, displayGrid);
        validateDisplayGrid(displayGrid);
        validateCoordinate(displayGrid[0].length, displayGrid.length, x, z);
        if (totalMineCount < 0) {
            throw new IllegalArgumentException("总雷数不能为负数（当前：" + totalMineCount + "）");
        }
        MinesweeperState currentState = displayGrid[z][x];
        MinesweeperState[][] newDisplayGrid = deepCopyDisplayGrid(displayGrid);
        int newFlagCount = currentFlagCount;
        boolean success = false;
        if (currentState == MinesweeperState.UNOPENED && currentFlagCount < totalMineCount) {
            newDisplayGrid[z][x] = MinesweeperState.FLAGGED;
            newFlagCount++;
            success = true;
        } else if (currentState == MinesweeperState.FLAGGED) {
            newDisplayGrid[z][x] = MinesweeperState.UNOPENED;
            newFlagCount--;
            success = true;
        }
        boolean gameWin = isGameWin(mineGrid, newDisplayGrid);
        return new FlagResult(newDisplayGrid, newFlagCount, success, gameWin);
    }

    /**
     * 连锁翻开空方块
     */
    private static void chainFlipEmptyBlocks(boolean[][] mineGrid, MinesweeperState[][] displayGrid, int startX, int startZ) {
        int width = mineGrid[0].length;
        int height = mineGrid.length;
        boolean[][] visited = new boolean[height][width];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[] { startX, startZ });
        visited[startZ][startX] = true;
        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int currX = pos[0];
            int currZ = pos[1];
            for (int dz = -1; dz <= 1; dz++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (dx == 0 && dz == 0) continue;
                    int newX = currX + dx;
                    int newZ = currZ + dz;
                    if (isOutOfBounds(width, height, newX, newZ) || visited[newZ][newX]) {
                        continue;
                    }
                    MinesweeperState state = displayGrid[newZ][newX];
                    if (state != MinesweeperState.UNOPENED) {
                        continue;
                    }
                    visited[newZ][newX] = true;
                    int adjacentMineCount = countAdjacentMines(mineGrid, newX, newZ);
                    if (adjacentMineCount == 0) {
                        displayGrid[newZ][newX] = MinesweeperState.OPEN_EMPTY;
                        queue.add(new int[] { newX, newZ });
                    } else {
                        displayGrid[newZ][newX] = MinesweeperState.fromInt(adjacentMineCount);
                    }
                }
            }
        }
    }

    /**
     * 计算指定位置相邻8格的雷数
     */
    public static int countAdjacentMines(boolean[][] mineGrid, int x, int z) {
        validateCoordinate(mineGrid[0].length, mineGrid.length, x, z);
        int width = mineGrid[0].length;
        int height = mineGrid.length;
        int count = 0;
        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dz == 0) continue;
                int newX = x + dx;
                int newZ = z + dz;
                if (isOutOfBounds(width, height, newX, newZ)) {
                    continue;
                }
                if (mineGrid[newZ][newX]) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 判断游戏是否胜利
     */
    public static boolean isGameWin(boolean[][] mineGrid, MinesweeperState[][] displayGrid) {
        validateGridConsistency(mineGrid, displayGrid);
        int width = mineGrid[0].length;
        int height = mineGrid.length;
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                if (!mineGrid[z][x]) {
                    if (displayGrid[z][x] == MinesweeperState.UNOPENED) return false;
                } else {
                    if (displayGrid[z][x] != MinesweeperState.FLAGGED) return false;
                }
            }
        }
        return true;
    }

    /**
     * 暴露所有雷
     */
    private static void revealAllMines(MinesweeperState[][] displayGrid, boolean[][] mineGrid, int deathX, int deathZ) {
        validateGridConsistency(mineGrid, displayGrid);
        int width = mineGrid[0].length;
        int height = mineGrid.length;
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                if (mineGrid[z][x]) {
                    displayGrid[z][x] = (x == deathX && z == deathZ) ? MinesweeperState.DEATH_BOMB : MinesweeperState.BOMB;
                } else if (displayGrid[z][x] == MinesweeperState.FLAGGED) {
                    int adjacentMineCount = countAdjacentMines(mineGrid, x, z);
                    displayGrid[z][x] = MinesweeperState.getWrongFlagByNumber(adjacentMineCount);
                }
            }
        }
    }

    /**
     * 深拷贝雷区数组
     */
    public static boolean[][] deepCopyMineGrid(boolean[][] original) {
        validateMineGrid(original);
        boolean[][] copy = new boolean[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }

    /**
     * 深拷贝显示状态数组
     */
    public static MinesweeperState[][] deepCopyDisplayGrid(MinesweeperState[][] original) {
        validateDisplayGrid(original);
        MinesweeperState[][] copy = new MinesweeperState[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }

    /**
     * 统一校验坐标合法性
     */
    private static void validateCoordinate(int width, int height, int x, int z) {
        if (isOutOfBounds(width, height, x, z)) {
            throw new IllegalArgumentException("坐标(" + x + "," + z + ")超出棋盘范围（0~" + (width - 1) + ", 0~" + (height - 1) + "）");
        }
    }

    /**
     * 校验雷区和显示数组尺寸一致
     */
    private static void validateGridConsistency(boolean[][] mineGrid, MinesweeperState[][] displayGrid) {
        validateMineGrid(mineGrid);
        validateDisplayGrid(displayGrid);
        if (mineGrid.length != displayGrid.length || mineGrid[0].length != displayGrid[0].length) {
            throw new IllegalArgumentException(
                    "雷区数组和显示数组尺寸不匹配！雷区：" + mineGrid[0].length + "x" + mineGrid.length +
                            "，显示数组：" + displayGrid[0].length + "x" + displayGrid.length);
        }
    }

    /**
     * 校验雷区数组合法性
     */
    private static void validateMineGrid(boolean[][] mineGrid) {
        if (mineGrid == null || mineGrid.length == 0 || mineGrid[0].length == 0) {
            throw new IllegalArgumentException("雷区数组不能为空且尺寸必须大于0");
        }
    }

    /**
     * 校验显示数组合法性
     */
    static void validateDisplayGrid(MinesweeperState[][] displayGrid) {
        if (displayGrid == null || displayGrid.length == 0 || displayGrid[0].length == 0) {
            throw new IllegalArgumentException("显示数组不能为空且尺寸必须大于0");
        }
    }

    /**
     * 判断坐标是否越界
     */
    private static boolean isOutOfBounds(int width, int height, int x, int z) {
        return x < 0 || x >= width || z < 0 || z >= height;
    }

    /**
     * 判断坐标是否在初始安全区（3x3）
     */
    private static boolean isInInitialSafeZone(int x, int z, int startX, int startZ) {
        return Math.abs(x - startX) <= 1 && Math.abs(z - startZ) <= 1;
    }
}
