package com.simple_block_game.common.simpleTenDrops.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsDisplayEntity;
import com.simple_block_game.common.simpleTenDrops.data.DropletLevel;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import lombok.NonNull;

public class BlockTenDropsDisplayEntityRenderer extends BaseBlockEntityRenderer<BlockTenDropsDisplayEntity, BlockTenDropsDisplayEntityRenderer.BlockTenDropDisplayEntityRenderState> {

    public static class BlockTenDropDisplayEntityRenderState extends GameBlockEntityRenderState {

        public DropletLevel dropletLevel = DropletLevel.EMPTY;
    }

    private static final Identifier TEXTURE_ONE = SimpleBlockGame.getId("textures/block/simple_ten_drops/ten_drops_display_1.png");
    private static final Identifier TEXTURE_TWO = SimpleBlockGame.getId("textures/block/simple_ten_drops/ten_drops_display_2.png");
    private static final Identifier TEXTURE_THREE = SimpleBlockGame.getId("textures/block/simple_ten_drops/ten_drops_display_3.png");
    private static final Identifier TEXTURE_FOUR = SimpleBlockGame.getId("textures/block/simple_ten_drops/ten_drops_display_4.png");
    private static final Identifier TEXTURE_BURST = SimpleBlockGame.getId("textures/block/simple_ten_drops/droplet_burst.png");

    public BlockTenDropsDisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public BlockTenDropDisplayEntityRenderState createRenderState() {
        return new BlockTenDropDisplayEntityRenderState();
    }

    @Override
    public void extractRenderState(BlockTenDropsDisplayEntity blockEntity, BlockTenDropDisplayEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.dropletLevel = blockEntity.getDropletLevel();
    }

    @Override
    protected Identifier getTextureForState(BlockTenDropDisplayEntityRenderState state) {
        return switch (state.dropletLevel) {
            case EMPTY -> null;
            case ONE -> TEXTURE_ONE;
            case TWO -> TEXTURE_TWO;
            case THREE -> TEXTURE_THREE;
            case FOUR -> TEXTURE_FOUR;
            case BURST -> TEXTURE_BURST;
        };
    }
}
