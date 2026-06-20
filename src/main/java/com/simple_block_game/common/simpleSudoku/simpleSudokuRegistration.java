package com.simple_block_game.common.simpleSudoku;

import com.simple_block_game.common.base.block.BaseVerticalRefreshBlock;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuCore;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuCoreEntity;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuDisplay;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuDisplayEntity;
import com.simple_block_game.common.simpleSudoku.renderer.BlockSudokuCoreEntityRenderer;
import com.simple_block_game.common.simpleSudoku.renderer.BlockSudokuDisplayEntityRenderer;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import com.gto.registrylib.tooltip.SubNode;
import com.gto.registrylib.util.entry.BlockEntityTypeEntry;
import com.gto.registrylib.util.entry.BlockEntry;

import static com.simple_block_game.SimpleBlockGame.REGISTRY;
import static com.simple_block_game.common.SimpleBlockGameRecipe.UNCONDITIONAL_CRITERION;
import static com.simple_block_game.common.SimpleBlockGameRegistration.TAB_GANM;
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.createVerticalBlock;

public class simpleSudokuRegistration {

    public static void init() {
        REGISTRY.addRecipeData(prov -> prov.shaped(RecipeCategory.COMBAT, BLOCK_SUDOKU_CORE)
                .pattern("QQQ")
                .pattern("QIQ")
                .pattern("QQQ")
                .define('Q', Items.QUARTZ_PILLAR)
                .define('I', Items.BOOK)
                .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                .save(prov, "make_sudoku_core"));
    }

    // sudoku
    public static final BlockEntry<BlockSudokuCore> BLOCK_SUDOKU_CORE = REGISTRY
            .block(REGISTRY, "sudoku_core", BlockSudokuCore::new)
            .langCn("数独核心方块")
            .lang("Sudoku Core")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_sudoku/sudoku_core")))
                    .addTooltip((collector, _) -> {
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.sudoku_core.1")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.sudoku_core.2")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.sudoku_core.3")));
                    }))
            .register();

    public static final BlockEntry<BlockSudokuDisplay> BLOCK_SUDOKU_DISPLAY = REGISTRY
            .block(REGISTRY, "sudoku_display", BlockSudokuDisplay::new)
            .langCn("数独显示方块")
            .lang("Sudoku Display")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_center"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_sudoku/sudoku_display"))))
            .register();

    public static final BlockEntry<BaseVerticalRefreshBlock> BLOCK_SUDOKU_REFRESH = REGISTRY
            .block(REGISTRY, "sudoku_refresh", p -> BaseVerticalRefreshBlock.create(p, "sudoku"))
            .langCn("数独控制方块")
            .lang("Sudoku Refresh")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/vertical_refresh"))))
            .register();

    public static final BlockEntityTypeEntry<BlockSudokuCoreEntity> BLOCK_SUDOKU_CORE_ENTITY = REGISTRY
            .blockEntity(REGISTRY, "sudoku_core_entity", (_, p, s) -> new BlockSudokuCoreEntity(p, s))
            .validBlock(BLOCK_SUDOKU_CORE)
            .renderer(() -> () -> BlockSudokuCoreEntityRenderer::new)
            .register();

    public static final BlockEntityTypeEntry<BlockSudokuDisplayEntity> BLOCK_SUDOKU_DISPLAY_ENTITY = REGISTRY
            .blockEntity(REGISTRY, "sudoku_display_entity", (_, p, s) -> new BlockSudokuDisplayEntity(p, s))
            .validBlock(BLOCK_SUDOKU_DISPLAY)
            .renderer(() -> () -> BlockSudokuDisplayEntityRenderer::new)
            .register();
}
