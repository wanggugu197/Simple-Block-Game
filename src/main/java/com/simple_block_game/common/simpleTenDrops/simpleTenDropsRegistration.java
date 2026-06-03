package com.simple_block_game.common.simpleTenDrops;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.BaseVerticalRefreshBlock;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsCore;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsCoreEntity;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsDisplay;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsDisplayEntity;

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
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.registerVerticalBlock;

public class simpleTenDropsRegistration {

    public static void init() {}

    // ==================== Ten Drops ====================
    public static final BlockEntry<BlockTenDropsCore> BLOCK_TEN_DROPS_CORE = REGISTRATE
            .object("ten_drops_core")
            .block(BlockTenDropsCore::new)
            .blockstate((block, prov) -> registerVerticalBlock(block.get(), prov, "block/base/vertical_side"))
            .item((a, b) -> new BlockItem(a, b) {

                @Override
                public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                            @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.ten_drops_core.1"));
                    tooltipComponents.add(Component.translatable("tooltip.ten_drops_core.2"));
                    tooltipComponents.add(Component.translatable("tooltip.ten_drops_core.3"));
                    tooltipComponents.add(Component.translatable("tooltip.ten_drops_core.4"));
                    tooltipComponents.add(Component.translatable("tooltip.ten_drops_core.5"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            })
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple_ten_drops/ten_drops_core")))
            .tab(TAB_GANM.getKey())
            .build()
            .recipe((ctx, prov) -> net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ctx.get())
                    .pattern("QQQ").pattern("QIQ").pattern("QQQ")
                    .define('Q', Items.QUARTZ_PILLAR)
                    .define('I', Items.WATER_BUCKET)
                    .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                    .save(prov, prov.safeId(ctx.get())))
            .register();

    public static final BlockEntry<BlockTenDropsDisplay> BLOCK_TEN_DROPS_DISPLAY = REGISTRATE
            .object("ten_drops_display")
            .block(BlockTenDropsDisplay::new)
            .blockstate((block, prov) -> registerVerticalBlock(block.get(), prov, "block/base/vertical_center"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple_ten_drops/ten_drops_display")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntry<BaseVerticalRefreshBlock> BLOCK_TEN_DROPS_REFRESH = REGISTRATE
            .object("ten_drops_refresh")
            .block(p -> BaseVerticalRefreshBlock.create(p, "ten_drops"))
            .blockstate((block, prov) -> registerVerticalBlock(block.get(), prov, "block/base/vertical_side"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("block/base/vertical_refresh")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_TEN_DROPS_CORE_ENTITY = REGISTRATE
            .object("ten_drops_core_entity")
            .blockEntity(REGISTRATE, "ten_drops_core_entity", (b, p, s) -> new BlockTenDropsCoreEntity(p, s))
            .validBlock(BLOCK_TEN_DROPS_CORE)
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_TEN_DROPS_DISPLAY_ENTITY = REGISTRATE
            .object("ten_drops_display_entity")
            .blockEntity(REGISTRATE, "ten_drops_display_entity", (b, p, s) -> new BlockTenDropsDisplayEntity(p, s))
            .validBlock(BLOCK_TEN_DROPS_DISPLAY)
            .register();
}
