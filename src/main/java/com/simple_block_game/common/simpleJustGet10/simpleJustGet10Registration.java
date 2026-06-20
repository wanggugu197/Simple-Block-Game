package com.simple_block_game.common.simpleJustGet10;

import com.simple_block_game.common.base.block.BaseRotatedRefreshBlock;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10Core;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10CoreEntity;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10Display;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10DisplayEntity;
import com.simple_block_game.common.simpleJustGet10.renderer.BlockJustGet10CoreEntityRenderer;
import com.simple_block_game.common.simpleJustGet10.renderer.BlockJustGet10DisplayEntityRenderer;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import com.gto.registrylib.tooltip.SubNode;
import com.gto.registrylib.util.entry.BlockEntityTypeEntry;
import com.gto.registrylib.util.entry.BlockEntry;

import static com.simple_block_game.SimpleBlockGame.REGISTRY;
import static com.simple_block_game.common.SimpleBlockGameRecipe.UNCONDITIONAL_CRITERION;
import static com.simple_block_game.common.SimpleBlockGameRegistration.TAB_GANM;
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.createHorizontalBlock;

public class simpleJustGet10Registration {

    public static void init() {
        REGISTRY.addRecipeData(prov -> prov.shaped(RecipeCategory.COMBAT, BLOCK_JUST_GET_10_CORE)
                .pattern("QQQ")
                .pattern("QIQ")
                .pattern("QQQ")
                .define('Q', Items.QUARTZ_PILLAR)
                .define('I', Items.CRAFTING_TABLE)
                .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                .save(prov, "make_just_get_10_core"));
    }

    public static final BlockEntry<BlockJustGet10Core> BLOCK_JUST_GET_10_CORE = REGISTRY
            .block(REGISTRY, "just_get_10_core", BlockJustGet10Core::new)
            .langCn("合成10核心方块")
            .lang("Just Get 10 Core")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_just_get_10/just_get_10_core")))
                    .addTooltip((collector, _) -> {
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.just_get_10_core.1")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.just_get_10_core.2")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.just_get_10_core.3")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.just_get_10_core.4")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.just_get_10_core.5")));
                    }))
            .register();

    public static final BlockEntry<BlockJustGet10Display> BLOCK_JUST_GET_10_DISPLAY = REGISTRY
            .block(REGISTRY, "just_get_10_display", BlockJustGet10Display::new)
            .langCn("合成10显示方块")
            .lang("Just Get 10 Display")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_center"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_just_get_10/just_get_10_display"))))
            .register();

    public static final BlockEntry<BaseRotatedRefreshBlock> BLOCK_JUST_GET_10_REFRESH = REGISTRY
            .block(REGISTRY, "just_get_10_refresh", p -> BaseRotatedRefreshBlock.create(p, "just_get_10"))
            .langCn("合成10控制方块")
            .lang("Just Get 10 Refresh")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/rotated_refresh"))))
            .register();

    public static final BlockEntityTypeEntry<BlockJustGet10CoreEntity> BLOCK_JUST_GET_10_CORE_ENTITY = REGISTRY
            .blockEntity(REGISTRY, "just_get_10_core_entity", (_, p, s) -> new BlockJustGet10CoreEntity(p, s))
            .validBlock(BLOCK_JUST_GET_10_CORE)
            .renderer(() -> () -> BlockJustGet10CoreEntityRenderer::new)
            .register();

    public static final BlockEntityTypeEntry<BlockJustGet10DisplayEntity> BLOCK_JUST_GET_10_DISPLAY_ENTITY = REGISTRY
            .blockEntity(REGISTRY, "just_get_10_display_entity", (_, p, s) -> new BlockJustGet10DisplayEntity(p, s))
            .validBlock(BLOCK_JUST_GET_10_DISPLAY)
            .renderer(() -> () -> BlockJustGet10DisplayEntityRenderer::new)
            .register();
}
