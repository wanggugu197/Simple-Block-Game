package com.simple_block_game.common.simpleMemoryKey;

import com.simple_block_game.common.base.block.BaseVerticalRefreshBlock;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyButton;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyButtonEntity;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyCore;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyCoreEntity;
import com.simple_block_game.common.simpleMemoryKey.renderer.BlockMemoryKeyButtonEntityRenderer;
import com.simple_block_game.common.simpleMemoryKey.renderer.BlockMemoryKeyCoreEntityRenderer;

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

public class simpleMemoryKeyRegistration {

    public static void init() {
        REGISTRYLIB.addRecipeData(prov -> {
            prov.shaped(RecipeCategory.COMBAT, BLOCK_MEMORY_KEY_CORE)
                    .pattern("QQQ")
                    .pattern("QIQ")
                    .pattern("QQQ")
                    .define('Q', Items.QUARTZ_PILLAR)
                    .define('I', Items.REDSTONE_BLOCK)
                    .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                    .save(prov, "make_memory_key_core");
        });
    }

    // memory key
    public static final BlockEntry<BlockMemoryKeyCore> BLOCK_MEMORY_KEY_CORE = REGISTRYLIB
            .block(REGISTRYLIB, "memory_key_core", BlockMemoryKeyCore::new)
            .langCn("记忆键核心方块")
            .lang("Memory Key Core")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_center"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_memory_key/memory_key_core_all_success"))).addTooltip((collector, _) -> {
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.memory_key_core.1")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.memory_key_core.2")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.memory_key_core.3")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.memory_key_core.4")));
                        collector.node(new SubNode.Basic(Component.translatable("tooltip.memory_key_core.5")));
                    }))
            .register();

    public static final BlockEntry<BlockMemoryKeyButton> BLOCK_MEMORY_KEY_BUTTON = REGISTRYLIB
            .block(REGISTRYLIB, "memory_key_button", BlockMemoryKeyButton::new)
            .langCn("记忆键按钮方块")
            .lang("Memory Key Button")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_center"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_memory_key/memory_key_button_all"))))
            .register();

    public static final BlockEntry<BaseVerticalRefreshBlock> BLOCK_MEMORY_KEY_REFRESH = REGISTRYLIB
            .block(REGISTRYLIB, "memory_key_refresh", p -> BaseVerticalRefreshBlock.create(p, "memory_key"))
            .langCn("记忆键控制方块")
            .lang("Memory Key Refresh")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/vertical_refresh"))))
            .register();

    public static final BlockEntityTypeEntry<BlockMemoryKeyCoreEntity> BLOCK_MEMORY_KEY_CORE_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "memory_key_core_entity", (_, p, s) -> new BlockMemoryKeyCoreEntity(p, s))
            .validBlock(BLOCK_MEMORY_KEY_CORE)
            .renderer(() -> () -> BlockMemoryKeyCoreEntityRenderer::new)
            .register();

    public static final BlockEntityTypeEntry<BlockMemoryKeyButtonEntity> BLOCK_MEMORY_KEY_BUTTON_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "memory_key_button_entity", (_, p, s) -> new BlockMemoryKeyButtonEntity(p, s))
            .validBlock(BLOCK_MEMORY_KEY_BUTTON)
            .renderer(() -> () -> BlockMemoryKeyButtonEntityRenderer::new)
            .register();
}
