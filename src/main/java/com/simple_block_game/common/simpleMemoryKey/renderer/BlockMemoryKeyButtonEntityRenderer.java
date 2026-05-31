package com.simple_block_game.common.simpleMemoryKey.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyButtonEntity;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyPosition;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import lombok.NonNull;

/**
 * 记忆键游戏按键方块实体渲染器
 * 根据按键位置和闪烁状态选择对应的材质
 */
public class BlockMemoryKeyButtonEntityRenderer extends BaseBlockEntityRenderer<BlockMemoryKeyButtonEntity, BlockMemoryKeyButtonEntityRenderer.BlockMemoryKeyButtonEntityRenderState> {

    public static class BlockMemoryKeyButtonEntityRenderState extends GameBlockEntityRenderState {

        public MemoryKeyPosition position = MemoryKeyPosition.NORTH;
        public boolean flashing = false;
    }

    private static final String TEXTURE_FORMAT_ACTIVE = "textures/block/simple_memory_key/memory_key_button_%d.png";
    private static final String TEXTURE_FORMAT_INACTIVE = "textures/block/simple_memory_key/memory_key_unactivated_button_%d.png";

    public BlockMemoryKeyButtonEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public BlockMemoryKeyButtonEntityRenderState createRenderState() {
        return new BlockMemoryKeyButtonEntityRenderState();
    }

    @Override
    public void extractRenderState(BlockMemoryKeyButtonEntity blockEntity, BlockMemoryKeyButtonEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.position = blockEntity.getPosition();
        state.flashing = blockEntity.isFlashing();
    }

    @Override
    protected Identifier getTextureForState(BlockMemoryKeyButtonEntityRenderState state) {
        if (state.position == null) {
            return null;
        }
        String format = state.flashing ? TEXTURE_FORMAT_ACTIVE : TEXTURE_FORMAT_INACTIVE;
        return SimpleBlockGame.getId(String.format(format, state.position.getId()));
    }
}
