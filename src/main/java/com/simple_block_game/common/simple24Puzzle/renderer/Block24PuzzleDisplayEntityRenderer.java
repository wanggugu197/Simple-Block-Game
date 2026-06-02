package com.simple_block_game.common.simple24Puzzle.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleDisplayEntity;
import com.simple_block_game.common.simple24Puzzle.data.GameToken24Puzzle;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import lombok.NonNull;

/**
 * 24点显示方块实体渲染器
 * 继承基类，负责根据显示Token选择对应的材质
 */
public class Block24PuzzleDisplayEntityRenderer extends BaseBlockEntityRenderer<Block24PuzzleDisplayEntity, Block24PuzzleDisplayEntityRenderer.Block24PuzzleDisplayEntityRenderState> {

    public static class Block24PuzzleDisplayEntityRenderState extends GameBlockEntityRenderState {

        public GameToken24Puzzle token = null;
    }

    private static final String TEXTURE_PATH_FORMAT = "textures/block/simple24puzzle/24puzzle_display_%s.png";

    public Block24PuzzleDisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Block24PuzzleDisplayEntityRenderState createRenderState() {
        return new Block24PuzzleDisplayEntityRenderState();
    }

    @Override
    public void extractRenderState(Block24PuzzleDisplayEntity blockEntity, Block24PuzzleDisplayEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.token = blockEntity.getToken();
    }

    @Override
    protected Identifier getTextureForState(Block24PuzzleDisplayEntityRenderState state) {
        if (state.token == null) return null;
        return SimpleBlockGame.getId(String.format(TEXTURE_PATH_FORMAT, state.token.getLowerCaseName()));
    }
}
