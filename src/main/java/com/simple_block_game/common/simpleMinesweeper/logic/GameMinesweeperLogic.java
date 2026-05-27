package com.simple_block_game.common.simpleMinesweeper.logic;

import com.simple_block_game.common.simpleMinesweeper.data.MinesweeperState;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class GameMinesweeperLogic {

    private static final Random RANDOM = ThreadLocalRandom.current();

    public record FlipResult(boolean[][] mineGrid, MinesweeperState[][] displayGrid, boolean gameOver, boolean gameWin, int flagCount) {}

    public record FlagResult(MinesweeperState[][] displayGrid, int flagCount, boolean success, boolean gameWin) {}

    private GameMinesweeperLogic() {}

    public static boolean[][] generateMineGrid(int width, int height, int mineCount, int startX, int startZ) {
        if (width <= 0 || height <= 0) return new boolean[0][0];

        boolean[][] mineGrid = new boolean[height][width];

        int safeZoneStartX = Math.max(0, startX - 1);
        int safeZoneEndX = Math.min(width - 1, startX + 1);
        int safeZoneStartZ = Math.max(0, startZ - 1);
        int safeZoneEndZ = Math.min(height - 1, startZ + 1);

        int safeCells = (safeZoneEndX - safeZoneStartX + 1) * (safeZoneEndZ - safeZoneStartZ + 1);
        int availableCells = width * height - safeCells;
        int actualMineCount = Math.min(mineCount, availableCells);

        if (actualMineCount <= 0) return mineGrid;

        int[] positions = new int[availableCells];
        int idx = 0;
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                if (x < safeZoneStartX || x > safeZoneEndX || z < safeZoneStartZ || z > safeZoneEndZ) {
                    positions[idx++] = (z << 16) | x;
                }
            }
        }

        for (int i = positions.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            int temp = positions[i];
            positions[i] = positions[j];
            positions[j] = temp;
        }

        for (int i = 0; i < actualMineCount; i++) {
            int pos = positions[i];
            int z = pos >> 16;
            int x = pos & 0xFFFF;
            mineGrid[z][x] = true;
        }

        return mineGrid;
    }

    public static MinesweeperState[][] initDisplayGrid(int width, int height) {
        MinesweeperState[][] grid = new MinesweeperState[height][width];
        for (int z = 0; z < height; z++) Arrays.fill(grid[z], MinesweeperState.UNOPENED);
        return grid;
    }

    public static FlipResult processFlip(boolean[][] mineGrid, MinesweeperState[][] displayGrid, int x, int z, int flagCount) {
        if (!isValidGrid(mineGrid, displayGrid)) {
            return new FlipResult(new boolean[0][0], new MinesweeperState[0][0], true, false, flagCount);
        }
        if (x < 0 || x >= mineGrid[0].length || z < 0 || z >= mineGrid.length) {
            return new FlipResult(copy(mineGrid), copy(displayGrid), false, isWin(mineGrid, displayGrid), flagCount);
        }

        MinesweeperState[][] newGrid = copy(displayGrid);
        MinesweeperState current = newGrid[z][x];

        if (current != MinesweeperState.UNOPENED) {
            return new FlipResult(copy(mineGrid), newGrid, false, isWin(mineGrid, newGrid), flagCount);
        }
        if (mineGrid[z][x]) {
            revealMines(newGrid, mineGrid, x, z);
            return new FlipResult(copy(mineGrid), newGrid, true, isWin(mineGrid, newGrid), flagCount);
        }
        int adj = countAdjacent(mineGrid, x, z);
        if (adj == 0) {
            newGrid[z][x] = MinesweeperState.OPEN_EMPTY;
            chainFlip(mineGrid, newGrid, x, z);
        } else {
            newGrid[z][x] = MinesweeperState.fromInt(adj);
        }
        return new FlipResult(copy(mineGrid), newGrid, false, isWin(mineGrid, newGrid), flagCount);
    }

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

    private static boolean isValidGrid(boolean[][] mineGrid, MinesweeperState[][] displayGrid) {
        if (mineGrid == null || displayGrid == null) return false;
        if (mineGrid.length != displayGrid.length) return false;
        if (mineGrid.length == 0) return false;
        if (mineGrid[0] == null || displayGrid[0] == null) return false;
        return mineGrid[0].length == displayGrid[0].length;
    }
}
