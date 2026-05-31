package com.simple_block_game.common.base.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;

public class BlockRefreshEntityRenderer extends BaseBlockEntityRenderer<BlockRefreshEntity, GameBlockEntityRenderState> {

    private static final Identifier TEXTURE_REFRESH = SimpleBlockGame.getId("textures/block/base_refresh.png");

    public BlockRefreshEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public GameBlockEntityRenderState createRenderState() {
        return new GameBlockEntityRenderState();
    }

    @Override
    protected Identifier getTextureForState(GameBlockEntityRenderState state) {
        return TEXTURE_REFRESH;
    }
}
