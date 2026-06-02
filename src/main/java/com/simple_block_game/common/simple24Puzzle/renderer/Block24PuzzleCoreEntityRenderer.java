package com.simple_block_game.common.simple24Puzzle.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.base.renderer.GameBlockEntityRenderState;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleCoreEntity;
import com.simple_block_game.common.simple24Puzzle.data.GameToken24Puzzle;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;
import com.simple_block_game.util.renderer.TextsRenderable;

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

/**
 * 24点核心方块实体渲染器
 * 继承基类，负责根据展开状态选择对应的材质
 */
public class Block24PuzzleCoreEntityRenderer extends BaseBlockEntityRenderer<Block24PuzzleCoreEntity, Block24PuzzleCoreEntityRenderer.Block24PuzzleCoreEntityRenderState> implements TextsRenderable {

    public static class Block24PuzzleCoreEntityRenderState extends GameBlockEntityRenderState {

        public boolean unfolded = false;
        public String puzzleTokens = "";
    }

    private static final Identifier TEXTURE_OPEN = SimpleBlockGame.getId("textures/block/simple24puzzle/24puzzle_core_gaming.png");
    private static final Identifier TEXTURE_CLOSE = SimpleBlockGame.getId("textures/block/simple24puzzle/24puzzle_core_idle.png");

    public Block24PuzzleCoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Block24PuzzleCoreEntityRenderState createRenderState() {
        return new Block24PuzzleCoreEntityRenderState();
    }

    @Override
    public void extractRenderState(Block24PuzzleCoreEntity blockEntity, Block24PuzzleCoreEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
        StringBuilder sb = new StringBuilder();
        for (GameToken24Puzzle token : blockEntity.getInputTokens()) {
            sb.append(token.getSerializedName());
        }
        state.puzzleTokens = sb.toString();
    }

    @Override
    protected Identifier getTextureForState(Block24PuzzleCoreEntityRenderState state) {
        return state.unfolded ? TEXTURE_OPEN : TEXTURE_CLOSE;
    }

    @Override
    public void submit(Block24PuzzleCoreEntityRenderer.Block24PuzzleCoreEntityRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);

        if (state.unfolded && !state.puzzleTokens.isEmpty()) {
            poseStack.pushPose();
            setupTransformation(poseStack, state.facing);
            poseStack.translate(3.5, 2, 0);
            renderText(poseStack, state.puzzleTokens, 0xFF000000);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public @NonNull AABB getRenderBoundingBox(Block24PuzzleCoreEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos).inflate(10);
    }
}
