package com.simple_block_game.common.simple24Puzzle;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.BaseRotatedRefreshBlock;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleCore;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleCoreEntity;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleDisplay;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleDisplayEntity;

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

public class simple24PuzzleRegistration {

    public static void init() {}

    // ==================== 24 puzzle ====================
    public static final BlockEntry<Block24PuzzleCore> BLOCK_24PUZZLE_CORE = REGISTRATE
            .object("24puzzle_core")
            .block(Block24PuzzleCore::new)
            .blockstate((block, prov) -> registerHorizontalBlock(block.get(), prov, "block/base/rotated_side"))
            .item((a, b) -> new BlockItem(a, b) {

                @Override
                public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                            @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.24puzzle_core.1"));
                    tooltipComponents.add(Component.translatable("tooltip.24puzzle_core.2"));
                    tooltipComponents.add(Component.translatable("tooltip.24puzzle_core.3"));
                    tooltipComponents.add(Component.translatable("tooltip.24puzzle_core.4"));
                    tooltipComponents.add(Component.translatable("tooltip.24puzzle_core.5"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            })
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple24puzzle/24puzzle_core")))
            .tab(TAB_GANM.getKey())
            .build()
            .recipe((ctx, prov) -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ctx.get())
                    .pattern("QQQ").pattern("QIQ").pattern("QQQ")
                    .define('Q', Items.QUARTZ_PILLAR)
                    .define('I', Items.CALCITE)
                    .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                    .save(prov, prov.safeId(ctx.get())))
            .register();

    public static final BlockEntry<Block24PuzzleDisplay> BLOCK_24PUZZLE_DISPLAY = REGISTRATE
            .object("24puzzle_display")
            .block(Block24PuzzleDisplay::new)
            .blockstate((block, prov) -> registerHorizontalBlock(block.get(), prov, "block/base/rotated_center"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("item/simple24puzzle/24puzzle_display")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntry<BaseRotatedRefreshBlock> BLOCK_24PUZZLE_REFRESH = REGISTRATE
            .object("24puzzle_refresh")
            .block(p -> BaseRotatedRefreshBlock.create(p, "simple24puzzle"))
            .blockstate((block, prov) -> registerHorizontalBlock(block.get(), prov, "block/base/rotated_side"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("block/base/rotated_refresh")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_24PUZZLE_CORE_ENTITY = REGISTRATE
            .object("24puzzle_core_entity")
            .blockEntity(REGISTRATE, "24puzzle_core_entity", (b, p, s) -> new Block24PuzzleCoreEntity(p, s))
            .validBlock(BLOCK_24PUZZLE_CORE)
            .register();

    public static final BlockEntityEntry<BlockEntity> BLOCK_24PUZZLE_DISPLAY_ENTITY = REGISTRATE
            .object("24puzzle_display_entity")
            .blockEntity(REGISTRATE, "24puzzle_display_entity", (b, p, s) -> new Block24PuzzleDisplayEntity(p, s))
            .validBlock(BLOCK_24PUZZLE_DISPLAY)
            .register();
}
