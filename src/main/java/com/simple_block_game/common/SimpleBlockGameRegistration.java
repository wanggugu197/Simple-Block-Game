package com.simple_block_game.common;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.common.base.block.RotatedFrame;
import com.simple_block_game.common.base.block.VerticalFrame;
import com.simple_block_game.common.simple2048.simple2048Registration;
import com.simple_block_game.common.simple24Puzzle.simple24PuzzleRegistration;
import com.simple_block_game.common.simpleJustGet10.simpleJustGet10Registration;
import com.simple_block_game.common.simpleMemoryKey.simpleMemoryKeyRegistration;
import com.simple_block_game.common.simpleMinesweeper.simpleMinesweeperRegistration;
import com.simple_block_game.common.simpleSudoku.simpleSudokuRegistration;
import com.simple_block_game.common.simpleTenDrops.simpleTenDropsRegistration;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import static com.simple_block_game.registry.SimpleBlockGameRegistration.REGISTRATE;
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.registerHorizontalBlock;
import static com.simple_block_game.util.generator.ModBlockModelGeneratorHelper.registerVerticalBlock;

/**
 * 方块和实体注册类
 * 渲染器注册已移至 ClientInit.java，使用延迟加载机制避免服务端加载客户端类
 */
public class SimpleBlockGameRegistration {

    public static void init() {
        simple2048Registration.init();
        simpleMinesweeperRegistration.init();
        simpleMemoryKeyRegistration.init();
        simpleTenDropsRegistration.init();
        simpleSudokuRegistration.init();
        simple24PuzzleRegistration.init();
        simpleJustGet10Registration.init();
    }

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

    // ==================== Refresh Entity (统一的刷新实体) ====================
    public static final BlockEntityEntry<BlockEntity> BLOCK_REFRESH_ENTITY = REGISTRATE
            .object("refresh_entity")
            .blockEntity(REGISTRATE, "refresh_entity", (b, p, s) -> new BlockRefreshEntity(p, s))
            .validBlock(simple2048Registration.BLOCK_2048_REFRESH)
            .validBlock(simpleMinesweeperRegistration.BLOCK_MINESWEEPER_REFRESH)
            .validBlock(simpleMemoryKeyRegistration.BLOCK_MEMORY_KEY_REFRESH)
            .validBlock(simpleTenDropsRegistration.BLOCK_TEN_DROPS_REFRESH)
            .validBlock(simpleSudokuRegistration.BLOCK_SUDOKU_REFRESH)
            .validBlock(simple24PuzzleRegistration.BLOCK_24PUZZLE_REFRESH)
            .validBlock(simpleJustGet10Registration.BLOCK_JUST_GET_10_REFRESH)
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
