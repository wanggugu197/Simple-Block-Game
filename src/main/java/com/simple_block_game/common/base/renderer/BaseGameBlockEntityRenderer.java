package com.simple_block_game.common.base.renderer;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class BaseGameBlockEntityRenderer<T extends BaseGameBlockEntity, S extends BaseGameBlockEntityRenderState> implements BlockEntityRenderer<T, S> {

    protected static final float OFFSET = 0.2f;

    protected BaseGameBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    protected abstract Identifier getTextureForState(S state);

    @Override
    public void extractRenderState(T blockEntity, S state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getFacing();
    }

    @Override
    public void submit(S state, PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        poseStack.pushPose();

        Direction facing = state.facing;
        float offsetX = 0.5f;
        float offsetY = 0.5f;
        float offsetZ = 0.5f;

        switch (facing) {
            case NORTH -> offsetZ = 1.0f + OFFSET;
            case SOUTH -> offsetZ = -OFFSET;
            case EAST -> offsetX = 1.0f + OFFSET;
            case WEST -> offsetX = -OFFSET;
            case UP -> offsetY = 1.0f + OFFSET;
            case DOWN -> offsetY = -OFFSET;
        }

        poseStack.translate(offsetX, offsetY, offsetZ);
        applyRotation(poseStack, facing);

        Identifier texture = getTextureForState(state);
        if (texture != null) {
            collector.submitCustomGeometry(poseStack, RenderTypes.eyes(texture), BaseGameBlockEntityRenderer::renderFace);
        }

        poseStack.popPose();
    }

    private static void applyRotation(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case NORTH -> {}
            case SOUTH -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0f));
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

    private static void renderFace(PoseStack.Pose pose, VertexConsumer consumer) {
        float w = 0.5f;
        float h = 0.5f;
        int color = 0xFFFFFFFF;
        int light = 0x00F000F0;
        int overlay = OverlayTexture.NO_OVERLAY;
        float nx = 0.0f;
        float ny = 0.0f;
        float nz = 1.0f;

        consumer.addVertex(pose, -w, -h, 0.0f).setColor(color).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
        consumer.addVertex(pose, w, -h, 0.0f).setColor(color).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
        consumer.addVertex(pose, w, h, 0.0f).setColor(color).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
        consumer.addVertex(pose, -w, h, 0.0f).setColor(color).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(nx, ny, nz);
    }
}
