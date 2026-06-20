package com.simple_block_game.common.simpleJustGet10.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10DisplayEntity;
import com.simple_block_game.common.simpleJustGet10.data.ValueJustGet10;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import com.mapleutillib.api.blockEntityRenderer.SingleFaceBlockEntityRenderer;
import org.jspecify.annotations.NonNull;

import static com.mapleutillib.api.blockEntityRenderer.ModRenderTypes.RenderStyle.PAINTING_LIKE;

public class BlockJustGet10DisplayEntityRenderer extends SingleFaceBlockEntityRenderer<BlockJustGet10DisplayEntity, BlockJustGet10DisplayEntityRenderer.BlockJustGet10DisplayEntityRenderState> {

    public static class BlockJustGet10DisplayEntityRenderState extends GameBlockEntityRenderState {

        public ValueJustGet10 displayValue = ValueJustGet10.NUM_1;
        public boolean highlighted = false;
    }

    private static final String TEXTURE_PATH_FORMAT = "textures/block/simple_just_get_10/just_get_10_display_%d.png";
    private static final String TEXTURE_PATH_FORMAT_HIGHLIGHTED = "textures/block/simple_just_get_10/just_get_10_display_%d_highlighted.png";

    public BlockJustGet10DisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context, true, PAINTING_LIKE);
    }

    @Override
    public @NonNull BlockJustGet10DisplayEntityRenderState createRenderState() {
        return new BlockJustGet10DisplayEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull BlockJustGet10DisplayEntity blockEntity, @NonNull BlockJustGet10DisplayEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.displayValue = blockEntity.getValue();
        state.highlighted = blockEntity.isHighlighted();
    }

    @Override
    protected Identifier getTextureForState(BlockJustGet10DisplayEntityRenderState state) {
        if (state.displayValue == null) return null;

        String format = state.highlighted ? TEXTURE_PATH_FORMAT_HIGHLIGHTED : TEXTURE_PATH_FORMAT;
        return SimpleBlockGame.getId(String.format(format, state.displayValue.getValue()));
    }
}
