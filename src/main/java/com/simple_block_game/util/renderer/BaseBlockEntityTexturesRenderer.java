package com.simple_block_game.util.renderer;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.List;

public abstract class BaseBlockEntityTexturesRenderer<T extends BaseGameBlockEntity> extends BaseBlockEntityRenderer<T> {

    protected BaseBlockEntityTexturesRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected abstract List<ResourceLocation> getTextures(T blockEntity);

    @Override
    protected ResourceLocation getTexture(T blockEntity) {
        return null;
    }

    @Override
    public void render(T blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        List<ResourceLocation> textures = getTextures(blockEntity);
        if (textures == null || textures.isEmpty()) {
            poseStack.popPose();
            return;
        }

        Direction facing = blockEntity.getFacing();
        setupTransformation(poseStack, facing);

        for (int i = 0; i < textures.size(); i++) {
            ResourceLocation texture = textures.get(i);
            if (texture != null) {
                poseStack.pushPose();
                poseStack.translate(0, 0, i * OFFSET);
                VertexConsumer consumer = buffer.getBuffer(ModRenderTypes.style(texture));
                renderFace(poseStack.last(), consumer, packedLight, packedOverlay);
                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }
}
