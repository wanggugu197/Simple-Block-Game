package com.simple_block_game.common.simple24Puzzle.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleDisplayEntity;
import com.simple_block_game.common.simple24Puzzle.data.GameToken24Puzzle;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import com.mapleutillib.api.blockEntityRenderer.SingleFaceBlockEntityRenderer;
import org.jspecify.annotations.NonNull;

import static com.mapleutillib.api.blockEntityRenderer.ModRenderTypes.RenderStyle.PAINTING_LIKE;

public class Block24PuzzleDisplayEntityRenderer extends SingleFaceBlockEntityRenderer<Block24PuzzleDisplayEntity, Block24PuzzleDisplayEntityRenderer.Block24PuzzleDisplayEntityRenderState> {

    public static class Block24PuzzleDisplayEntityRenderState extends GameBlockEntityRenderState {

        public GameToken24Puzzle token = null;
    }

    private static final String TEXTURE_PATH_FORMAT = "textures/block/simple24puzzle/24puzzle_display_%s.png";

    public Block24PuzzleDisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context, true, PAINTING_LIKE);
    }

    @Override
    public @NonNull Block24PuzzleDisplayEntityRenderState createRenderState() {
        return new Block24PuzzleDisplayEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull Block24PuzzleDisplayEntity blockEntity, @NonNull Block24PuzzleDisplayEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.token = blockEntity.getToken();
    }

    @Override
    protected Identifier getTextureForState(Block24PuzzleDisplayEntityRenderState state) {
        if (state.token == null) return null;
        return SimpleBlockGame.getId(String.format(TEXTURE_PATH_FORMAT, state.token.getLowerCaseName()));
    }
}
