package com.simple_block_game.common.simple2048.data;

import net.minecraft.util.StringRepresentable;

import lombok.Getter;
import lombok.NonNull;

public enum Value2048 implements StringRepresentable {

    ZERO(0, "0"),
    TWO(2, "2"),
    FOUR(4, "4"),
    EIGHT(8, "8"),
    SIXTEEN(16, "16"),
    THIRTY_TWO(32, "32"),
    SIXTY_FOUR(64, "64"),
    ONE_TWENTY_EIGHT(128, "128"),
    TWO_FIFTY_SIX(256, "256"),
    FIVE_TWELVE(512, "512"),
    ONE_K(1024, "1024"),
    TWO_K(2048, "2048"),
    FOUR_K(4096, "4096"),
    EIGHT_K(8192, "8192"),
    SIXTEEN_K(16384, "16384"),
    THIRTY_TWO_K(32768, "32768"),
    SIXTY_FOUR_K(65536, "65536");

    @Getter
    private final int value;
    private final String serializedName;

    Value2048(int value, String serializedName) {
        this.value = value;
        this.serializedName = serializedName;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.serializedName;
    }

    public static Value2048 fromInt(int value) {
        for (Value2048 val : values()) {
            if (val.value == value) {
                return val;
            }
        }
        return ZERO;
    }
}
