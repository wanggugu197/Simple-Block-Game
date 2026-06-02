package com.simple_block_game.common.simpleTenDrops.block;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleTenDrops.data.TenDropsGameState;
import com.simple_block_game.common.simpleTenDrops.logic.GameTenDropsHelper;
import com.simple_block_game.util.multiVersion.MultiVersionHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

public class BlockTenDropsCore extends BaseVerticalBlock implements IGameCoreBlock {

    public BlockTenDropsCore(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(UNFOLDED, false));
    }

    private static final MapCodec<BlockTenDropsCore> CODEC = simpleCodec(BlockTenDropsCore::new);

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UNFOLDED);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockTenDropsCoreEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return (_, _, _, entity) -> {
            if (entity instanceof BlockTenDropsCoreEntity coreEntity) coreEntity.tick();
        };
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                     @NonNull BlockPos pos, @NonNull Player player,
                                                     @NonNull BlockHitResult hit) {
        if (!SimpleBlockGameConfig.enableTenDropGame.get()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.FAIL;

        if (!state.getValue(UNFOLDED)) {
            if (!checkLayoutAreaIsEmpty(serverLevel, pos, state)) {
                MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.common.obstructed"), true);
                return InteractionResult.PASS;
            }
            unfoldGame(serverLevel, pos, state, player);
            MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.ten_drops.game_started"), true);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos pos, BlockState state) {
        return GameTenDropsHelper.checkLayoutAreaIsEmpty(level, pos);
    }

    @Override
    public boolean unfoldGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        GameTenDropsHelper.generateLayout(level, pos);
        level.setBlock(pos, state.setValue(UNFOLDED, true), 3);

        getCoreEntity(level, pos).ifPresent(BlockTenDropsCoreEntity::initialize);
        startGame(level, pos, state, player);
        return true;
    }

    @Override
    public void startGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        getCoreEntity(level, pos).ifPresent(entity -> {
            entity.setGameState(TenDropsGameState.PLAYING);
            MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.ten_drops.start"), true);
        });
    }

    @Override
    public void resetGame(ServerLevel level, BlockPos pos, BlockState state) {
        getCoreEntity(level, pos).ifPresent(BlockTenDropsCoreEntity::completeReset);
    }

    @Override
    public void minimizeGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameTenDropsHelper.minimizeLayout(level, pos);
        level.setBlock(pos, state.setValue(UNFOLDED, false), 3);
    }

    @Override
    public void closeGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameTenDropsHelper.destroyLayout(level, pos);
    }

    @Override
    public BlockEntity getGameCoreEntity(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    private java.util.Optional<BlockTenDropsCoreEntity> getCoreEntity(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof BlockTenDropsCoreEntity entity ? java.util.Optional.of(entity) : java.util.Optional.empty();
    }
}
