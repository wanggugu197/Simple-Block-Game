package com.simple_block_game.util.renderer;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.NonNull;

import java.util.List;

public abstract class BaseBlockEntityTexturesRenderer<T extends BaseGameBlockEntity, S extends GameBlockEntityRenderState> extends BaseBlockEntityRenderer<T, S> {

    protected BaseBlockEntityTexturesRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected abstract List<Identifier> getTexturesForState(S state);

    @Override
    protected Identifier getTextureForState(S state) {
        return null;
    }

    @Override
    public void submit(S state, PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        poseStack.pushPose();

        List<Identifier> textures = getTexturesForState(state);
        if (textures == null || textures.isEmpty()) {
            poseStack.popPose();
            return;
        }

        setupTransformation(poseStack, state.facing);
        for (int i = 0; i < textures.size(); i++) {
            Identifier texture = textures.get(i);
            if (texture != null) {
                submitTextureWithLayerOffset(poseStack, collector, texture, i);
            }
        }

        poseStack.popPose();
    }
}
