package com.simple_block_game.common.base.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * 游戏核心方块的公共接口
 * 定义游戏生命周期管理的标准方法
 */
public interface IGameCoreBlock {

    /**
     * 检查游戏布局区域是否为空
     * 
     * @param serverLevel 服务端世界
     * @param pos         核心方块位置
     * @param state       核心方块状态
     * @return 是否可以放置布局
     */
    boolean checkLayoutAreaIsEmpty(ServerLevel serverLevel, BlockPos pos, BlockState state);

    /**
     * 展开游戏（放置布局方块）
     * 
     * @param serverLevel 服务端世界
     * @param pos         核心方块位置
     * @param state       核心方块状态
     * @param player      触发玩家
     * @return 是否展开成功
     */
    boolean unfoldGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player);

    /**
     * 开始/重载游戏（初始化游戏数据）
     * 
     * @param serverLevel 服务端世界
     * @param pos         核心方块位置
     * @param state       核心方块状态
     * @param player      触发玩家
     */
    void startGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player);

    /**
     * 重置游戏（保持布局，重置数据）
     * 
     * @param serverLevel 服务端世界
     * @param pos         核心方块位置
     * @param state       核心方块状态
     */
    void resetGame(ServerLevel serverLevel, BlockPos pos, BlockState state);

    /**
     * 最小化游戏（保留数据，隐藏布局）
     * 
     * @param serverLevel 服务端世界
     * @param pos         核心方块位置
     * @param state       核心方块状态
     */
    void minimizeGame(ServerLevel serverLevel, BlockPos pos, BlockState state);

    /**
     * 关闭游戏（移除布局，清理数据）
     * 
     * @param serverLevel 服务端世界
     * @param pos         核心方块位置
     * @param state       核心方块状态
     */
    void closeGame(ServerLevel serverLevel, BlockPos pos, BlockState state);

    /**
     * 获取游戏是否已展开
     * 
     * @param state 核心方块状态
     * @return 是否已展开
     */
    boolean isGameUnfolded(BlockState state);

    /**
     * 获取游戏核心实体
     * 
     * @param serverLevel 服务端世界
     * @param pos         核心方块位置
     * @return 游戏核心实体，如果不存在返回null
     */
    @Nullable
    BlockEntity getGameCoreEntity(ServerLevel serverLevel, BlockPos pos);
}
