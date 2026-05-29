package com.simple_block_game.common.base.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.NonNull;

/**
 * 垂直固定框架方块
 */
public class VerticalFrame extends BaseVerticalBlock {

    private static final MapCodec<VerticalFrame> CODEC = simpleCodec(VerticalFrame::new);

    public VerticalFrame(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }
}
