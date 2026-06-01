package com.simple_block_game.common.simpleMemoryKey.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyCoreEntity;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyGameState;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * 记忆键游戏核心方块实体渲染器
 * 根据游戏状态和生命值选择对应的材质
 */
public class BlockMemoryKeyCoreEntityRenderer extends BaseBlockEntityRenderer<BlockMemoryKeyCoreEntity> {

    private static final String TEXTURE_FORMAT = "textures/block/simple_memory_key/memory_key_core_%s.png";
    private static final String TEXTURE_FORMAT_LIVES = "textures/block/simple_memory_key/memory_key_core_%s_%d.png";

    public BlockMemoryKeyCoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(BlockMemoryKeyCoreEntity blockEntity) {
        boolean unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
        if (!unfolded) {
            return SimpleBlockGame.getId(String.format(TEXTURE_FORMAT, "folded"));
        }
        MemoryKeyGameState gameState = blockEntity.getGameState();
        int remainingLives = blockEntity.getRemainingLives();
        if (gameState == MemoryKeyGameState.ALL_SUCCESS || gameState == MemoryKeyGameState.GAME_OVER) {
            return SimpleBlockGame.getId(String.format(TEXTURE_FORMAT, gameState.getSerializedName()));
        }
        return SimpleBlockGame.getId(String.format(TEXTURE_FORMAT_LIVES, gameState.getSerializedName(), remainingLives));
    }
}
