package com.simple_block_game.common.simpleMemoryKey.logic;

import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyLevel;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class GameMemoryKeyLogic {

    public static final int MAX_SEQUENCE_LENGTH = 27;
    public static final int BUTTON_COUNT = MemoryKeyPosition.getTotalPositions();

    private static final Random RANDOM = ThreadLocalRandom.current();

    private GameMemoryKeyLogic() {}

    public static List<Integer> generateFullSequence() {
        int minLength = Math.min(9, MAX_SEQUENCE_LENGTH);
        int length = RANDOM.nextInt(MAX_SEQUENCE_LENGTH - minLength + 1) + minLength;

        List<Integer> sequence = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            sequence.add(RANDOM.nextInt(BUTTON_COUNT));
        }

        return Collections.unmodifiableList(sequence);
    }

    public static boolean isAllLevelsComplete(MemoryKeyLevel currentLevel) {
        return currentLevel.isLastLevel();
    }

    public static int getSequenceLengthForLevel(MemoryKeyLevel level) {
        return level.getSequenceLength();
    }
}
