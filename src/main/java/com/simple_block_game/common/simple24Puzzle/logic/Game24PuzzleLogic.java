package com.simple_block_game.common.simple24Puzzle.logic;

import com.simple_block_game.common.simple24Puzzle.data.GameToken24Puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public final class Game24PuzzleLogic {

    private static final Random RANDOM = new Random();
    private static final double EPSILON = 0.0001;
    private static final double TARGET = 24.0;

    /** 生成一个确保有解的24点谜题（包含4个数字Token） */
    public static List<GameToken24Puzzle> generateValidPuzzle() {
        GameToken24Puzzle[] allNumbers = Arrays.stream(GameToken24Puzzle.values())
                .filter(GameToken24Puzzle::isNumber)
                .toArray(GameToken24Puzzle[]::new);
        while (true) {
            List<GameToken24Puzzle> puzzle = new ArrayList<>();
            int[] numbs = new int[4];
            for (int i = 0; i < 4; i++) {
                GameToken24Puzzle token = allNumbers[RANDOM.nextInt(allNumbers.length)];
                puzzle.add(token);
                numbs[i] = token.getValue();
            }
            if (hasSolution(numbs)) {
                return puzzle;
            }
        }
    }

    public static boolean verifyResult(List<GameToken24Puzzle> inputTokens, List<GameToken24Puzzle> puzzleTokens) {
        if (inputTokens == null || puzzleTokens == null || inputTokens.isEmpty() || !isValidExpressionSyntax(inputTokens)) return false;
        List<Integer> expected = puzzleTokens.stream().map(GameToken24Puzzle::getValue).sorted().toList();
        List<Integer> input = inputTokens.stream().filter(GameToken24Puzzle::isNumber).map(GameToken24Puzzle::getValue).sorted().toList();
        if (expected.size() != 4 || !expected.equals(input)) return false;
        try {
            return Math.abs(evaluateExpression(inputTokens) - TARGET) < EPSILON;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidExpressionSyntax(List<GameToken24Puzzle> tokens) {
        if (tokens == null || tokens.isEmpty()) return false;
        int bracketCount = 0;
        GameToken24Puzzle last = null;
        for (GameToken24Puzzle token : tokens) {
            if (token == GameToken24Puzzle.L_BRACKET) bracketCount++;
            else if (token == GameToken24Puzzle.R_BRACKET && bracketCount-- < 0) return false;
            if (last != null && isOperator(last) && isOperator(token)) return false;
            last = token;
        }
        return bracketCount == 0;
    }

    private static boolean isOperator(GameToken24Puzzle token) {
        return !token.isNumber() && token != GameToken24Puzzle.L_BRACKET && token != GameToken24Puzzle.R_BRACKET;
    }

    /**
     * 获取当前谜题的标准解法提示 (Hint)
     * 当玩家在MC中卡住，或者管理员想看答案时，可调用此方法返回一个公式字符串。
     */
    public static String getHint(List<GameToken24Puzzle> puzzleTokens) {
        if (puzzleTokens == null || puzzleTokens.size() != 4) return "";
        double[] arr = puzzleTokens.stream().mapToDouble(GameToken24Puzzle::getValue).toArray();
        String[] expr = puzzleTokens.stream().map(GameToken24Puzzle::getSerializedName).toArray(String[]::new);
        return solveAndGetExpr(arr, expr, 4);
    }

    private static String solveAndGetExpr(double[] arr, String[] expr, int n) {
        if (n == 1) {
            if (Math.abs(arr[0] - TARGET) < EPSILON) {
                return expr[0];
            }
            return "";
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double p = arr[i], q = arr[j];
                String ep = expr[i], eq = expr[j];
                arr[j] = arr[n - 1];
                expr[j] = expr[n - 1];
                arr[i] = p + q;
                expr[i] = "(" + ep + " + " + eq + ")";
                String res = solveAndGetExpr(arr, expr, n - 1);
                if (!res.isEmpty()) return res;
                arr[i] = p - q;
                expr[i] = "(" + ep + " − " + eq + ")";
                res = solveAndGetExpr(arr, expr, n - 1);
                if (!res.isEmpty()) return res;
                arr[i] = q - p;
                expr[i] = "(" + eq + " − " + ep + ")";
                res = solveAndGetExpr(arr, expr, n - 1);
                if (!res.isEmpty()) return res;
                arr[i] = p * q;
                expr[i] = ep + " × " + eq;
                res = solveAndGetExpr(arr, expr, n - 1);
                if (!res.isEmpty()) return res;
                if (Math.abs(q) > EPSILON) {
                    arr[i] = p / q;
                    expr[i] = ep + " ÷ " + eq;
                    res = solveAndGetExpr(arr, expr, n - 1);
                    if (!res.isEmpty()) return res;
                }
                if (Math.abs(p) > EPSILON) {
                    arr[i] = q / p;
                    expr[i] = eq + " ÷ " + ep;
                    res = solveAndGetExpr(arr, expr, n - 1);
                    if (!res.isEmpty()) return res;
                }
                arr[i] = p;
                arr[j] = q;
                expr[i] = ep;
                expr[j] = eq;
            }
        }
        return "";
    }

    /** 使用双栈（中缀转后缀）计算 Token 序列的值 */
    private static double evaluateExpression(List<GameToken24Puzzle> tokens) throws EmptyStackException, ArithmeticException {
        Stack<Double> values = new Stack<>();
        Stack<GameToken24Puzzle> ops = new Stack<>();
        for (GameToken24Puzzle token : tokens) {
            if (token.isNumber()) {
                values.push((double) token.getValue());
            } else if (token == GameToken24Puzzle.L_BRACKET) {
                ops.push(token);
            } else if (token == GameToken24Puzzle.R_BRACKET) {
                while (!ops.isEmpty() && ops.peek() != GameToken24Puzzle.L_BRACKET) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                if (!ops.isEmpty()) ops.pop();
            } else {
                while (!ops.isEmpty() && hasPrecedence(token, ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(token);
            }
        }
        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }
        if (values.size() != 1) {
            throw new IllegalArgumentException("Invalid expression structure");
        }
        return values.pop();
    }

    private static boolean hasPrecedence(GameToken24Puzzle op1, GameToken24Puzzle op2) {
        if (op2 == GameToken24Puzzle.L_BRACKET) return false;
        boolean op1High = op1 == GameToken24Puzzle.MUL || op1 == GameToken24Puzzle.DIV;
        boolean op2High = op2 == GameToken24Puzzle.MUL || op2 == GameToken24Puzzle.DIV;
        return op2High || !op1High;
    }

    private static double applyOp(GameToken24Puzzle op, double b, double a) {
        return switch (op) {
            case ADD -> a + b;
            case SUB -> a - b;
            case MUL -> a * b;
            case DIV -> {
                if (Math.abs(b) < 0.00001) throw new ArithmeticException("Division by zero");
                yield a / b;
            }
            default -> 0;
        };
    }

    /** 判断4个数字是否有解 */
    private static boolean hasSolution(int[] numbs) {
        double[] workSpace = new double[4];
        for (int i = 0; i < 4; i++) {
            workSpace[i] = numbs[i];
        }
        return dfsSolve(workSpace, 4);
    }

    private static boolean dfsSolve(double[] arr, int n) {
        if (n == 1) {
            return Math.abs(arr[0] - TARGET) < EPSILON;
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double p = arr[i];
                double q = arr[j];
                arr[j] = arr[n - 1];
                arr[i] = p + q;
                if (dfsSolve(arr, n - 1)) return true;
                arr[i] = p - q;
                if (dfsSolve(arr, n - 1)) return true;
                arr[i] = q - p;
                if (dfsSolve(arr, n - 1)) return true;
                arr[i] = p * q;
                if (dfsSolve(arr, n - 1)) return true;
                if (Math.abs(q) > EPSILON) {
                    arr[i] = p / q;
                    if (dfsSolve(arr, n - 1)) return true;
                }
                if (Math.abs(p) > EPSILON) {
                    arr[i] = q / p;
                    if (dfsSolve(arr, n - 1)) return true;
                }
                arr[i] = p;
                arr[j] = q;
            }
        }
        return false;
    }
}
