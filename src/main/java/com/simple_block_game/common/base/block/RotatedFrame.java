package com.simple_block_game.common.base.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

public class RotatedFrame extends BaseRotatedBlock {

    public RotatedFrame(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static final MapCodec<RotatedFrame> CODEC = simpleCodec(RotatedFrame::new);

    @Override
    protected @NotNull MapCodec<? extends BaseRotatedBlock> codec() {
        return CODEC;
    }
}
