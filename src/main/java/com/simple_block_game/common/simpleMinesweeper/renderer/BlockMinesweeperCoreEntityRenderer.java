package com.simple_block_game.common.simpleMinesweeper.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.base.renderer.BaseGameBlockEntityCubeRenderer;
import com.simple_block_game.common.base.renderer.BaseGameBlockEntityRenderState;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCoreEntity;
import com.simple_block_game.common.simpleMinesweeper.data.PresetDifficulty;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import lombok.NonNull;

public class BlockMinesweeperCoreEntityRenderer extends BaseGameBlockEntityCubeRenderer<BlockMinesweeperCoreEntity, BlockMinesweeperCoreEntityRenderer.BlockMinesweeperCoreEntityRenderState> {

    public static class BlockMinesweeperCoreEntityRenderState extends BaseGameBlockEntityRenderState {

        public boolean unfolded = false;
        public PresetDifficulty difficulty = PresetDifficulty.EASY;
    }

    private static final String TEXTURE_FORMAT = "textures/block/simple_minesweeper/minesweeper_core_%s_%s.png";
    private static final String TEXTURE_FORMAT_CUSTOM = "textures/block/simple_minesweeper/minesweeper_core_%s_%s.png";

    public BlockMinesweeperCoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public BlockMinesweeperCoreEntityRenderState createRenderState() {
        return new BlockMinesweeperCoreEntityRenderState();
    }

    @Override
    public void extractRenderState(BlockMinesweeperCoreEntity blockEntity, BlockMinesweeperCoreEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
        state.difficulty = blockEntity.getPresetDifficulty();
    }

    @Override
    protected CubeTextures getTexturesForState(BlockMinesweeperCoreEntityRenderState state) {
        String stateName = state.unfolded ? "active" : "idle";

        if (state.difficulty == PresetDifficulty.CUSTOM && !state.unfolded) {
            return getCustomTextures();
        } else {
            Identifier front = SimpleBlockGame.getId(String.format(TEXTURE_FORMAT, state.difficulty.getSerializedName(), stateName));
            return new CubeTextures(front, null, null, null, null, null);
        }
    }

    private CubeTextures getCustomTextures() {
        Identifier front = SimpleBlockGame.getId(String.format(TEXTURE_FORMAT_CUSTOM, "custom", "idle"));
        Identifier left = SimpleBlockGame.getId(String.format(TEXTURE_FORMAT_CUSTOM, "z_axis", "decreases"));
        Identifier right = SimpleBlockGame.getId(String.format(TEXTURE_FORMAT_CUSTOM, "z_axis", "increases"));
        Identifier top = SimpleBlockGame.getId(String.format(TEXTURE_FORMAT_CUSTOM, "x_axis", "increases"));
        Identifier bottom = SimpleBlockGame.getId(String.format(TEXTURE_FORMAT_CUSTOM, "x_axis", "decreases"));
        return new CubeTextures(front, null, left, right, top, bottom);
    }
}
