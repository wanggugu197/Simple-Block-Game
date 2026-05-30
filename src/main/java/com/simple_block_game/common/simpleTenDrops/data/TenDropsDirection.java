package com.simple_block_game.common.simpleTenDrops.data;

import lombok.Getter;

/**
 * 方向枚举，定义水滴爆炸后飞溅的四个方向
 */
@Getter
public enum TenDropsDirection {

    /**
     * 北方向 - 行坐标减少
     */
    NORTH(0, -1),

    /**
     * 南方向 - 行坐标增加
     */
    SOUTH(0, 1),

    /**
     * 东方向 - 列坐标增加
     */
    EAST(1, 0),

    /**
     * 西方向 - 列坐标减少
     */
    WEST(-1, 0);

    private final int dx;
    private final int dy;

    /**
     * 构造方向枚举值
     *
     * @param dx 列方向偏移量
     * @param dy 行方向偏移量
     */
    TenDropsDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * 获取所有方向数组
     *
     * @return 包含四个方向的数组
     */
    public static TenDropsDirection[] allDirections() {
        return values();
    }
}
