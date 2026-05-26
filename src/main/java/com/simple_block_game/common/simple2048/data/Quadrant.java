package com.simple_block_game.common.simple2048.data;

/** 2048游戏四方向枚举 */
public enum Quadrant {

    NULL,
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public int getRotationCount() {
        return switch (this) {
            case LEFT -> 0;
            case UP -> 1;
            case RIGHT -> 2;
            case DOWN -> 3;
            default -> 0;
        };
    }
}
