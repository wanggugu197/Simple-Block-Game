package com.simple_block_game.common.simpleMinesweeper.block;

import com.simple_block_game.common.base.block.BaseVerticalRefreshBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

/** 扫雷游戏的刷新控制方块 */
public class BlockMinesweeperRefresh extends BaseVerticalRefreshBlock {

    private static final MapCodec<BlockMinesweeperRefresh> CODEC = simpleCodec(BlockMinesweeperRefresh::new);

    public BlockMinesweeperRefresh(BlockBehaviour.Properties properties) {
        super(properties,
                "msg.minesweeper.core_not_found",
                "msg.minesweeper.core_invalid",
                "msg.minesweeper.minimized",
                "msg.minesweeper.closed",
                "msg.minesweeper.reset");
    }

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalRefreshBlock> codec() {
        return CODEC;
    }
}
