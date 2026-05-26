package com.simple_block_game.common.simpleMemoryKey.logic;

import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyLevel;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/** 记忆键游戏核心逻辑 */
public final class GameMemoryKeyLogic {

    public static final int MAX_SEQUENCE_LENGTH = 27;
    public static final int BUTTON_COUNT = MemoryKeyPosition.getTotalPositions();

    private static final Random RANDOM = ThreadLocalRandom.current();

    public record GameResult(boolean success, boolean gameOver, boolean levelComplete, String message) {}

    public static List<Integer> generateFullSequence() {
        int length = RANDOM.nextInt(MAX_SEQUENCE_LENGTH - 18 + 1) + 18;
        List<Integer> sequence = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            sequence.add(RANDOM.nextInt(BUTTON_COUNT));
        }

        return Collections.unmodifiableList(sequence);
    }

    public static List<Integer> getLevelSequence(List<Integer> fullSequence, MemoryKeyLevel level) {
        if (!isValidSequence(fullSequence)) {
            throw new IllegalArgumentException("完整序列不能为空");
        }

        int length = level.getSequenceLength();

        if (level.isLastLevel() || length >= fullSequence.size()) {
            return new ArrayList<>(fullSequence);
        }

        return new ArrayList<>(fullSequence.subList(0, length));
    }

    public static GameResult validateInput(
                                           List<Integer> fullSequence,
                                           MemoryKeyLevel currentLevel,
                                           List<Integer> currentSequence,
                                           int inputButtonId) {
        if (!isValidSequence(fullSequence)) {
            return new GameResult(false, true, false, "序列未初始化");
        }

        int expectedLength = currentLevel.getSequenceLength();
        int currentIndex = currentSequence.size();

        if (currentIndex >= expectedLength) {
            return new GameResult(false, false, false, "序列已完成");
        }

        boolean inputCorrect = inputButtonId == fullSequence.get(currentIndex);

        if (inputCorrect) {
            boolean levelComplete = (currentIndex + 1 == expectedLength);
            return new GameResult(true, false, levelComplete,
                    levelComplete ? "关卡完成" : "输入正确");
        }

        return new GameResult(false, false, false, "输入错误");
    }

    public static boolean isGameOver(int remainingLives) {
        return remainingLives <= 0;
    }

    public static boolean isAllLevelsComplete(MemoryKeyLevel currentLevel) {
        return currentLevel.isLastLevel();
    }

    public static MemoryKeyLevel getNextLevel(MemoryKeyLevel currentLevel) {
        return currentLevel.next();
    }

    public static int getSequenceLengthForLevel(MemoryKeyLevel level) {
        return level.getSequenceLength();
    }

    public static int getFlashDurationForLevel(MemoryKeyLevel level) {
        return level.getFlashDurationMs();
    }

    public static boolean isValidSequence(List<Integer> sequence) {
        if (sequence == null || sequence.isEmpty()) {
            return false;
        }

        for (Integer buttonId : sequence) {
            if (buttonId < 0 || buttonId >= BUTTON_COUNT) {
                return false;
            }
        }

        return true;
    }

    public static double getProgress(MemoryKeyLevel currentLevel) {
        return (double) currentLevel.getLevelNumber() / MemoryKeyLevel.getTotalLevels();
    }

    public static GameResult handlePlayerInput(
                                               List<Integer> fullSequence,
                                               MemoryKeyLevel currentLevel,
                                               List<Integer> currentSequence,
                                               int remainingLives,
                                               int inputButtonId) {
        if (currentSequence == null) {
            currentSequence = new ArrayList<>();
        }

        GameResult result = validateInput(fullSequence, currentLevel, currentSequence, inputButtonId);

        if (!result.success()) {
            int newLives = remainingLives - 1;
            boolean gameOver = isGameOver(newLives);

            return new GameResult(false, gameOver, false,
                    gameOver ? "游戏结束" : "输入错误，剩余" + newLives + "次机会");
        }

        if (result.levelComplete()) {
            if (isAllLevelsComplete(currentLevel)) {
                return new GameResult(true, false, true, "恭喜通关！");
            }
            return new GameResult(true, false, true, "关卡完成，进入下一关");
        }

        return new GameResult(true, false, false, "输入正确");
    }
}
