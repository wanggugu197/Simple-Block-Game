package com.simple_block_game.common.simpleMemoryKey.logic;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.reward.BaseGameReward;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyLevel;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/**
 * 记忆键游戏奖励处理器
 */
public final class GameMemoryKeyReward extends BaseGameReward {

    private GameMemoryKeyReward() {}

    public static void handleReward(ServerLevel level, Player player, MemoryKeyLevel currentLevel, boolean isSuccess) {
        if (level == null || player == null || currentLevel == null) return;
        if (player.isDeadOrDying()) return;

        if (isSuccess) {
            String reward = SimpleBlockGameConfig.MEMORY_KEY_CONFIG.allSuccessReward.get();
            dropLoot(level, player, id(reward));
        } else {
            String reward = switch (currentLevel) {
                case LEVEL_1 -> SimpleBlockGameConfig.MEMORY_KEY_CONFIG.level1Reward.get();
                case LEVEL_2 -> SimpleBlockGameConfig.MEMORY_KEY_CONFIG.level2Reward.get();
                case LEVEL_3 -> SimpleBlockGameConfig.MEMORY_KEY_CONFIG.level3Reward.get();
                case LEVEL_4 -> SimpleBlockGameConfig.MEMORY_KEY_CONFIG.level4Reward.get();
                case LEVEL_5 -> SimpleBlockGameConfig.MEMORY_KEY_CONFIG.level5Reward.get();
                case LEVEL_6 -> SimpleBlockGameConfig.MEMORY_KEY_CONFIG.level6Reward.get();
            };
            dropLoot(level, player, id(reward));
        }
    }
}
