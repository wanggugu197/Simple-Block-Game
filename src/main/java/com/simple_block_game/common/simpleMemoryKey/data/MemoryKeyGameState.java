package com.simple_block_game.common.simpleMemoryKey.data;

import net.minecraft.util.StringRepresentable;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 记忆键游戏状态机枚举
 */
public enum MemoryKeyGameState implements StringRepresentable {

    IDLE(0, "idle"),
    DEMONSTRATING(1, "demonstrating"),
    PLAYER_INPUT(2, "player_input"),
    ERROR(3, "error"),
    LEVEL_SUCCESS(4, "level_success"),
    ALL_SUCCESS(5, "all_success"),
    GAME_OVER(6, "game_over");

    private static final MemoryKeyGameState[] VALUES = values();

    private static final Map<String, MemoryKeyGameState> NAME_MAP = new HashMap<>(VALUES.length);

    static {
        for (MemoryKeyGameState state : VALUES) {
            NAME_MAP.put(state.serializedName, state);
        }
    }

    @Getter
    private final int value;
    private final String serializedName;

    MemoryKeyGameState(int value, String serializedName) {
        this.value = value;
        this.serializedName = serializedName;
    }

    @Override
    public @NonNull String getSerializedName() {
        return serializedName;
    }

    /**
     * 根据整数值获取枚举值
     */
    public static MemoryKeyGameState fromInt(int value) {
        if (value >= 0 && value < VALUES.length) {
            return VALUES[value];
        }
        return IDLE;
    }
}
