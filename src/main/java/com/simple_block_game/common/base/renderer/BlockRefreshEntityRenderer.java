package com.simple_block_game.common.base.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.BlockRefreshEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;

public class BlockRefreshEntityRenderer extends BaseGameBlockEntityRenderer<BlockRefreshEntity, BaseGameBlockEntityRenderState> {

    private static final Identifier TEXTURE_REFRESH = SimpleBlockGame.getId("textures/block/base_refresh.png");

    public BlockRefreshEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public BaseGameBlockEntityRenderState createRenderState() {
        return new BaseGameBlockEntityRenderState();
    }

    @Override
    protected Identifier getTextureForState(BaseGameBlockEntityRenderState state) {
        return TEXTURE_REFRESH;
    }
}
