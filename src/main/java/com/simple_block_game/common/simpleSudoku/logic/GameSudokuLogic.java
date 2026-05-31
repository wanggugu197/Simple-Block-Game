package com.simple_block_game.common.simpleSudoku.logic;

import com.simple_block_game.common.simpleSudoku.data.Difficulty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class GameSudokuLogic {

    public static final int SIZE = 9;
    public static final int BOX_SIZE = 3;
    public static final int TOTAL_CELLS = SIZE * SIZE;

    public record PuzzleResult(int[][] puzzle, int[][] solution) {}

    private GameSudokuLogic() {}

    private static final ThreadLocal<Random> RANDOM = ThreadLocal.withInitial(Random::new);

    public static PuzzleResult generatePuzzle(Difficulty difficulty, boolean diagonalMode) {
        int[][] solution = new int[SIZE][SIZE];
        fillBoardRecursive(solution, diagonalMode, RANDOM.get());
        int[][] puzzle = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(solution[i], 0, puzzle[i], 0, SIZE);
        }
        createPuzzle(puzzle, difficulty, diagonalMode);
        return new PuzzleResult(puzzle, solution);
    }

    public static boolean isValid(int[][] board, int row, int col, int num, boolean diagonalMode) {
        if (num < 1 || num > SIZE) return false;
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num) return false;
            if (board[i][col] == num) return false;
        }
        int boxRowStart = (row / BOX_SIZE) * BOX_SIZE;
        int boxColStart = (col / BOX_SIZE) * BOX_SIZE;
        for (int r = boxRowStart; r < boxRowStart + BOX_SIZE; r++) {
            for (int c = boxColStart; c < boxColStart + BOX_SIZE; c++) {
                if (board[r][c] == num) return false;
            }
        }
        if (diagonalMode) {
            if (row == col) {
                for (int i = 0; i < SIZE; i++) {
                    if (i != row && board[i][i] == num) return false;
                }
            }
            if (row + col == SIZE - 1) {
                for (int i = 0; i < SIZE; i++) {
                    if (i != row && board[i][SIZE - 1 - i] == num) return false;
                }
            }
        }
        return true;
    }

    public static boolean isBoardValidComplete(int[][] board, boolean diagonalMode) {
        boolean[][] rowSeen = new boolean[SIZE][SIZE + 1];
        boolean[][] colSeen = new boolean[SIZE][SIZE + 1];
        boolean[][] boxSeen = new boolean[SIZE][SIZE + 1];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int val = board[row][col];
                if (val < 1 || val > SIZE) return false;
                if (rowSeen[row][val]) return false;
                rowSeen[row][val] = true;
                if (colSeen[col][val]) return false;
                colSeen[col][val] = true;
                int boxIdx = (row / BOX_SIZE) * BOX_SIZE + (col / BOX_SIZE);
                if (boxSeen[boxIdx][val]) return false;
                boxSeen[boxIdx][val] = true;
            }
        }
        if (diagonalMode) {
            boolean[] diag1Seen = new boolean[SIZE + 1];
            boolean[] diag2Seen = new boolean[SIZE + 1];
            for (int i = 0; i < SIZE; i++) {
                int val1 = board[i][i];
                if (diag1Seen[val1]) return false;
                diag1Seen[val1] = true;
                int val2 = board[i][SIZE - 1 - i];
                if (diag2Seen[val2]) return false;
                diag2Seen[val2] = true;
            }
        }
        return true;
    }

    private static boolean fillBoardRecursive(int[][] board, boolean diagonalMode, Random random) {
        int[] numbers = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    for (int i = numbers.length - 1; i > 0; i--) {
                        int j = random.nextInt(i + 1);
                        int temp = numbers[i];
                        numbers[i] = numbers[j];
                        numbers[j] = temp;
                    }
                    for (int num : numbers) {
                        if (isValid(board, row, col, num, diagonalMode)) {
                            board[row][col] = num;
                            if (fillBoardRecursive(board, diagonalMode, random)) {
                                return true;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static void createPuzzle(int[][] board, Difficulty difficulty, boolean diagonalMode) {
        Random random = RANDOM.get();
        int targetEmpty = TOTAL_CELLS - difficulty.getTargetHints();
        List<Integer> positions = new ArrayList<>(TOTAL_CELLS);
        for (int i = 0; i < TOTAL_CELLS; i++) {
            positions.add(i);
        }
        Collections.shuffle(positions, random);
        int emptied = 0;
        for (int pos : positions) {
            if (emptied >= targetEmpty) break;
            int row = pos / SIZE;
            int col = pos % SIZE;
            int backup = board[row][col];
            board[row][col] = 0;
            if (countSolutions(board, diagonalMode, 0, 0, 0) != 1) {
                board[row][col] = backup;
            } else {
                emptied++;
            }
        }
    }

    private static int countSolutions(int[][] board, boolean diagonalMode, int row, int col, int count) {
        if (row == SIZE) return count + 1;
        if (col == SIZE) return countSolutions(board, diagonalMode, row + 1, 0, count);
        if (board[row][col] != 0) return countSolutions(board, diagonalMode, row, col + 1, count);
        if (count >= 2) return count;
        for (int num = 1; num <= SIZE; num++) {
            if (isValid(board, row, col, num, diagonalMode)) {
                board[row][col] = num;
                count = countSolutions(board, diagonalMode, row, col + 1, count);
                board[row][col] = 0;
                if (count >= 2) return count;
            }
        }
        return count;
    }
}
