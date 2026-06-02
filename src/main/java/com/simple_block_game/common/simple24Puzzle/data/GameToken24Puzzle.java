package com.simple_block_game.common.simple24Puzzle.data;

import net.minecraft.util.StringRepresentable;

import lombok.Getter;
import lombok.NonNull;

public enum GameToken24Puzzle implements StringRepresentable {

    // 数字 1 - 13
    ONE("1", "1", 1, true),
    TWO("2", "2", 2, true),
    THREE("3", "3", 3, true),
    FOUR("4", "4", 4, true),
    FIVE("5", "5", 5, true),
    SIX("6", "6", 6, true),
    SEVEN("7", "7", 7, true),
    EIGHT("8", "8", 8, true),
    NINE("9", "9", 9, true),
    TEN("10", "10", 10, true),
    JACK("11", "11", 11, true),
    QUEEN("12", "12", 12, true),
    KING("13", "13", 13, true),

    // 运算符与括号
    ADD("+", "add", -1, false),
    SUB("−", "sub", -2, false),
    MUL("×", "mul", -3, false),
    DIV("÷", "div", -4, false),
    L_BRACKET("(", "l_bracket", -5, false),
    R_BRACKET(")", "r_bracket", -6, false);

    private final String name;
    @Getter
    private final String lowerCaseName;
    @Getter
    private final int value;
    @Getter
    private final boolean isNumber;

    GameToken24Puzzle(String name, String lowerCaseName, int value, boolean isNumber) {
        this.name = name;
        this.lowerCaseName = lowerCaseName;
        this.value = value;
        this.isNumber = isNumber;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name;
    }
}
