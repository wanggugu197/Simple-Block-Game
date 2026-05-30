package com.simple_block_game.common.simple2048.logic;

import com.simple_block_game.common.simple2048.data.Quadrant;

import java.util.concurrent.ThreadLocalRandom;

public final class Game2048Logic {

    public static final int GRID_SIZE = 4;
    private static final double SPAWN_TWO_CHANCE = 0.9D;

    public record MoveResult(int[][] newGrid, int score, int maxNumber, boolean moved, boolean gameOver) {}

    private Game2048Logic() {}

    public static int[][] initGrid() {
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        addRandomNumber(grid);
        addRandomNumber(grid);
        return grid;
    }

    public static boolean isInvalidGrid(int[][] grid) {
        if (grid == null) return true;
        if (grid.length != GRID_SIZE) return true;
        for (int[] row : grid) {
            if (row == null || row.length != GRID_SIZE) return true;
        }
        return false;
    }

    public static MoveResult processMove(int[][] originalGrid, Quadrant direction) {
        if (isInvalidGrid(originalGrid)) return new MoveResult(initGrid(), 0, 0, false, false);

        int[][] grid = copy(originalGrid);
        if (direction == null || direction == Quadrant.NULL) {
            return new MoveResult(grid, 0, findMax(grid), false, canNotMove(grid));
        }

        boolean moved = false;
        int score = 0;
        int maxNumber = findMax(grid);

        int[][] working = rotateToLeft(grid, direction);

        for (int row = 0; row < GRID_SIZE; row++) {
            MergeResult mergeResult = mergeLineWithScore(working[row]);
            if (!arraysEqual(working[row], mergeResult.line)) moved = true;
            working[row] = mergeResult.line;
            score += mergeResult.score;
            maxNumber = Math.max(maxNumber, findMaxInLine(working[row]));
        }

        int[][] finalGrid = rotateBack(working, direction);

        if (moved) {
            addRandomNumber(finalGrid);
            maxNumber = Math.max(maxNumber, findMax(finalGrid));
        }

        boolean gameOver = canNotMove(finalGrid);
        return new MoveResult(finalGrid, score, maxNumber, moved, gameOver);
    }

    public static int[][] copy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }

    private record MergeResult(int[] line, int score) {}

    private static int[][] rotateToLeft(int[][] grid, Quadrant direction) {
        int[][] result = copy(grid);
        for (int i = 0; i < direction.getRotationCount(); i++) {
            result = rotate90Left(result);
        }
        return result;
    }

    private static int[][] rotateBack(int[][] grid, Quadrant direction) {
        int[][] result = copy(grid);
        for (int i = 0; i < (4 - direction.getRotationCount()) % 4; i++) {
            result = rotate90Left(result);
        }
        return result;
    }

    private static int[][] rotate90Left(int[][] grid) {
        int[][] result = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                result[GRID_SIZE - 1 - j][i] = grid[i][j];
            }
        }
        return result;
    }

    private static MergeResult mergeLineWithScore(int[] line) {
        int[] filtered = filterZero(line);
        int[] merged = new int[GRID_SIZE];
        int index = 0;
        int score = 0;

        for (int i = 0; i < filtered.length; i++) {
            if (i + 1 < filtered.length && filtered[i] == filtered[i + 1]) {
                int newValue = filtered[i] * 2;
                merged[index++] = newValue;
                score += newValue;
                i++;
            } else {
                merged[index++] = filtered[i];
            }
        }
        return new MergeResult(merged, score);
    }

    private static int[] filterZero(int[] line) {
        int count = 0;
        for (int num : line) if (num != 0) count++;
        int[] result = new int[count];
        int index = 0;
        for (int num : line) if (num != 0) result[index++] = num;
        return result;
    }

    private static void addRandomNumber(int[][] grid) {
        int emptyCount = 0;
        for (int[] row : grid) for (int num : row) if (num == 0) emptyCount++;
        if (emptyCount == 0) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int target = random.nextInt(emptyCount);
        int count = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == 0) {
                    if (count == target) {
                        grid[i][j] = random.nextDouble() < SPAWN_TWO_CHANCE ? 2 : 4;
                        return;
                    }
                    count++;
                }
            }
        }
    }

    private static boolean canNotMove(int[][] grid) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == 0) return false;
                if (i < GRID_SIZE - 1 && grid[i][j] == grid[i + 1][j]) return false;
                if (j < GRID_SIZE - 1 && grid[i][j] == grid[i][j + 1]) return false;
            }
        }
        return true;
    }

    private static int findMax(int[][] grid) {
        int max = 0;
        for (int[] row : grid) for (int num : row) max = Math.max(max, num);
        return max;
    }

    private static int findMaxInLine(int[] line) {
        int max = 0;
        for (int num : line) max = Math.max(max, num);
        return max;
    }

    private static boolean arraysEqual(int[] a, int[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) if (a[i] != b[i]) return false;
        return true;
    }
}
