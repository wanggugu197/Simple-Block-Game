package com.simple_block_game.util.renderer;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.NonNull;

public abstract class BaseBlockEntityRenderer<T extends BaseGameBlockEntity, S extends GameBlockEntityRenderState> implements BlockEntityRenderer<T, S> {

    protected static final float OFFSET = 0.002f;

    protected BaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    protected abstract Identifier getTextureForState(S state);

    @Override
    public void extractRenderState(T blockEntity, S state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getFacing();
    }

    @Override
    public void submit(S state, PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        poseStack.pushPose();

        Identifier texture = getTextureForState(state);
        if (texture == null) {
            poseStack.popPose();
            return;
        }
        setupTransformation(poseStack, state.facing);
        submitTexture(poseStack, collector, texture);

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

    protected void submitTexture(PoseStack poseStack, @NonNull SubmitNodeCollector collector, Identifier texture) {
        collector.submitCustomGeometry(poseStack, ModRenderTypes.style(texture), BaseBlockEntityRenderer::renderFace);
    }

    protected void submitTextureWithLayerOffset(PoseStack poseStack, @NonNull SubmitNodeCollector collector, Identifier texture, int layerIndex) {
        poseStack.pushPose();
        poseStack.translate(0, 0, layerIndex * OFFSET);
        submitTexture(poseStack, collector, texture);
        poseStack.popPose();
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

    protected static void renderFace(PoseStack.Pose pose, VertexConsumer consumer) {
        float w = 0.5f;
        float h = 0.5f;
        int color = 0xFFFFFFFF;
        int light = 0x00F000F0;
        int overlay = OverlayTexture.NO_OVERLAY;
        float nx = 1.0f;
        float ny = 1.0f;
        float nz = 1.0f;

        consumer.addVertex(pose, -w, -h, 0.0f).setColor(color).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
        consumer.addVertex(pose, w, -h, 0.0f).setColor(color).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
        consumer.addVertex(pose, w, h, 0.0f).setColor(color).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
        consumer.addVertex(pose, -w, h, 0.0f).setColor(color).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
    }
}
