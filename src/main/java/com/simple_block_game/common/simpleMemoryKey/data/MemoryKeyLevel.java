package com.simple_block_game.common.simpleMemoryKey.data;

import net.minecraft.util.StringRepresentable;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 记忆键游戏关卡配置枚举
 */
public enum MemoryKeyLevel implements StringRepresentable {

    LEVEL_1(1, 3, 1000, "level_1"),
    LEVEL_2(2, 6, 900, "level_2"),
    LEVEL_3(3, 9, 800, "level_3"),
    LEVEL_4(4, 12, 700, "level_4"),
    LEVEL_5(5, 15, 600, "level_5"),
    LEVEL_6(6, 18, 500, "level_6");

    private static final Map<Integer, MemoryKeyLevel> LEVEL_MAP = new HashMap<>();
    private static final MemoryKeyLevel[] VALUES = values();

    static {
        for (MemoryKeyLevel level : values()) {
            LEVEL_MAP.put(level.levelNumber, level);
        }
    }

    @Getter
    private final int levelNumber;      // 关卡编号(1-6)
    @Getter
    private final int sequenceLength;   // 序列长度
    @Getter
    private final int flashDurationMs;  // 闪烁时长(ms)
    private final String serializedName;

    MemoryKeyLevel(int levelNumber, int sequenceLength, int flashDurationMs, String serializedName) {
        this.levelNumber = levelNumber;
        this.sequenceLength = sequenceLength;
        this.flashDurationMs = flashDurationMs;
        this.serializedName = serializedName;
    }

    @Override
    public @NonNull String getSerializedName() {
        return serializedName;
    }

    /**
     * 根据关卡编号获取枚举，无效时返回LEVEL_1
     */
    public static MemoryKeyLevel fromLevelNumber(int level) {
        return LEVEL_MAP.getOrDefault(level, LEVEL_1);
    }

    /**
     * 获取下一关，最后一关返回自身
     */
    public MemoryKeyLevel next() {
        return this == LEVEL_6 ? LEVEL_6 : VALUES[ordinal() + 1];
    }

    /**
     * 是否为最后一关
     */
    public boolean isLastLevel() {
        return this == LEVEL_6;
    }
}
