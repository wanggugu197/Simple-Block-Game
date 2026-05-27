package com.simple_block_game.common.simpleTenDrop.block;

import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleTenDrop.data.TenDropGameState;
import com.simple_block_game.common.simpleTenDrop.logic.GameTenDropHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.NonNull;

public class BlockTenDropCore extends BaseVerticalBlock implements IGameCoreBlock {

    public static final BooleanProperty UNFOLDED = BooleanProperty.create("unfolded");

    public static final IntegerProperty WATER_DROPS = IntegerProperty.create("water_drops", 0, 11);

    public BlockTenDropCore(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any()
                .setValue(UNFOLDED, false)
                .setValue(WATER_DROPS, 10));
    }

    private static final MapCodec<BlockTenDropCore> CODEC = simpleCodec(BlockTenDropCore::new);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNFOLDED, WATER_DROPS);
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos pos, BlockState state) {
        return GameTenDropHelper.checkLayoutAreaIsEmpty(level, pos);
    }

    @Override
    public boolean unfoldGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        GameTenDropHelper.generateLayout(level, pos);
        level.setBlock(pos, state.setValue(UNFOLDED, true), 3);

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockTenDropCoreEntity entity) {
            entity.initialize();
        }

        return true;
    }

    @Override
    public void startGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockTenDropCoreEntity entity) {
            entity.setGameState(TenDropGameState.PLAYING);
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.translatable("msg.ten_drop.start"));
        }
    }

    @Override
    public void resetGame(ServerLevel level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockTenDropCoreEntity entity) {
            entity.completeReset();
        }
    }

    @Override
    public void minimizeGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameTenDropHelper.minimizeLayout(level, pos);
        level.setBlock(pos, state.setValue(UNFOLDED, false), 3);
    }

    @Override
    public void closeGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameTenDropHelper.destroyLayout(level, pos);
        if (state.getBlock() instanceof BlockTenDropCore) {
            dropAsItem(level, pos, state);
        }
    }

    @Override
    public BlockEntity getGameCoreEntity(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    private void dropAsItem(ServerLevel level, BlockPos pos, BlockState state) {
        net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                new net.minecraft.world.item.ItemStack(this));
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }
}
