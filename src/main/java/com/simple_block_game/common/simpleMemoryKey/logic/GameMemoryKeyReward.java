package com.simple_block_game.common.simpleMemoryKey.logic;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.reward.BaseGameReward;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyLevel;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * 记忆键游戏奖励处理器
 */
public final class GameMemoryKeyReward extends BaseGameReward {

    private static final Int2ObjectOpenHashMap<Identifier> LEVEL_REWARDS = new Int2ObjectOpenHashMap<>();
    private static final Identifier ALL_SUCCESS_REWARD;

    static {
        var config = SimpleBlockGameConfig.MEMORY_KEY_CONFIG;

        LEVEL_REWARDS.put(1, id(config.level1Reward.get()));
        LEVEL_REWARDS.put(2, id(config.level2Reward.get()));
        LEVEL_REWARDS.put(3, id(config.level3Reward.get()));
        LEVEL_REWARDS.put(4, id(config.level4Reward.get()));
        LEVEL_REWARDS.put(5, id(config.level5Reward.get()));
        LEVEL_REWARDS.put(6, id(config.level6Reward.get()));

        ALL_SUCCESS_REWARD = id(config.allSuccessReward.get());
    }

    private GameMemoryKeyReward() {}

    public static void handleReward(ServerLevel level, Player player, MemoryKeyLevel currentLevel, boolean isSuccess) {
        if (level == null || player == null || currentLevel == null) return;
        if (player.isDeadOrDying()) return;

        if (isSuccess) {
            if (ALL_SUCCESS_REWARD != null) {
                dropLoot(level, player, ALL_SUCCESS_REWARD);
            }
        } else {
            Identifier tableId = LEVEL_REWARDS.getOrDefault(currentLevel.getLevelNumber(), null);
            if (tableId != null) {
                dropLoot(level, player, tableId);
            }
        }
    }
}
