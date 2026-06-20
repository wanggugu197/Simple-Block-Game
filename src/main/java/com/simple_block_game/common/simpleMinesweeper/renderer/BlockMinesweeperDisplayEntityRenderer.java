package com.simple_block_game.common.simpleMinesweeper.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperDisplayEntity;
import com.simple_block_game.common.simpleMinesweeper.data.MinesweeperState;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import com.mapleutillib.api.blockEntityRenderer.SingleFaceBlockEntityRenderer;
import org.jspecify.annotations.NonNull;

import static com.mapleutillib.api.blockEntityRenderer.ModRenderTypes.RenderStyle.PAINTING_LIKE;

public class BlockMinesweeperDisplayEntityRenderer extends SingleFaceBlockEntityRenderer<BlockMinesweeperDisplayEntity, BlockMinesweeperDisplayEntityRenderer.BlockMinesweeperDisplayEntityRenderState> {

    public static class BlockMinesweeperDisplayEntityRenderState extends GameBlockEntityRenderState {

        public MinesweeperState displayState = MinesweeperState.UNOPENED;
        public boolean flipped;
    }

    private static final String TEXTURE_PATH_FORMAT = "textures/block/simple_minesweeper/minesweeper_display_%s.png";

    public BlockMinesweeperDisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context, true, PAINTING_LIKE);
    }

    @Override
    public @NonNull BlockMinesweeperDisplayEntityRenderState createRenderState() {
        return new BlockMinesweeperDisplayEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull BlockMinesweeperDisplayEntity blockEntity, @NonNull BlockMinesweeperDisplayEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.displayState = blockEntity.getState();
        long tick = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0L;
        state.flipped = (tick % 20) < 10;
    }

    @Override
    protected Identifier getTextureForState(BlockMinesweeperDisplayEntityRenderState state) {
        String a;
        if (state.displayState.isWrongFlag()) {
            a = state.flipped ? "flagged" : state.displayState.getWrongNumber().getSerializedName();
        } else {
            a = state.displayState.getSerializedName();
        }
        return SimpleBlockGame.getId(String.format(TEXTURE_PATH_FORMAT, a));
    }
}
