package com.simple_block_game.common.simple2048;

import com.simple_block_game.common.base.block.BaseRotatedRefreshBlock;
import com.simple_block_game.common.simple2048.block.Block2048Core;
import com.simple_block_game.common.simple2048.block.Block2048CoreEntity;
import com.simple_block_game.common.simple2048.block.Block2048Display;
import com.simple_block_game.common.simple2048.block.Block2048DisplayEntity;
import com.simple_block_game.common.simple2048.renderer.Block2048CoreEntityRenderer;
import com.simple_block_game.common.simple2048.renderer.Block2048DisplayEntityRenderer;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import com.gto.registrylib.tooltip.SubNode;
import com.gto.registrylib.util.entry.BlockEntityTypeEntry;
import com.gto.registrylib.util.entry.BlockEntry;

import static com.simple_block_game.SimpleBlockGame.REGISTRYLIB;
import static com.simple_block_game.common.SimpleBlockGameRecipe.UNCONDITIONAL_CRITERION;
import static com.simple_block_game.common.SimpleBlockGameRegistration.TAB_GANM;
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.createHorizontalBlock;

public class simple2048Registration {

    public static void init() {
        REGISTRYLIB.addRecipeData(prov -> prov.shaped(RecipeCategory.COMBAT, BLOCK_2048_CORE)
                .pattern("QQQ")
                .pattern("QIQ")
                .pattern("QQQ")
                .define('Q', Items.QUARTZ_PILLAR)
                .define('I', Items.IRON_BLOCK)
                .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                .save(prov, "make_2048_core"));
    }

    // 2048
    public static final BlockEntry<Block2048Core> BLOCK_2048_CORE = REGISTRYLIB
            .block(REGISTRYLIB, "2048_core", Block2048Core::new)
            .langCn("2048 核心方块")
            .lang("2048 Core")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple2048/2048_core_open")))
                    .addTooltip((collector, _) -> {
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.2048_core.1")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.2048_core.2")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.2048_core.3")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.2048_core.4")));
                    }))
            .register();

    public static final BlockEntry<Block2048Display> BLOCK_2048_DISPLAY = REGISTRYLIB
            .block(REGISTRYLIB, "2048_display", Block2048Display::new)
            .langCn("2048 显示方块")
            .lang("2048 Display")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_center"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple2048/2048_display_2048"))))
            .register();

    public static final BlockEntry<BaseRotatedRefreshBlock> BLOCK_2048_REFRESH = REGISTRYLIB
            .block(REGISTRYLIB, "2048_refresh", p -> BaseRotatedRefreshBlock.create(p, "simple2048"))
            .langCn("2048 控制方块")
            .lang("2048 Refresh")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/rotated_refresh"))))
            .register();

    public static final BlockEntityTypeEntry<Block2048CoreEntity> BLOCK_2048_CORE_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "2048_core_entity", (_, p, s) -> new Block2048CoreEntity(p, s))
            .validBlock(BLOCK_2048_CORE)
            .renderer(() -> () -> Block2048CoreEntityRenderer::new)
            .register();

    public static final BlockEntityTypeEntry<Block2048DisplayEntity> BLOCK_2048_DISPLAY_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "2048_display_entity", (_, p, s) -> new Block2048DisplayEntity(p, s))
            .validBlock(BLOCK_2048_DISPLAY)
            .renderer(() -> () -> Block2048DisplayEntityRenderer::new)
            .register();
}
