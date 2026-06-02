package com.simple_block_game.util.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

/**
 * 可渲染文本的接口
 * 提供默认的文本渲染实现
 */
public interface TextsRenderable {

    Font font = Minecraft.getInstance().font;

    /**
     * 在指定位置渲染文本
     * 
     * @param poseStack 姿态堆栈
     * @param text      要渲染的文本
     * @param color     文本颜色（ARGB格式）
     */
    default void renderText(PoseStack poseStack, String text, int color) {
        float scale = 0.0625f;
        poseStack.pushPose();
        poseStack.translate(0, 0.25, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
        poseStack.scale(scale, scale, scale);
        float textWidth = font.width(text);
        float x = -textWidth / 2.0f;
        float y = 0;
        MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        font.drawInBatch(
                Component.translatable(text),
                x,
                y,
                color,
                false,
                poseStack.last().pose(),
                bufferSource,
                Font.DisplayMode.NORMAL,
                0,
                0x00F000F0);
        poseStack.popPose();
    }
}
