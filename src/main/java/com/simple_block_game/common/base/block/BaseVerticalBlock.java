package com.simple_block_game.common.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;

import java.util.function.Supplier;

import javax.annotation.Nullable;

/** 垂直固定方块基类，无方向属性 */
public abstract class BaseVerticalBlock extends BaseEntityBlock {

    protected BaseVerticalBlock(Properties properties) {
        super(properties.mapColor(MapColor.TERRACOTTA_WHITE)
                .strength(100000.0F, 640000.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK)
                .noLootTable());
    }

    protected static <B extends BaseVerticalBlock> MapCodec<B> simpleCodec(Supplier<B> factory) {
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
