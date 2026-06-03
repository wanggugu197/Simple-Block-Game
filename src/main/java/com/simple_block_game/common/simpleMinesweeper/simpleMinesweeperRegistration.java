package com.simple_block_game.common.simpleMinesweeper;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.BaseVerticalRefreshBlock;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCore;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCoreEntity;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperDisplay;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperDisplayEntity;

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

public class simpleMinesweeperRegistration {

    public static void init() {}

    // ==================== Minesweeper ====================
    public static final BlockEntry<BlockMinesweeperCore> BLOCK_MINESWEEPER_CORE = REGISTRATE
            .object("minesweeper_core")
            .block(BlockMinesweeperCore::new)
            .blockstate((block, prov) -> registerVerticalBlock(block.get(), prov, "block/base/vertical_side"))
            .item((a, b) -> new BlockItem(a, b) {

                @Override
                public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                            @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.minesweeper_core.1"));
                    tooltipComponents.add(Component.translatable("tooltip.minesweeper_core.2"));
                    tooltipComponents.add(Component.translatable("tooltip.minesweeper_core.3"));
                    tooltipComponents.add(Component.translatable("tooltip.minesweeper_core.4"));
                    tooltipComponents.add(Component.translatable("tooltip.minesweeper_core.5"));
                    tooltipComponents.add(Component.translatable("tooltip.minesweeper_core.6"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            })
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple_minesweeper/minesweeper_core")))
            .tab(TAB_GANM.getKey())
            .build()
            .recipe((ctx, prov) -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ctx.get())
                    .pattern("QQQ").pattern("QIQ").pattern("QQQ")
                    .define('Q', Items.QUARTZ_PILLAR)
                    .define('I', Items.TNT)
                    .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                    .save(prov, prov.safeId(ctx.get())))
            .register();

    public static final BlockEntry<BlockMinesweeperDisplay> BLOCK_MINESWEEPER_DISPLAY = REGISTRATE
            .object("minesweeper_display")
            .block(BlockMinesweeperDisplay::new)
            .blockstate((block, prov) -> registerVerticalBlock(block.get(), prov, "block/base/vertical_center"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple_minesweeper/minesweeper_display")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntry<BaseVerticalRefreshBlock> BLOCK_MINESWEEPER_REFRESH = REGISTRATE
            .object("minesweeper_refresh")
            .block(p -> BaseVerticalRefreshBlock.create(p, "minesweeper"))
            .blockstate((block, prov) -> registerVerticalBlock(block.get(), prov, "block/base/vertical_side"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("block/base/vertical_refresh")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_MINESWEEPER_CORE_ENTITY = REGISTRATE
            .object("minesweeper_core_entity")
            .blockEntity(REGISTRATE, "minesweeper_core_entity", (b, p, s) -> new BlockMinesweeperCoreEntity(p, s))
            .validBlock(BLOCK_MINESWEEPER_CORE)
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_MINESWEEPER_DISPLAY_ENTITY = REGISTRATE
            .object("minesweeper_display_entity")
            .blockEntity(REGISTRATE, "minesweeper_display_entity", (b, p, s) -> new BlockMinesweeperDisplayEntity(p, s))
            .validBlock(BLOCK_MINESWEEPER_DISPLAY)
            .register();
}
