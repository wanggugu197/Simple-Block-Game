package com.simple_block_game.common.simpleMinesweeper.data;

import net.minecraft.util.StringRepresentable;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

/** 扫雷方块状态枚举 */
public enum MinesweeperState implements StringRepresentable {

    UNOPENED(0, "unopened"),
    FLAGGED(-1, "flagged"),
    OPEN_EMPTY(-5, "open_empty"),
    BOMB(-9, "bomb"),
    DEATH_BOMB(-10, "death_bomb"),

    NUMBER_1(1, "number_1"),
    NUMBER_2(2, "number_2"),
    NUMBER_3(3, "number_3"),
    NUMBER_4(4, "number_4"),
    NUMBER_5(5, "number_5"),
    NUMBER_6(6, "number_6"),
    NUMBER_7(7, "number_7"),
    NUMBER_8(8, "number_8"),

    WRONG_FLAG_1(-11, "wrong_flag_1"),
    WRONG_FLAG_2(-12, "wrong_flag_2"),
    WRONG_FLAG_3(-13, "wrong_flag_3"),
    WRONG_FLAG_4(-14, "wrong_flag_4"),
    WRONG_FLAG_5(-15, "wrong_flag_5"),
    WRONG_FLAG_6(-16, "wrong_flag_6"),
    WRONG_FLAG_7(-17, "wrong_flag_7"),
    WRONG_FLAG_8(-18, "wrong_flag_8");

    private static final MinesweeperState[] VALUES = values();

    private static final Map<String, MinesweeperState> NAME_MAP = new HashMap<>(VALUES.length);
    static {
        for (MinesweeperState state : VALUES) {
            NAME_MAP.put(state.serializedName, state);
        }
    }

    @Getter
    private final int value;
    private final String serializedName;

    MinesweeperState(int value, String serializedName) {
        this.value = value;
        this.serializedName = serializedName;
    }

    @Override
    public @NonNull String getSerializedName() {
        return serializedName;
    }

    public static MinesweeperState fromInt(int value) {
        if (value >= 1 && value <= 8) return VALUES[4 + value];
        if (value >= -18 && value <= -11) return VALUES[12 + (-value - 10)];

        return switch (value) {
            case -10 -> DEATH_BOMB;
            case -9 -> BOMB;
            case -5 -> OPEN_EMPTY;
            case -1 -> FLAGGED;
            case 0 -> UNOPENED;
            default -> UNOPENED;
        };
    }

    public static MinesweeperState fromSerializedName(String name) {
        return NAME_MAP.getOrDefault(name, UNOPENED);
    }

    public static MinesweeperState getWrongFlagByNumber(int number) {
        if (number < 1 || number > 8) {
            throw new IllegalArgumentException("错误标记数字必须为1-8（当前：" + number + "）");
        }
        return fromInt(-10 - number);
    }

    public boolean isNumberState() {
        return value >= 1 && value <= 8;
    }

    public boolean isUnopened() {
        return this == UNOPENED;
    }

    public boolean isFlagged() {
        return this == FLAGGED;
    }

    public boolean isWrongFlag() {
        return value >= -18 && value <= -11;
    }

    public MinesweeperState getWrongNumber() {
        if (!isWrongFlag()) return fromInt(value);
        return fromInt(-10 - value);
    }
}
