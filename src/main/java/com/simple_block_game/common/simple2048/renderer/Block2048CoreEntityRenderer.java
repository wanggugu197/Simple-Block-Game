package com.simple_block_game.common.simple2048.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.base.renderer.BaseGameBlockEntityRenderState;
import com.simple_block_game.common.base.renderer.BaseGameBlockEntityRenderer;
import com.simple_block_game.common.simple2048.block.Block2048CoreEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import lombok.NonNull;

/**
 * 2048核心方块实体渲染器
 * 继承基类，负责根据展开状态选择对应的材质
 */
public class Block2048CoreEntityRenderer extends BaseGameBlockEntityRenderer<Block2048CoreEntity, Block2048CoreEntityRenderer.Block2048CoreEntityRenderState> {

    public static class Block2048CoreEntityRenderState extends BaseGameBlockEntityRenderState {

        public boolean unfolded = false;
    }

    private static final Identifier TEXTURE_OPEN = SimpleBlockGame.getId("textures/block/simple2048/2048_core_open.png");
    private static final Identifier TEXTURE_CLOSE = SimpleBlockGame.getId("textures/block/simple2048/2048_core_closed.png");

    public Block2048CoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Block2048CoreEntityRenderState createRenderState() {
        return new Block2048CoreEntityRenderState();
    }

    @Override
    public void extractRenderState(Block2048CoreEntity blockEntity, Block2048CoreEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
    }

    @Override
    protected Identifier getTextureForState(Block2048CoreEntityRenderState state) {
        if (state.unfolded) return TEXTURE_OPEN;
        return TEXTURE_CLOSE;
    }
}
