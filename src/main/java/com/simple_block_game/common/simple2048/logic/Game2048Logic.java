package com.simple_block_game.common.simple2048.logic;

import com.simple_block_game.common.simple2048.data.Quadrant;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/** 2048游戏核心逻辑 */
public final class Game2048Logic {

    public static final int GRID_SIZE = 4;
    private static final Random RANDOM = ThreadLocalRandom.current();

    public record MoveResult(int[][] newGrid, int score, int maxNumber, boolean moved, boolean gameOver) {}

    private Game2048Logic() {}

    /** 初始化棋盘，添加两个随机数字 */
    public static int[][] initGrid() {
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        addRandomNumber(grid);
        addRandomNumber(grid);
        return grid;
    }

    /** 处理玩家移动 */
    public static MoveResult processMove(int[][] originalGrid, Quadrant direction) {
        int[][] grid = copy(originalGrid);
        boolean moved = false;
        int score = 0;
        int maxNumber = findMax(grid);

        int[][] working = rotateToLeft(grid, direction);

        for (int row = 0; row < GRID_SIZE; row++) {
            int[] result = mergeLine(working[row]);
            int lineScore = calculateLineScore(working[row], result);
            if (!arraysEqual(working[row], result)) moved = true;
            working[row] = result;
            score += lineScore;
            maxNumber = Math.max(maxNumber, findMaxInLine(result));
        }

        int[][] finalGrid = rotateBack(working, direction);

        if (moved) {
            addRandomNumber(finalGrid);
            maxNumber = Math.max(maxNumber, findMax(finalGrid));
        }

        boolean gameOver = !canMove(finalGrid);

        return new MoveResult(finalGrid, score, maxNumber, moved, gameOver);
    }

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

    private static int[] mergeLine(int[] line) {
        int[] filtered = filterZero(line);
        int[] merged = new int[GRID_SIZE];
        int index = 0;

        for (int i = 0; i < filtered.length; i++) {
            if (i + 1 < filtered.length && filtered[i] == filtered[i + 1]) {
                merged[index++] = filtered[i] * 2;
                i++;
            } else {
                merged[index++] = filtered[i];
            }
        }
        return merged;
    }

    private static int[] filterZero(int[] line) {
        int count = 0;
        for (int num : line) if (num != 0) count++;
        int[] result = new int[count];
        int index = 0;
        for (int num : line) if (num != 0) result[index++] = num;
        return result;
    }

    private static int calculateLineScore(int[] original, int[] merged) {
        int score = 0;
        for (int num : merged) {
            if (num > 4) score += num;
        }
        return score;
    }

    private static void addRandomNumber(int[][] grid) {
        int emptyCount = 0;
        for (int[] row : grid) for (int num : row) if (num == 0) emptyCount++;
        if (emptyCount == 0) return;

        int target = RANDOM.nextInt(emptyCount);
        int count = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == 0) {
                    if (count == target) {
                        grid[i][j] = RANDOM.nextDouble() < 0.9 ? 2 : 4;
                        return;
                    }
                    count++;
                }
            }
        }
    }

    private static boolean canMove(int[][] grid) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == 0) return true;
                if (i < GRID_SIZE - 1 && grid[i][j] == grid[i + 1][j]) return true;
                if (j < GRID_SIZE - 1 && grid[i][j] == grid[i][j + 1]) return true;
            }
        }
        return false;
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

    public static int[][] copy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }
}
