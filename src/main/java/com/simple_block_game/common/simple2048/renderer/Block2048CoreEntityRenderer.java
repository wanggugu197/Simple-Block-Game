package com.simple_block_game.common.simple2048.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simple2048.block.Block2048CoreEntity;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * 2048核心方块实体渲染器
 * 继承基类，负责根据展开状态选择对应的材质
 */
public class Block2048CoreEntityRenderer extends BaseBlockEntityRenderer<Block2048CoreEntity> {

    private static final ResourceLocation TEXTURE_OPEN = SimpleBlockGame.getId("textures/block/simple2048/2048_core_open.png");
    private static final ResourceLocation TEXTURE_CLOSE = SimpleBlockGame.getId("textures/block/simple2048/2048_core_closed.png");

    public Block2048CoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(Block2048CoreEntity blockEntity) {
        boolean unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
        if (unfolded) return TEXTURE_OPEN;
        return TEXTURE_CLOSE;
    }
}
