package com.simple_block_game.common.simpleMinesweeper.data;

import net.minecraft.util.StringRepresentable;

import lombok.Getter;
import lombok.NonNull;

@Getter
public enum PresetDifficulty implements StringRepresentable {

    EASY(9, 9, 10, "easy"),
    NORMAL(16, 16, 40, "normal"),
    HARD(16, 30, 99, "hard"),
    EXPERT(30, 64, 450, "expert"),
    CUSTOM(64, 64, 1000, "custom");

    private final int width;
    private final int height;
    private final int mineCount;
    private final String serializedName;

    PresetDifficulty(int width, int height, int mineCount, String serializedName) {
        this.width = width;
        this.height = height;
        this.mineCount = mineCount;
        this.serializedName = serializedName;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.serializedName;
    }

    public PresetDifficulty next() {
        PresetDifficulty[] values = PresetDifficulty.values();
        int nextIndex = (this.ordinal() + 1) % values.length;
        return values[nextIndex];
    }

    public PresetDifficulty prev() {
        PresetDifficulty[] values = PresetDifficulty.values();
        int prevIndex = (this.ordinal() - 1 + values.length) % values.length;
        return values[prevIndex];
    }

    public String getDisplayName() {
        return "preset.minesweeper.difficulty." + this.name().toLowerCase();
    }
}
