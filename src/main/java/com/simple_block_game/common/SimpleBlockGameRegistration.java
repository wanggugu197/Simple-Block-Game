package com.simple_block_game.common;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.*;
import com.simple_block_game.common.simple2048.block.Block2048Core;
import com.simple_block_game.common.simple2048.block.Block2048CoreEntity;
import com.simple_block_game.common.simple2048.block.Block2048Display;
import com.simple_block_game.common.simple2048.block.Block2048DisplayEntity;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleCore;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleCoreEntity;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleDisplay;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleDisplayEntity;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyButton;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyButtonEntity;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyCore;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyCoreEntity;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCore;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCoreEntity;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperDisplay;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperDisplayEntity;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuCore;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuCoreEntity;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuDisplay;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuDisplayEntity;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsCore;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsCoreEntity;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsDisplay;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsDisplayEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.simple_block_game.common.SimpleBlockGameRecipe.UNCONDITIONAL_CRITERION;
import static com.simple_block_game.registry.SimpleBlockGameRegistration.REGISTRATE;
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.registerHorizontalBlock;
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.registerVerticalBlock;

/**
 * 方块和实体注册类
 * 渲染器注册已移至 ClientInit.java，使用延迟加载机制避免服务端加载客户端类
 */
public class SimpleBlockGameRegistration {

    public static void init() {}

    public static ItemEntry<Item> SIMPLE_BLOCK_GAME = REGISTRATE
            .object("simple_block_game")
            .item(Item::new)
            .register();

    // 创造模式标签注册
    public static RegistryEntry<CreativeModeTab, CreativeModeTab> TAB_GANM = REGISTRATE
            .object("game")
            .generic(Registries.CREATIVE_MODE_TAB, () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.simple_block_game.simple_block_game_tab"))
                    .icon(() -> SIMPLE_BLOCK_GAME.asStack())
                    .displayItems((params, output) -> output.accept(SIMPLE_BLOCK_GAME))
                    .build())
            .register();

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

    // ==================== Refresh Entity (统一的刷新实体) ====================
    public static final BlockEntityEntry<BlockEntity> BLOCK_REFRESH_ENTITY = REGISTRATE
            .object("refresh_entity")
            .blockEntity(REGISTRATE, "refresh_entity", (b, p, s) -> new BlockRefreshEntity(p, s))
            .validBlock(BLOCK_2048_REFRESH)
            .validBlock(BLOCK_MINESWEEPER_REFRESH)
            .validBlock(BLOCK_MEMORY_KEY_REFRESH)
            .validBlock(BLOCK_TEN_DROPS_REFRESH)
            .validBlock(BLOCK_SUDOKU_REFRESH)
            .validBlock(BLOCK_24PUZZLE_REFRESH)
            .register();

    // ==================== 框架方块 ====================
    public static final BlockEntry<RotatedFrame> BLOCK_ROTATED_FRAME = REGISTRATE
            .object("rotated_frame")
            .block(RotatedFrame::new)
            .lang("Frame")
            .blockstate((block, prov) -> registerHorizontalBlock(block.get(), prov, "block/base/rotated_side"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("block/base/rotated_side")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();

    public static final BlockEntry<VerticalFrame> BLOCK_VERTICAL_FRAME = REGISTRATE
            .object("vertical_frame")
            .block(VerticalFrame::new)
            .lang("Frame")
            .blockstate((block, prov) -> registerVerticalBlock(block.get(), prov, "block/base/vertical_side"))
            .item(BlockItem::new)
            .model((item, prov) -> prov.withExistingParent(item.getName(), SimpleBlockGame.getId("block/base/vertical_side")))
            .tab(TAB_GANM.getKey())
            .build()
            .register();
}
