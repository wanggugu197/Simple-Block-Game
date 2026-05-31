package com.simple_block_game.common.simpleSudoku.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuCoreEntity;
import com.simple_block_game.common.simpleSudoku.data.Difficulty;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.NonNull;

public class BlockSudokuCoreEntityRenderer extends BaseBlockEntityRenderer<BlockSudokuCoreEntity, BlockSudokuCoreEntityRenderer.BlockSudokuCoreEntityRenderState> {

    public static class BlockSudokuCoreEntityRenderState extends GameBlockEntityRenderState {

        public boolean unfolded = false;
        public Difficulty difficulty = Difficulty.EASY;
        public boolean diagonalMode = false;
    }

    private static final String TEXTURE = "textures/block/simple_sudoku/sudoku_core_%s_%s.png";
    private static final Identifier GRID_TEXTURE = SimpleBlockGame.getId("textures/block/simple_sudoku/sudoku_grid.png");

    public BlockSudokuCoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public BlockSudokuCoreEntityRenderState createRenderState() {
        return new BlockSudokuCoreEntityRenderState();
    }

    @Override
    public void extractRenderState(BlockSudokuCoreEntity blockEntity, BlockSudokuCoreEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
        state.difficulty = blockEntity.getDifficulty();
        state.diagonalMode = blockEntity.isDiagonalMode();
    }

    @Override
    protected Identifier getTextureForState(BlockSudokuCoreEntityRenderState state) {
        return SimpleBlockGame.getId(String.format(TEXTURE, state.difficulty.getSerializedName(), state.diagonalMode ? "diagonal" : "normal"));
    }

    @Override
    public void submit(BlockSudokuCoreEntityRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);

        poseStack.pushPose();
        if (!state.unfolded) {
            poseStack.popPose();
            return;
        }
        setupTransformation(poseStack, state.facing);
        poseStack.translate(5f, 5f, OFFSET);
        poseStack.scale(9.0f, 9.0f, 0);
        submitTexture(poseStack, collector, GRID_TEXTURE);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public boolean shouldRender(BlockSudokuCoreEntity blockEntity, Vec3 cameraPosition) {
        return Vec3.atCenterOf(blockEntity.getBlockPos())
                .multiply(1.0, 0.0, 1.0)
                .closerThan(cameraPosition.multiply(1.0, 0.0, 1.0), this.getViewDistance());
    }

    @Override
    public @NonNull AABB getRenderBoundingBox(BlockSudokuCoreEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos).inflate(10);
    }
}
