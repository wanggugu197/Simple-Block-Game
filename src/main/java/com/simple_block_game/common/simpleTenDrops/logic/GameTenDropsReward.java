package com.simple_block_game.common.simpleTenDrops.logic;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.reward.BaseGameReward;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public final class GameTenDropsReward extends BaseGameReward {

    private GameTenDropsReward() {}

    private static final Int2ObjectOpenHashMap<Identifier> LEVEL_REWARDS = new Int2ObjectOpenHashMap<>();

    static {
        var config = SimpleBlockGameConfig.TEN_DROP_CONFIG;

        LEVEL_REWARDS.put(1, id(config.level1Reward.get()));
        LEVEL_REWARDS.put(2, id(config.level2Reward.get()));
        LEVEL_REWARDS.put(3, id(config.level3Reward.get()));
        LEVEL_REWARDS.put(4, id(config.level4Reward.get()));
        LEVEL_REWARDS.put(5, id(config.level5Reward.get()));
        LEVEL_REWARDS.put(6, id(config.level6Reward.get()));
        LEVEL_REWARDS.put(7, id(config.level7Reward.get()));
        LEVEL_REWARDS.put(8, id(config.level8Reward.get()));
        LEVEL_REWARDS.put(9, id(config.level9Reward.get()));
        LEVEL_REWARDS.put(10, id(config.level10Reward.get()));
    }

    public static void handleLevelReward(ServerLevel serverLevel, Player player, int level) {
        if (serverLevel == null || player == null) return;
        dropLoot(serverLevel, player, LEVEL_REWARDS.getOrDefault(level, null));
    }
}
