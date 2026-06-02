package com.simple_block_game.common.simpleSudoku.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleSudoku.block.BlockSudokuCoreEntity;
import com.simple_block_game.common.simpleSudoku.data.SudokuDifficulty;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class BlockSudokuCoreEntityRenderer extends BaseBlockEntityRenderer<BlockSudokuCoreEntity> {

    private static final String TEXTURE = "textures/block/simple_sudoku/sudoku_core_%s_%s.png";
    private static final ResourceLocation GRID_TEXTURE = SimpleBlockGame.getId("textures/block/simple_sudoku/sudoku_grid.png");

    public BlockSudokuCoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(BlockSudokuCoreEntity blockEntity) {
        SudokuDifficulty difficulty = blockEntity.getDifficulty();
        boolean diagonalMode = blockEntity.isDiagonalMode();
        return SimpleBlockGame.getId(String.format(TEXTURE, difficulty.getSerializedName(), diagonalMode ? "diagonal" : "normal"));
    }

    @Override
    public void render(BlockSudokuCoreEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        super.render(blockEntity, partialTicks, poseStack, buffer, packedLight, packedOverlay);

        boolean unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
        if (!unfolded) {
            return;
        }

        poseStack.pushPose();
        setupTransformation(poseStack, blockEntity.getFacing());
        poseStack.translate(5f, 5f, OFFSET);
        poseStack.scale(9.0f, 9.0f, 0);

        VertexConsumer consumer = buffer.getBuffer(com.simple_block_game.util.renderer.ModRenderTypes.style(GRID_TEXTURE));
        renderFace(poseStack.last(), consumer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(BlockSudokuCoreEntity blockEntity) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(BlockSudokuCoreEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos).inflate(10);
    }
}
