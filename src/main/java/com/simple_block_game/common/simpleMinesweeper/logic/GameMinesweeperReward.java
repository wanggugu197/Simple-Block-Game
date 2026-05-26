package com.simple_block_game.common.simpleMinesweeper.logic;

import com.simple_block_game.common.base.reward.BaseGameReward;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import it.unimi.dsi.fastutil.floats.Float2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectSortedMap;

/** 扫雷游戏奖励处理器 */
public final class GameMinesweeperReward extends BaseGameReward {

    private static final Float2ObjectRBTreeMap<Identifier> REWARD_TABLE = new Float2ObjectRBTreeMap<>();

    static {
        REWARD_TABLE.put(0.08f, id("minecraft:chests/desert_pyramid"));
        REWARD_TABLE.put(0.15f, id("minecraft:chests/abandoned_mineshaft"));
        REWARD_TABLE.put(0.22f, id("minecraft:chests/pillager_outpost"));
        REWARD_TABLE.put(0.29f, id("minecraft:chests/woodland_mansion"));
        REWARD_TABLE.put(0.35f, id("minecraft:chests/bastion_treasure"));
        REWARD_TABLE.put(0.40f, id("minecraft:chests/end_city_treasure"));
    }

    private GameMinesweeperReward() {}

    public static void handleReward(ServerLevel level, Player player, float mineContent) {
        if (player.isDeadOrDying()) return;

        Float2ObjectSortedMap<Identifier> subMap = REWARD_TABLE.headMap(mineContent);
        if (subMap.isEmpty()) return;

        Identifier lootTableId = REWARD_TABLE.get(subMap.lastFloatKey());
        if (lootTableId != null) {
            dropLoot(level, player, lootTableId);
        }
    }
}
