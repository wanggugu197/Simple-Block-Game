package com.simple_block_game.common.simpleMemoryKey.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyCoreEntity;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyGameState;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import com.mapleutillib.api.blockEntityRenderer.SingleFaceBlockEntityRenderer;
import org.jspecify.annotations.NonNull;

import static com.mapleutillib.api.blockEntityRenderer.ModRenderTypes.RenderStyle.PAINTING_LIKE;

public class BlockMemoryKeyCoreEntityRenderer extends SingleFaceBlockEntityRenderer<BlockMemoryKeyCoreEntity, BlockMemoryKeyCoreEntityRenderer.BlockMemoryKeyCoreEntityRenderState> {

    public static class BlockMemoryKeyCoreEntityRenderState extends GameBlockEntityRenderState {

        public boolean unfolded = false;
        public MemoryKeyGameState gameState = MemoryKeyGameState.IDLE;
        public int remainingLives = 3;
    }

    private static final String TEXTURE_FORMAT = "textures/block/simple_memory_key/memory_key_core_%s.png";
    private static final String TEXTURE_FORMAT_LIVES = "textures/block/simple_memory_key/memory_key_core_%s_%d.png";

    public BlockMemoryKeyCoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context, true, PAINTING_LIKE);
    }

    @Override
    public @NonNull BlockMemoryKeyCoreEntityRenderState createRenderState() {
        return new BlockMemoryKeyCoreEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull BlockMemoryKeyCoreEntity blockEntity, @NonNull BlockMemoryKeyCoreEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
        state.gameState = blockEntity.getGameState();
        state.remainingLives = blockEntity.getRemainingLives();
    }

    @Override
    protected Identifier getTextureForState(BlockMemoryKeyCoreEntityRenderState state) {
        if (!state.unfolded) {
            return SimpleBlockGame.getId(String.format(TEXTURE_FORMAT, "folded"));
        }
        if (state.gameState == MemoryKeyGameState.ALL_SUCCESS || state.gameState == MemoryKeyGameState.GAME_OVER) {
            return SimpleBlockGame.getId(String.format(TEXTURE_FORMAT, state.gameState.getSerializedName()));
        }
        return SimpleBlockGame.getId(String.format(TEXTURE_FORMAT_LIVES, state.gameState.getSerializedName(), state.remainingLives));
    }
}
