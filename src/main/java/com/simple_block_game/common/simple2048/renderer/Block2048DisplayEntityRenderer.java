package com.simple_block_game.common.simple2048.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.renderer.BaseGameBlockEntityRenderState;
import com.simple_block_game.common.base.renderer.BaseGameBlockEntityRenderer;
import com.simple_block_game.common.simple2048.block.Block2048DisplayEntity;
import com.simple_block_game.common.simple2048.data.Value2048;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import lombok.NonNull;

/**
 * 2048显示方块实体渲染器
 * 继承基类，负责根据显示值选择对应的材质
 */
public class Block2048DisplayEntityRenderer extends BaseGameBlockEntityRenderer<Block2048DisplayEntity, Block2048DisplayEntityRenderer.Block2048DisplayEntityRenderState> {

    public static class Block2048DisplayEntityRenderState extends BaseGameBlockEntityRenderState {

        public Value2048 displayValue = Value2048.ZERO;
    }

    private static final String TEXTURE_PATH_FORMAT = "textures/block/simple2048/2048_display_%d.png";

    public Block2048DisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Block2048DisplayEntityRenderState createRenderState() {
        return new Block2048DisplayEntityRenderState();
    }

    @Override
    public void extractRenderState(Block2048DisplayEntity blockEntity, Block2048DisplayEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.displayValue = blockEntity.getValue();
    }

    @Override
    protected Identifier getTextureForState(Block2048DisplayEntityRenderState state) {
        if (state.displayValue == Value2048.ZERO) return null;
        return SimpleBlockGame.getId(String.format(TEXTURE_PATH_FORMAT, state.displayValue.getValue()));
    }
}
