package com.simple_block_game.common.simpleJustGet10.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10CoreEntity;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BlockJustGet10CoreEntityRenderer extends BaseBlockEntityRenderer<BlockJustGet10CoreEntity> {

    private static final ResourceLocation TEXTURE_OPEN = SimpleBlockGame.getId("textures/block/simple_just_get_10/just_get_10_core_open.png");
    private static final ResourceLocation TEXTURE_CLOSE = SimpleBlockGame.getId("textures/block/simple_just_get_10/just_get_10_core_closed.png");

    public BlockJustGet10CoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(BlockJustGet10CoreEntity blockEntity) {
        boolean unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
        return unfolded ? TEXTURE_OPEN : TEXTURE_CLOSE;
    }
}
