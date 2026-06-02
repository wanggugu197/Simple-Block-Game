package com.simple_block_game.common.simple24Puzzle.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleDisplayEntity;
import com.simple_block_game.common.simple24Puzzle.data.GameToken24Puzzle;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * 24点显示方块实体渲染器
 * 继承基类，负责根据显示Token选择对应的材质
 */
public class Block24PuzzleDisplayEntityRenderer extends BaseBlockEntityRenderer<Block24PuzzleDisplayEntity> {

    private static final String TEXTURE_PATH_FORMAT = "textures/block/simple24puzzle/24puzzle_display_%s.png";

    public Block24PuzzleDisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(Block24PuzzleDisplayEntity blockEntity) {
        GameToken24Puzzle token = blockEntity.getToken();
        if (token == null) return null;
        return SimpleBlockGame.getId(String.format(TEXTURE_PATH_FORMAT, token.getLowerCaseName()));
    }
}
