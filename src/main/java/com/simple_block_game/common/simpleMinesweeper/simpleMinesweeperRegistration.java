package com.simple_block_game.common.simpleMinesweeper;

import com.simple_block_game.common.base.block.BaseVerticalRefreshBlock;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCore;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCoreEntity;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperDisplay;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperDisplayEntity;
import com.simple_block_game.common.simpleMinesweeper.renderer.BlockMinesweeperCoreEntityRenderer;
import com.simple_block_game.common.simpleMinesweeper.renderer.BlockMinesweeperDisplayEntityRenderer;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import com.gto.registrylib.tooltip.SubNode;
import com.gto.registrylib.util.entry.BlockEntityTypeEntry;
import com.gto.registrylib.util.entry.BlockEntry;

import static com.simple_block_game.SimpleBlockGame.REGISTRYLIB;
import static com.simple_block_game.common.SimpleBlockGameRecipe.UNCONDITIONAL_CRITERION;
import static com.simple_block_game.common.SimpleBlockGameRegistration.TAB_GANM;
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.createVerticalBlock;

public class simpleMinesweeperRegistration {

    public static void init() {
        REGISTRYLIB.addRecipeData(prov -> prov.shaped(RecipeCategory.COMBAT, BLOCK_MINESWEEPER_CORE)
                .pattern("QQQ")
                .pattern("QIQ")
                .pattern("QQQ")
                .define('Q', Items.QUARTZ_PILLAR)
                .define('I', Items.TNT)
                .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                .save(prov, "make_minesweeper_core"));
    }

    // minesweeper
    public static final BlockEntry<BlockMinesweeperCore> BLOCK_MINESWEEPER_CORE = REGISTRYLIB
            .block(REGISTRYLIB, "minesweeper_core", BlockMinesweeperCore::new)
            .langCn("扫雷核心方块")
            .lang("Minesweeper Core")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_minesweeper/minesweeper_core")))
                    .addTooltip((collector, _) -> {
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.minesweeper_core.1")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.minesweeper_core.2")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.minesweeper_core.3")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.minesweeper_core.4")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.minesweeper_core.5")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.minesweeper_core.6")));
                    }))
            .register();

    public static final BlockEntry<BlockMinesweeperDisplay> BLOCK_MINESWEEPER_DISPLAY = REGISTRYLIB
            .block(REGISTRYLIB, "minesweeper_display", BlockMinesweeperDisplay::new)
            .langCn("扫雷显示方块")
            .lang("Minesweeper Display")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_center"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_minesweeper/minesweeper_display"))))
            .register();

    public static final BlockEntry<BaseVerticalRefreshBlock> BLOCK_MINESWEEPER_REFRESH = REGISTRYLIB
            .block(REGISTRYLIB, "minesweeper_refresh", p -> BaseVerticalRefreshBlock.create(p, "minesweeper"))
            .langCn("扫雷控制方块")
            .lang("Minesweeper Refresh")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/vertical_refresh"))))
            .register();

    public static final BlockEntityTypeEntry<BlockMinesweeperCoreEntity> BLOCK_MINESWEEPER_CORE_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "minesweeper_core_entity", (_, p, s) -> new BlockMinesweeperCoreEntity(p, s))
            .validBlock(BLOCK_MINESWEEPER_CORE)
            .renderer(() -> () -> BlockMinesweeperCoreEntityRenderer::new)
            .register();

    public static final BlockEntityTypeEntry<BlockMinesweeperDisplayEntity> BLOCK_MINESWEEPER_DISPLAY_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "minesweeper_display_entity", (_, p, s) -> new BlockMinesweeperDisplayEntity(p, s))
            .validBlock(BLOCK_MINESWEEPER_DISPLAY)
            .renderer(() -> () -> BlockMinesweeperDisplayEntityRenderer::new)
            .register();
}
