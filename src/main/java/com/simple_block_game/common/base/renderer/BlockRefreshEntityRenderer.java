package com.simple_block_game.common.base.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BlockRefreshEntityRenderer extends BaseBlockEntityRenderer<BlockRefreshEntity> {

    private static final ResourceLocation TEXTURE_REFRESH = SimpleBlockGame.getId("textures/block/base_refresh.png");

    public BlockRefreshEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(BlockRefreshEntity blockEntity) {
        return TEXTURE_REFRESH;
    }
}
