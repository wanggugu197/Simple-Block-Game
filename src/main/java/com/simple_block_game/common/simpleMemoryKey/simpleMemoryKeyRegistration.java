package com.simple_block_game.common.simpleMemoryKey;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.BaseVerticalRefreshBlock;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyButton;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyButtonEntity;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyCore;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyCoreEntity;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.simple_block_game.common.SimpleBlockGameRecipe.UNCONDITIONAL_CRITERION;
import static com.simple_block_game.common.SimpleBlockGameRegistration.TAB_GANM;
import static com.simple_block_game.registry.SimpleBlockGameRegistration.REGISTRATE;
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.registerVerticalBlock;

public class simpleMemoryKeyRegistration {

    public static void init() {}

    // ==================== Memory Key ====================
    public static final BlockEntry<BlockMemoryKeyCore> BLOCK_MEMORY_KEY_CORE = REGISTRATE
            .object("memory_key_core")
            .block(BlockMemoryKeyCore::new)
            .blockstate((block, prov) -> registerVerticalBlock(block.get(), prov, "block/base/vertical_center"))
            .item((a, b) -> new BlockItem(a, b) {

                @Override
                public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                            @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.memory_key_core.1"));
                    tooltipComponents.add(Component.translatable("tooltip.memory_key_core.2"));
                    tooltipComponents.add(Component.translatable("tooltip.memory_key_core.3"));
                    tooltipComponents.add(Component.translatable("tooltip.memory_key_core.4"));
                    tooltipComponents.add(Component.translatable("tooltip.memory_key_core.5"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            })
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple_memory_key/memory_key_core_all_success")))
            .tab(TAB_GANM.getKey())
            .build()
            .recipe((ctx, prov) -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ctx.get())
                    .pattern("QQQ").pattern("QIQ").pattern("QQQ")
                    .define('Q', Items.QUARTZ_PILLAR)
                    .define('I', Items.REDSTONE_BLOCK)
                    .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                    .save(prov, prov.safeId(ctx.get())))
            .register();

    public static final BlockEntry<BlockMemoryKeyButton> BLOCK_MEMORY_KEY_BUTTON = REGISTRATE
            .object("memory_key_button")
            .block(BlockMemoryKeyButton::new)
            .blockstate((block, prov) -> registerVerticalBlock(block.get(), prov, "block/base/vertical_center"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple_memory_key/memory_key_button_all")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntry<BaseVerticalRefreshBlock> BLOCK_MEMORY_KEY_REFRESH = REGISTRATE
            .object("memory_key_refresh")
            .block(p -> BaseVerticalRefreshBlock.create(p, "memory_key"))
            .blockstate((block, prov) -> registerVerticalBlock(block.get(), prov, "block/base/vertical_side"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("block/base/vertical_refresh")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_MEMORY_KEY_CORE_ENTITY = REGISTRATE
            .object("memory_key_core_entity")
            .blockEntity(REGISTRATE, "memory_key_core_entity", (b, p, s) -> new BlockMemoryKeyCoreEntity(p, s))
            .validBlock(BLOCK_MEMORY_KEY_CORE)
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_MEMORY_KEY_BUTTON_ENTITY = REGISTRATE
            .object("memory_key_button_entity")
            .blockEntity(REGISTRATE, "memory_key_button_entity", (b, p, s) -> new BlockMemoryKeyButtonEntity(p, s))
            .validBlock(BLOCK_MEMORY_KEY_BUTTON)
            .register();
}
