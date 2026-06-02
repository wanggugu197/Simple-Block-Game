package com.simple_block_game.common.simple24Puzzle.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleCoreEntity;
import com.simple_block_game.common.simple24Puzzle.data.GameToken24Puzzle;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;
import com.simple_block_game.util.renderer.TextsRenderable;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * 24点核心方块实体渲染器
 * 继承基类，负责根据展开状态选择对应的材质
 */
public class Block24PuzzleCoreEntityRenderer extends BaseBlockEntityRenderer<Block24PuzzleCoreEntity> implements TextsRenderable {

    private static final ResourceLocation TEXTURE_OPEN = SimpleBlockGame.getId("textures/block/simple24puzzle/24puzzle_core_gaming.png");
    private static final ResourceLocation TEXTURE_CLOSE = SimpleBlockGame.getId("textures/block/simple24puzzle/24puzzle_core_idle.png");

    public Block24PuzzleCoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(Block24PuzzleCoreEntity blockEntity) {
        boolean unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
        return unfolded ? TEXTURE_OPEN : TEXTURE_CLOSE;
    }

    @Override
    public void render(Block24PuzzleCoreEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        super.render(blockEntity, partialTicks, poseStack, buffer, packedLight, packedOverlay);

        boolean unfolded = blockEntity.getBlockState().getValue(IGameCoreBlock.UNFOLDED);
        if (unfolded && !blockEntity.getInputTokens().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (GameToken24Puzzle token : blockEntity.getInputTokens()) {
                sb.append(token.getSerializedName());
            }
            String text = sb.toString();

            poseStack.pushPose();
            setupTransformation(poseStack, blockEntity.getFacing());
            poseStack.translate(3.5, 2, 0);
            renderText(poseStack, text, 0xFF000000);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(Block24PuzzleCoreEntity blockEntity) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(Block24PuzzleCoreEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos).inflate(10);
    }
}
