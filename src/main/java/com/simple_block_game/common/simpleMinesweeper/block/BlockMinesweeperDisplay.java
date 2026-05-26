package com.simple_block_game.common.simpleMinesweeper.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.simpleMinesweeper.data.MinesweeperState;
import com.simple_block_game.common.simpleMinesweeper.logic.GameMinesweeperHelper;
import com.simple_block_game.common.simpleMinesweeper.logic.GameMinesweeperLogic;
import com.simple_block_game.common.simpleMinesweeper.logic.GameMinesweeperReward;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockMinesweeperDisplay extends BaseVerticalBlock {

    public static final EnumProperty<@NonNull MinesweeperState> DISPLAY_STATE = EnumProperty.create("display_state", MinesweeperState.class);

    public BlockMinesweeperDisplay(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DISPLAY_STATE, MinesweeperState.UNOPENED));
    }

    private static final MapCodec<BlockMinesweeperDisplay> CODEC = simpleCodec(BlockMinesweeperDisplay::new);

    @Override
    protected @NotNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(@NonNull StateDefinition.Builder<Block, @NonNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DISPLAY_STATE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockMinesweeperDisplayEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (!(be instanceof BlockMinesweeperDisplayEntity displayEntity)) {
            return InteractionResult.FAIL;
        }
        BlockPos corePos = displayEntity.getCorePos();
        if (corePos == null) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.position_error"));
            return InteractionResult.FAIL;
        }
        int[] relativePos = calculateRelativeGridPos(corePos, pos);
        int gridX = relativePos[0];
        int gridZ = relativePos[1];
        BlockEntity coreBe = serverLevel.getBlockEntity(corePos);
        if (!(coreBe instanceof BlockMinesweeperCoreEntity coreEntity)) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.core_not_found"));
            return InteractionResult.FAIL;
        }
        if (gridX < 0 || gridX >= coreEntity.getGridWidth() || gridZ < 0 || gridZ >= coreEntity.getGridHeight()) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.invalid_position"));
            return InteractionResult.FAIL;
        }
        MinesweeperState currentState = displayEntity.getDisplayState();
        if (coreEntity.isGameOver()) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.game_is_over"));
            return InteractionResult.FAIL;
        }
        if (player.isSecondaryUseActive()) {
            handleShiftClick(serverLevel, coreEntity, currentState, player, gridX, gridZ);
        } else {
            handleClick(serverLevel, coreEntity, currentState, player, gridX, gridZ);
        }

        return InteractionResult.SUCCESS;
    }

    private int[] calculateRelativeGridPos(BlockPos corePos, BlockPos displayPos) {
        int gridX = displayPos.getX() - (corePos.getX() + 1);
        int gridZ = displayPos.getZ() - (corePos.getZ() + 1);
        return new int[] { gridX, gridZ };
    }

    private void handleShiftClick(ServerLevel level, BlockMinesweeperCoreEntity coreEntity, MinesweeperState currentState, Player player, int gridX, int gridZ) {
        if (!currentState.isUnopened() && !currentState.isFlagged()) {
            return;
        }
        boolean[][] mineGrid = coreEntity.getMineGrid();
        int totalMineCount = coreEntity.getTotalMineCount();
        int currentFlagCount = coreEntity.getCurrentFlagCount();
        BlockPos corePos = coreEntity.getBlockPos();
        MinesweeperState[][] displayGrid = GameMinesweeperHelper.readDisplayGrid(level, corePos, coreEntity.getGridWidth(), coreEntity.getGridHeight());
        if (GameMinesweeperHelper.isFirstOpen(displayGrid)) {
            return;
        }
        GameMinesweeperLogic.FlagResult flagResult = GameMinesweeperLogic.processFlag(mineGrid, displayGrid, gridX, gridZ, currentFlagCount, totalMineCount);
        if (flagResult.success()) {
            GameMinesweeperHelper.writeDisplayGrid(level, corePos, flagResult.displayGrid());
            coreEntity.setCurrentFlagCount(flagResult.flagCount());
            coreEntity.setChanged();
        }
        if (flagResult.gameWin()) {
            handleWin(level, coreEntity, player);
        }
    }

    private void handleClick(ServerLevel level, BlockMinesweeperCoreEntity coreEntity, MinesweeperState currentState, Player player, int gridX, int gridZ) {
        BlockPos corePos = coreEntity.getBlockPos();
        int width = coreEntity.getGridWidth();
        int height = coreEntity.getGridHeight();
        if (currentState.isNumberState()) {
            boolean isMatched = GameMinesweeperHelper.isFlagCountMatched(level, corePos, gridX, gridZ, currentState.getValue(), width, height);
            if (isMatched) {
                boolean[] over = GameMinesweeperHelper.openSurroundingBlocks(level, coreEntity, gridX, gridZ);
                if (over[0]) {
                    coreEntity.setGameOver(true);
                    GameMinesweeperHelper.generateAllMineExplosions(level, corePos, coreEntity);
                    player.sendOverlayMessage(Component.translatable("msg.minesweeper.game_over"));
                } else if (over[1]) {
                    handleWin(level, coreEntity, player);
                }
                return;
            }
        }
        if (currentState.isUnopened()) {
            MinesweeperState[][] displayGrid = GameMinesweeperHelper.readDisplayGrid(level, corePos, width, height);
            if (GameMinesweeperHelper.isFirstOpen(displayGrid)) {
                coreEntity.setMineGrid(gridX, gridZ);
            }
            boolean[][] mineGrid = coreEntity.getMineGrid();
            GameMinesweeperLogic.FlipResult flipResult = GameMinesweeperLogic.processFlip(
                    mineGrid, displayGrid, gridX, gridZ, coreEntity.getCurrentFlagCount());
            GameMinesweeperHelper.writeDisplayGrid(level, corePos, flipResult.displayGrid());
            if (flipResult.gameOver()) {
                coreEntity.setGameOver(true);
                GameMinesweeperHelper.generateAllMineExplosions(level, corePos, coreEntity);
                player.sendOverlayMessage(Component.translatable("msg.minesweeper.game_over"));
            } else if (flipResult.gameWin()) {
                handleWin(level, coreEntity, player);
            }
        }
    }

    private static void handleWin(ServerLevel level, BlockMinesweeperCoreEntity coreEntity, Player player) {
        coreEntity.setGameOver(true);
        GameMinesweeperReward.handleReward(level, player, coreEntity.getMineContent());
        player.sendOverlayMessage(Component.translatable("msg.minesweeper.game_win"));
    }

    public static void setDisplayState(BlockGetter level, BlockPos pos, MinesweeperState newState) {
        if (level == null || pos == null || newState == null) return;
        if (level.getBlockEntity(pos) instanceof BlockMinesweeperDisplayEntity entity) {
            entity.setDisplayState(newState);
        }
        if (level instanceof Level realLevel && !realLevel.isClientSide()) {
            BlockState state = realLevel.getBlockState(pos);
            if (state.hasProperty(DISPLAY_STATE)) {
                realLevel.setBlock(pos, state.setValue(DISPLAY_STATE, newState), 3);
            }
        }
    }

    public static MinesweeperState getDisplayState(BlockGetter level, BlockPos pos) {
        if (level == null || pos == null) return MinesweeperState.UNOPENED;
        if (level.getBlockEntity(pos) instanceof BlockMinesweeperDisplayEntity entity) {
            return entity.getDisplayState();
        }
        BlockState state = level.getBlockState(pos);
        return state.hasProperty(DISPLAY_STATE) ? state.getValue(DISPLAY_STATE) : MinesweeperState.UNOPENED;
    }
}
