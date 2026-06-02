package com.simple_block_game.common.simpleSudoku.logic;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.reward.BaseGameReward;
import com.simple_block_game.common.simpleSudoku.data.SudokuDifficulty;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public final class GameSudokuReward extends BaseGameReward {

    private GameSudokuReward() {}

    public static void handleCompleteReward(ServerLevel level, Player player, SudokuDifficulty difficulty) {
        String lootTable = switch (difficulty) {
            case EASY -> SimpleBlockGameConfig.SUDOKU_CONFIG.easyReward.get();
            case MEDIUM -> SimpleBlockGameConfig.SUDOKU_CONFIG.mediumReward.get();
            case HARD -> SimpleBlockGameConfig.SUDOKU_CONFIG.hardReward.get();
            case EXPERT -> SimpleBlockGameConfig.SUDOKU_CONFIG.expertReward.get();
        };
        dropLoot(level, player, id(lootTable));
    }
}
