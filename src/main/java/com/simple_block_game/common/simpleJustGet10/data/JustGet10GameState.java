package com.simple_block_game.common.simpleJustGet10.data;

import net.minecraft.util.StringRepresentable;

import lombok.NonNull;

public enum JustGet10GameState implements StringRepresentable {

    IDLE("idle"),
    PLAYING("playing"),
    GAME_OVER("game_over");

    private final String serializedName;

    JustGet10GameState(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public @NonNull String getSerializedName() {
        return serializedName;
    }

    public static JustGet10GameState fromSerializedName(String name) {
        for (JustGet10GameState state : values()) {
            if (state.serializedName.equals(name)) {
                return state;
            }
        }
        return IDLE;
    }
}
