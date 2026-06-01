package com.simple_block_game.util.renderer;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public abstract class BaseBlockEntityCubeRenderer<T extends BaseGameBlockEntity> extends BaseBlockEntityRenderer<T> {

    protected static final float HALF_SIZE = 0.5f;

    public BaseBlockEntityCubeRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public record CubeTextures(ResourceLocation front, ResourceLocation back, ResourceLocation left, ResourceLocation right, ResourceLocation top, ResourceLocation bottom) {}

    @Override
    protected ResourceLocation getTexture(T blockEntity) {
        return null;
    }

    protected abstract CubeTextures getTextures(T blockEntity);

    @Override
    public void render(T blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        CubeTextures textures = getTextures(blockEntity);
        if (textures == null) {
            poseStack.popPose();
            return;
        }

        poseStack.translate(0.5f, 0.5f, 0.5f);
        applyRotation(poseStack, blockEntity.getFacing());
        poseStack.scale(1 + OFFSET, 1 + OFFSET, 1 + OFFSET);

        renderFace(poseStack, buffer, textures.front, 0, 0, HALF_SIZE, 0, 0, 0, packedLight, packedOverlay);
        renderFace(poseStack, buffer, textures.back, 0, 0, -HALF_SIZE, 180, 0, 0, packedLight, packedOverlay);
        renderFace(poseStack, buffer, textures.right, HALF_SIZE, 0, 0, 90, 0, 90, packedLight, packedOverlay);
        renderFace(poseStack, buffer, textures.left, -HALF_SIZE, 0, 0, -90, 0, -90, packedLight, packedOverlay);
        renderFace(poseStack, buffer, textures.top, 0, HALF_SIZE, 0, 0, -90, 180, packedLight, packedOverlay);
        renderFace(poseStack, buffer, textures.bottom, 0, -HALF_SIZE, 0, 0, 90, 0, packedLight, packedOverlay);

        poseStack.popPose();
    }

    protected void renderFace(PoseStack poseStack, MultiBufferSource buffer, ResourceLocation texture,
                              float tx, float ty, float tz, float ry, float rx, float rz, int packedLight, int packedOverlay) {
        if (texture == null) return;

        poseStack.pushPose();
        poseStack.translate(tx, ty, tz);
        if (ry != 0) poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(ry));
        if (rx != 0) poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(rx));
        if (rz != 0) poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(rz));

        VertexConsumer consumer = buffer.getBuffer(ModRenderTypes.style(texture));
        renderFace(poseStack.last(), consumer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
