package com.simple_block_game.common.simpleSudoku;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.BaseVerticalRefreshBlock;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuCore;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuCoreEntity;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuDisplay;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuDisplayEntity;

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

public class simpleSudokuRegistration {

    public static void init() {}

    // ==================== Sudoku ====================
    public static final BlockEntry<BlockSudokuCore> BLOCK_SUDOKU_CORE = REGISTRATE
            .object("sudoku_core")
            .block(BlockSudokuCore::new)
            .blockstate((block, prov) -> registerVerticalBlock(block.get(), prov, "block/base/vertical_side"))
            .item((a, b) -> new BlockItem(a, b) {

                @Override
                public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                            @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.sudoku_core.1"));
                    tooltipComponents.add(Component.translatable("tooltip.sudoku_core.2"));
                    tooltipComponents.add(Component.translatable("tooltip.sudoku_core.3"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            })
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple_sudoku/sudoku_core")))
            .tab(TAB_GANM.getKey())
            .build()
            .recipe((ctx, prov) -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ctx.get())
                    .pattern("QQQ").pattern("QIQ").pattern("QQQ")
                    .define('Q', Items.QUARTZ_PILLAR)
                    .define('I', Items.BOOK)
                    .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                    .save(prov, prov.safeId(ctx.get())))
            .register();

    public static final BlockEntry<BlockSudokuDisplay> BLOCK_SUDOKU_DISPLAY = REGISTRATE
            .object("sudoku_display")
            .block(BlockSudokuDisplay::new)
            .blockstate((block, prov) -> registerVerticalBlock(block.get(), prov, "block/base/vertical_center"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple_sudoku/sudoku_display")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntry<BaseVerticalRefreshBlock> BLOCK_SUDOKU_REFRESH = REGISTRATE
            .object("sudoku_refresh")
            .block(p -> BaseVerticalRefreshBlock.create(p, "sudoku"))
            .blockstate((block, prov) -> registerVerticalBlock(block.get(), prov, "block/base/vertical_side"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("block/base/vertical_refresh")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_SUDOKU_CORE_ENTITY = REGISTRATE
            .object("sudoku_core_entity")
            .blockEntity(REGISTRATE, "sudoku_core_entity", (b, p, s) -> new BlockSudokuCoreEntity(p, s))
            .validBlock(BLOCK_SUDOKU_CORE)
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_SUDOKU_DISPLAY_ENTITY = REGISTRATE
            .object("sudoku_display_entity")
            .blockEntity(REGISTRATE, "sudoku_display_entity", (b, p, s) -> new BlockSudokuDisplayEntity(p, s))
            .validBlock(BLOCK_SUDOKU_DISPLAY)
            .register();
}
