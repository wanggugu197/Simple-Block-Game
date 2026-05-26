package com.simple_block_game.common.simpleMemoryKey.data;

import net.minecraft.util.StringRepresentable;

import org.jspecify.annotations.NonNull;

/** 记忆键游戏状态机枚举 */
public enum MemoryKeyGameState implements StringRepresentable {

    IDLE("idle"),
    DEMONSTRATING("demonstrating"),
    PLAYER_INPUT("player_input"),
    ERROR("error"),
    LEVEL_SUCCESS("level_success"),
    ALL_SUCCESS("all_success"),
    GAME_OVER("game_over");

    private final String serializedName;

    MemoryKeyGameState(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public @NonNull String getSerializedName() {
        return serializedName;
    }
}
