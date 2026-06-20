package com.simple_block_game.common.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import lombok.NonNull;

public abstract class BaseRotatedBlock extends com.mapleutillib.api.baseBlock.BaseRotatedBlock {

    protected BaseRotatedBlock(BlockBehaviour.Properties properties) {
        super(properties.mapColor(MapColor.TERRACOTTA_WHITE)
                .strength(100000.0F, 7200000.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK)
                .isValidSpawn((_, _, _, _) -> false)
                .noLootTable());
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean canEntityDestroy(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull Entity entity) {
        return false;
    }

    @Override
    public void onBlockExploded(@NonNull BlockState state, @NonNull ServerLevel level, @NonNull BlockPos blockPos, @NonNull Explosion explosion) {}

    @Override
    public boolean canBeReplaced(@NonNull BlockState state, @NonNull BlockPlaceContext context) {
        return false;
    }
}
