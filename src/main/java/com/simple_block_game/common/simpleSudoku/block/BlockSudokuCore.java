package com.simple_block_game.common.simpleSudoku.block;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleSudoku.data.Difficulty;
import com.simple_block_game.common.simpleSudoku.data.SudokuGameState;
import com.simple_block_game.common.simpleSudoku.logic.GameSudokuHelper;
import com.simple_block_game.common.simpleSudoku.logic.GameSudokuReward;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

public class BlockSudokuCore extends BaseVerticalBlock implements IGameCoreBlock {

    public BlockSudokuCore(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(UNFOLDED, false));
    }

    public static final MapCodec<BlockSudokuCore> CODEC = simpleCodec(BlockSudokuCore::new);

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(@NonNull StateDefinition.Builder<Block, @NonNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UNFOLDED);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockSudokuCoreEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                     @NonNull BlockPos pos, @NonNull Player player,
                                                     @NonNull BlockHitResult hit) {
        if (!SimpleBlockGameConfig.enableSudokuGame.get()) return InteractionResult.PASS;

        if (level.isClientSide()) return InteractionResult.SUCCESS;
        ServerLevel serverLevel = (ServerLevel) level;

        if (hit.getDirection() == Direction.UP) {
            return handleTopClick(serverLevel, state, pos, player, hit);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleTopClick(ServerLevel level, BlockState state, BlockPos pos, Player player, BlockHitResult hit) {
        ClickArea area = getClickArea(hit, pos);
        if (area == ClickArea.NONE) return InteractionResult.PASS;

        BlockSudokuCoreEntity core = getCoreEntity(level, pos);
        if (core == null) {
            player.displayClientMessage(Component.translatable("msg.common.entity_error"), true);
            return InteractionResult.PASS;
        }
        if (core.getGameState() == SudokuGameState.PLAYING) {
            return InteractionResult.PASS;
        }

        return switch (area) {
            case CENTER -> {
                if (!state.getValue(UNFOLDED)) {
                    unfoldGame(level, pos, state, player);
                } else {
                    startGame(level, pos, state, player);
                }
                yield InteractionResult.SUCCESS;
            }
            case DIFFICULTY_UP -> {
                switchDifficulty(level, pos, player, true);
                yield InteractionResult.SUCCESS;
            }
            case DIFFICULTY_DOWN -> {
                switchDifficulty(level, pos, player, false);
                yield InteractionResult.SUCCESS;
            }
            case DIAGONAL_TOGGLE -> {
                toggleDiagonalMode(level, pos, player);
                yield InteractionResult.SUCCESS;
            }
            default -> InteractionResult.PASS;
        };
    }

    private static final double CORNER_THRESHOLD = 4.0;
    private static final double CENTER_MIN = CORNER_THRESHOLD;
    private static final double CENTER_MAX = 16.0 - CORNER_THRESHOLD;

    private ClickArea getClickArea(BlockHitResult hit, BlockPos pos) {
        Vec3 worldPos = hit.getLocation();
        double u = Mth.clamp((worldPos.x - pos.getX()) * 16, 0, 16);
        double v = Mth.clamp((worldPos.z - pos.getZ()) * 16, 0, 16);

        if (u >= CENTER_MIN && u <= CENTER_MAX && v >= CENTER_MIN && v <= CENTER_MAX) {
            return ClickArea.CENTER;
        }
        if (u <= CORNER_THRESHOLD && v >= CENTER_MAX) return ClickArea.DIFFICULTY_UP;
        if (u <= CORNER_THRESHOLD && v <= CORNER_THRESHOLD) return ClickArea.DIFFICULTY_DOWN;
        if (u >= CENTER_MAX && v >= CENTER_MAX) return ClickArea.DIAGONAL_TOGGLE;
        return ClickArea.NONE;
    }

    private void switchDifficulty(ServerLevel level, BlockPos pos, Player player, boolean forward) {
        BlockSudokuCoreEntity core = getCoreEntityOrError(level, pos, player);
        if (core == null) return;

        Difficulty newDifficulty = forward ? core.getDifficulty().next() : core.getDifficulty().prev();
        core.setDifficulty(newDifficulty);
        player.displayClientMessage(Component.translatable("msg.sudoku.difficulty_switched",
                Component.translatable(newDifficulty.getDisplayName())), true);
    }

    private void toggleDiagonalMode(ServerLevel level, BlockPos pos, Player player) {
        BlockSudokuCoreEntity core = getCoreEntityOrError(level, pos, player);
        if (core == null) return;

        boolean newDiagonalMode = !core.isDiagonalMode();
        core.setDiagonalMode(newDiagonalMode);
        player.displayClientMessage(Component.translatable(newDiagonalMode ? "msg.sudoku.diagonal_enabled" : "msg.sudoku.diagonal_disabled"), true);
    }

    private BlockSudokuCoreEntity getCoreEntityOrError(ServerLevel level, BlockPos pos, Player player) {
        BlockSudokuCoreEntity core = getCoreEntity(level, pos);
        if (core == null) {
            player.displayClientMessage(Component.translatable("msg.common.entity_error"), true);
        }
        return core;
    }

    private enum ClickArea {
        CENTER,
        DIFFICULTY_UP,
        DIFFICULTY_DOWN,
        DIAGONAL_TOGGLE,
        NONE
    }

    private BlockSudokuCoreEntity getCoreEntity(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof BlockSudokuCoreEntity coreEntity ? coreEntity : null;
    }

    public static void reset(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BlockSudokuCoreEntity coreEntity)) return;
        coreEntity.reset();
    }

    public void setDiagonalMode(ServerLevel level, BlockPos pos, boolean diagonalMode) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BlockSudokuCoreEntity coreEntity)) return;
        coreEntity.setDiagonalMode(diagonalMode);
    }

    public void onComplete(ServerLevel level, BlockPos pos, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BlockSudokuCoreEntity coreEntity)) return;

        coreEntity.setGameState(SudokuGameState.COMPLETE);
        GameSudokuReward.handleCompleteReward(level, player, coreEntity.getDifficulty());
        player.displayClientMessage(Component.translatable("msg.sudoku.complete"), true);
    }

    @Override
    public boolean checkLayoutAreaIsEmpty(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        return GameSudokuHelper.checkLayoutAreaIsEmpty(serverLevel, pos);
    }

    @Override
    public boolean unfoldGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player) {
        if (!checkLayoutAreaIsEmpty(serverLevel, pos, state)) {
            return false;
        }

        GameSudokuHelper.generateLayout(serverLevel, pos, state.getValue(FACING));
        serverLevel.setBlock(pos, state.setValue(UNFOLDED, true), Block.UPDATE_ALL);

        BlockSudokuCoreEntity coreEntity = getCoreEntity(serverLevel, pos);
        if (coreEntity != null) {
            GameSudokuHelper.initGame(serverLevel, pos, coreEntity.getDifficulty(), coreEntity.isDiagonalMode());
            coreEntity.setGameState(SudokuGameState.PLAYING);
        }

        player.displayClientMessage(Component.translatable("msg.sudoku.game_started"), true);
        return true;
    }

    @Override
    public void startGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player) {
        BlockSudokuCoreEntity coreEntity = getCoreEntity(serverLevel, pos);
        if (coreEntity != null) {
            GameSudokuHelper.initGame(serverLevel, pos, coreEntity.getDifficulty(), coreEntity.isDiagonalMode());
            coreEntity.setGameState(SudokuGameState.PLAYING);
            player.displayClientMessage(Component.translatable("msg.sudoku.game_started"), true);
        }
    }

    @Override
    public void resetGame(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        GameSudokuHelper.resetLayout(serverLevel, pos);
    }

    @Override
    public void minimizeGame(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        GameSudokuHelper.minimizeLayout(serverLevel, pos);
    }

    @Override
    public void closeGame(ServerLevel serverLevel, BlockPos pos, BlockState state) {
        GameSudokuHelper.closeLayout(serverLevel, pos);
    }

    @Override
    public BlockEntity getGameCoreEntity(ServerLevel serverLevel, BlockPos pos) {
        return serverLevel.getBlockEntity(pos);
    }
}
