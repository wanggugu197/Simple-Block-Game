package com.simple_block_game.common.simpleMinesweeper.logic;

import com.simple_block_game.common.simpleMinesweeper.data.MinesweeperState;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/** 扫雷游戏核心逻辑 */
public final class GameMinesweeperLogic {

    private static final Random RANDOM = ThreadLocalRandom.current();

    public record FlipResult(boolean[][] mineGrid, MinesweeperState[][] displayGrid, boolean gameOver, boolean gameWin, int flagCount) {}

    public record FlagResult(MinesweeperState[][] displayGrid, int flagCount, boolean success, boolean gameWin) {}

    private GameMinesweeperLogic() {}

    /** 生成雷区布局（首次点击位置周围3x3安全） */
    public static boolean[][] generateMineGrid(int width, int height, int mineCount, int startX, int startZ) {
        boolean[][] mineGrid = new boolean[height][width];
        int placed = 0;
        while (placed < mineCount) {
            int x = RANDOM.nextInt(width);
            int z = RANDOM.nextInt(height);
            if (!isSafe(x, z, startX, startZ) && !mineGrid[z][x]) {
                mineGrid[z][x] = true;
                placed++;
            }
        }
        return mineGrid;
    }

    public static MinesweeperState[][] initDisplayGrid(int width, int height) {
        MinesweeperState[][] grid = new MinesweeperState[height][width];
        for (int z = 0; z < height; z++) Arrays.fill(grid[z], MinesweeperState.UNOPENED);
        return grid;
    }

    /** 处理方块翻开操作 */
    public static FlipResult processFlip(boolean[][] mineGrid, MinesweeperState[][] displayGrid, int x, int z, int flagCount) {
        MinesweeperState current = displayGrid[z][x];
        if (current != MinesweeperState.UNOPENED) {
            return new FlipResult(copy(mineGrid), copy(displayGrid), false, isWin(mineGrid, displayGrid), flagCount);
        }
        if (mineGrid[z][x]) {
            revealMines(displayGrid, mineGrid, x, z);
            return new FlipResult(copy(mineGrid), displayGrid, true, isWin(mineGrid, displayGrid), flagCount);
        }
        int adj = countAdjacent(mineGrid, x, z);
        if (adj == 0) {
            displayGrid[z][x] = MinesweeperState.OPEN_EMPTY;
            chainFlip(mineGrid, displayGrid, x, z);
        } else {
            displayGrid[z][x] = MinesweeperState.fromInt(adj);
        }
        return new FlipResult(copy(mineGrid), copy(displayGrid), false, isWin(mineGrid, displayGrid), flagCount);
    }

    /** 处理旗帜标记操作 */
    public static FlagResult processFlag(boolean[][] mineGrid, MinesweeperState[][] displayGrid, int x, int z, int flagCount, int totalMines) {
        MinesweeperState[][] newGrid = copy(displayGrid);
        int newFlagCount = flagCount;
        boolean success = false;

        if (displayGrid[z][x] == MinesweeperState.UNOPENED && flagCount < totalMines) {
            newGrid[z][x] = MinesweeperState.FLAGGED;
            newFlagCount++;
            success = true;
        } else if (displayGrid[z][x] == MinesweeperState.FLAGGED) {
            newGrid[z][x] = MinesweeperState.UNOPENED;
            newFlagCount--;
            success = true;
        }
        return new FlagResult(newGrid, newFlagCount, success, isWin(mineGrid, newGrid));
    }

    /** BFS连锁翻开空白区域 */
    private static void chainFlip(boolean[][] mineGrid, MinesweeperState[][] displayGrid, int startX, int startZ) {
        int w = mineGrid[0].length, h = mineGrid.length;
        boolean[][] visited = new boolean[h][w];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[] { startX, startZ });
        visited[startZ][startX] = true;

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            for (int dz = -1; dz <= 1; dz++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (dx == 0 && dz == 0) continue;
                    int nx = pos[0] + dx, nz = pos[1] + dz;
                    if (nx < 0 || nx >= w || nz < 0 || nz >= h || visited[nz][nx]) continue;
                    if (displayGrid[nz][nx] != MinesweeperState.UNOPENED) continue;
                    visited[nz][nx] = true;
                    int adj = countAdjacent(mineGrid, nx, nz);
                    if (adj == 0) {
                        displayGrid[nz][nx] = MinesweeperState.OPEN_EMPTY;
                        queue.add(new int[] { nx, nz });
                    } else {
                        displayGrid[nz][nx] = MinesweeperState.fromInt(adj);
                    }
                }
            }
        }
    }

    /** 计算周围地雷数量 */
    public static int countAdjacent(boolean[][] mineGrid, int x, int z) {
        int count = 0;
        int w = mineGrid[0].length, h = mineGrid.length;
        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dz == 0) continue;
                int nx = x + dx, nz = z + dz;
                if (nx >= 0 && nx < w && nz >= 0 && nz < h && mineGrid[nz][nx]) count++;
            }
        }
        return count;
    }

    /** 判断游戏是否胜利 */
    public static boolean isWin(boolean[][] mineGrid, MinesweeperState[][] displayGrid) {
        int w = mineGrid[0].length, h = mineGrid.length;
        for (int z = 0; z < h; z++) {
            for (int x = 0; x < w; x++) {
                if (!mineGrid[z][x] && displayGrid[z][x] == MinesweeperState.UNOPENED) return false;
                if (mineGrid[z][x] && displayGrid[z][x] != MinesweeperState.FLAGGED) return false;
            }
        }
        return true;
    }

    /** 揭示所有地雷（游戏结束时调用） */
    private static void revealMines(MinesweeperState[][] displayGrid, boolean[][] mineGrid, int deathX, int deathZ) {
        int w = mineGrid[0].length, h = mineGrid.length;
        for (int z = 0; z < h; z++) {
            for (int x = 0; x < w; x++) {
                if (mineGrid[z][x]) {
                    displayGrid[z][x] = (x == deathX && z == deathZ) ? MinesweeperState.DEATH_BOMB : MinesweeperState.BOMB;
                } else if (displayGrid[z][x] == MinesweeperState.FLAGGED) {
                    displayGrid[z][x] = MinesweeperState.getWrongFlagByNumber(countAdjacent(mineGrid, x, z));
                }
            }
        }
    }

    public static boolean[][] copy(boolean[][] original) {
        boolean[][] copy = new boolean[original.length][];
        for (int i = 0; i < original.length; i++) copy[i] = Arrays.copyOf(original[i], original[i].length);
        return copy;
    }

    public static MinesweeperState[][] copy(MinesweeperState[][] original) {
        MinesweeperState[][] copy = new MinesweeperState[original.length][];
        for (int i = 0; i < original.length; i++) copy[i] = Arrays.copyOf(original[i], original[i].length);
        return copy;
    }

    private static boolean isSafe(int x, int z, int startX, int startZ) {
        return Math.abs(x - startX) <= 1 && Math.abs(z - startZ) <= 1;
    }
}
