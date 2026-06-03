package com.simple_block_game.common.simpleMemoryKey.block;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyGameState;
import com.simple_block_game.common.simpleMemoryKey.logic.GameMemoryKeyHelper;
import com.simple_block_game.util.multiVersion.MultiVersionHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

/**
 * 记忆键游戏核心方块
 */
public class BlockMemoryKeyCore extends BaseVerticalBlock implements IGameCoreBlock {

    public BlockMemoryKeyCore(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(UNFOLDED, false));
    }

    private static final MapCodec<BlockMemoryKeyCore> CODEC = simpleCodec(BlockMemoryKeyCore::new);

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
        return new BlockMemoryKeyCoreEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
        if (level.isClientSide()) return null;

        return (l, p, s, entity) -> {
            if (entity instanceof BlockMemoryKeyCoreEntity coreEntity) {
                coreEntity.tick();
            }
        };
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (!SimpleBlockGameConfig.enableMemoryKeyGame.get()) return InteractionResult.PASS;

        if (level.isClientSide() || hit.getDirection() != Direction.UP) {
            return hit.getDirection() == Direction.UP ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        if (!state.getValue(UNFOLDED)) {
            unfoldGame(serverLevel, pos, state, player);
        } else {
            BlockMemoryKeyCoreEntity core = getCore(serverLevel, pos);
            if (core != null && core.getGameState() == MemoryKeyGameState.IDLE) {
                startGame(serverLevel, pos, state, player);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private BlockMemoryKeyCoreEntity getCore(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof BlockMemoryKeyCoreEntity core ? core : null;
    }

    @Override
    public boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos pos, BlockState state) {
        return GameMemoryKeyHelper.checkLayoutAreaIsEmpty(level, pos);
    }

    /**
     * 展开记忆键游戏布局
     */
    @Override
    public boolean unfoldGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        if (!checkLayoutAreaIsEmpty(level, pos, state)) {
            MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.common.obstructed"), true);
            return false;
        }
        GameMemoryKeyHelper.generateLayout(level, pos);
        level.setBlock(pos, state.setValue(UNFOLDED, true), Block.UPDATE_ALL);
        BlockMemoryKeyCoreEntity core = getCore(level, pos);
        if (core != null) {
            core.initialize();
            syncBlockState(level, pos);
        }
        MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.memory_key.game_ready"), true);
        return true;
    }

    @Override
    public void startGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        BlockMemoryKeyCoreEntity core = getCore(level, pos);
        if (core == null) {
            MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.common.entity_error"), true);
            return;
        }

        GameMemoryKeyHelper.startGame(level, pos, core);
        MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.memory_key.watch_sequence"), true);
    }

    @Override
    public void resetGame(ServerLevel level, BlockPos pos, BlockState state) {
        BlockMemoryKeyCoreEntity core = getCore(level, pos);
        if (core != null) {
            core.completeReset();
            GameMemoryKeyHelper.resetButtonStates(level, pos);
            syncBlockState(level, pos);
        }
    }

    /**
     * 同步方块状态到客户端
     */
    private void syncBlockState(ServerLevel level, BlockPos pos) {
        BlockState currentState = level.getBlockState(pos);
        if (!currentState.getValue(UNFOLDED)) {
            currentState = currentState.setValue(UNFOLDED, true);
            level.setBlock(pos, currentState, Block.UPDATE_ALL);
        }
        level.sendBlockUpdated(pos, currentState, level.getBlockState(pos), Block.UPDATE_ALL);
    }

    @Override
    public void minimizeGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameMemoryKeyHelper.minimizeLayout(level, pos);
    }

    @Override
    public void closeGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameMemoryKeyHelper.closeLayout(level, pos);
    }

    @Override
    public BlockEntity getGameCoreEntity(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    public static void handleButtonClick(ServerLevel level, BlockPos corePos, int buttonId, Player player) {
        BlockEntity be = level.getBlockEntity(corePos);
        if (!(be instanceof BlockMemoryKeyCoreEntity core)) return;
        GameMemoryKeyHelper.handlePlayerInput(level, corePos, core, buttonId, player);
    }
}
