package com.simple_block_game.common.simple2048.data;

import net.minecraft.util.StringRepresentable;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

/** 2048游戏数值枚举 */
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

    private static final Map<Integer, Value2048> VALUE_MAP = new HashMap<>();
    static {
        for (Value2048 val : values()) {
            VALUE_MAP.put(val.value, val);
        }
    }

    @Getter
    private final int value;
    private final String serializedName;

    Value2048(int value, String serializedName) {
        this.value = value;
        this.serializedName = serializedName;
    }

    @Override
    public @NonNull String getSerializedName() {
        return serializedName;
    }

    public static Value2048 fromInt(int value) {
        return VALUE_MAP.getOrDefault(value, ZERO);
    }
}
