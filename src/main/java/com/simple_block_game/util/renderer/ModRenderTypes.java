package com.simple_block_game.util.renderer;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public class ModRenderTypes {

    private static final Function<Identifier, RenderType> PAINTING_LIKE_RENDERER = Util.memoize((texture) -> RenderType.create(
            "simple_block_game_painting",
            RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE)
                    .withTexture("Sampler0", texture)
                    .useLightmap()
                    .useOverlay()
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .sortOnUpload()
                    .createRenderSetup()));

    public static RenderType style(Identifier texture) {
        return PAINTING_LIKE_RENDERER.apply(texture);
    }
}
