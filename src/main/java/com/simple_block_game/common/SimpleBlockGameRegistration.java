package com.simple_block_game.common;

import net.minecraft.world.item.CreativeModeTab;

import com.gto.registrylib.util.entry.BlockEntityTypeEntry;
import com.gto.registrylib.util.entry.BlockEntry;
import com.gto.registrylib.util.entry.RegistryEntry;
import com.simple_block_game.common.base.block.RotatedFrame;
import com.simple_block_game.common.simple2048.block.*;
import com.simple_block_game.common.simpleMinesweeper.block.*;

import java.util.Map;

import static com.simple_block_game.SimpleBlockGame.REGISTRYLIB;

public class SimpleBlockGameRegistration {

    public static void init() {}

    public static final BlockEntry<RotatedFrame> BLOCK_ROTATED_FRAME = REGISTRYLIB
            .block(REGISTRYLIB, "rotated_frame", RotatedFrame::new)
            .langCn("框架方块")
            .lang("Frame")
            .noBlockstate()
            .register();

    public static final BlockEntry<BlockMinesweeperFrame> BLOCK_VERTICAL_FRAME = REGISTRYLIB
            .block(REGISTRYLIB, "vertical_frame", BlockMinesweeperFrame::new)
            .langCn("框架方块")
            .lang("Frame")
            .noBlockstate()
            .register();

    // 2048
    public static final BlockEntry<Block2048Core> BLOCK_2048_CORE = REGISTRYLIB
            .block(REGISTRYLIB, "2048_core", Block2048Core::new)
            .langCn("2048 核心方块")
            .lang("2048 Core")
            .noBlockstate()
            .simpleItem()
            .register();

    public static final BlockEntry<Block2048Display> BLOCK_2048_DISPLAY = REGISTRYLIB
            .block(REGISTRYLIB, "2048_display", Block2048Display::new)
            .langCn("2048 显示方块")
            .lang("2048 Display")
            .noBlockstate()
            .simpleItem()
            .register();

    public static final BlockEntry<Block2048Refresh> BLOCK_2048_REFRESH = REGISTRYLIB
            .block(REGISTRYLIB, "2048_refresh", Block2048Refresh::new)
            .langCn("2048 控制方块")
            .lang("2048 Refresh")
            .noBlockstate()
            .register();

    public static final BlockEntityTypeEntry<Block2048CoreEntity> BLOCK_2048_CORE_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "2048_core_entity", (_, p, s) -> new Block2048CoreEntity(p, s))
            .validBlock(BLOCK_2048_CORE)
            .register();

    public static final BlockEntityTypeEntry<Block2048DisplayEntity> BLOCK_2048_DISPLAY_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "2048_display_entity", (_, p, s) -> new Block2048DisplayEntity(p, s))
            .validBlock(BLOCK_2048_DISPLAY)
            .register();

    // minesweeper
    public static final BlockEntry<BlockMinesweeperCore> BLOCK_MINESWEEPER_CORE = REGISTRYLIB
            .block(REGISTRYLIB, "minesweeper_core", BlockMinesweeperCore::new)
            .langCn("扫雷核心方块")
            .lang("Minesweeper Core")
            .noBlockstate()
            .simpleItem()
            .register();
    public static final BlockEntry<BlockMinesweeperDisplay> BLOCK_MINESWEEPER_DISPLAY = REGISTRYLIB
            .block(REGISTRYLIB, "minesweeper_display", BlockMinesweeperDisplay::new)
            .langCn("扫雷显示方块")
            .lang("Minesweeper Display")
            .noBlockstate()
            .simpleItem()
            .register();
    public static final BlockEntry<BlockMinesweeperRefresh> BLOCK_MINESWEEPER_REFRESH = REGISTRYLIB
            .block(REGISTRYLIB, "minesweeper_refresh", BlockMinesweeperRefresh::new)
            .langCn("扫雷控制方块")
            .lang("Minesweeper Refresh")
            .noBlockstate()
            .register();

    public static final BlockEntityTypeEntry<BlockMinesweeperCoreEntity> BLOCK_MINESWEEPER_CORE_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "minesweeper_core_entity", (_, p, s) -> new BlockMinesweeperCoreEntity(p, s))
            .validBlock(BLOCK_MINESWEEPER_CORE)
            .register();
    public static final BlockEntityTypeEntry<BlockMinesweeperDisplayEntity> BLOCK_MINESWEEPER_DISPLAY_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "minesweeper_display_entity", (_, p, s) -> new BlockMinesweeperDisplayEntity(p, s))
            .validBlock(BLOCK_MINESWEEPER_DISPLAY)
            .register();
    public static final BlockEntityTypeEntry<BlockMinesweeperRefreshEntity> BLOCK_MINESWEEPER_REFRESH_ENTITY = REGISTRYLIB
            .blockEntity(REGISTRYLIB, "minesweeper_refresh_entity", (_, p, s) -> new BlockMinesweeperRefreshEntity(p, s))
            .validBlock(BLOCK_MINESWEEPER_REFRESH)
            .register();

    // 创造模式标签注册
    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> TAB_GANM = REGISTRYLIB
            .creativeTab("simple_block_game_tab", "Simple Block Game", Map.of("zh_cn", "简单方块游戏"), builder -> {
                builder.icon(() -> BLOCK_2048_DISPLAY.asItem().getDefaultInstance());
                builder.displayItems((_, output) -> {
                    output.accept(BLOCK_2048_CORE.asItem());
                    output.accept(BLOCK_2048_DISPLAY.asItem());
                    output.accept(BLOCK_MINESWEEPER_CORE.get());
                    output.accept(BLOCK_MINESWEEPER_DISPLAY.get());
                });
            });
}
