package com.simple_block_game.common.simpleTenDrops.block;

import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.simpleTenDrops.logic.GameTenDropsHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

public class BlockTenDropsDisplay extends BaseVerticalBlock {

    public BlockTenDropsDisplay(Properties properties) {
        super(properties);
    }

    private static final MapCodec<BlockTenDropsDisplay> CODEC = simpleCodec(BlockTenDropsDisplay::new);

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockTenDropsDisplayEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockTenDropsDisplayEntity entity) {
            if (GameTenDropsHelper.handleDisplayClick((ServerLevel) level, entity, player)) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
