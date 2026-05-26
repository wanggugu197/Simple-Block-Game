package com.simple_block_game.common.simple2048.block;

import com.simple_block_game.common.base.block.BaseRotatedRefreshBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

/** 2048游戏的刷新控制方块 */
public class Block2048Refresh extends BaseRotatedRefreshBlock {

    public static final MapCodec<Block2048Refresh> CODEC = simpleCodec(Block2048Refresh::new);

    public Block2048Refresh(BlockBehaviour.Properties properties) {
        super(properties,
                "msg.simple2048.core_not_found",
                "msg.simple2048.minimized",
                "msg.simple2048.closed",
                "msg.simple2048.reset");
    }

    @Override
    protected @NonNull MapCodec<? extends BaseRotatedRefreshBlock> codec() {
        return CODEC;
    }
}
