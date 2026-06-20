package com.simple_block_game.common.simpleTenDrops;

import com.simple_block_game.common.base.block.BaseVerticalRefreshBlock;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsCore;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsCoreEntity;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsDisplay;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsDisplayEntity;
import com.simple_block_game.common.simpleTenDrops.renderer.BlockTenDropsCoreEntityRenderer;
import com.simple_block_game.common.simpleTenDrops.renderer.BlockTenDropsDisplayEntityRenderer;

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

public class simpleTenDropsRegistration {

    public static void init() {
        REGISTRY.addRecipeData(prov -> prov.shaped(RecipeCategory.COMBAT, BLOCK_TEN_DROPS_CORE)
                .pattern("QQQ")
                .pattern("QIQ")
                .pattern("QQQ")
                .define('Q', Items.QUARTZ_PILLAR)
                .define('I', Items.WATER_BUCKET)
                .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                .save(prov, "make_ten_drops_core"));
    }

    // ten drop
    public static final BlockEntry<BlockTenDropsCore> BLOCK_TEN_DROPS_CORE = REGISTRY
            .block(REGISTRY, "ten_drops_core", BlockTenDropsCore::new)
            .langCn("十滴水核心方块")
            .lang("Ten Drops Core")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_ten_drops/ten_drops_core"))).addTooltip((collector, _) -> {
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.ten_drops_core.1")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.ten_drops_core.2")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.ten_drops_core.3")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.ten_drops_core.4")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.ten_drops_core.5")));
                    }))
            .register();
    public static final BlockEntry<BlockTenDropsDisplay> BLOCK_TEN_DROPS_DISPLAY = REGISTRY
            .block(REGISTRY, "ten_drops_display", BlockTenDropsDisplay::new)
            .langCn("十滴水显示方块")
            .lang("Ten Drops Display")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_center"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_ten_drops/ten_drops_display"))))
            .register();
    public static final BlockEntry<BaseVerticalRefreshBlock> BLOCK_TEN_DROPS_REFRESH = REGISTRY
            .block(REGISTRY, "ten_drops_refresh", p -> BaseVerticalRefreshBlock.create(p, "ten_drops"))
            .langCn("十滴水控制方块")
            .lang("Ten Drops Refresh")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/vertical_refresh"))))
            .register();

    public static final BlockEntityTypeEntry<BlockTenDropsCoreEntity> BLOCK_TEN_DROPS_CORE_ENTITY = REGISTRY
            .blockEntity(REGISTRY, "ten_drops_core_entity", (_, p, s) -> new BlockTenDropsCoreEntity(p, s))
            .validBlock(BLOCK_TEN_DROPS_CORE)
            .renderer(() -> () -> BlockTenDropsCoreEntityRenderer::new)
            .register();
    public static final BlockEntityTypeEntry<BlockTenDropsDisplayEntity> BLOCK_TEN_DROPS_DISPLAY_ENTITY = REGISTRY
            .blockEntity(REGISTRY, "ten_drops_display_entity", (_, p, s) -> new BlockTenDropsDisplayEntity(p, s))
            .validBlock(BLOCK_TEN_DROPS_DISPLAY)
            .renderer(() -> () -> BlockTenDropsDisplayEntityRenderer::new)
            .register();
}
