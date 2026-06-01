package com.simple_block_game.common.simpleMinesweeper.renderer;

import com.simple_block_game.SimpleBlockGame;
import com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperDisplayEntity;
import com.simple_block_game.common.simpleMinesweeper.data.MinesweeperState;
import com.simple_block_game.util.renderer.BaseBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * 扫雷显示方块实体渲染器
 * 继承基类，负责根据方块状态的 display_state 属性选择对应的材质
 */
public class BlockMinesweeperDisplayEntityRenderer extends BaseBlockEntityRenderer<BlockMinesweeperDisplayEntity> {

    private static final String TEXTURE_PATH_FORMAT = "textures/block/simple_minesweeper/minesweeper_display_%s.png";

    public BlockMinesweeperDisplayEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ResourceLocation getTexture(BlockMinesweeperDisplayEntity blockEntity) {
        MinesweeperState displayState = blockEntity.getState();
        long tick = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0L;
        boolean flipped = (tick % 20) < 10;

        String textureName;
        if (displayState.isWrongFlag()) {
            textureName = flipped ? "flagged" : displayState.getWrongNumber().getSerializedName();
        } else {
            textureName = displayState.getSerializedName();
        }
        return SimpleBlockGame.getId(String.format(TEXTURE_PATH_FORMAT, textureName));
    }
}
