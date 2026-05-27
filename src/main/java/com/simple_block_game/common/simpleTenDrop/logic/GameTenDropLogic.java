package com.simple_block_game.common.simpleTenDrop.logic;

import com.simple_block_game.common.simpleTenDrop.data.Direction;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class GameTenDropLogic {

    public static final int GRID_SIZE = 6;
    public static final int INITIAL_WATER_DROPS = 10;
    public static final int LEVEL_CLEAR_REWARD = 15;

    private static final int CRITICAL_LEVEL = 4;
    private static final int BURST_THRESHOLD = 5;

    private static final Random RANDOM = ThreadLocalRandom.current();

    public record GameResult(
                             int[][] newGrid,
                             int waterDrops,
                             int comboCount,
                             boolean gameActive,
                             boolean victory,
                             boolean gameOver) {}

    public record FlyingDroplet(int x, int y, Direction direction) {

        public FlyingDroplet move() {
            return new FlyingDroplet(x + direction.getDx(), y + direction.getDy(), direction);
        }
    }

    private record LevelConfig(
                               double coverageRate,
                               double[] levelWeights,
                               int rewardThreshold,
                               int kValue,
                               boolean clusterHighLevels) {}

    private GameTenDropLogic() {}

    /**
     * 根据当前关卡获取对应的关卡配置
     * 
     * @param level 当前关卡等级
     * @return 包含覆盖率、等级权重、奖励阈值等配置的 LevelConfig 对象
     */
    private static LevelConfig getLevelConfig(int level) {
        if (level <= 3) {
            return new LevelConfig(
                    0.50,
                    new double[] { 0.15, 0.25, 0.35, 0.25 },
                    3,
                    3,
                    true);
        } else if (level <= 7) {
            return new LevelConfig(
                    0.80,
                    new double[] { 0.25, 0.40, 0.25, 0.10 },
                    4,
                    4,
                    true);
        } else {
            return new LevelConfig(
                    0.40,
                    new double[] { 0.40, 0.40, 0.15, 0.05 },
                    6,
                    5,
                    false);
        }
    }

    /**
     * 获取指定关卡的初始水滴数量
     * 
     * @param level 关卡等级
     * @return 初始水滴数量，随关卡等级增加而增加
     */
    public static int getInitialWaterDrops(int level) {
        return INITIAL_WATER_DROPS + level * 2;
    }

    /**
     * 初始化游戏网格（默认第一关）
     * 
     * @return 初始化后的 6x6 网格
     */
    public static int[][] initGrid() {
        return generateLevelGrid(1);
    }

    /**
     * 根据关卡等级生成游戏网格
     * 网格会根据配置进行中心聚集或边缘分散布局，并确保有潜在的连锁反应可能
     * 
     * @param level 关卡等级
     * @return 生成的 6x6 游戏网格
     */
    public static int[][] generateLevelGrid(int level) {
        LevelConfig config = getLevelConfig(level);
        int[][] grid;

        do {
            grid = new int[GRID_SIZE][GRID_SIZE];
            int totalCells = GRID_SIZE * GRID_SIZE;
            int targetDroplets = (int) (totalCells * config.coverageRate());

            Set<Integer> filledCells = new HashSet<>();
            while (filledCells.size() < targetDroplets) {
                int idx;
                if (config.clusterHighLevels()) {
                    idx = getCentralBiasedIndex();
                } else {
                    idx = getEdgeBiasedIndex();
                }
                if (filledCells.add(idx)) {
                    int row = idx / GRID_SIZE;
                    int col = idx % GRID_SIZE;
                    grid[row][col] = generateWeightedLevel(config.levelWeights());
                }
            }
        } while (!hasPotentialChainReaction(grid));

        return grid;
    }

    /**
     * 获取中心偏向的单元格索引（70%概率返回中心区域）
     * 
     * @return 网格中的单元格索引
     */
    private static int getCentralBiasedIndex() {
        if (RANDOM.nextDouble() < 0.7) {
            int row = RANDOM.nextInt(2) + 1;
            int col = RANDOM.nextInt(2) + 1;
            return row * GRID_SIZE + col;
        }
        return RANDOM.nextInt(GRID_SIZE * GRID_SIZE);
    }

    /**
     * 获取边缘偏向的单元格索引（60%概率返回边缘区域）
     * 
     * @return 网格中的单元格索引
     */
    private static int getEdgeBiasedIndex() {
        if (RANDOM.nextDouble() < 0.6) {
            int edgeType = RANDOM.nextInt(4);
            return switch (edgeType) {
                case 0 -> RANDOM.nextInt(GRID_SIZE);
                case 1 -> (GRID_SIZE - 1) * GRID_SIZE + RANDOM.nextInt(GRID_SIZE);
                case 2 -> RANDOM.nextInt(GRID_SIZE) * GRID_SIZE;
                case 3 -> RANDOM.nextInt(GRID_SIZE) * GRID_SIZE + (GRID_SIZE - 1);
                default -> RANDOM.nextInt(GRID_SIZE * GRID_SIZE);
            };
        }
        return RANDOM.nextInt(GRID_SIZE * GRID_SIZE);
    }

    /**
     * 根据权重数组随机生成水滴等级（1-4级）
     * 
     * @param weights 各等级的权重比例数组
     * @return 生成的水滴等级（1-4）
     */
    private static int generateWeightedLevel(double[] weights) {
        double rand = RANDOM.nextDouble();
        double cumulative = 0;
        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (rand <= cumulative) {
                return i + 1;
            }
        }
        return 1;
    }

    /**
     * 检查网格是否有潜在的连锁反应可能
     * 条件：存在达到临界值的水滴，或相邻两个水滴之和达到临界值
     * 
     * @param grid 游戏网格
     * @return 是否存在潜在连锁反应
     */
    private static boolean hasPotentialChainReaction(int[][] grid) {
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (grid[y][x] >= CRITICAL_LEVEL) {
                    return true;
                }

                for (Direction dir : Direction.allDirections()) {
                    int nx = x + dir.getDx();
                    int ny = y + dir.getDy();
                    if (isInBounds(nx, ny) && grid[y][x] + grid[ny][nx] >= CRITICAL_LEVEL) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 处理玩家点击操作，执行水滴增加和连锁爆炸逻辑
     * 
     * @param originalGrid 原始游戏网格
     * @param waterDrops   当前剩余水滴数量
     * @param x            点击的列坐标
     * @param y            点击的行坐标
     * @param currentLevel 当前关卡等级
     * @return 包含新网格、剩余水滴、连击数、游戏状态的 GameResult
     */
    public static GameResult processClick(int[][] originalGrid, int waterDrops, int x, int y, int currentLevel) {
        if (waterDrops <= 0) {
            return new GameResult(originalGrid, waterDrops, 0, false, false, isGameOver(originalGrid, waterDrops));
        }

        int[][] grid = copyGrid(originalGrid);

        if (grid[y][x] == 0) {
            return new GameResult(grid, waterDrops, 0, true, false, false);
        }

        grid[y][x]++;
        int newWaterDrops = waterDrops - 1;
        int comboCount = 0;
        int eliminatedCount = 0;

        Queue<int[]> burstQueue = new LinkedList<>();
        if (grid[y][x] >= BURST_THRESHOLD) {
            burstQueue.add(new int[] { x, y });
            grid[y][x] = 0;
            eliminatedCount++;
        }

        List<FlyingDroplet> flyingDroplets = new ArrayList<>();

        while (!burstQueue.isEmpty() || !flyingDroplets.isEmpty()) {
            while (!burstQueue.isEmpty()) {
                int[] pos = burstQueue.poll();
                int bx = pos[0];
                int by = pos[1];

                for (Direction dir : Direction.allDirections()) {
                    flyingDroplets.add(new FlyingDroplet(bx, by, dir));
                }
            }

            List<FlyingDroplet> newFlying = new ArrayList<>();
            for (FlyingDroplet fd : flyingDroplets) {
                FlyingDroplet moved = fd.move();

                if (isInBounds(moved.x(), moved.y())) {
                    if (grid[moved.y()][moved.x()] > 0) {
                        grid[moved.y()][moved.x()]++;

                        if (grid[moved.y()][moved.x()] >= BURST_THRESHOLD) {
                            burstQueue.add(new int[] { moved.x(), moved.y() });
                            grid[moved.y()][moved.x()] = 0;
                            eliminatedCount++;
                            comboCount++;
                        }
                    } else {
                        newFlying.add(moved);
                    }
                }
            }
            flyingDroplets = newFlying;
        }

        if (eliminatedCount > 0) {
            LevelConfig config = getLevelConfig(currentLevel);
            int reward = calculateReward(eliminatedCount, comboCount, config.rewardThreshold(), config.kValue());
            newWaterDrops += reward;
        }

        boolean victory = isVictory(grid);
        boolean gameOver = isGameOver(grid, newWaterDrops);
        boolean gameActive = !victory && !gameOver;

        return new GameResult(grid, newWaterDrops, comboCount, gameActive, victory, gameOver);
    }

    /**
     * 处理玩家点击操作（默认关卡等级为1）
     * 
     * @param originalGrid 原始游戏网格
     * @param waterDrops   当前剩余水滴数量
     * @param x            点击的列坐标
     * @param y            点击的行坐标
     * @return 包含新网格、剩余水滴、连击数、游戏状态的 GameResult
     */
    public static GameResult processClick(int[][] originalGrid, int waterDrops, int x, int y) {
        return processClick(originalGrid, waterDrops, x, y, 1);
    }

    /**
     * 根据消除数量和连击数计算奖励水滴
     * 
     * @param eliminatedCount 消除的水滴数量
     * @param comboCount      连击次数
     * @param rewardThreshold 奖励阈值
     * @param kValue          消除数量奖励系数
     * @return 奖励的水滴数量
     */
    private static int calculateReward(int eliminatedCount, int comboCount, int rewardThreshold, int kValue) {
        int reward = 0;

        if (comboCount >= rewardThreshold) {
            reward += comboCount - rewardThreshold + 1;
        }

        reward += eliminatedCount / kValue;

        return reward;
    }

    /**
     * 计算网格中所有水滴的总潜力值（即触发爆炸所需的最小点击次数）
     * 
     * @param grid 游戏网格
     * @return 总潜力值
     */
    public static int calculateTotalPotential(int[][] grid) {
        int total = 0;
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell > 0) {
                    total += CRITICAL_LEVEL - cell + 1;
                }
            }
        }
        return total;
    }

    /**
     * 判断是否胜利（网格中所有水滴都被消除）
     * 
     * @param grid 游戏网格
     * @return 是否胜利
     */
    public static boolean isVictory(int[][] grid) {
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断游戏是否结束（水滴用尽且仍有剩余水滴）
     * 
     * @param grid       游戏网格
     * @param waterDrops 当前剩余水滴数量
     * @return 是否游戏结束
     */
    public static boolean isGameOver(int[][] grid, int waterDrops) {
        if (waterDrops > 0) {
            return false;
        }

        for (int[] row : grid) {
            for (int cell : row) {
                if (cell > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取网格中剩余的水滴数量（非零单元格数）
     * 
     * @param grid 游戏网格
     * @return 剩余水滴数量
     */
    public static int getRemainingDroplets(int[][] grid) {
        int count = 0;
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell > 0) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 深拷贝游戏网格
     * 
     * @param original 原始网格
     * @return 网格的深拷贝
     */
    public static int[][] copyGrid(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }

    /**
     * 检查坐标是否在网格边界内
     * 
     * @param x 列坐标
     * @param y 行坐标
     * @return 是否在边界内
     */
    private static boolean isInBounds(int x, int y) {
        return x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE;
    }

    /**
     * 生成下一关的游戏网格
     * 
     * @param currentLevel 当前关卡等级
     * @return 下一关的游戏网格
     */
    public static int[][] generateNextLevelGrid(int currentLevel) {
        return generateLevelGrid(currentLevel);
    }

    /**
     * 获取关卡通关奖励的水滴数量
     * 
     * @return 通关奖励水滴数
     */
    public static int getLevelClearReward() {
        return LEVEL_CLEAR_REWARD;
    }
}
