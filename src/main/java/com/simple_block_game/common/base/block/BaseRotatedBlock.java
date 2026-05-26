package com.simple_block_game.common.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;

import java.util.function.Supplier;

import javax.annotation.Nullable;

/** 水平旋转方块基类，支持水平方向朝向 */
public abstract class BaseRotatedBlock extends BaseEntityBlock {

    public static final EnumProperty<@NonNull Direction> FACING = HorizontalDirectionalBlock.FACING;

    protected BaseRotatedBlock(BlockBehaviour.Properties properties) {
        super(properties.mapColor(MapColor.TERRACOTTA_WHITE)
                .strength(100000.0F, 640000.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK)
                .noLootTable());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(@NonNull StateDefinition.Builder<Block, @NonNull BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NonNull BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected static <B extends BaseRotatedBlock> MapCodec<B> simpleCodec(Supplier<B> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(propertiesCodec()).apply(instance, (_) -> factory.get()));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return null;
    }

    @Override
    public @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }
}
