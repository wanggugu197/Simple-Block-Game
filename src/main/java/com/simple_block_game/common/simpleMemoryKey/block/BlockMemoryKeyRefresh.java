package com.simple_block_game.common.simpleMemoryKey.block;

import com.simple_block_game.common.base.block.BaseVerticalRefreshBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

/** 记忆键游戏的刷新控制方块 */
public class BlockMemoryKeyRefresh extends BaseVerticalRefreshBlock {

    private static final MapCodec<BlockMemoryKeyRefresh> CODEC = simpleCodec(BlockMemoryKeyRefresh::new);

    public BlockMemoryKeyRefresh(BlockBehaviour.Properties properties) {
        super(properties,
                "msg.memory_key.core_not_found",
                "msg.memory_key.core_invalid",
                "msg.memory_key.minimized",
                "msg.memory_key.closed",
                "msg.memory_key.reset");
    }

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalRefreshBlock> codec() {
        return CODEC;
    }
}
