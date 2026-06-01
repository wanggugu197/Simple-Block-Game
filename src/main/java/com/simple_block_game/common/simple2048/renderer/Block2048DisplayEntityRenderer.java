package com.simple_block_game.common.simple2048.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.simple2048.block.Block2048DisplayEntity;
import com.simple_block_game.common.simple2048.data.Value2048;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * 2048显示方块实体渲染器
 * 继承基类，负责根据显示值选择对应的材质
 */
public class Block2048DisplayEntityRenderer extends BaseBlockEntityRenderer<Block2048DisplayEntity> {

    private static final String TEXTURE_PATH_FORMAT = "textures/block/simple2048/2048_display_%d.png";

    public Block2048DisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(Block2048DisplayEntity blockEntity) {
        Value2048 displayValue = blockEntity.getValue();
        if (displayValue == Value2048.ZERO) return null;
        return SimpleBlockGame.getId(String.format(TEXTURE_PATH_FORMAT, displayValue.getValue()));
    }
}
