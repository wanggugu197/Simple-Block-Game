package com.simple_block_game.common.simpleTenDrops.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.base.renderer.BaseGameBlockEntityRenderState;
import com.simple_block_game.common.base.renderer.BaseGameBlockEntityRenderer;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsCoreEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import lombok.NonNull;

public class BlockTenDropsCoreEntityRenderer extends BaseGameBlockEntityRenderer<BlockTenDropsCoreEntity, BlockTenDropsCoreEntityRenderer.BlockTenDropsCoreEntityRenderState> {

    public static class BlockTenDropsCoreEntityRenderState extends BaseGameBlockEntityRenderState {

        public boolean unfolded = false;
        public int waterDrops = 0;
    }

    private static final Identifier TEXTURE_CLOSE = SimpleBlockGame.getId("textures/block/simple_ten_drops/ten_drops_display_3.png");
    private static final String TEXTURE_DROPS = "textures/block/simple_ten_drops/ten_drops_core_%d.png";

    public BlockTenDropsCoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public BlockTenDropsCoreEntityRenderState createRenderState() {
        return new BlockTenDropsCoreEntityRenderState();
    }

    @Override
    public void extractRenderState(BlockTenDropsCoreEntity blockEntity, BlockTenDropsCoreEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
        state.waterDrops = blockEntity.getWaterDrops();
        if (state.waterDrops > 20) state.waterDrops = 20;
    }

    @Override
    protected Identifier getTextureForState(BlockTenDropsCoreEntityRenderState state) {
        if (!state.unfolded) return TEXTURE_CLOSE;
        return SimpleBlockGame.getId(String.format(TEXTURE_DROPS, state.waterDrops));
    }
}
