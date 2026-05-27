package com.simple_block_game.common.simpleTenDrop.data;

import net.minecraft.util.StringRepresentable;

import org.jspecify.annotations.NonNull;

/**
 * 十滴游戏状态枚举，定义游戏在不同阶段的状态
 * 状态流转：IDLE -> PLAYING -> BURSTING -> REWARD -> (VICTORY|GAME_OVER|PLAYING)
 */
public enum TenDropGameState implements StringRepresentable {

    /** 空闲状态 - 游戏尚未开始或等待玩家操作 */
    IDLE("idle"),

    /** 游戏进行中 - 玩家可以正常点击操作 */
    PLAYING("playing"),

    /** 爆炸动画中 - 连锁反应正在处理，禁止玩家操作 */
    BURSTING("bursting"),

    /** 奖励结算中 - 显示消除奖励，准备下一回合 */
    REWARD("reward"),

    /** 胜利状态 - 所有水滴已消除 */
    VICTORY("victory"),

    /** 游戏结束 - 水滴用尽且仍有剩余水滴 */
    GAME_OVER("game_over");

    private final String serializedName;

    /**
     * 构造游戏状态枚举值
     * 
     * @param serializedName 序列化名称，用于网络传输和持久化
     */
    TenDropGameState(String serializedName) {
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
     * 判断游戏是否处于活跃状态（玩家可操作或动画进行中）
     * 
     * @return 是否为活跃状态
     */
    public boolean isGameActive() {
        return this == PLAYING || this == BURSTING;
    }

    /**
     * 判断游戏是否已结束（胜利或失败）
     * 
     * @return 是否为结束状态
     */
    public boolean isGameEnded() {
        return this == VICTORY || this == GAME_OVER;
    }

    /**
     * 判断是否为空闲状态
     * 
     * @return 是否为空闲状态
     */
    public boolean isIdle() {
        return this == IDLE;
    }

    /**
     * 判断是否正在进行爆炸动画
     * 
     * @return 是否为爆炸状态
     */
    public boolean isBursting() {
        return this == BURSTING;
    }

    /**
     * 判断是否正在显示奖励
     * 
     * @return 是否为奖励状态
     */
    public boolean isReward() {
        return this == REWARD;
    }

    /**
     * 判断玩家是否可以进行点击操作
     * 
     * @return 是否可操作
     */
    public boolean isInteractive() {
        return this == PLAYING;
    }

    /**
     * 根据序列化名称获取对应的枚举值
     * 
     * @param name 序列化名称
     * @return 对应的游戏状态枚举，未找到返回 IDLE
     */
    public static TenDropGameState fromSerializedName(String name) {
        for (TenDropGameState state : values()) {
            if (state.serializedName.equals(name)) {
                return state;
            }
        }
        return IDLE;
    }
}
