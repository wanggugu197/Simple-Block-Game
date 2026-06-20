package com.simple_block_game.common.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.mapleutillib.api.baseBlock.DirectionBlockEntity;

public abstract class BaseGameBlockEntity extends DirectionBlockEntity {

    protected BaseGameBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
