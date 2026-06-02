package com.simple_block_game.common;

import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.common.base.block.RotatedFrame;
import com.simple_block_game.common.base.block.VerticalFrame;
import com.simple_block_game.common.base.renderer.BlockRefreshEntityRenderer;
import com.simple_block_game.common.simple2048.simple2048Registration;
import com.simple_block_game.common.simple24Puzzle.simple24PuzzleRegistration;
import com.simple_block_game.common.simpleMemoryKey.simpleMemoryKeyRegistration;
import com.simple_block_game.common.simpleMinesweeper.simpleMinesweeperRegistration;
import com.simple_block_game.common.simpleSudoku.simpleSudokuRegistration;
import com.simple_block_game.common.simpleTenDrops.simpleTenDropsRegistration;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import com.gto.registrylib.util.entry.BlockEntityTypeEntry;
import com.gto.registrylib.util.entry.BlockEntry;
import com.gto.registrylib.util.entry.ItemEntry;
import com.gto.registrylib.util.entry.RegistryEntry;

import java.util.Map;

import static com.simple_block_game.SimpleBlockGame.REGISTRYLIB;
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.createHorizontalBlock;
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.createVerticalBlock;

/**
 * 方块和实体注册类
 */
public class SimpleBlockGameRegistration {

    public static void init() {
        simple2048Registration.init();
        simpleMinesweeperRegistration.init();
        simpleMemoryKeyRegistration.init();
        simpleTenDropsRegistration.init();
        simpleSudokuRegistration.init();
        simple24PuzzleRegistration.init();
    }

    public static final ItemEntry<Item> COPPER_COIN = REGISTRYLIB
            .item("simple_block_game", Item::new)
            .langCn("简单方块游戏")
            .lang("Simple Block Game")
            .defaultModel()
            .register();

    // 创造模式标签注册
    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> TAB_GANM = REGISTRYLIB
            .creativeTab("simple_block_game_tab", "Simple Block Game", Map.of("zh_cn", "简单方块游戏"), builder -> {
                builder.icon(COPPER_COIN::asStack);
                builder.displayItems((_, output) -> output.accept(COPPER_COIN.asStack()));
            });

    // 统一的刷新实体，绑定所有刷新方块
    public static final BlockEntityTypeEntry<BlockRefreshEntity> BLOCK_REFRESH_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "refresh_entity", BlockRefreshEntity::new)
            .validBlock(simple2048Registration.BLOCK_2048_REFRESH)
            .validBlock(simpleMinesweeperRegistration.BLOCK_MINESWEEPER_REFRESH)
            .validBlock(simpleMemoryKeyRegistration.BLOCK_MEMORY_KEY_REFRESH)
            .validBlock(simpleTenDropsRegistration.BLOCK_TEN_DROPS_REFRESH)
            .validBlock(simpleSudokuRegistration.BLOCK_SUDOKU_REFRESH)
            .validBlock(simple24PuzzleRegistration.BLOCK_24PUZZLE_REFRESH)
            .renderer(() -> () -> BlockRefreshEntityRenderer::new)
            .register();

    // 框架方块
    public static final BlockEntry<RotatedFrame> BLOCK_ROTATED_FRAME = REGISTRYLIB
            .block(REGISTRYLIB, "rotated_frame", RotatedFrame::new)
            .langCn("框架方块")
            .lang("Frame")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/rotated_side"))))
            .register();

    public static final BlockEntry<VerticalFrame> BLOCK_VERTICAL_FRAME = REGISTRYLIB
            .block(REGISTRYLIB, "vertical_frame", VerticalFrame::new)
            .langCn("框架方块")
            .lang("Frame")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/vertical_side"))))
            .register();
}
