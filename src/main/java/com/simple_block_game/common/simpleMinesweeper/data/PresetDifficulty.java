package com.simple_block_game.common.simpleMinesweeper.data;

import net.minecraft.util.StringRepresentable;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

/** 扫雷预设难度枚举 */
public enum PresetDifficulty implements StringRepresentable {

    EASY(9, 9, 10, "easy"),
    NORMAL(16, 16, 40, "normal"),
    HARD(16, 30, 99, "hard"),
    EXPERT(30, 64, 450, "expert"),
    CUSTOM(64, 64, 1000, "custom");

    private static final PresetDifficulty[] VALUES = values();

    @Getter
    private final int width;
    @Getter
    private final int height;
    @Getter
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
        return serializedName;
    }

    public PresetDifficulty next() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    public PresetDifficulty prev() {
        return VALUES[(ordinal() - 1 + VALUES.length) % VALUES.length];
    }

    public String getDisplayName() {
        return "msg.minesweeper.difficulty." + name().toLowerCase();
    }
}
