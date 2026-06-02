package com.simple_block_game.data.lang;

import static com.simple_block_game.data.lang.LangHandler.addLang;

public class ItemTooltip {

    public static void init() {
        // ========== Tooltips ==========
        // 2048
        addLang("tooltip.2048_core.1", "点击核心方块正面中心区域开始", "Click the center of the core block front face to start.");
        addLang("tooltip.2048_core.2", "开始后点击正面的上、下、左、右区域移动数字", "After starting, click the up, down, left, or right areas on the front face to move tiles.");
        addLang("tooltip.2048_core.3", "相同数字会合并并获得分数", "Matching numbers merge and give score.");
        addLang("tooltip.2048_core.4", "不能继续移动时游戏结束", "The game ends when no moves remain.");

        // Minesweeper
        addLang("tooltip.minesweeper_core.1", "点击核心方块顶部中心区域展开或开始", "Click the center of the core block top face to unfold or start.");
        addLang("tooltip.minesweeper_core.2", "开始前可点击顶部角落切换难度", "Before starting, click top corners to change difficulty.");
        addLang("tooltip.minesweeper_core.3", "自定义模式下可调整尺寸和雷数", "Custom mode allows size and mine-count changes.");
        addLang("tooltip.minesweeper_core.4", "点击格子翻开", "Click a cell to reveal it.");
        addLang("tooltip.minesweeper_core.5", "Shift点击格子插旗或取消旗帜", "Shift click a cell to place or remove a flag.");
        addLang("tooltip.minesweeper_core.6", "避开所有雷并完成扫雷即可获胜", "Clear the board without hitting mines to win.");

        // Memory Key
        addLang("tooltip.memory_key_core.1", "点击核心方块顶部展开，再次点击开始", "Click the top of the core block to unfold, then click again to start.");
        addLang("tooltip.memory_key_core.2", "观察按钮闪烁顺序", "Watch the flashing button sequence.");
        addLang("tooltip.memory_key_core.3", "按相同顺序点击周围按钮", "Click the surrounding buttons in the same order.");
        addLang("tooltip.memory_key_core.4", "输入正确进入下一关，输入错误会扣生命", "Correct input advances the level; wrong input costs a life.");
        addLang("tooltip.memory_key_core.5", "完成全部关卡获胜", "Complete all levels to win.");

        // Ten Drops
        addLang("tooltip.ten_drops_core.1", "点击核心方块开始", "Click the core block to start.");
        addLang("tooltip.ten_drops_core.2", "点击水滴格子提升等级并消耗次数", "Click droplet cells to increase their level and spend moves.");
        addLang("tooltip.ten_drops_core.3", "水滴爆裂后会向四个方向传播并触发连锁", "Bursting droplets spread in four directions and trigger chains.");
        addLang("tooltip.ten_drops_core.4", "清空全部水滴获胜", "Clear all droplets to win.");
        addLang("tooltip.ten_drops_core.5", "次数耗尽则失败", "Running out of moves means failure.");

        // Sudoku
        addLang("tooltip.sudoku_core.1", "点击核心方块顶部中心区域展开或开始", "Click the center of the core block top face to unfold or start.");
        addLang("tooltip.sudoku_core.2", "开始前可点击顶部角落切换难度", "Before starting, click top corners to change difficulty.");
        addLang("tooltip.sudoku_core.3", "点击右上角可开启/关闭对角线模式", "Click the top-right corner to enable/disable diagonal mode.");
        addLang("tooltip.sudoku_core.4", "点击数字格子可填写或清除数字", "Click number cells to fill or clear digits.");
        addLang("tooltip.sudoku_core.5", "完成数独即可获胜", "Complete the sudoku to win.");

        // 24 Puzzle
        addLang("tooltip.24puzzle_core.1", "点击核心方块正面中心区域展开游戏", "Click the center of the core block front face to unfold the game.");
        addLang("tooltip.24puzzle_core.2", "使用显示的4个数字和运算符组合计算出24", "Use the 4 displayed numbers with operators to calculate 24.");
        addLang("tooltip.24puzzle_core.3", "点击显示方块选择数字或运算符输入表达式", "Click display blocks to select numbers or operators for your expression.");
        addLang("tooltip.24puzzle_core.4", "点击左上角清除输入，点击右上角撤销最后输入", "Click top-left to clear input, top-right to undo last entry.");
        addLang("tooltip.24puzzle_core.5", "点击核心方块验证答案，连续答对可获得奖励", "Click the core block to verify your answer. Consecutive correct answers earn rewards.");
    }
}
