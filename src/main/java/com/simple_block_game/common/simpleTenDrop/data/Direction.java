package com.simple_block_game.common.simpleTenDrop.data;

import lombok.Getter;

/**
 * 方向枚举，定义水滴爆炸后飞溅的四个方向
 */
@Getter
public enum Direction {

    /** 北方向 - 行坐标减少 */
    NORTH(0, -1),

    /** 南方向 - 行坐标增加 */
    SOUTH(0, 1),

    /** 东方向 - 列坐标增加 */
    EAST(1, 0),

    /** 西方向 - 列坐标减少 */
    WEST(-1, 0);

    private final int dx;
    private final int dy;

    /**
     * 构造方向枚举值
     * 
     * @param dx 列方向偏移量
     * @param dy 行方向偏移量
     */
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * 获取所有方向数组
     * 
     * @return 包含四个方向的数组
     */
    public static Direction[] allDirections() {
        return values();
    }

    /**
     * 获取反方向
     * 
     * @return 当前方向的相反方向
     */
    public Direction getOpposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }

    /**
     * 判断是否为水平方向（东或西）
     * 
     * @return 是否为水平方向
     */
    public boolean isHorizontal() {
        return this == EAST || this == WEST;
    }

    /**
     * 判断是否为垂直方向（北或南）
     * 
     * @return 是否为垂直方向
     */
    public boolean isVertical() {
        return this == NORTH || this == SOUTH;
    }
}
