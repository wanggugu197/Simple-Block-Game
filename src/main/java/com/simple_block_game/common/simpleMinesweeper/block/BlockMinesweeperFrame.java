package com.simple_block_game.common.simpleMinesweeper.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

import com.mojang.serialization.MapCodec;
import com.simple_block_game.common.base.block.BaseVerticalBlock;
import org.jetbrains.annotations.NotNull;

public class BlockMinesweeperFrame extends BaseVerticalBlock {

    public BlockMinesweeperFrame(BlockBehaviour.Properties properties) {
        super(properties);
    }

    private static final MapCodec<BlockMinesweeperFrame> CODEC = simpleCodec(BlockMinesweeperFrame::new);

    @Override
    protected @NotNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }
}
