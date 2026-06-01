package com.simple_block_game.common.simpleMemoryKey.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyButtonEntity;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyPosition;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * 记忆键游戏按键方块实体渲染器
 * 根据按键位置和闪烁状态选择对应的材质
 */
public class BlockMemoryKeyButtonEntityRenderer extends BaseBlockEntityRenderer<BlockMemoryKeyButtonEntity> {

    private static final String TEXTURE_FORMAT_ACTIVE = "textures/block/simple_memory_key/memory_key_button_%d.png";
    private static final String TEXTURE_FORMAT_INACTIVE = "textures/block/simple_memory_key/memory_key_unactivated_button_%d.png";

    public BlockMemoryKeyButtonEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(BlockMemoryKeyButtonEntity blockEntity) {
        MemoryKeyPosition position = blockEntity.getPosition();
        if (position == null) {
            return null;
        }
        boolean flashing = blockEntity.isFlashing();
        String format = flashing ? TEXTURE_FORMAT_ACTIVE : TEXTURE_FORMAT_INACTIVE;
        return SimpleBlockGame.getId(String.format(format, position.getId()));
    }
}
