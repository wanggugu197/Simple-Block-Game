package com.simple_block_game.common.simple24Puzzle.logic;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.reward.BaseGameReward;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleCoreEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/**
 * 24点游戏奖励处理器
 * 根据完成时间和连击次数给予不同等级的奖励
 */
public final class Game24PuzzleReward extends BaseGameReward {

    private static final int TICKS_PER_SECOND = 20;

    private Game24PuzzleReward() {}

    /**
     * 根据游戏状态处理奖励
     * 当连续成功次数达到阈值时，根据消耗时间给予对应等级奖励
     */
    public static void handleReward(ServerLevel level, Player player, Block24PuzzleCoreEntity coreEntity) {
        if (!canReward(level, player, coreEntity)) {
            return;
        }
        ResourceLocation rewardTable = determineRewardTable(coreEntity);
        dropLoot(level, player, rewardTable);
        coreEntity.resetCombo(level.getGameTime());
    }

    private static boolean canReward(ServerLevel level, Player player, Block24PuzzleCoreEntity coreEntity) {
        return level != null && player != null && coreEntity != null && coreEntity.getCompletedCount() >= SimpleBlockGameConfig.GAME_24PUZZLE_CONFIG.comboThreshold.get();
    }

    private static ResourceLocation determineRewardTable(Block24PuzzleCoreEntity coreEntity) {
        double elapsedSeconds = (coreEntity.getLastSuccessTime() - coreEntity.getStartTime()) / (double) TICKS_PER_SECOND;
        var config = SimpleBlockGameConfig.GAME_24PUZZLE_CONFIG;
        if (elapsedSeconds <= config.timeTier1Seconds.get()) return id(config.rewardTier1.get());
        else if (elapsedSeconds <= config.timeTier2Seconds.get()) return id(config.rewardTier2.get());
        else if (elapsedSeconds <= config.timeTier3Seconds.get()) return id(config.rewardTier3.get());
        else if (elapsedSeconds <= config.timeTier4Seconds.get()) return id(config.rewardTier4.get());
        return id(config.defaultReward.get());
    }
}
