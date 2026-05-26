package com.simple_block_game.common.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;

/** 游戏核心方块接口，定义游戏生命周期管理 */
public interface IGameCoreBlock {

    BooleanProperty UNFOLDED = BooleanProperty.create("unfolded");

    boolean checkLayoutAreaIsEmpty(ServerLevel serverLevel, BlockPos pos, BlockState state);

    boolean unfoldGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player);

    void startGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player);

    void resetGame(ServerLevel serverLevel, BlockPos pos, BlockState state);

    void minimizeGame(ServerLevel serverLevel, BlockPos pos, BlockState state);

    void closeGame(ServerLevel serverLevel, BlockPos pos, BlockState state);

    boolean isGameUnfolded(BlockState state);

    @Nullable
    BlockEntity getGameCoreEntity(ServerLevel serverLevel, BlockPos pos);
}
