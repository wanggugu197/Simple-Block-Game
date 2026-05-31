package com.simple_block_game.common.simpleSudoku.data;

import net.minecraft.util.StringRepresentable;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

public enum SudokuGameState implements StringRepresentable {

    IDLE("idle"),
    PLAYING("playing"),
    COMPLETE("complete");

    private final String serializedName;

    SudokuGameState(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public @NotNull String getSerializedName() {
        return serializedName;
    }

    public static @NotNull SudokuGameState fromSerializedName(@NotNull String serializedName) {
        for (SudokuGameState state : SudokuGameState.values()) {
            if (state.getSerializedName().equals(serializedName)) {
                return state;
            }
        }
        return IDLE;
    }

    public static final Codec<SudokuGameState> CODEC = StringRepresentable.fromEnum(SudokuGameState::values);
}
