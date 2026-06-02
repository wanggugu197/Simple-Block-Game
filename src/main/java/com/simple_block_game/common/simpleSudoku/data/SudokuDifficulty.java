package com.simple_block_game.common.simpleSudoku.data;

import net.minecraft.util.StringRepresentable;

import com.mojang.serialization.Codec;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public enum SudokuDifficulty implements StringRepresentable {

    EASY(52, "easy"),
    MEDIUM(40, "medium"),
    HARD(32, "hard"),
    EXPERT(22, "expert");

    @Getter
    private final int targetHints;
    private final String serializedName;

    SudokuDifficulty(int targetHints, String serializedName) {
        this.targetHints = targetHints;
        this.serializedName = serializedName;
    }

    @Override
    public @NotNull String getSerializedName() {
        return serializedName;
    }

    public static @NotNull SudokuDifficulty fromSerializedName(@NotNull String serializedName) {
        for (SudokuDifficulty diff : SudokuDifficulty.values()) {
            if (diff.getSerializedName().equals(serializedName)) {
                return diff;
            }
        }
        return EASY;
    }

    public SudokuDifficulty next() {
        return switch (this) {
            case EASY -> MEDIUM;
            case MEDIUM -> HARD;
            case HARD -> EXPERT;
            case EXPERT -> EASY;
        };
    }

    public SudokuDifficulty prev() {
        return switch (this) {
            case EASY -> EXPERT;
            case MEDIUM -> EASY;
            case HARD -> MEDIUM;
            case EXPERT -> HARD;
        };
    }

    public String getDisplayName() {
        return "msg.sudoku.difficulty." + name().toLowerCase();
    }

    public static final Codec<SudokuDifficulty> CODEC = StringRepresentable.fromEnum(SudokuDifficulty::values);
}
