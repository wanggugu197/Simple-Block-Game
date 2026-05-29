package com.simple_block_game.common;

import com.simple_block_game.common.base.block.*;
import com.simple_block_game.common.base.renderer.BlockRefreshEntityRenderer;
import com.simple_block_game.common.simple2048.block.Block2048Core;
import com.simple_block_game.common.simple2048.block.Block2048CoreEntity;
import com.simple_block_game.common.simple2048.block.Block2048Display;
import com.simple_block_game.common.simple2048.block.Block2048DisplayEntity;
import com.simple_block_game.common.simple2048.renderer.Block2048CoreEntityRenderer;
import com.simple_block_game.common.simple2048.renderer.Block2048DisplayEntityRenderer;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyButton;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyButtonEntity;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyCore;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyCoreEntity;
import com.simple_block_game.common.simpleMemoryKey.renderer.BlockMemoryKeyButtonEntityRenderer;
import com.simple_block_game.common.simpleMemoryKey.renderer.BlockMemoryKeyCoreEntityRenderer;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCore;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCoreEntity;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperDisplay;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperDisplayEntity;
import com.simple_block_game.common.simpleMinesweeper.renderer.BlockMinesweeperCoreEntityRenderer;
import com.simple_block_game.common.simpleMinesweeper.renderer.BlockMinesweeperDisplayEntityRenderer;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsCore;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsCoreEntity;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsDisplay;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsDisplayEntity;
import com.simple_block_game.common.simpleTenDrops.renderer.BlockTenDropsCoreEntityRenderer;
import com.simple_block_game.common.simpleTenDrops.renderer.BlockTenDropsDisplayEntityRenderer;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import com.gto.registrylib.util.entry.BlockEntityTypeEntry;
import com.gto.registrylib.util.entry.BlockEntry;
import com.gto.registrylib.util.entry.ItemEntry;
import com.gto.registrylib.util.entry.RegistryEntry;

import java.util.Map;

import static com.simple_block_game.SimpleBlockGame.REGISTRYLIB;
import static com.simple_block_game.registry.generator.ModBlockModelGeneratorHelpper.createHorizontalBlock;
import static com.simple_block_game.registry.generator.ModBlockModelGeneratorHelpper.createVerticalBlock;

/**
 * 方块和实体注册类
 */
public class SimpleBlockGameRegistration {

    public static void init() {}

    public static final ItemEntry<Item> COPPER_COIN = REGISTRYLIB
            .item("simple_block_game", Item::new)
            .langCn("简单方块游戏")
            .lang("Simple Block Game")
            .defaultModel()
            .register();

    // 创造模式标签注册
    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> TAB_GANM = REGISTRYLIB
            .creativeTab("simple_block_game_tab", "Simple Block Game", Map.of("zh_cn", "简单方块游戏"), builder -> {
                builder.icon(COPPER_COIN::asStack);
                builder.displayItems((_, output) -> output.accept(COPPER_COIN.asStack()));
            });

