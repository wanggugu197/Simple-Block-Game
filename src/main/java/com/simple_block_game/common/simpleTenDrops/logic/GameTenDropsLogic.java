package com.simple_block_game.common.simpleTenDrops.logic;

import com.simple_block_game.common.simpleTenDrops.data.TenDropsDirection;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class GameTenDropsLogic {

    public static final int GRID_SIZE = 6;
    public static final int INITIAL_WATER_DROPS = 10;
    private static final int CRITICAL_LEVEL = 4;
    private static final Random RANDOM = ThreadLocalRandom.current();

    @Getter
    @AllArgsConstructor
    private static class LevelConfig {

        double coverageRate;
        double[] levelWeights;
        int rewardThreshold;
        int kValue;
        boolean clusterHighLevels;
    }

    private GameTenDropsLogic() {}

    private static LevelConfig getLevelConfig(int level) {
        return switch (level) {
            case 1, 2 -> new LevelConfig(0.50, new double[] { 0.15, 0.25, 0.35, 0.25 }, 3, 3, true);
            case 3, 4 -> new LevelConfig(0.80, new double[] { 0.25, 0.40, 0.25, 0.10 }, 4, 4, true);
            case 5, 6 -> new LevelConfig(0.60, new double[] { 0.30, 0.45, 0.20, 0.05 }, 5, 4, true);
            case 7, 8 -> new LevelConfig(0.40, new double[] { 0.40, 0.40, 0.15, 0.05 }, 6, 5, false);
            default -> new LevelConfig(0.45, new double[] { 0.50, 0.38, 0.10, 0.02 }, 6, 6, false);
        };
    }

    public static int[][] initGrid() {
        return generateLevelGrid(1);
    }

    public static int[][] generateLevelGrid(int level) {
        LevelConfig config = getLevelConfig(level);
        int[][] grid;

        do {
            grid = new int[GRID_SIZE][GRID_SIZE];
            fillGridWithDroplets(grid, config);
        } while (!hasPotentialChainReaction(grid));

        return grid;
    }

    private static void fillGridWithDroplets(int[][] grid, LevelConfig config) {
        int totalCells = GRID_SIZE * GRID_SIZE;
        int targetDroplets = (int) (totalCells * config.coverageRate);
        BitSet filledCells = new BitSet(totalCells);

        while (filledCells.cardinality() < targetDroplets) {
            int idx = config.clusterHighLevels ? getCentralBiasedIndex() : getEdgeBiasedIndex();
            if (filledCells.get(idx)) continue;

            filledCells.set(idx);
            int row = idx / GRID_SIZE;
            int col = idx % GRID_SIZE;
            grid[row][col] = generateWeightedLevel(config.levelWeights);
        }
    }

    private static int getCentralBiasedIndex() {
        return RANDOM.nextDouble() < 0.7 ? (RANDOM.nextInt(2) + 1) * GRID_SIZE + (RANDOM.nextInt(2) + 1) : RANDOM.nextInt(GRID_SIZE * GRID_SIZE);
    }

    private static int getEdgeBiasedIndex() {
        if (RANDOM.nextDouble() >= 0.6) return RANDOM.nextInt(GRID_SIZE * GRID_SIZE);

        return switch (RANDOM.nextInt(4)) {
            case 0 -> RANDOM.nextInt(GRID_SIZE);
            case 1 -> (GRID_SIZE - 1) * GRID_SIZE + RANDOM.nextInt(GRID_SIZE);
            case 2 -> RANDOM.nextInt(GRID_SIZE) * GRID_SIZE;
            case 3 -> RANDOM.nextInt(GRID_SIZE) * GRID_SIZE + (GRID_SIZE - 1);
            default -> RANDOM.nextInt(GRID_SIZE * GRID_SIZE);
        };
    }

    private static int generateWeightedLevel(double[] weights) {
        double rand = RANDOM.nextDouble(), cumulative = 0;
        for (int i = 0; i < weights.length; cumulative += weights[i], i++) {
            if (rand <= cumulative) return i + 1;
        }
        return 1;
    }

    public static boolean hasPotentialChainReaction(int[][] grid) {
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (grid[y][x] >= CRITICAL_LEVEL) return true;

                for (TenDropsDirection dir : TenDropsDirection.allDirections()) {
                    int nx = x + dir.getDx(), ny = y + dir.getDy();
                    if (isInBounds(nx, ny) && grid[y][x] + grid[ny][nx] >= CRITICAL_LEVEL) return true;
                }
            }
        }
        return false;
    }

    public static boolean isVictory(int[][] grid) {
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell > 0) return false;
            }
        }
        return true;
    }

    private static boolean isInBounds(int x, int y) {
        return x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE;
    }
}
