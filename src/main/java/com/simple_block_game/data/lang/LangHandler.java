package com.simple_block_game.data.lang;

import static com.simple_block_game.SimpleBlockGame.REGISTRYLIB;

public class LangHandler {

    public static void addLang(String key, String cn, String en) {
        REGISTRYLIB.lang("en_us", key, en);
        REGISTRYLIB.lang("zh_cn", key, cn);
    }

    public static void init() {
        if (!REGISTRYLIB.doDatagen()) return;

        // ========== 公共翻译键 ==========
        addLang("msg.common.refresh_entity_error", "刷新方块实体错误！", "Refresh block entity error!");
        addLang("msg.common.entity_error", "方块实体错误！", "Block entity error!");
        addLang("msg.common.obstructed", "布局区域被阻挡！请先清空区域。", "Layout area is obstructed! Please clear the area first.");

        // ========== Game 2048 ==========
        addLang("msg.simple2048.game_started", "2048 游戏已开始！", "2048 Game Started!");
        addLang("msg.simple2048.obstructed", "无法生成 2048 布局，区域被方块遮挡！", "Cannot place 2048 layout - area is obstructed!");
        addLang("msg.simple2048.move_score", "得分+%d | 总分：%d", "Score +%d | Total: %d");
        addLang("msg.simple2048.unmoveable", "已经无法再移动了 | 最大值：%d | 总分: %d", "It can no longer be moved | Maximum value：%d | Total: %d");

        addLang("msg.simple2048.core_not_found", "未找到 2048 核心方块！", "2048 Core Block not found!");
        addLang("msg.simple2048.core_invalid", "无效的 2048 核心方块！", "Invalid 2048 core block!");
        addLang("msg.simple2048.minimized", "2048 游戏已最小化", "2048 Game Minimized");
        addLang("msg.simple2048.closed", "2048 游戏已关闭", "2048 Game Closed");
        addLang("msg.simple2048.reset", "2048 游戏已重置", "2048 Game Reset");

        // ========== Game Minesweeper ==========
        addLang("msg.minesweeper.game_already_started", "游戏已经开始！", "Game already started!");
        addLang("msg.minesweeper.layout_placed", "场地布置完毕！难度：%s | 尺寸：%dx%d | 再次点击以开始游戏", "The field layout is complete! Difficulty: %s | Size: %dx%d | Click again to start the game");
        addLang("msg.minesweeper.game_started", "游戏开始！难度：%s | 地雷数：%d | 尺寸：%dx%d", "Game started! Difficulty: %s | Mines: %d | Size: %dx%d");
        addLang("msg.minesweeper.difficulty_switched", "难度已切换为%s！尺寸：%dx%d | 地雷数：%d", "Difficulty switched to %s! Size: %dx%d | Mines: %d");
        addLang("msg.minesweeper.custom_mine", "地雷数量已修改！尺寸：%dx%d | 地雷数：%d", "Number of mines modified! Size: %dx%d | Number of mines: %d");
        addLang("msg.minesweeper.custom_size", "尺寸已修改！尺寸：%dx%d | 地雷数：%d", "Size modified! Size: %dx%d | Number of mines: %d");
        addLang("msg.minesweeper.position_error", "位置数据无效！", "Invalid position data!");
        addLang("msg.minesweeper.invalid_position", "位置超出有效网格范围！", "Position is out of valid grid range!");
        addLang("msg.minesweeper.game_is_over", "游戏已结束！请重新开始！", "Game over! Please start again! ");
        addLang("msg.minesweeper.game_over", "游戏结束！你踩到了地雷！", "Game over! You stepped on a mine!");
        addLang("msg.minesweeper.game_win", "恭喜！你赢得了游戏！", "Congratulations! You won the game!");

        addLang("msg.minesweeper.core_not_found", "未找到扫雷核心方块！", "Minesweeper core block not found!");
        addLang("msg.minesweeper.core_invalid", "无效的扫雷核心方块！", "Invalid minesweeper core block!");
        addLang("msg.minesweeper.minimized", "扫雷布局已最小化！", "Minesweeper layout minimized!");
        addLang("msg.minesweeper.closed", "扫雷布局已关闭！", "Minesweeper layout closed!");
        addLang("msg.minesweeper.reset", "扫雷游戏已重置！", "Minesweeper game reset!");

        addLang("msg.minesweeper.difficulty.easy", "简单", "Easy");
        addLang("msg.minesweeper.difficulty.normal", "普通", "Normal");
        addLang("msg.minesweeper.difficulty.hard", "困难", "Hard");
        addLang("msg.minesweeper.difficulty.expert", "专家", "Expert");
        addLang("msg.minesweeper.difficulty.custom", "自定义", "Custom");

        // ========== Game Memory Key ==========
        addLang("msg.memory_key.game_ready", "场地布置完毕！点击开始游戏", "Layout complete! Click to start the game");
        addLang("msg.memory_key.watch_sequence", "观看演示序列...", "Watch the sequence...");
        addLang("msg.memory_key.all_levels_complete", "恭喜通关！", "Congratulations! You completed all levels!");
        addLang("msg.memory_key.level_complete", "关卡 %d 完成！", "Level %d complete!");
        addLang("msg.memory_key.game_over", "游戏结束！当前关卡：%d", "Game over! Current level: %d");
        addLang("msg.memory_key.wrong_input", "输入错误！剩余 %d 次机会", "Wrong input! %d chances remaining");

        addLang("msg.memory_key.core_not_found", "未找到记忆键核心方块！", "Memory Key Core block not found!");
        addLang("msg.memory_key.core_invalid", "无效的记忆键核心方块！", "Invalid Memory Key Core block!");
        addLang("msg.memory_key.minimized", "记忆键布局已最小化！", "Memory Key layout minimized!");
        addLang("msg.memory_key.closed", "记忆键布局已关闭！", "Memory Key layout closed!");
        addLang("msg.memory_key.reset", "记忆键游戏已重置！", "Memory Key game reset!");

        // ========== Game Ten Drop ==========
        addLang("msg.ten_drops.start", "十滴水游戏开始！", "Ten Drop Game Started!");
        addLang("msg.ten_drops.game_started", "十滴水游戏布局已展开！", "Ten Drop layout deployed!");
        addLang("msg.ten_drops.obstructed", "无法生成十滴水布局，区域被阻挡！", "Cannot place Ten Drop layout - area is obstructed!");
        addLang("msg.ten_drops.victory", "恭喜通关！进入下一关！", "Victory! Proceeding to next level!");
        addLang("msg.ten_drops.game_over", "游戏结束！水滴用尽！", "Game Over! No water drops left!");
        addLang("msg.ten_drops.level_up", "第 %d 关", "Level %d");
        addLang("msg.ten_drops.water_drops", "剩余水滴: %d", "Water drops: %d");
        addLang("msg.ten_drops.combo", "连击 x%d！", "Combo x%d!");
        addLang("msg.ten_drops.no_drops", "没有水滴了！", "No water drops!");
        addLang("msg.ten_drops.invalid_click", "无效点击！", "Invalid click!");

        addLang("msg.ten_drops.core_not_found", "未找到十滴水核心方块！", "Ten Drop Core block not found!");
        addLang("msg.ten_drops.core_invalid", "无效的十滴水核心方块！", "Invalid Ten Drop Core block!");
        addLang("msg.ten_drops.minimized", "十滴水布局已最小化！", "Ten Drop layout minimized!");
        addLang("msg.ten_drops.closed", "十滴水布局已关闭！", "Ten Drop layout closed!");
        addLang("msg.ten_drops.reset", "十滴水游戏已重置！", "Ten Drop game reset!");
    }
}
