package com.simple_block_game.common.simpleTenDrop.block;

import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.simpleTenDrop.data.DropletLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

public class BlockTenDropDisplay extends BaseVerticalBlock {

    public static final EnumProperty<@NonNull DropletLevel> DROPLET_LEVEL = EnumProperty.create("droplet_level", DropletLevel.class);

    public BlockTenDropDisplay(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DROPLET_LEVEL, DropletLevel.EMPTY));
    }

    public static final MapCodec<BlockTenDropDisplay> CODEC = simpleCodec(BlockTenDropDisplay::new);

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(@NonNull StateDefinition.Builder<Block, @NonNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DROPLET_LEVEL);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockTenDropDisplayEntity(pos, state);
    }

    @Override
    public @org.jspecify.annotations.NonNull InteractionResult useWithoutItem(@org.jspecify.annotations.NonNull BlockState state, @org.jspecify.annotations.NonNull Level level, @org.jspecify.annotations.NonNull BlockPos pos, @org.jspecify.annotations.NonNull Player player, @org.jspecify.annotations.NonNull BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockTenDropDisplayEntity entity) {
            if (entity.handlePlayerClick()) {
                BlockState newState = state.setValue(DROPLET_LEVEL, entity.getDropletLevel());
                level.setBlock(pos, newState, 3);
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    public static void setDropletLevel(BlockGetter level, BlockPos pos, DropletLevel newLevel) {
        if (level == null || pos == null || newLevel == null) return;

        if (level.getBlockEntity(pos) instanceof BlockTenDropDisplayEntity entity) {
            entity.setDropletLevel(newLevel);
        }

        if (level instanceof Level realLevel && !realLevel.isClientSide()) {
            BlockState state = realLevel.getBlockState(pos);
            if (state.hasProperty(DROPLET_LEVEL)) {
                realLevel.setBlock(pos, state.setValue(DROPLET_LEVEL, newLevel), 3);
            }
        }
    }

    public static void setDropletLevel(BlockGetter level, BlockPos pos, int value) {
        setDropletLevel(level, pos, DropletLevel.fromLevel(value));
    }

    public static DropletLevel getDropletLevel(BlockGetter level, BlockPos pos) {
        if (level == null || pos == null) return DropletLevel.EMPTY;

        if (level.getBlockEntity(pos) instanceof BlockTenDropDisplayEntity entity) {
            return entity.getDropletLevel();
        }

        return DropletLevel.EMPTY;
    }

    public static int getDropletLevelValue(BlockGetter level, BlockPos pos) {
        return getDropletLevel(level, pos).getLevel();
    }

    public static void setCorePos(ServerLevel level, BlockPos pos, BlockPos corePos) {
        if (level == null || pos == null) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockTenDropDisplayEntity entity) {
            entity.setCorePos(corePos);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level,
                                                                  @NonNull BlockState state,
                                                                  @NonNull BlockEntityType<T> type) {
        return null;
    }
}
