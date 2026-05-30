package com.simple_block_game.common.simpleTenDrops.data;

import net.minecraft.util.StringRepresentable;

import lombok.Getter;
import lombok.NonNull;

/**
 * 水滴等级枚举，定义水滴的不同状态
 * 等级从0（空）到5（爆炸），达到5级时会爆炸并向四个方向飞溅
 */
public enum DropletLevel implements StringRepresentable {

    /**
     * 空状态 - 单元格中没有水滴
     */
    EMPTY(0, "empty"),

    /**
     * 1级水滴
     */
    ONE(1, "one"),

    /**
     * 2级水滴
     */
    TWO(2, "two"),

    /**
     * 3级水滴
     */
    THREE(3, "three"),

    /**
     * 4级水滴 - 达到此等级时，再添加一滴水就会爆炸
     */
    FOUR(4, "four"),

    /**
     * 爆炸状态 - 水滴已爆炸，会产生飞溅效果
     */
    BURST(5, "burst");

    @Getter
    private final int level;
    private final String serializedName;

    /**
     * 构造水滴等级枚举值
     */
    DropletLevel(int level, String serializedName) {
        this.level = level;
        this.serializedName = serializedName;
    }

    /**
     * 获取序列化名称
     */
    @Override
    public @NonNull String getSerializedName() {
        return serializedName;
    }

    /**
     * 获取下一级水滴状态
     */
    public DropletLevel nextLevel() {
        return switch (this) {
            case EMPTY -> ONE;
            case ONE -> TWO;
            case TWO -> THREE;
            case THREE -> FOUR;
            case FOUR -> BURST;
            case BURST -> EMPTY;
        };
    }

    /**
     * 根据等级数值获取对应的枚举值
     */
    public static DropletLevel fromLevel(int level) {
        return switch (level) {
            case 1 -> ONE;
            case 2 -> TWO;
            case 3 -> THREE;
            case 4 -> FOUR;
            case 5 -> BURST;
            default -> EMPTY;
        };
    }
}
