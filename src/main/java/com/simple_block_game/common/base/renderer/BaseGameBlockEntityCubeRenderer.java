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

public abstract class BaseGameBlockEntityCubeRenderer<T extends BaseGameBlockEntity, S extends BaseGameBlockEntityRenderState> implements BlockEntityRenderer<T, S> {

    protected static final float SCALE = 1.5f;
    protected static final float HALF_SIZE = 0.5f;

    public BaseGameBlockEntityCubeRenderer(BlockEntityRendererProvider.Context context) {}

    public record CubeTextures(Identifier front, Identifier back, Identifier left, Identifier right, Identifier top, Identifier bottom) {}

    protected abstract CubeTextures getTexturesForState(S state);

    @Override
    public void extractRenderState(T blockEntity, S state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getFacing();
    }

    @Override
    public void submit(S state, PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        poseStack.pushPose();

        CubeTextures textures = getTexturesForState(state);
        if (textures == null) {
            poseStack.popPose();
            return;
        }

        poseStack.translate(0.5f, 0.5f, 0.5f);

        Direction facing = state.facing;
        applyRotation(poseStack, facing);

        poseStack.scale(SCALE, SCALE, SCALE);

        if (textures.front != null) {
            poseStack.pushPose();
            poseStack.translate(0, 0, HALF_SIZE);
            collector.submitCustomGeometry(poseStack, RenderTypes.eyes(textures.front), BaseGameBlockEntityCubeRenderer::renderFace);
            poseStack.popPose();
        }

        if (textures.back != null) {
            poseStack.pushPose();
            poseStack.translate(0, 0, -HALF_SIZE);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0f));
            collector.submitCustomGeometry(poseStack, RenderTypes.eyes(textures.back), BaseGameBlockEntityCubeRenderer::renderFace);
            poseStack.popPose();
        }

        if (textures.right != null) {
            poseStack.pushPose();
            poseStack.translate(HALF_SIZE, 0, 0);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0f));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90.0f));
            collector.submitCustomGeometry(poseStack, RenderTypes.eyes(textures.right), BaseGameBlockEntityCubeRenderer::renderFace);
            poseStack.popPose();
        }

        if (textures.left != null) {
            poseStack.pushPose();
            poseStack.translate(-HALF_SIZE, 0, 0);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-90.0f));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-90.0f));
            collector.submitCustomGeometry(poseStack, RenderTypes.eyes(textures.left), BaseGameBlockEntityCubeRenderer::renderFace);
            poseStack.popPose();
        }

        if (textures.top != null) {
            poseStack.pushPose();
            poseStack.translate(0, HALF_SIZE, 0);
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0f));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180.0f));
            collector.submitCustomGeometry(poseStack, RenderTypes.eyes(textures.top), BaseGameBlockEntityCubeRenderer::renderFace);
            poseStack.popPose();
        }

        if (textures.bottom != null) {
            poseStack.pushPose();
            poseStack.translate(0, -HALF_SIZE, 0);
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0f));
            collector.submitCustomGeometry(poseStack, RenderTypes.eyes(textures.bottom), BaseGameBlockEntityCubeRenderer::renderFace);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    protected static void applyRotation(PoseStack poseStack, Direction facing) {
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

    protected static void renderFace(PoseStack.Pose pose, VertexConsumer consumer) {
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
