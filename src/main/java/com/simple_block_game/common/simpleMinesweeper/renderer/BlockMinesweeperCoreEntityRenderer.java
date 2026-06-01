package com.simple_block_game.common.simpleMinesweeper.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCoreEntity;
import com.simple_block_game.common.simpleMinesweeper.data.PresetDifficulty;
import com.simple_block_game.util.renderer.BaseBlockEntityCubeRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BlockMinesweeperCoreEntityRenderer extends BaseBlockEntityCubeRenderer<BlockMinesweeperCoreEntity> {

    private static final String TEXTURE_FORMAT = "textures/block/simple_minesweeper/minesweeper_core_%s_%s.png";
    private static final String TEXTURE_FORMAT_CUSTOM = "textures/block/simple_minesweeper/minesweeper_core_%s_%s.png";

    public BlockMinesweeperCoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected CubeTextures getTextures(BlockMinesweeperCoreEntity blockEntity) {
        boolean unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
        PresetDifficulty difficulty = blockEntity.getPresetDifficulty();
        String stateName = unfolded ? "active" : "idle";

        if (difficulty == PresetDifficulty.CUSTOM && !unfolded) {
            return getCustomTextures();
        } else {
            ResourceLocation front = SimpleBlockGame.getId(String.format(TEXTURE_FORMAT, difficulty.getSerializedName(), stateName));
            return new CubeTextures(front, null, null, null, null, null);
        }
    }

    private CubeTextures getCustomTextures() {
        ResourceLocation front = SimpleBlockGame.getId(String.format(TEXTURE_FORMAT_CUSTOM, "custom", "idle"));
        ResourceLocation left = SimpleBlockGame.getId(String.format(TEXTURE_FORMAT_CUSTOM, "z_axis", "decreases"));
        ResourceLocation right = SimpleBlockGame.getId(String.format(TEXTURE_FORMAT_CUSTOM, "z_axis", "increases"));
        ResourceLocation top = SimpleBlockGame.getId(String.format(TEXTURE_FORMAT_CUSTOM, "x_axis", "increases"));
        ResourceLocation bottom = SimpleBlockGame.getId(String.format(TEXTURE_FORMAT_CUSTOM, "x_axis", "decreases"));
        return new CubeTextures(front, null, left, right, top, bottom);
    }
}
