package com.simple_block_game.data.lang;

import com.simple_block_game.data.lang.lang.CNEN;
import com.simple_block_game.data.lang.lang.ChineseConverter;
import com.simple_block_game.data.lang.lang.SimplifiedChineseLanguageProvider;
import com.simple_block_game.data.lang.lang.TraditionalChineseLanguageProvider;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.data.LanguageProvider;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class LangHandler {

    private static final Map<String, CNEN> LANGS = new Object2ObjectOpenHashMap<>();

    private static void addLang(String key, CNEN CNEN) {
        if (LANGS.containsKey(key)) throw new IllegalArgumentException("Duplicate key: " + key);
        LANGS.put(key, CNEN);
    }

    public static void addLang(String key, String cn, String en) {
        addLang(key, new CNEN(cn, en));
    }

    public static Component addLANG(String key, String cn, String en) {
        addLang(key, new CNEN(cn, en));
        return Component.translatable(key);
    }

    public static void addCN(String key, String cn) {
        addLang(key, cn, null);
    }

    public static void init() {
        ItemTooltip.init();
        GameMessage.init();

        addLANG("itemGroup.simple_block_game.simple_block_game_tab", "简单方块游戏", "Simple Block Game");

        // ==================== 2048 ====================
        addLANG("block.simple_block_game.2048_core", "2048 核心方块", null);
        addLANG("block.simple_block_game.2048_display", "2048 显示方块", null);
        addLANG("block.simple_block_game.2048_refresh", "2048 控制方块", null);
        // ==================== Minesweeper ====================
        addLANG("block.simple_block_game.minesweeper_core", "扫雷核心方块", null);
        addLANG("block.simple_block_game.minesweeper_display", "扫雷显示方块", null);
        addLANG("block.simple_block_game.minesweeper_refresh", "扫雷控制方块", null);
        // ==================== Memory Key ====================
        addLANG("block.simple_block_game.memory_key_core", "记忆键核心方块", null);
        addLANG("block.simple_block_game.memory_key_button", "记忆键按钮方块", null);
        addLANG("block.simple_block_game.memory_key_refresh", "记忆键控制方块", null);
        // ==================== Ten Drops ====================
        addLANG("block.simple_block_game.ten_drops_core", "十滴水核心方块", null);
        addLANG("block.simple_block_game.ten_drops_display", "十滴水显示方块", null);
        addLANG("block.simple_block_game.ten_drops_refresh", "十滴水控制方块", null);
        // ==================== Sudoku ====================
        addLANG("block.simple_block_game.sudoku_core", "数独核心方块", null);
        addLANG("block.simple_block_game.sudoku_display", "数独显示方块", null);
        addLANG("block.simple_block_game.sudoku_refresh", "数独控制方块", null);
        // ==================== Frame ====================
        addLANG("block.simple_block_game.rotated_frame", "框架方块", null);
        addLANG("block.simple_block_game.vertical_frame", "框架方块", null);

        // ==================== Items ====================
        addLANG("item.simple_block_game.simple_block_game", "简单方块游戏", null);
    }

    public static void enInitialize(LanguageProvider provider) {
        init();
        LANGS.forEach((k, v) -> {
            if (v.en() == null) return;
            provider.add(k, v.en());
        });
    }

    public static void cnInitialize(SimplifiedChineseLanguageProvider provider) {
        LANGS.forEach((k, v) -> {
            if (v.cn() == null) return;
            provider.add(k, v.cn());
        });
    }

    public static void twInitialize(TraditionalChineseLanguageProvider provider) {
        LANGS.forEach((k, v) -> {
            if (v.cn() == null) return;
            provider.add(k, ChineseConverter.convert(v.cn()));
        });
    }
}
