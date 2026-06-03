package com.simple_block_game.common.simpleJustGet10.logic;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.reward.BaseGameReward;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public final class GameJustGet10Reward extends BaseGameReward {

    private static final Int2ObjectOpenHashMap<ResourceLocation> SCORE_REWARDS = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectOpenHashMap<ResourceLocation> MAX_REWARDS = new Int2ObjectOpenHashMap<>();

    static {
        var config = SimpleBlockGameConfig.justGet10RewardConfig;

        SCORE_REWARDS.put(config.scoreThreshold1.get().intValue(), id(config.scoreReward1.get()));
        SCORE_REWARDS.put(config.scoreThreshold2.get().intValue(), id(config.scoreReward2.get()));
        SCORE_REWARDS.put(config.scoreThreshold3.get().intValue(), id(config.scoreReward3.get()));
        SCORE_REWARDS.put(config.scoreThreshold4.get().intValue(), id(config.scoreReward4.get()));
        SCORE_REWARDS.put(config.scoreThreshold5.get().intValue(), id(config.scoreReward5.get()));

        MAX_REWARDS.put(9, id(config.maxReward9.get()));
        MAX_REWARDS.put(10, id(config.maxReward10.get()));
        MAX_REWARDS.put(11, id(config.maxReward11.get()));
        MAX_REWARDS.put(12, id(config.maxReward12.get()));
        MAX_REWARDS.put(13, id(config.maxReward13.get()));
    }

    private GameJustGet10Reward() {}

    public static void handleScoreReward(ServerLevel level, Player player, int oldScore, int newScore) {
        if (oldScore >= newScore) return;
        SCORE_REWARDS.keySet().forEach(threshold -> {
            if (threshold > oldScore && threshold <= newScore) {
                ResourceLocation tableId = SCORE_REWARDS.getOrDefault(threshold, null);
                if (tableId != null) {
                    dropLoot(level, player, tableId);
                }
            }
        });
    }

    public static void handleMaxValueReward(ServerLevel level, Player player, int oldMax, int newMax) {
        if (oldMax >= newMax) return;
        MAX_REWARDS.keySet().forEach(threshold -> {
            if (threshold > oldMax && threshold <= newMax) {
                ResourceLocation tableId = MAX_REWARDS.getOrDefault(threshold, null);
                if (tableId != null) {
                    dropLoot(level, player, tableId);
                }
            }
        });
    }
}
