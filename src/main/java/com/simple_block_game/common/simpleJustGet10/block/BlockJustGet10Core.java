package com.simple_block_game.common.simpleJustGet10.block;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleJustGet10.data.JustGet10GameState;
import com.simple_block_game.common.simpleJustGet10.data.ValueJustGet10;
import com.simple_block_game.common.simpleJustGet10.logic.GameJustGet10Helper;
import com.simple_block_game.common.simpleJustGet10.logic.GameJustGet10Reward;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

public class BlockJustGet10Core extends BaseRotatedBlock implements IGameCoreBlock {

    public static final MapCodec<BlockJustGet10Core> CODEC = simpleCodec(BlockJustGet10Core::new);

    public BlockJustGet10Core(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(UNFOLDED, false));
    }

    @Override
    protected @NonNull MapCodec<? extends BaseRotatedBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(@NonNull StateDefinition.Builder<Block, @NonNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UNFOLDED);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockJustGet10CoreEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (!SimpleBlockGameConfig.enableJustGet10Game.get()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ServerLevel serverLevel = (ServerLevel) level;
        if (!state.getValue(UNFOLDED)) {
            if (!checkLayoutAreaIsEmpty(serverLevel, pos, state)) {
                MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.common.obstructed"), true);
                return InteractionResult.PASS;
            }
            unfoldGame(serverLevel, pos, state, player);
        }
        return InteractionResult.SUCCESS;
    }

    public void handleDisplayClick(ServerLevel serverLevel, BlockPos displayPos, Player player) {
        BlockJustGet10CoreEntity core = getCoreEntity(serverLevel, displayPos);
        if (core == null) return;
        BlockPos corePos = core.getBlockPos();
        Direction facing = core.getBlockState().getValue(FACING);
        BlockPos gridPos = GameJustGet10Helper.getGridPosition(corePos, displayPos, facing);
        if (gridPos == null) return;
        int row = gridPos.getX();
        int col = gridPos.getZ();
        if (core.getGameState() == JustGet10GameState.IDLE) {
            core.setGameState(JustGet10GameState.PLAYING);
        }
        boolean clickedHighlighted = isHighlighted(serverLevel, displayPos);
        var highlights = GameJustGet10Helper.getHighlightPositions(serverLevel, corePos, facing, row, col);
        GameJustGet10Helper.clearAllHighlights(serverLevel, corePos, facing);
        if (highlights.isEmpty()) return;
        if (clickedHighlighted && highlights.size() >= 2) {
            processMerge(serverLevel, core, corePos, facing, row, col,
                    GameJustGet10Helper.getDisplayPosition(corePos, row, col, facing), player);
        } else {
            GameJustGet10Helper.setHighlights(serverLevel, corePos, facing, highlights);
        }
    }

    private boolean isHighlighted(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof BlockJustGet10DisplayEntity entity && entity.isHighlighted();
    }

    private void processMerge(ServerLevel serverLevel, BlockJustGet10CoreEntity core,
                              BlockPos corePos, Direction facing, int row, int col,
                              BlockPos selectedPos, Player player) {
        var result = GameJustGet10Helper.processMerge(serverLevel, corePos, facing, row, col, core.getScore());
        if (result.success()) {
            int oldScore = core.getScore();
            int oldMax = core.getMaxValue().getValue();
            int earned = result.scoreGained();
            core.addScore(earned);
            ValueJustGet10 val = GameJustGet10Helper.getDisplayValue(serverLevel, selectedPos);
            if (val != null) core.setMaxValue(val);
            MultiVersionHelper.sendPlayerMessage(player,
                    Component.translatable("msg.just_get_10.score", earned, core.getScore()), true);
            GameJustGet10Reward.handleScoreReward(serverLevel, player, oldScore, core.getScore());
            GameJustGet10Reward.handleMaxValueReward(serverLevel, player, oldMax, core.getMaxValue().getValue());
            if (GameJustGet10Helper.checkGameOver(serverLevel, corePos, facing)) {
                core.setGameState(JustGet10GameState.GAME_OVER);
                MultiVersionHelper.sendPlayerMessage(player,
                        Component.translatable("msg.just_get_10.game_over", core.getMaxValue().getValue(), core.getScore()), true);
            }
        }
    }

    private BlockJustGet10CoreEntity getCoreEntity(ServerLevel level, BlockPos displayPos) {
        BlockEntity be = level.getBlockEntity(displayPos);
        if (!(be instanceof BlockJustGet10DisplayEntity de)) return null;
        BlockPos corePos = de.getCorePos();
        if (corePos == null) return null;
        BlockEntity coreBe = level.getBlockEntity(corePos);
        return coreBe instanceof BlockJustGet10CoreEntity ? (BlockJustGet10CoreEntity) coreBe : null;
    }

    @Override
    public boolean checkLayoutAreaIsEmpty(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        return GameJustGet10Helper.checkLayoutAreaIsEmpty(serverLevel, pos, state.getValue(FACING));
    }

    @Override
    public boolean unfoldGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player) {
        Direction facing = state.getValue(FACING);
        if (!checkLayoutAreaIsEmpty(serverLevel, pos, state)) {
            MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.common.obstructed"), true);
            return false;
        }
        GameJustGet10Helper.generateLayout(serverLevel, pos, facing);
        serverLevel.setBlock(pos, state.setValue(UNFOLDED, true), Block.UPDATE_ALL);
        GameJustGet10Helper.initBoard(serverLevel, pos, facing);
        if (serverLevel.getBlockEntity(pos) instanceof BlockJustGet10CoreEntity core) {
            core.completeReset();
            core.setGameState(JustGet10GameState.PLAYING);
        }
        MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.just_get_10.game_started"), true);
        return true;
    }

    @Override
    public void startGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player) {
        GameJustGet10Helper.initBoard(serverLevel, pos, state.getValue(FACING));
        if (serverLevel.getBlockEntity(pos) instanceof BlockJustGet10CoreEntity core) {
            core.completeReset();
            core.setGameState(JustGet10GameState.PLAYING);
        }
        MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.just_get_10.game_started"), true);
    }

    @Override
    public void resetGame(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        GameJustGet10Helper.resetLayout(serverLevel, pos, state.getValue(FACING));
    }

    @Override
    public void minimizeGame(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        GameJustGet10Helper.minimizeLayout(serverLevel, pos, state.getValue(FACING));
    }

    @Override
    public void closeGame(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        GameJustGet10Helper.closeLayout(serverLevel, pos, state.getValue(FACING));
    }

    @Override
    public BlockEntity getGameCoreEntity(ServerLevel serverLevel, BlockPos pos) {
        return serverLevel.getBlockEntity(pos);
    }
}
