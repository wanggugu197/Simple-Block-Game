package com.simple_block_game.common.simpleJustGet10.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10CoreEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import com.mapleutillib.api.blockEntityRenderer.SingleFaceBlockEntityRenderer;
import org.jspecify.annotations.NonNull;

import static com.mapleutillib.api.blockEntityRenderer.ModRenderTypes.RenderStyle.PAINTING_LIKE;

public class BlockJustGet10CoreEntityRenderer extends SingleFaceBlockEntityRenderer<BlockJustGet10CoreEntity, BlockJustGet10CoreEntityRenderer.BlockJustGet10CoreEntityRenderState> {

    public static class BlockJustGet10CoreEntityRenderState extends GameBlockEntityRenderState {

        public boolean unfolded = false;
    }

    private static final Identifier TEXTURE_OPEN = SimpleBlockGame.getId("textures/block/simple_just_get_10/just_get_10_core_open.png");
    private static final Identifier TEXTURE_CLOSE = SimpleBlockGame.getId("textures/block/simple_just_get_10/just_get_10_core_closed.png");

    public BlockJustGet10CoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context, true, PAINTING_LIKE);
    }

    @Override
    public @NonNull BlockJustGet10CoreEntityRenderState createRenderState() {
        return new BlockJustGet10CoreEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull BlockJustGet10CoreEntity blockEntity, @NonNull BlockJustGet10CoreEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
    }

    @Override
    protected Identifier getTextureForState(BlockJustGet10CoreEntityRenderState state) {
        if (state.unfolded) return TEXTURE_OPEN;
        return TEXTURE_CLOSE;
    }
}
