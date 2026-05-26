package com.simple_block_game.common.simpleMinesweeper.block;

import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.simpleMinesweeper.data.MinesweeperState;
import com.simple_block_game.common.simpleMinesweeper.logic.GameMinesweeperHelper;
import com.simple_block_game.common.simpleMinesweeper.logic.GameMinesweeperLogic;
import com.simple_block_game.common.simpleMinesweeper.logic.GameMinesweeperReward;

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
import org.jspecify.annotations.NonNull;

/** 扫雷游戏显示方块 */
public class BlockMinesweeperDisplay extends BaseVerticalBlock {

    public static final EnumProperty<MinesweeperState> DISPLAY_STATE = EnumProperty.create("display_state", MinesweeperState.class);

    private static final MapCodec<BlockMinesweeperDisplay> CODEC = simpleCodec(BlockMinesweeperDisplay::new);

    public BlockMinesweeperDisplay(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(DISPLAY_STATE, MinesweeperState.UNOPENED));
    }

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DISPLAY_STATE);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockMinesweeperDisplayEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ServerLevel serverLevel = (ServerLevel) level;
        BlockMinesweeperDisplayEntity display = getDisplayEntity(serverLevel, pos);
        if (display == null) return InteractionResult.FAIL;

        BlockPos corePos = display.getCorePos();
        if (corePos == null) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.position_error"));
            return InteractionResult.FAIL;
        }

        BlockMinesweeperCoreEntity core = getCoreEntity(serverLevel, corePos);
        if (core == null) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.core_not_found"));
            return InteractionResult.FAIL;
        }

        int[] rel = getRelativePos(corePos, pos);
        if (!isValidPos(core, rel[0], rel[1])) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.invalid_position"));
            return InteractionResult.FAIL;
        }

        if (core.isGameOver()) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.game_is_over"));
            return InteractionResult.FAIL;
        }

        MinesweeperState currentState = display.getDisplayState();
        if (player.isSecondaryUseActive()) {
            flag(serverLevel, core, currentState, player, rel[0], rel[1]);
        } else {
            flip(serverLevel, core, currentState, player, rel[0], rel[1]);
        }
        return InteractionResult.SUCCESS;
    }

    private BlockMinesweeperDisplayEntity getDisplayEntity(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof BlockMinesweeperDisplayEntity display ? display : null;
    }

    private BlockMinesweeperCoreEntity getCoreEntity(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof BlockMinesweeperCoreEntity core ? core : null;
    }

    private int[] getRelativePos(BlockPos corePos, BlockPos displayPos) {
        return new int[] { displayPos.getX() - corePos.getX() - 1, displayPos.getZ() - corePos.getZ() - 1 };
    }

    private boolean isValidPos(BlockMinesweeperCoreEntity core, int x, int z) {
        return x >= 0 && x < core.getGridWidth() && z >= 0 && z < core.getGridHeight();
    }

    private void flag(ServerLevel level, BlockMinesweeperCoreEntity core, MinesweeperState state, Player player, int x, int z) {
        if (!state.isUnopened() && !state.isFlagged()) return;

        BlockPos corePos = core.getBlockPos();
        MinesweeperState[][] grid = GameMinesweeperHelper.readDisplayGrid(level, corePos, core.getGridWidth(), core.getGridHeight());
        if (GameMinesweeperHelper.isFirstOpen(grid)) return;

        GameMinesweeperLogic.FlagResult result = GameMinesweeperLogic.processFlag(
                core.getMineGrid(), grid, x, z, core.getCurrentFlagCount(), core.getTotalMineCount());

        if (result.success()) {
            GameMinesweeperHelper.writeDisplayGrid(level, corePos, result.displayGrid());
            core.setCurrentFlagCount(result.flagCount());
            core.setChanged();
        }
        if (result.gameWin()) win(level, core, player);
    }

    private void flip(ServerLevel level, BlockMinesweeperCoreEntity core, MinesweeperState state, Player player, int x, int z) {
        BlockPos corePos = core.getBlockPos();
        int w = core.getGridWidth(), h = core.getGridHeight();

        if (state.isNumberState()) {
            if (GameMinesweeperHelper.isFlagCountMatched(level, corePos, x, z, state.getValue(), w, h)) {
                boolean[] over = GameMinesweeperHelper.openSurrounding(level, core, x, z);
                if (over[0]) lose(level, corePos, core, player);
                else if (over[1]) win(level, core, player);
            }
            return;
        }

        if (state.isUnopened()) {
            MinesweeperState[][] grid = GameMinesweeperHelper.readDisplayGrid(level, corePos, w, h);
            if (GameMinesweeperHelper.isFirstOpen(grid)) core.setMineGrid(x, z);

            GameMinesweeperLogic.FlipResult result = GameMinesweeperLogic.processFlip(
                    core.getMineGrid(), grid, x, z, core.getCurrentFlagCount());

            GameMinesweeperHelper.writeDisplayGrid(level, corePos, result.displayGrid());
            if (result.gameOver()) lose(level, corePos, core, player);
            else if (result.gameWin()) win(level, core, player);
        }
    }

    private void lose(ServerLevel level, BlockPos corePos, BlockMinesweeperCoreEntity core, Player player) {
        core.setGameOver(true);
        GameMinesweeperHelper.generateExplosions(level, corePos, core);
        player.sendOverlayMessage(Component.translatable("msg.minesweeper.game_over"));
    }

    private static void win(ServerLevel level, BlockMinesweeperCoreEntity core, Player player) {
        core.setGameOver(true);
        GameMinesweeperReward.handleReward(level, player, core.getMineContent());
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
