package com.simple_block_game.common.simple2048.logic;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.reward.BaseGameReward;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/** 2048游戏奖励处理器 */
public final class Game2048Reward extends BaseGameReward {

    private static final Int2ObjectOpenHashMap<Identifier> SCORE_REWARDS = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectOpenHashMap<Identifier> MAX_REWARDS = new Int2ObjectOpenHashMap<>();

    static {
        var config = SimpleBlockGameConfig.GAME_2048_CONFIG;

        SCORE_REWARDS.put(config.scoreThreshold1.get().intValue(), id(config.scoreReward1.get()));
        SCORE_REWARDS.put(config.scoreThreshold2.get().intValue(), id(config.scoreReward2.get()));
        SCORE_REWARDS.put(config.scoreThreshold3.get().intValue(), id(config.scoreReward3.get()));
        SCORE_REWARDS.put(config.scoreThreshold4.get().intValue(), id(config.scoreReward4.get()));
        SCORE_REWARDS.put(config.scoreThreshold5.get().intValue(), id(config.scoreReward5.get()));
        SCORE_REWARDS.put(config.scoreThreshold6.get().intValue(), id(config.scoreReward6.get()));
        SCORE_REWARDS.put(config.scoreThreshold7.get().intValue(), id(config.scoreReward7.get()));
        SCORE_REWARDS.put(config.scoreThreshold8.get().intValue(), id(config.scoreReward8.get()));
        SCORE_REWARDS.put(config.scoreThreshold9.get().intValue(), id(config.scoreReward9.get()));
        SCORE_REWARDS.put(config.scoreThreshold10.get().intValue(), id(config.scoreReward10.get()));
        SCORE_REWARDS.put(config.scoreThreshold11.get().intValue(), id(config.scoreReward11.get()));
        SCORE_REWARDS.put(config.scoreThreshold12.get().intValue(), id(config.scoreReward12.get()));
        SCORE_REWARDS.put(config.scoreThreshold13.get().intValue(), id(config.scoreReward13.get()));
        SCORE_REWARDS.put(config.scoreThreshold14.get().intValue(), id(config.scoreReward14.get()));

        MAX_REWARDS.put(1024, id(config.maxReward1024.get()));
        MAX_REWARDS.put(2048, id(config.maxReward2048.get()));
        MAX_REWARDS.put(4096, id(config.maxReward4096.get()));
        MAX_REWARDS.put(8192, id(config.maxReward8192.get()));
        MAX_REWARDS.put(16384, id(config.maxReward16384.get()));
        MAX_REWARDS.put(32768, id(config.maxReward32768.get()));
        MAX_REWARDS.put(65536, id(config.maxReward65536.get()));
    }

    private Game2048Reward() {}

    public static void handleScoreReward(ServerLevel level, Player player, int oldScore, int newScore) {
        if (oldScore >= newScore) return;
        SCORE_REWARDS.keySet().forEach(threshold -> {
            if (threshold > oldScore && threshold <= newScore) {
                Identifier tableId = SCORE_REWARDS.get(threshold);
                if (tableId != null) dropLoot(level, player, tableId);
            }
        });
    }

    public static void handleMaxNumberReward(ServerLevel level, Player player, int oldMax, int newMax) {
        if (oldMax >= newMax) return;
        MAX_REWARDS.keySet().forEach(threshold -> {
            if (threshold > oldMax && threshold <= newMax) {
                Identifier tableId = MAX_REWARDS.get(threshold);
                if (tableId != null) dropLoot(level, player, tableId);
            }
        });
    }
}
