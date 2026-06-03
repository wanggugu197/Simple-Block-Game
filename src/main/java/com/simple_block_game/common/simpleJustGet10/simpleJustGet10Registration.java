package com.simple_block_game.common.simpleJustGet10;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.BaseRotatedRefreshBlock;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10Core;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10CoreEntity;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10Display;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10DisplayEntity;

import net.minecraft.data.recipes.RecipeCategory;
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
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.registerHorizontalBlock;

public class simpleJustGet10Registration {

    public static void init() {}

    public static final BlockEntry<BlockJustGet10Core> BLOCK_JUST_GET_10_CORE = REGISTRATE
            .object("just_get_10_core")
            .block(BlockJustGet10Core::new)
            .blockstate((block, prov) -> registerHorizontalBlock(block.get(), prov, "block/base/rotated_side"))
            .item((a, b) -> new BlockItem(a, b) {

                @Override
                public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                            @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.just_get_10_core.1"));
                    tooltipComponents.add(Component.translatable("tooltip.just_get_10_core.2"));
                    tooltipComponents.add(Component.translatable("tooltip.just_get_10_core.3"));
                    tooltipComponents.add(Component.translatable("tooltip.just_get_10_core.4"));
                    tooltipComponents.add(Component.translatable("tooltip.just_get_10_core.5"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            })
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple_just_get_10/just_get_10_core")))
            .tab(TAB_GANM.getKey())
            .build()
            .recipe((ctx, prov) -> net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ctx.get())
                    .pattern("QQQ").pattern("QIQ").pattern("QQQ")
                    .define('Q', Items.QUARTZ_PILLAR)
                    .define('I', Items.CRAFTING_TABLE)
                    .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                    .save(prov, prov.safeId(ctx.get())))
            .register();

    public static final BlockEntry<BlockJustGet10Display> BLOCK_JUST_GET_10_DISPLAY = REGISTRATE
            .object("just_get_10_display")
            .block(BlockJustGet10Display::new)
            .blockstate((block, prov) -> registerHorizontalBlock(block.get(), prov, "block/base/rotated_center"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple_just_get_10/just_get_10_display")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntry<BaseRotatedRefreshBlock> BLOCK_JUST_GET_10_REFRESH = REGISTRATE
            .object("just_get_10_refresh")
            .block(p -> BaseRotatedRefreshBlock.create(p, "just_get_10"))
            .blockstate((block, prov) -> registerHorizontalBlock(block.get(), prov, "block/base/rotated_side"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("block/base/rotated_refresh")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_JUST_GET_10_CORE_ENTITY = REGISTRATE
            .object("just_get_10_core_entity")
            .blockEntity(REGISTRATE, "just_get_10_core_entity", (b, p, s) -> new BlockJustGet10CoreEntity(p, s))
            .validBlock(BLOCK_JUST_GET_10_CORE)
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_JUST_GET_10_DISPLAY_ENTITY = REGISTRATE
            .object("just_get_10_display_entity")
            .blockEntity(REGISTRATE, "just_get_10_display_entity", (b, p, s) -> new BlockJustGet10DisplayEntity(p, s))
            .validBlock(BLOCK_JUST_GET_10_DISPLAY)
            .register();
}
