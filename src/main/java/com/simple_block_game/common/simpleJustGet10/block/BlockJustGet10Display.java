package com.simple_block_game.common.simpleJustGet10.block;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.simpleJustGet10.data.ValueJustGet10;
import com.simple_block_game.util.multiVersion.MultiVersionHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

public class BlockJustGet10Display extends BaseRotatedBlock {

    public static final MapCodec<BlockJustGet10Display> CODEC = simpleCodec(BlockJustGet10Display::new);

    public BlockJustGet10Display(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseRotatedBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockJustGet10DisplayEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (!SimpleBlockGameConfig.enableJustGet10Game.get()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        ServerLevel serverLevel = (ServerLevel) level;
        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (!(be instanceof BlockJustGet10DisplayEntity de)) return InteractionResult.PASS;
        BlockPos corePos = de.getCorePos();
        if (corePos == null) {
            MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.just_get_10.core_not_found"), true);
            return InteractionResult.FAIL;
        }
        BlockState coreState = serverLevel.getBlockState(corePos);
        if (!(coreState.getBlock() instanceof BlockJustGet10Core core)) {
            MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.just_get_10.core_invalid"), true);
            return InteractionResult.FAIL;
        }
        core.handleDisplayClick(serverLevel, pos, player);
        return InteractionResult.SUCCESS;
    }

    public static void setDisplayValue(BlockGetter level, BlockPos pos, ValueJustGet10 value) {
        if (level == null || pos == null) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockJustGet10DisplayEntity entity) entity.setValue(value);
    }

    public static ValueJustGet10 getDisplayValue(BlockGetter level, BlockPos pos) {
        if (level == null || pos == null) return null;
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof BlockJustGet10DisplayEntity entity ? entity.getValue() : null;
    }

    public static void setCorePos(BlockGetter level, BlockPos pos, BlockPos corePos) {
        if (level == null || pos == null) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockJustGet10DisplayEntity entity) entity.setCorePos(corePos);
    }

    public static void setHighlighted(BlockGetter level, BlockPos pos, boolean highlighted) {
        if (level == null || pos == null) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockJustGet10DisplayEntity entity) entity.setHighlighted(highlighted);
    }
}
