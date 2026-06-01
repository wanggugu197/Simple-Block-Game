package com.simple_block_game.util.renderer;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public abstract class BaseBlockEntityRenderer<T extends BaseGameBlockEntity> implements BlockEntityRenderer<T> {

    protected static final float OFFSET = 0.002f;

    protected BaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    protected abstract ResourceLocation getTexture(T blockEntity);

    @Override
    public void render(T blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        ResourceLocation texture = getTexture(blockEntity);
        if (texture == null) {
            poseStack.popPose();
            return;
        }

        Direction facing = blockEntity.getFacing();
        setupTransformation(poseStack, facing);

        VertexConsumer consumer = buffer.getBuffer(ModRenderTypes.style(texture));
        renderFace(poseStack.last(), consumer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    protected void setupTransformation(PoseStack poseStack, Direction facing) {
        float[] offsets = calculateOffsets(facing);
        poseStack.translate(offsets[0], offsets[1], offsets[2]);
        applyRotation(poseStack, facing);
    }

    protected float[] calculateOffsets(Direction facing) {
        return switch (facing) {
            case NORTH -> new float[] { 0.5f, 0.5f, -OFFSET };
            case SOUTH -> new float[] { 0.5f, 0.5f, 1.0f + OFFSET };
            case EAST -> new float[] { 1.0f + OFFSET, 0.5f, 0.5f };
            case WEST -> new float[] { -OFFSET, 0.5f, 0.5f };
            case UP -> new float[] { 0.5f, 1.0f + OFFSET, 0.5f };
            case DOWN -> new float[] { 0.5f, -OFFSET, 0.5f };
        };
    }

    protected static void applyRotation(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case NORTH -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0f));
            case SOUTH -> {}
            case EAST -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0f));
            case WEST -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270.0f));
            case UP -> {
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0f));
                poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0f));
            }
            case DOWN -> {
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0f));
                poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0f));
            }
        }
    }

    protected static void renderFace(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, int packedOverlay) {
        float w = 0.5f;
        float h = 0.5f;
        int color = 0xFFFFFFFF;
        float nx = 1.0f;
        float ny = 1.0f;
        float nz = 1.0f;

        consumer.addVertex(pose, -w, -h, 0.0f).setColor(color).setUv(0.0f, 1.0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(nx, ny, nz);
        consumer.addVertex(pose, w, -h, 0.0f).setColor(color).setUv(1.0f, 1.0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(nx, ny, nz);
        consumer.addVertex(pose, w, h, 0.0f).setColor(color).setUv(1.0f, 0.0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(nx, ny, nz);
        consumer.addVertex(pose, -w, h, 0.0f).setColor(color).setUv(0.0f, 0.0f).setOverlay(packedOverlay).setLight(packedLight).setNormal(nx, ny, nz);
    }
}
