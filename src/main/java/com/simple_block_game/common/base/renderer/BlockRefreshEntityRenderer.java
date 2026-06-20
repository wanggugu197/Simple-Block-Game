package com.simple_block_game.common.base.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.BlockRefreshEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;

import com.mapleutillib.api.blockEntityRenderer.SingleFaceBlockEntityRenderer;
import org.jspecify.annotations.NonNull;

import static com.mapleutillib.api.blockEntityRenderer.ModRenderTypes.RenderStyle.PAINTING_LIKE;

public class BlockRefreshEntityRenderer extends SingleFaceBlockEntityRenderer<BlockRefreshEntity, GameBlockEntityRenderState> {

    private static final Identifier TEXTURE_REFRESH = SimpleBlockGame.getId("textures/block/base_refresh.png");

    public BlockRefreshEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context, true, PAINTING_LIKE);
    }

    @Override
    public @NonNull GameBlockEntityRenderState createRenderState() {
        return new GameBlockEntityRenderState();
    }

    @Override
    protected Identifier getTextureForState(GameBlockEntityRenderState state) {
        return TEXTURE_REFRESH;
    }
}
