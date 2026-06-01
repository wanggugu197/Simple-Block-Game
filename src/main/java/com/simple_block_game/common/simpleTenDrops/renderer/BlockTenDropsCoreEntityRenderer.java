package com.simple_block_game.common.simpleTenDrops.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsCoreEntity;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BlockTenDropsCoreEntityRenderer extends BaseBlockEntityRenderer<BlockTenDropsCoreEntity> {

    private static final ResourceLocation TEXTURE_CLOSE = SimpleBlockGame.getId("textures/block/simple_ten_drops/ten_drops_display_3.png");
    private static final String TEXTURE_DROPS = "textures/block/simple_ten_drops/ten_drops_core_%d.png";

    public BlockTenDropsCoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(BlockTenDropsCoreEntity blockEntity) {
        boolean unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
        if (!unfolded) return TEXTURE_CLOSE;
        int waterDrops = blockEntity.getWaterDrops();
        if (waterDrops > 20) waterDrops = 20;
        return SimpleBlockGame.getId(String.format(TEXTURE_DROPS, waterDrops));
    }
}
