package com.simple_block_game.common.simpleJustGet10.data;

import net.minecraft.util.StringRepresentable;

import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

public enum ValueJustGet10 implements StringRepresentable {

    NUM_1(1, "1"),
    NUM_2(2, "2"),
    NUM_3(3, "3"),
    NUM_4(4, "4"),
    NUM_5(5, "5"),
    NUM_6(6, "6"),
    NUM_7(7, "7"),
    NUM_8(8, "8"),
    NUM_9(9, "9"),
    NUM_10(10, "10"),
    NUM_11(11, "11"),
    NUM_12(12, "12"),
    NUM_13(13, "13"),
    NUM_14(14, "14"),
    NUM_15(15, "15"),
    NUM_16(16, "16"),
    NUM_17(17, "17"),
    NUM_18(18, "18"),
    NUM_19(19, "19"),
    NUM_20(20, "20");

    @Getter
    private final int value;
    private final String name;

    ValueJustGet10(int value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name;
    }

    public Optional<ValueJustGet10> getNext() {
        return getByValue(this.value + 1);
    }

    public static Optional<ValueJustGet10> getByValue(int val) {
        for (ValueJustGet10 v : values()) {
            if (v.getValue() == val) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }
}
