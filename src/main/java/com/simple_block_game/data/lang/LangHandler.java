package com.simple_block_game.data.lang;

import static com.simple_block_game.SimpleBlockGame.REGISTRYLIB;

public class LangHandler {

    public static void addLang(String key, String cn, String en) {
        REGISTRYLIB.lang("en_us", key, en);
        REGISTRYLIB.lang("zh_cn", key, cn);
    }

    public static void init() {
        if (!REGISTRYLIB.doDatagen()) return;

        // Game 2048
        addLang("msg.simple2048.game_started", "2048 游戏已开始！", "2048 Game Started!");
        addLang("msg.simple2048.obstructed", "无法生成 2048 布局，区域被方块遮挡！", "Cannot place 2048 layout - area is obstructed!");
        addLang("msg.simple2048.move_score", "得分+%d | 总分：%d", "Score +%d | Total: %d");
        addLang("msg.simple2048.unmoveable", "已经无法再移动了 | 最大值：%d | 总分: %d", "It can no longer be moved | Maximum value：%d | Total: %d");
        addLang("msg.simple2048.core_not_found", "未找到 2048 核心方块！", "2048 Core Block not found!");
        addLang("msg.simple2048.minimized", "2048 游戏已最小化", "2048 Game Minimized");
        addLang("msg.simple2048.closed", "2048 游戏已关闭", "2048 Game Closed");
        addLang("msg.simple2048.reset", "2048 游戏已重置", "2048 Game Reset");

        // Game Minesweeper
        addLang("msg.minesweeper.game_already_started", "游戏已经开始！", "Game already started!");
        addLang("msg.minesweeper.entity_error", "方块实体错误！", "Block entity error!");
        addLang("msg.minesweeper.layout_obstructed", "布局区域被阻挡！请先清空区域。", "Layout area is obstructed! Please clear the area first.");
        addLang("msg.minesweeper.layout_placed", "场地布置完毕！难度：%s | 尺寸：%dx%d | 再次点击以开始游戏", "The field layout is complete! Difficulty: %s | Size: %dx%d | Click again to start the game");
        addLang("msg.minesweeper.game_started", "游戏开始！难度：%s | 地雷数：%d | 尺寸：%dx%d", "Game started! Difficulty: %s | Mines: %d | Size: %dx%d");
        addLang("msg.minesweeper.difficulty_switched", "难度已切换为%s！尺寸：%dx%d | 地雷数：%d", "Difficulty switched to %s! Size: %dx%d | Mines: %d");
        addLang("msg.minesweeper.custom_mine", "地雷数量已修改！尺寸：%dx%d | 地雷数：%d", "Number of mines modified! Size: %dx%d | Number of mines: %d");
        addLang("msg.minesweeper.custom_size", "尺寸已修改！尺寸：%dx%d | 地雷数：%d", "Size modified! Size: %dx%d | Number of mines: %d");
        addLang("msg.minesweeper.position_error", "位置数据无效！", "Invalid position data!");
        addLang("msg.minesweeper.core_not_found", "未找到扫雷核心方块！", "Minesweeper core block not found!");
        addLang("msg.minesweeper.invalid_position", "位置超出有效网格范围！", "Position is out of valid grid range!");
        addLang("msg.minesweeper.game_is_over", "游戏已结束！请重新开始！", "Game over! Please start again! ");
        addLang("msg.minesweeper.game_over", "游戏结束！你踩到了地雷！", "Game over! You stepped on a mine!");
        addLang("msg.minesweeper.game_win", "恭喜！你赢得了游戏！", "Congratulations! You won the game!");
        addLang("msg.minesweeper.refresh_entity_error", "刷新方块实体错误！", "Refresh block entity error!");
        addLang("msg.minesweeper.core_invalid", "无效的扫雷核心方块！", "Invalid minesweeper core block!");
        addLang("msg.minesweeper.minimized", "扫雷布局已最小化！", "Minesweeper layout minimized!");
        addLang("msg.minesweeper.closed", "扫雷布局已关闭！", "Minesweeper layout closed!");
        addLang("msg.minesweeper.reset", "扫雷游戏已重置！", "Minesweeper game reset!");
        addLang("preset.minesweeper.difficulty.easy", "简单", "Easy");
        addLang("preset.minesweeper.difficulty.normal", "普通", "Normal");
        addLang("preset.minesweeper.difficulty.hard", "困难", "Hard");
        addLang("preset.minesweeper.difficulty.expert", "专家", "Expert");
        addLang("preset.minesweeper.difficulty.custom", "自定义", "Custom");
    }
}