    // 2048
    public static final BlockEntry<Block2048Core> BLOCK_2048_CORE = REGISTRYLIB
            .block(REGISTRYLIB, "2048_core", Block2048Core::new)
            .langCn("2048 核心方块")
            .lang("2048 Core")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple2048/2048_core_open"))))
            .register();
    public static final BlockEntry<Block2048Display> BLOCK_2048_DISPLAY = REGISTRYLIB
            .block(REGISTRYLIB, "2048_display", Block2048Display::new)
            .langCn("2048 显示方块")
            .lang("2048 Display")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_center"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple2048/2048_display_2048"))))
            .register();
    public static final BlockEntry<BaseRotatedRefreshBlock> BLOCK_2048_REFRESH = REGISTRYLIB
            .block(REGISTRYLIB, "2048_refresh", p -> BaseRotatedRefreshBlock.create(p, "simple2048"))
            .langCn("2048 控制方块")
            .lang("2048 Refresh")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/rotated_refresh"))))
            .register();

    public static final BlockEntityTypeEntry<Block2048CoreEntity> BLOCK_2048_CORE_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "2048_core_entity", (_, p, s) -> new Block2048CoreEntity(p, s))
            .validBlock(BLOCK_2048_CORE)
            .renderer(() -> Block2048CoreEntityRenderer::new)
            .register();
    public static final BlockEntityTypeEntry<Block2048DisplayEntity> BLOCK_2048_DISPLAY_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "2048_display_entity", (_, p, s) -> new Block2048DisplayEntity(p, s))
            .validBlock(BLOCK_2048_DISPLAY)
            .renderer(() -> Block2048DisplayEntityRenderer::new)
            .register();

    // minesweeper
    public static final BlockEntry<BlockMinesweeperCore> BLOCK_MINESWEEPER_CORE = REGISTRYLIB
            .block(REGISTRYLIB, "minesweeper_core", BlockMinesweeperCore::new)
            .langCn("扫雷核心方块")
            .lang("Minesweeper Core")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_minesweeper/minesweeper_core"))))
            .register();
    public static final BlockEntry<BlockMinesweeperDisplay> BLOCK_MINESWEEPER_DISPLAY = REGISTRYLIB
            .block(REGISTRYLIB, "minesweeper_display", BlockMinesweeperDisplay::new)
            .langCn("扫雷显示方块")
            .lang("Minesweeper Display")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_center"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_minesweeper/minesweeper_display"))))
            .register();
    public static final BlockEntry<BaseVerticalRefreshBlock> BLOCK_MINESWEEPER_REFRESH = REGISTRYLIB
            .block(REGISTRYLIB, "minesweeper_refresh", p -> BaseVerticalRefreshBlock.create(p, "minesweeper"))
            .langCn("扫雷控制方块")
            .lang("Minesweeper Refresh")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/vertical_refresh"))))
            .register();

    public static final BlockEntityTypeEntry<BlockMinesweeperCoreEntity> BLOCK_MINESWEEPER_CORE_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "minesweeper_core_entity", (_, p, s) -> new BlockMinesweeperCoreEntity(p, s))
            .validBlock(BLOCK_MINESWEEPER_CORE)
            .renderer(() -> BlockMinesweeperCoreEntityRenderer::new)
            .register();
    public static final BlockEntityTypeEntry<BlockMinesweeperDisplayEntity> BLOCK_MINESWEEPER_DISPLAY_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "minesweeper_display_entity", (_, p, s) -> new BlockMinesweeperDisplayEntity(p, s))
            .validBlock(BLOCK_MINESWEEPER_DISPLAY)
            .renderer(() -> BlockMinesweeperDisplayEntityRenderer::new)
            .register();

    // memory key
    public static final BlockEntry<BlockMemoryKeyCore> BLOCK_MEMORY_KEY_CORE = REGISTRYLIB
            .block(REGISTRYLIB, "memory_key_core", BlockMemoryKeyCore::new)
            .langCn("记忆键核心方块")
            .lang("Memory Key Core")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_center"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_memory_key/memory_key_core_all_success"))))
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
            .renderer(() -> BlockMemoryKeyCoreEntityRenderer::new)
            .register();
    public static final BlockEntityTypeEntry<BlockMemoryKeyButtonEntity> BLOCK_MEMORY_KEY_BUTTON_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "memory_key_button_entity", (_, p, s) -> new BlockMemoryKeyButtonEntity(p, s))
            .validBlock(BLOCK_MEMORY_KEY_BUTTON)
            .renderer(() -> BlockMemoryKeyButtonEntityRenderer::new)
            .register();

    // ten drop
    public static final BlockEntry<BlockTenDropsCore> BLOCK_TEN_DROPS_CORE = REGISTRYLIB
            .block(REGISTRYLIB, "ten_drops_core", BlockTenDropsCore::new)
            .langCn("十滴水核心方块")
            .lang("Ten Drops Core")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_ten_drops/ten_drops_core"))))
            .register();
    public static final BlockEntry<BlockTenDropsDisplay> BLOCK_TEN_DROPS_DISPLAY = REGISTRYLIB
            .block(REGISTRYLIB, "ten_drops_display", BlockTenDropsDisplay::new)
            .langCn("十滴水显示方块")
            .lang("Ten Drops Display")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_center"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_ten_drops/ten_drops_display"))))
            .register();
    public static final BlockEntry<BaseVerticalRefreshBlock> BLOCK_TEN_DROPS_REFRESH = REGISTRYLIB
            .block(REGISTRYLIB, "ten_drops_refresh", p -> BaseVerticalRefreshBlock.create(p, "ten_drops"))
            .langCn("十滴水控制方块")
            .lang("Ten Drops Refresh")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/vertical_refresh"))))
            .register();

    public static final BlockEntityTypeEntry<BlockTenDropsCoreEntity> BLOCK_TEN_DROPS_CORE_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "ten_drops_core_entity", (_, p, s) -> new BlockTenDropsCoreEntity(p, s))
            .validBlock(BLOCK_TEN_DROPS_CORE)
            .renderer(() -> BlockTenDropsCoreEntityRenderer::new)
            .register();
    public static final BlockEntityTypeEntry<BlockTenDropsDisplayEntity> BLOCK_TEN_DROPS_DISPLAY_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "ten_drops_display_entity", (_, p, s) -> new BlockTenDropsDisplayEntity(p, s))
            .validBlock(BLOCK_TEN_DROPS_DISPLAY)
            .renderer(() -> BlockTenDropsDisplayEntityRenderer::new)
            .register();

    // 统一的刷新实体，绑定所有四个刷新方块
    public static final BlockEntityTypeEntry<BlockRefreshEntity> BLOCK_REFRESH_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "refresh_entity", BlockRefreshEntity::new)
            .validBlock(BLOCK_2048_REFRESH)
            .validBlock(BLOCK_MINESWEEPER_REFRESH)
            .validBlock(BLOCK_MEMORY_KEY_REFRESH)
            .validBlock(BLOCK_TEN_DROPS_REFRESH)
            .renderer(() -> BlockRefreshEntityRenderer::new)
            .register();

    // 框架方块
    public static final BlockEntry<RotatedFrame> BLOCK_ROTATED_FRAME = REGISTRYLIB
            .block(REGISTRYLIB, "rotated_frame", RotatedFrame::new)
            .langCn("框架方块")
            .lang("Frame")
            .blockstate(() -> (block, prov) -> createHorizontalBlock(block, prov, "block/base/rotated_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/rotated_side"))))
            .register();

    public static final BlockEntry<VerticalFrame> BLOCK_VERTICAL_FRAME = REGISTRYLIB
            .block(REGISTRYLIB, "vertical_frame", VerticalFrame::new)
            .langCn("框架方块")
            .lang("Frame")
            .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
            .item(builder -> builder.addTab(TAB_GANM.getKey())
                    .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("block/base/vertical_side"))))
            .register();
}
