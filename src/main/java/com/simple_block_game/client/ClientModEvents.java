package com.simple_block_game.client;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.common.base.renderer.BlockRefreshEntityRenderer;
import com.simple_block_game.common.simple2048.block.Block2048CoreEntity;
import com.simple_block_game.common.simple2048.block.Block2048DisplayEntity;
import com.simple_block_game.common.simple2048.renderer.Block2048CoreEntityRenderer;
import com.simple_block_game.common.simple2048.renderer.Block2048DisplayEntityRenderer;
import com.simple_block_game.common.simple2048.simple2048Registration;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleCoreEntity;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleDisplayEntity;
import com.simple_block_game.common.simple24Puzzle.renderer.Block24PuzzleCoreEntityRenderer;
import com.simple_block_game.common.simple24Puzzle.renderer.Block24PuzzleDisplayEntityRenderer;
import com.simple_block_game.common.simple24Puzzle.simple24PuzzleRegistration;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyButtonEntity;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyCoreEntity;
import com.simple_block_game.common.simpleMemoryKey.renderer.BlockMemoryKeyButtonEntityRenderer;
import com.simple_block_game.common.simpleMemoryKey.renderer.BlockMemoryKeyCoreEntityRenderer;
import com.simple_block_game.common.simpleMemoryKey.simpleMemoryKeyRegistration;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCoreEntity;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperDisplayEntity;
import com.simple_block_game.common.simpleMinesweeper.renderer.BlockMinesweeperCoreEntityRenderer;
import com.simple_block_game.common.simpleMinesweeper.renderer.BlockMinesweeperDisplayEntityRenderer;
import com.simple_block_game.common.simpleMinesweeper.simpleMinesweeperRegistration;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuCoreEntity;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuDisplayEntity;
import com.simple_block_game.common.simpleSudoku.renderer.BlockSudokuCoreEntityRenderer;
import com.simple_block_game.common.simpleSudoku.renderer.BlockSudokuDisplayEntityRenderer;
import com.simple_block_game.common.simpleSudoku.simpleSudokuRegistration;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsCoreEntity;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsDisplayEntity;
import com.simple_block_game.common.simpleTenDrops.renderer.BlockTenDropsCoreEntityRenderer;
import com.simple_block_game.common.simpleTenDrops.renderer.BlockTenDropsDisplayEntityRenderer;
import com.simple_block_game.common.simpleTenDrops.simpleTenDropsRegistration;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = SimpleBlockGame.MODID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                (BlockEntityType<Block2048CoreEntity>) (BlockEntityType<?>) simple2048Registration.BLOCK_2048_CORE_ENTITY.get(),
                Block2048CoreEntityRenderer::new);
        event.registerBlockEntityRenderer(
                (BlockEntityType<Block2048DisplayEntity>) (BlockEntityType<?>) simple2048Registration.BLOCK_2048_DISPLAY_ENTITY.get(),
                Block2048DisplayEntityRenderer::new);
        event.registerBlockEntityRenderer(
                (BlockEntityType<BlockMinesweeperCoreEntity>) (BlockEntityType<?>) simpleMinesweeperRegistration.BLOCK_MINESWEEPER_CORE_ENTITY.get(),
                BlockMinesweeperCoreEntityRenderer::new);
        event.registerBlockEntityRenderer(
                (BlockEntityType<BlockMinesweeperDisplayEntity>) (BlockEntityType<?>) simpleMinesweeperRegistration.BLOCK_MINESWEEPER_DISPLAY_ENTITY.get(),
                BlockMinesweeperDisplayEntityRenderer::new);
        event.registerBlockEntityRenderer(
                (BlockEntityType<BlockMemoryKeyCoreEntity>) (BlockEntityType<?>) simpleMemoryKeyRegistration.BLOCK_MEMORY_KEY_CORE_ENTITY.get(),
                BlockMemoryKeyCoreEntityRenderer::new);
        event.registerBlockEntityRenderer(
                (BlockEntityType<BlockMemoryKeyButtonEntity>) (BlockEntityType<?>) simpleMemoryKeyRegistration.BLOCK_MEMORY_KEY_BUTTON_ENTITY.get(),
                BlockMemoryKeyButtonEntityRenderer::new);
        event.registerBlockEntityRenderer(
                (BlockEntityType<BlockTenDropsCoreEntity>) (BlockEntityType<?>) simpleTenDropsRegistration.BLOCK_TEN_DROPS_CORE_ENTITY.get(),
                BlockTenDropsCoreEntityRenderer::new);
        event.registerBlockEntityRenderer(
                (BlockEntityType<BlockTenDropsDisplayEntity>) (BlockEntityType<?>) simpleTenDropsRegistration.BLOCK_TEN_DROPS_DISPLAY_ENTITY.get(),
                BlockTenDropsDisplayEntityRenderer::new);
        event.registerBlockEntityRenderer(
                (BlockEntityType<BlockSudokuCoreEntity>) (BlockEntityType<?>) simpleSudokuRegistration.BLOCK_SUDOKU_CORE_ENTITY.get(),
                BlockSudokuCoreEntityRenderer::new);
        event.registerBlockEntityRenderer(
                (BlockEntityType<BlockSudokuDisplayEntity>) (BlockEntityType<?>) simpleSudokuRegistration.BLOCK_SUDOKU_DISPLAY_ENTITY.get(),
                BlockSudokuDisplayEntityRenderer::new);
        event.registerBlockEntityRenderer(
                (BlockEntityType<Block24PuzzleCoreEntity>) (BlockEntityType<?>) simple24PuzzleRegistration.BLOCK_24PUZZLE_CORE_ENTITY.get(),
                Block24PuzzleCoreEntityRenderer::new);
        event.registerBlockEntityRenderer(
                (BlockEntityType<Block24PuzzleDisplayEntity>) (BlockEntityType<?>) simple24PuzzleRegistration.BLOCK_24PUZZLE_DISPLAY_ENTITY.get(),
                Block24PuzzleDisplayEntityRenderer::new);
        event.registerBlockEntityRenderer(
                (BlockEntityType<BlockRefreshEntity>) (BlockEntityType<?>) SimpleBlockGameRegistration.BLOCK_REFRESH_ENTITY.get(),
                BlockRefreshEntityRenderer::new);
    }
}
