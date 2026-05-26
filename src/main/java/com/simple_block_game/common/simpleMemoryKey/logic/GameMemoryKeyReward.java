package com.simple_block_game.common.simpleMemoryKey.logic;

import com.simple_block_game.common.base.reward.BaseGameReward;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyLevel;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/** 记忆键游戏奖励处理器 */
public final class GameMemoryKeyReward extends BaseGameReward {

    private static final Int2ObjectOpenHashMap<Identifier> LEVEL_REWARDS = new Int2ObjectOpenHashMap<>();

    static {
        LEVEL_REWARDS.put(1, id("minecraft:chests/simple_dungeon"));
        LEVEL_REWARDS.put(2, id("minecraft:chests/igloo_chest"));
        LEVEL_REWARDS.put(3, id("minecraft:chests/shipwreck_supply"));
        LEVEL_REWARDS.put(4, id("minecraft:chests/abandoned_mineshaft"));
        LEVEL_REWARDS.put(5, id("minecraft:chests/pillager_outpost"));
        LEVEL_REWARDS.put(6, id("minecraft:chests/bastion_treasure"));
    }

    private static final Identifier ALL_SUCCESS_REWARD = id("minecraft:chests/end_city_treasure");

    private GameMemoryKeyReward() {}

    public static void handleReward(ServerLevel level, Player player, MemoryKeyLevel currentLevel, boolean isSuccess) {
        if (player.isDeadOrDying()) return;

        if (isSuccess) {
            dropLoot(level, player, ALL_SUCCESS_REWARD);
        } else {
            Identifier tableId = LEVEL_REWARDS.get(currentLevel.getLevelNumber());
            if (tableId != null) {
                dropLoot(level, player, tableId);
            }
        }
    }
}
