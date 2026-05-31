package com.simple_block_game.common.simpleSudoku.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuDisplayEntity;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import lombok.NonNull;

public class BlockSudokuDisplayEntityRenderer extends BaseBlockEntityRenderer<BlockSudokuDisplayEntity, BlockSudokuDisplayEntityRenderer.BlockSudokuDisplayEntityRenderState> {

    public static class BlockSudokuDisplayEntityRenderState extends GameBlockEntityRenderState {

        public int value = 0;
        public boolean isInitial = false;
    }

    private static final String TEXTURE = "textures/block/simple_sudoku/sudoku_number_%s_%s.png";

    public BlockSudokuDisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public BlockSudokuDisplayEntityRenderState createRenderState() {
        return new BlockSudokuDisplayEntityRenderState();
    }

    @Override
    public void extractRenderState(BlockSudokuDisplayEntity blockEntity, BlockSudokuDisplayEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.value = blockEntity.getValue();
        state.isInitial = blockEntity.isInitial();
    }

    @Override
    protected Identifier getTextureForState(BlockSudokuDisplayEntityRenderState state) {
        if (state.isInitial) return SimpleBlockGame.getId(String.format(TEXTURE, "initial", state.value));
        return SimpleBlockGame.getId(String.format(TEXTURE, "uninitial", state.value));
    }
}
