package com.simple_block_game.common.simpleTenDrops.data;

import net.minecraft.util.StringRepresentable;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

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
     *
     * @param level          等级数值
     * @param serializedName 序列化名称，用于网络传输和渲染
     */
    DropletLevel(int level, String serializedName) {
        this.level = level;
        this.serializedName = serializedName;
    }

    /**
     * 获取序列化名称
     *
     * @return 状态的字符串表示
     */
    @Override
    public @NonNull String getSerializedName() {
        return serializedName;
    }

    /**
     * 判断是否可以添加水滴（非空且非爆炸状态）
     *
     * @return 是否可添加水滴
     */
    public boolean canAddDrop() {
        return this != BURST && this != EMPTY;
    }

    /**
     * 判断是否即将爆炸（当前为4级）
     *
     * @return 是否即将爆炸
     */
    public boolean willBurst() {
        return this == FOUR;
    }

    /**
     * 判断是否为空状态
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return this == EMPTY;
    }

    /**
     * 判断是否为爆炸状态
     *
     * @return 是否为爆炸状态
     */
    public boolean isBurst() {
        return this == BURST;
    }

    /**
     * 判断是否为有效水滴（1-4级）
     *
     * @return 是否为有效水滴
     */
    public boolean isValidDroplet() {
        return this != EMPTY && this != BURST;
    }

    /**
     * 获取下一级水滴状态
     *
     * @return 升级后的水滴等级
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
     * 获取上一级水滴状态
     *
     * @return 降级后的水滴等级
     */
    public DropletLevel previousLevel() {
        return switch (this) {
            case EMPTY -> BURST;
            case ONE -> EMPTY;
            case TWO -> ONE;
            case THREE -> TWO;
            case FOUR -> THREE;
            case BURST -> FOUR;
        };
    }

    /**
     * 根据等级数值获取对应的枚举值
     *
     * @param level 等级数值（0-5）
     * @return 对应的水滴等级枚举，超出范围返回EMPTY
     */
    public static DropletLevel fromLevel(int level) {
        return switch (level) {
            case 0 -> EMPTY;
            case 1 -> ONE;
            case 2 -> TWO;
            case 3 -> THREE;
            case 4 -> FOUR;
            case 5 -> BURST;
            default -> EMPTY;
        };
    }

    /**
     * 根据序列化名称获取对应的枚举值
     *
     * @param name 序列化名称
     * @return 对应的水滴等级枚举，未找到返回EMPTY
     */
    public static DropletLevel fromSerializedName(String name) {
        for (DropletLevel level : values()) {
            if (level.serializedName.equals(name)) {
                return level;
            }
        }
        return EMPTY;
    }
}
