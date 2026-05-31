package com.simple_block_game.common.simpleSudoku.data;

import net.minecraft.util.StringRepresentable;

import com.mojang.serialization.Codec;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public enum Difficulty implements StringRepresentable {

    EASY(52, "easy"),
    MEDIUM(40, "medium"),
    HARD(32, "hard"),
    EXPERT(22, "expert");

    @Getter
    private final int targetHints;
    private final String serializedName;

    Difficulty(int targetHints, String serializedName) {
        this.targetHints = targetHints;
        this.serializedName = serializedName;
    }

    @Override
    public @NotNull String getSerializedName() {
        return serializedName;
    }

    public static @NotNull Difficulty fromSerializedName(@NotNull String serializedName) {
        for (Difficulty diff : Difficulty.values()) {
            if (diff.getSerializedName().equals(serializedName)) {
                return diff;
            }
        }
        return EASY;
    }

    public Difficulty next() {
        return switch (this) {
            case EASY -> MEDIUM;
            case MEDIUM -> HARD;
            case HARD -> EXPERT;
            case EXPERT -> EASY;
        };
    }

    public Difficulty prev() {
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

    public static final Codec<Difficulty> CODEC = StringRepresentable.fromEnum(Difficulty::values);
}
