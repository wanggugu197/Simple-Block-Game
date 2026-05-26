package com.simple_block_game.common.simple2048.logic;

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
        SCORE_REWARDS.put(500, id("minecraft:chests/igloo_chest"));
        SCORE_REWARDS.put(1000, id("minecraft:chests/shipwreck_treasure"));
        SCORE_REWARDS.put(1500, id("minecraft:chests/underwater_ruin_big"));
        SCORE_REWARDS.put(2000, id("minecraft:chests/desert_pyramid"));
        SCORE_REWARDS.put(3000, id("minecraft:chests/abandoned_mineshaft"));
        SCORE_REWARDS.put(4000, id("minecraft:chests/jungle_temple"));
        SCORE_REWARDS.put(5000, id("minecraft:chests/pillager_outpost"));
        SCORE_REWARDS.put(7500, id("minecraft:chests/stronghold_library"));
        SCORE_REWARDS.put(10000, id("minecraft:chests/bastion_other"));
        SCORE_REWARDS.put(15000, id("minecraft:chests/bastion_treasure"));
        SCORE_REWARDS.put(20000, id("minecraft:chests/woodland_mansion"));
        SCORE_REWARDS.put(25000, id("minecraft:chests/ancient_city_ice_box"));
        SCORE_REWARDS.put(30000, id("minecraft:chests/ancient_city"));
        SCORE_REWARDS.put(50000, id("minecraft:chests/end_city_treasure"));

        MAX_REWARDS.put(1024, id("minecraft:chests/simple_dungeon"));
        MAX_REWARDS.put(2048, id("minecraft:chests/village/village_weaponsmith"));
        MAX_REWARDS.put(4096, id("minecraft:chests/woodland_mansion"));
        MAX_REWARDS.put(8192, id("minecraft:chests/ancient_city"));
        MAX_REWARDS.put(16384, id("minecraft:chests/bastion_treasure"));
        MAX_REWARDS.put(32768, id("minecraft:chests/buried_treasure"));
        MAX_REWARDS.put(65536, id("minecraft:chests/end_city_treasure"));
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
