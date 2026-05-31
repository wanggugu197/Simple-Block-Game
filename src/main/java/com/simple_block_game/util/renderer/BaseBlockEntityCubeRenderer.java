package com.simple_block_game.util.renderer;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.NonNull;

public abstract class BaseBlockEntityCubeRenderer<T extends BaseGameBlockEntity, S extends GameBlockEntityRenderState> extends BaseBlockEntityRenderer<T, S> {

    protected static final float HALF_SIZE = 0.5f;

    public BaseBlockEntityCubeRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public record CubeTextures(Identifier front, Identifier back, Identifier left, Identifier right, Identifier top, Identifier bottom) {}

    @Override
    protected Identifier getTextureForState(S state) {
        return null;
    }

    protected abstract CubeTextures getTexturesForState(S state);

    @Override
    public void submit(S state, PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        poseStack.pushPose();

        CubeTextures textures = getTexturesForState(state);
        if (textures == null) {
            poseStack.popPose();
            return;
        }

        // 应用基础变换
        poseStack.translate(0.5f, 0.5f, 0.5f);
        applyRotation(poseStack, state.facing);
        poseStack.scale(1 + OFFSET, 1 + OFFSET, 1 + OFFSET);

        // 渲染六个面
        renderFace(poseStack, collector, textures.front, 0, 0, HALF_SIZE, 0, 0, 0);
        renderFace(poseStack, collector, textures.back, 0, 0, -HALF_SIZE, 180, 0, 0);
        renderFace(poseStack, collector, textures.right, HALF_SIZE, 0, 0, 90, 0, 90);
        renderFace(poseStack, collector, textures.left, -HALF_SIZE, 0, 0, -90, 0, -90);
        renderFace(poseStack, collector, textures.top, 0, HALF_SIZE, 0, 0, -90, 180);
        renderFace(poseStack, collector, textures.bottom, 0, -HALF_SIZE, 0, 0, 90, 0);

        poseStack.popPose();
    }

    /**
     * 渲染单个面
     * 
     * @param poseStack 姿态栈
     * @param collector 渲染节点收集器
     * @param texture   纹理
     * @param tx        平移X
     * @param ty        平移Y
     * @param tz        平移Z
     * @param ry        Y轴旋转角度
     * @param rx        X轴旋转角度
     * @param rz        Z轴旋转角度
     */
    protected void renderFace(PoseStack poseStack, SubmitNodeCollector collector, Identifier texture,
                              float tx, float ty, float tz, float ry, float rx, float rz) {
        if (texture == null) return;

        poseStack.pushPose();
        poseStack.translate(tx, ty, tz);
        if (ry != 0) poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(ry));
        if (rx != 0) poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(rx));
        if (rz != 0) poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(rz));
        submitTexture(poseStack, collector, texture);
        poseStack.popPose();
    }
}
