package com.simple_block_game.common.simpleMinesweeper.data;

import net.minecraft.util.StringRepresentable;

import lombok.Getter;
import lombok.NonNull;

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

    @Getter
    private final int value;
    private final String serializedName;

    MinesweeperState(int value, String serializedName) {
        this.value = value;
        this.serializedName = serializedName;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.serializedName;
    }

    public static MinesweeperState fromInt(int value) {
        return switch (value) {
            case -10 -> DEATH_BOMB;
            case -9 -> BOMB;
            case -5 -> OPEN_EMPTY;
            case -1 -> FLAGGED;
            case 0 -> UNOPENED;

            case 1 -> NUMBER_1;
            case 2 -> NUMBER_2;
            case 3 -> NUMBER_3;
            case 4 -> NUMBER_4;
            case 5 -> NUMBER_5;
            case 6 -> NUMBER_6;
            case 7 -> NUMBER_7;
            case 8 -> NUMBER_8;

            case -11 -> WRONG_FLAG_1;
            case -12 -> WRONG_FLAG_2;
            case -13 -> WRONG_FLAG_3;
            case -14 -> WRONG_FLAG_4;
            case -15 -> WRONG_FLAG_5;
            case -16 -> WRONG_FLAG_6;
            case -17 -> WRONG_FLAG_7;
            case -18 -> WRONG_FLAG_8;

            default -> UNOPENED;
        };
    }

    public static MinesweeperState getWrongFlagByNumber(int number) {
        if (number < 1 || number > 8) {
            throw new IllegalArgumentException("错误标记数字必须为1-8（当前：" + number + "）");
        }
        return switch (number) {
            case 1 -> WRONG_FLAG_1;
            case 2 -> WRONG_FLAG_2;
            case 3 -> WRONG_FLAG_3;
            case 4 -> WRONG_FLAG_4;
            case 5 -> WRONG_FLAG_5;
            case 6 -> WRONG_FLAG_6;
            case 7 -> WRONG_FLAG_7;
            case 8 -> WRONG_FLAG_8;
            default -> OPEN_EMPTY;
        };
    }

    public boolean isNumberState() {
        return this.value >= 1 && this.value <= 8;
    }

    public boolean isUnopened() {
        return this == UNOPENED;
    }

    public boolean isFlagged() {
        return this == FLAGGED;
    }

    public boolean isWrongFlag() {
        return switch (this) {
            case WRONG_FLAG_1, WRONG_FLAG_2, WRONG_FLAG_3, WRONG_FLAG_4, WRONG_FLAG_5, WRONG_FLAG_6, WRONG_FLAG_7, WRONG_FLAG_8 -> true;
            default -> false;
        };
    }
}
