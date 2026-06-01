package com.simple_block_game.common.simpleSudoku.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuDisplayEntity;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BlockSudokuDisplayEntityRenderer extends BaseBlockEntityRenderer<BlockSudokuDisplayEntity> {

    private static final String TEXTURE = "textures/block/simple_sudoku/sudoku_number_%s_%s.png";

    public BlockSudokuDisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(BlockSudokuDisplayEntity blockEntity) {
        int value = blockEntity.getValue();
        boolean isInitial = blockEntity.isInitial();
        if (isInitial) return SimpleBlockGame.getId(String.format(TEXTURE, "initial", value));
        return SimpleBlockGame.getId(String.format(TEXTURE, "uninitial", value));
    }
}
