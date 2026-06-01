package com.simple_block_game.common.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import lombok.NonNull;

public abstract class BaseRotatedBlock extends BaseEntityBlock {

    public static final EnumProperty<@NonNull Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected BaseRotatedBlock(BlockBehaviour.Properties properties) {
        super(properties.mapColor(MapColor.TERRACOTTA_WHITE)
                .strength(100000.0F, 7200000.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK)
                .noLootTable());
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(@NonNull StateDefinition.Builder<Block, @NonNull BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NonNull BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return null;
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
    public void onBlockExploded(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull Explosion explosion) {}

    @Override
    public boolean canBeReplaced(@NonNull BlockState state, @NonNull BlockPlaceContext context) {
        return false;
    }
}
