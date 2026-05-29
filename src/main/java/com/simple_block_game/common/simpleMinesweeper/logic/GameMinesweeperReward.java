package com.simple_block_game.common.simpleMinesweeper.logic;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.reward.BaseGameReward;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import it.unimi.dsi.fastutil.floats.Float2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectSortedMap;

/**
 * 扫雷游戏奖励处理器
 */
public final class GameMinesweeperReward extends BaseGameReward {

    private static final Float2ObjectRBTreeMap<Identifier> REWARD_TABLE = new Float2ObjectRBTreeMap<>();

    static {
        var config = SimpleBlockGameConfig.MINESWEEPER_CONFIG;

        REWARD_TABLE.put(config.threshold1.get().floatValue(), id(config.reward1.get()));
        REWARD_TABLE.put(config.threshold2.get().floatValue(), id(config.reward2.get()));
        REWARD_TABLE.put(config.threshold3.get().floatValue(), id(config.reward3.get()));
        REWARD_TABLE.put(config.threshold4.get().floatValue(), id(config.reward4.get()));
        REWARD_TABLE.put(config.threshold5.get().floatValue(), id(config.reward5.get()));
        REWARD_TABLE.put(config.threshold6.get().floatValue(), id(config.reward6.get()));
        REWARD_TABLE.put(config.threshold7.get().floatValue(), id(config.reward7.get()));
        REWARD_TABLE.put(config.threshold8.get().floatValue(), id(config.reward8.get()));
        REWARD_TABLE.put(config.threshold9.get().floatValue(), id(config.reward9.get()));
        REWARD_TABLE.put(config.threshold10.get().floatValue(), id(config.reward10.get()));
    }

    private GameMinesweeperReward() {}

    public static void handleReward(ServerLevel level, Player player, float mineContent) {
        if (level == null || player == null) return;
        if (player.isDeadOrDying()) return;
        if (mineContent <= 0 || mineContent > 1.0f) return;

        Float2ObjectSortedMap<Identifier> subMap = REWARD_TABLE.headMap(mineContent);
        if (subMap.isEmpty()) return;

        Identifier lootTableId = REWARD_TABLE.getOrDefault(subMap.lastFloatKey(), null);
        if (lootTableId != null) {
            dropLoot(level, player, lootTableId);
        }
    }
}
