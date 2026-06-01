package com.simple_block_game.common.simpleTenDrops.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsDisplayEntity;
import com.simple_block_game.common.simpleTenDrops.data.DropletLevel;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BlockTenDropsDisplayEntityRenderer extends BaseBlockEntityRenderer<BlockTenDropsDisplayEntity> {

    private static final ResourceLocation TEXTURE_ONE = SimpleBlockGame.getId("textures/block/simple_ten_drops/ten_drops_display_1.png");
    private static final ResourceLocation TEXTURE_TWO = SimpleBlockGame.getId("textures/block/simple_ten_drops/ten_drops_display_2.png");
    private static final ResourceLocation TEXTURE_THREE = SimpleBlockGame.getId("textures/block/simple_ten_drops/ten_drops_display_3.png");
    private static final ResourceLocation TEXTURE_FOUR = SimpleBlockGame.getId("textures/block/simple_ten_drops/ten_drops_display_4.png");
    private static final ResourceLocation TEXTURE_BURST = SimpleBlockGame.getId("textures/block/simple_ten_drops/droplet_burst.png");

    public BlockTenDropsDisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(BlockTenDropsDisplayEntity blockEntity) {
        DropletLevel dropletLevel = blockEntity.getDropletLevel();
        return switch (dropletLevel) {
            case EMPTY -> null;
            case ONE -> TEXTURE_ONE;
            case TWO -> TEXTURE_TWO;
            case THREE -> TEXTURE_THREE;
            case FOUR -> TEXTURE_FOUR;
            case BURST -> TEXTURE_BURST;
        };
    }
}
