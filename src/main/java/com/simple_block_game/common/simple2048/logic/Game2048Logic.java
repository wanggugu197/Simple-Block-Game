package com.simple_block_game.common.simple2048.logic;

import com.simple_block_game.common.simple2048.data.Quadrant;
import lombok.Getter;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Game2048Logic {

    public static final int GRID_SIZE = 4;
    private static final double PROB_2 = 0.9D;
    private static final int NUM_2 = 2;
    private static final int NUM_4 = 4;
    private static final int EMPTY = 0;

    private static final Random RANDOM = ThreadLocalRandom.current();

    public record MoveResult(int[][] newGrid, @Getter int score, @Getter boolean gameOver, @Getter int maxNumber) {}

    /**
     * 处理单次移动
     */
    public static MoveResult processMove(int[][] originalGrid, Quadrant direction) {
        validateGrid(originalGrid);
        int[][] grid = deepCopyGrid(originalGrid);
        int score = 0;
        boolean gameOver = false;
        if (direction != Quadrant.NULL) {
            score = switch (direction) {
                case LEFT -> moveHorizontal(grid, false);
                case RIGHT -> moveHorizontal(grid, true);
                case UP -> moveVertical(grid, false);
                case DOWN -> moveVertical(grid, true);
                default -> 0;
            };
            boolean hasMoved = !isGridEqual(originalGrid, grid);
            if (hasMoved) generateRandomNum(grid);
            else gameOver = isGameOver(grid);
        }
        int maxNumber = getGridMaxNumber(grid);
        return new MoveResult(deepCopyGrid(grid), score, gameOver, maxNumber);
    }

    /**
     * 对比两个棋盘是否完全相同
     */
    private static boolean isGridEqual(int[][] grid1, int[][] grid2) {
        validateGrid(grid1);
        validateGrid(grid2);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid1[i][j] != grid2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 随机生成数字
     */
    public static void generateRandomNum(int[][] grid) {
        validateGrid(grid);
        int[] emptyCells = IntStream.range(0, GRID_SIZE * GRID_SIZE)
                .filter(i -> grid[i / GRID_SIZE][i % GRID_SIZE] == EMPTY)
                .toArray();
        if (emptyCells.length == 0) return;
        int targetIdx = emptyCells[RANDOM.nextInt(emptyCells.length)];
        int num = RANDOM.nextDouble() < PROB_2 ? NUM_2 : NUM_4;
        grid[targetIdx / GRID_SIZE][targetIdx % GRID_SIZE] = num;
    }

    /**
     * 初始化棋盘
     */
    public static int[][] initGrid() {
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        generateRandomNum(grid);
        generateRandomNum(grid);
        return grid;
    }

    /**
     * 获取棋盘当前的最大数字
     */
    public static int getGridMaxNumber(int[][] grid) {
        validateGrid(grid);
        return IntStream.range(0, GRID_SIZE)
                .map(row -> java.util.Arrays.stream(grid[row], 0, GRID_SIZE)
                        .max().orElse(0))
                .max().orElse(0);
    }

    /**
     * 失败判定
     */
    public static boolean isGameOver(int[][] grid) {
        validateGrid(grid);
        if (IntStream.range(0, GRID_SIZE).anyMatch(row -> IntStream.range(0, GRID_SIZE)
                .anyMatch(col -> grid[row][col] == EMPTY))) {
            return false;
        }
        boolean hasHorizontalMerge = IntStream.range(0, GRID_SIZE)
                .anyMatch(row -> IntStream.range(0, GRID_SIZE - 1)
                        .anyMatch(col -> grid[row][col] == grid[row][col + 1]));
        boolean hasVerticalMerge = IntStream.range(0, GRID_SIZE)
                .anyMatch(col -> IntStream.range(0, GRID_SIZE - 1)
                        .anyMatch(row -> grid[row][col] == grid[row + 1][col]));
        return !hasHorizontalMerge && !hasVerticalMerge;
    }

    /**
     * 处理水平移动（左/右）
     */
    private static int moveHorizontal(int[][] grid, boolean isRight) {
        int score = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            int[] rowArr = grid[row];
            if (isRight) reverse(rowArr);
            slide(rowArr);
            score += merge(rowArr);
            slide(rowArr);
            if (isRight) reverse(rowArr);
        }
        return score;
    }

    /**
     * 处理垂直移动（上/下）
     */
    private static int moveVertical(int[][] grid, boolean isDown) {
        transpose(grid);
        int score = moveHorizontal(grid, isDown);
        transpose(grid);
        return score;
    }

    /**
     * 单行左滑
     */
    private static void slide(int[] row) {
        int idx = 0;
        for (int num : row) if (num != EMPTY) row[idx++] = num;
        while (idx < GRID_SIZE) row[idx++] = EMPTY;
    }

    /**
     * 单行合并
     */
    private static int merge(int[] row) {
        int score = 0;
        for (int i = 0; i < GRID_SIZE - 1; i++) {
            if (row[i] != EMPTY && row[i] == row[i + 1]) {
                row[i] *= 2;
                score += row[i];
                row[i + 1] = EMPTY;
                i++;
            }
        }
        return score;
    }

    /**
     * 反转数组
     */
    private static void reverse(int[] row) {
        for (int i = 0; i < GRID_SIZE / 2; i++) {
            int temp = row[i];
            row[i] = row[GRID_SIZE - 1 - i];
            row[GRID_SIZE - 1 - i] = temp;
        }
    }

    /**
     * 矩阵转置
     */
    private static void transpose(int[][] grid) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = i + 1; j < GRID_SIZE; j++) {
                int temp = grid[i][j];
                grid[i][j] = grid[j][i];
                grid[j][i] = temp;
            }
        }
    }

    /**
     * 深拷贝数组
     */
    private static int[][] deepCopyGrid(int[][] original) {
        int[][] copy = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, GRID_SIZE);
        }
        return copy;
    }

    /**
     * 校验数组
     */
    private static void validateGrid(int[][] grid) {
        if (grid == null || grid.length != GRID_SIZE) {
            throw new IllegalArgumentException("棋盘数组必须是" + GRID_SIZE + "×" + GRID_SIZE);
        }
        for (int[] row : grid) {
            if (row == null || row.length != GRID_SIZE) {
                throw new IllegalArgumentException("棋盘每行必须有" + GRID_SIZE + "个元素");
            }
        }
    }
}
