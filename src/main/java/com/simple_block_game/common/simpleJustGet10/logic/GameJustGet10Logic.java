package com.simple_block_game.common.simpleJustGet10.logic;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.simpleJustGet10.data.ValueJustGet10;

import java.util.*;

/**
 * Just Get 10 游戏核心逻辑类
 */
public final class GameJustGet10Logic {

    public static final int SIZE = 5;
    private static final Random RANDOM = new Random();
    private static final int[] DR = { -1, 1, 0, 0 };
    private static final int[] DC = { 0, 0, -1, 1 };

    private GameJustGet10Logic() {}

    public record Point(int row, int col) {}

    public record MergeResult(int scoreGained, boolean success) {}

    public static ValueJustGet10[][] createNewBoard() {
        ValueJustGet10[][] board = new ValueJustGet10[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                board[r][c] = generateRandomValue(0);
            }
        }
        return board;
    }

    public static List<Point> getHighlightPositions(ValueJustGet10[][] board, int clickRow, int clickCol) {
        List<Point> component = findConnectedComponent(board, clickRow, clickCol);
        return component.size() >= 2 ? component : List.of();
    }

    public static MergeResult processMergeAndGravity(ValueJustGet10[][] board, int targetRow, int targetCol, List<Point> currentHighlight, int currentScore) {
        if (currentHighlight == null || currentHighlight.size() < 2) {
            return new MergeResult(0, false);
        }
        Point targetPoint = new Point(targetRow, targetCol);
        if (!currentHighlight.contains(targetPoint)) {
            return new MergeResult(0, false);
        }
        ValueJustGet10 originalValue = board[targetRow][targetCol];
        if (originalValue == null) {
            return new MergeResult(0, false);
        }
        for (Point p : currentHighlight) {
            board[p.row()][p.col()] = null;
        }
        ValueJustGet10 nextValue = originalValue.getNext().orElse(originalValue);
        board[targetRow][targetCol] = nextValue;
        int earnedScore = nextValue.getValue() * currentHighlight.size();
        applyGravityAndRefill(board, currentScore + earnedScore);
        return new MergeResult(earnedScore, true);
    }

    public static boolean checkGameOver(ValueJustGet10[][] board) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] != null && findConnectedComponent(board, r, c).size() >= 2) {
                    return false;
                }
            }
        }
        return true;
    }

    private static List<Point> findConnectedComponent(ValueJustGet10[][] board, int startRow, int startCol) {
        List<Point> component = new ArrayList<>(SIZE * SIZE);
        if (startRow < 0 || startRow >= SIZE || startCol < 0 || startCol >= SIZE) {
            return component;
        }
        ValueJustGet10 targetValue = board[startRow][startCol];
        if (targetValue == null) {
            return component;
        }
        boolean[][] visited = new boolean[SIZE][SIZE];
        Deque<Point> queue = new ArrayDeque<>(SIZE * SIZE);
        Point start = new Point(startRow, startCol);
        queue.add(start);
        visited[startRow][startCol] = true;
        while (!queue.isEmpty()) {
            Point current = queue.poll();
            component.add(current);
            for (int i = 0; i < 4; i++) {
                int nr = current.row() + DR[i];
                int nc = current.col() + DC[i];
                if (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE && !visited[nr][nc] && board[nr][nc] == targetValue) {
                    visited[nr][nc] = true;
                    queue.add(new Point(nr, nc));
                }
            }
        }
        return component;
    }

    private static void applyGravityAndRefill(ValueJustGet10[][] board, int score) {
        for (int col = 0; col < SIZE; col++) {
            int writePos = 0;
            for (int row = 0; row < SIZE; row++) {
                if (board[row][col] != null) {
                    if (writePos != row) {
                        board[writePos][col] = board[row][col];
                        board[row][col] = null;
                    }
                    writePos++;
                }
            }
            for (; writePos < SIZE; writePos++) {
                board[writePos][col] = generateRandomValue(score);
            }
        }
    }

    private static ValueJustGet10 generateRandomValue(int score) {
        double t = calculateDifficultyFactor(score);
        double p2 = lerp(0.32, t);
        double p3 = lerp(0.30, t);
        double p4 = lerp(0.05, t);
        double chance = RANDOM.nextDouble();
        if (chance < p4) return ValueJustGet10.NUM_4;
        if (chance < p4 + p3) return ValueJustGet10.NUM_3;
        if (chance < p4 + p3 + p2) return ValueJustGet10.NUM_2;
        return ValueJustGet10.NUM_1;
    }

    private static double calculateDifficultyFactor(int score) {
        var config = SimpleBlockGameConfig.justGet10RewardConfig;
        int start = config.difficultyStart.get();
        int max = config.difficultyMax.get();
        if (score < start) return 0.0;
        if (score >= max) return 1.0;
        return (score - start) / (double) (max - start);
    }

    private static double lerp(double start, double t) {
        return start + (0.25 - start) * t;
    }
}
