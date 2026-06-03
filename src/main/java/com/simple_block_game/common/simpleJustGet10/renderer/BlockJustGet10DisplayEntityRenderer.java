package com.simple_block_game.common.simpleJustGet10.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.simpleJustGet10.block.BlockJustGet10DisplayEntity;
import com.simple_block_game.common.simpleJustGet10.data.ValueJustGet10;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BlockJustGet10DisplayEntityRenderer extends BaseBlockEntityRenderer<BlockJustGet10DisplayEntity> {

    private static final String TEXTURE_PATH_FORMAT = "textures/block/simple_just_get_10/just_get_10_display_%d.png";
    private static final String TEXTURE_PATH_FORMAT_HIGHLIGHTED = "textures/block/simple_just_get_10/just_get_10_display_%d_highlighted.png";

    public BlockJustGet10DisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(BlockJustGet10DisplayEntity blockEntity) {
        ValueJustGet10 displayValue = blockEntity.getValue();
        if (displayValue == null) return null;

        String format = blockEntity.isHighlighted() ? TEXTURE_PATH_FORMAT_HIGHLIGHTED : TEXTURE_PATH_FORMAT;
        return SimpleBlockGame.getId(String.format(format, displayValue.getValue()));
    }
}
