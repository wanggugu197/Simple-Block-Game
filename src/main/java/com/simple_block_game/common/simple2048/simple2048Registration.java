package com.simple_block_game.common.simple2048;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.BaseRotatedRefreshBlock;
import com.simple_block_game.common.simple2048.block.Block2048Core;
import com.simple_block_game.common.simple2048.block.Block2048CoreEntity;
import com.simple_block_game.common.simple2048.block.Block2048Display;
import com.simple_block_game.common.simple2048.block.Block2048DisplayEntity;

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
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.registerHorizontalBlock;

public class simple2048Registration {

    public static void init() {}

    // ==================== 2048 ====================
    public static final BlockEntry<Block2048Core> BLOCK_2048_CORE = REGISTRATE
            .object("2048_core")
            .block(Block2048Core::new)
            .blockstate((block, prov) -> registerHorizontalBlock(block.get(), prov, "block/base/rotated_side"))
            .item((a, b) -> new BlockItem(a, b) {

                @Override
                public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                            @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.2048_core.1"));
                    tooltipComponents.add(Component.translatable("tooltip.2048_core.2"));
                    tooltipComponents.add(Component.translatable("tooltip.2048_core.3"));
                    tooltipComponents.add(Component.translatable("tooltip.2048_core.4"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            })
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple2048/2048_core_open")))
            .tab(TAB_GANM.getKey())
            .build()
            .recipe((ctx, prov) -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ctx.get())
                    .pattern("QQQ").pattern("QIQ").pattern("QQQ")
                    .define('Q', Items.QUARTZ_PILLAR)
                    .define('I', Items.IRON_BLOCK)
                    .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                    .save(prov, prov.safeId(ctx.get())))
            .register();

    public static final BlockEntry<Block2048Display> BLOCK_2048_DISPLAY = REGISTRATE
            .object("2048_display")
            .block(Block2048Display::new)
            .blockstate((block, prov) -> registerHorizontalBlock(block.get(), prov, "block/base/rotated_center"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple2048/2048_display_2048")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntry<BaseRotatedRefreshBlock> BLOCK_2048_REFRESH = REGISTRATE
            .object("2048_refresh")
            .block(p -> BaseRotatedRefreshBlock.create(p, "simple2048"))
            .blockstate((block, prov) -> registerHorizontalBlock(block.get(), prov, "block/base/rotated_side"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("block/base/rotated_refresh")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_2048_CORE_ENTITY = REGISTRATE
            .object("2048_core_entity")
            .blockEntity(REGISTRATE, "2048_core_entity", (b, p, s) -> new Block2048CoreEntity(p, s))
            .validBlock(BLOCK_2048_CORE)
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_2048_DISPLAY_ENTITY = REGISTRATE
            .object("2048_display_entity")
            .blockEntity(REGISTRATE, "2048_display_entity", (b, p, s) -> new Block2048DisplayEntity(p, s))
            .validBlock(BLOCK_2048_DISPLAY)
            .register();
}
