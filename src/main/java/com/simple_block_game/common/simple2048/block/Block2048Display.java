package com.simple_block_game.common.simple2048.block;

import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.simple2048.data.Value2048;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

/** 2048游戏显示方块 */
public class Block2048Display extends BaseRotatedBlock {

    public Block2048Display(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static final MapCodec<Block2048Display> CODEC = simpleCodec(Block2048Display::new);

    @Override
    protected @NonNull MapCodec<? extends BaseRotatedBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new Block2048DisplayEntity(pos, state);
    }

    public static void setDisplayValue(BlockGetter level, BlockPos pos, int newValue) {
        if (level == null || pos == null) return;
        Value2048 value = Value2048.fromInt(newValue);
        if (level.getBlockEntity(pos) instanceof Block2048DisplayEntity entity) {
            entity.setDisplayValue(value);
        }
    }

    public static int getDisplayValue(BlockGetter level, BlockPos pos) {
        if (level == null || pos == null) return 0;
        if (level.getBlockEntity(pos) instanceof Block2048DisplayEntity entity) {
            return entity.getDisplayValue();
        }
        return 0;
    }
}
