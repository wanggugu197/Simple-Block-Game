package com.simple_block_game.common.simpleSudoku.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleSudoku.data.SudokuDifficulty;
import com.simple_block_game.common.simpleSudoku.data.SudokuGameState;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import lombok.Getter;
import lombok.NonNull;

public class BlockSudokuCoreEntity extends BaseGameBlockEntity {

    private static final String DATA_KEY = "SudokuData";
    private static final String KEY_STATE = "State";
    private static final String KEY_DIFFICULTY = "Difficulty";
    private static final String KEY_DIAGONAL = "Diagonal";

    @Getter
    private SudokuGameState gameState = SudokuGameState.IDLE;
    @Getter
    private SudokuDifficulty difficulty = SudokuDifficulty.EASY;
    @Getter
    private boolean diagonalMode = false;

    public BlockSudokuCoreEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_SUDOKU_CORE_ENTITY.get(), pos, state);
    }

    public void setGameState(SudokuGameState gameState) {
        this.gameState = gameState;
        syncToClient();
    }

    public void setDifficulty(SudokuDifficulty difficulty) {
        this.difficulty = difficulty;
        syncToClient();
    }

    public void setDiagonalMode(boolean diagonalMode) {
        this.diagonalMode = diagonalMode;
        syncToClient();
    }

    public void reset() {
        gameState = SudokuGameState.IDLE;
        syncToClient();
    }

    private void syncToClient() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        tag.putString(KEY_STATE, gameState.getSerializedName());
        tag.putString(KEY_DIFFICULTY, difficulty.getSerializedName());
        tag.putBoolean(KEY_DIAGONAL, diagonalMode);
        output.store(DATA_KEY, CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(DATA_KEY, CompoundTag.CODEC).orElse(new CompoundTag());
        String stateStr = tag.getStringOr(KEY_STATE, SudokuGameState.IDLE.getSerializedName());
        gameState = SudokuGameState.fromSerializedName(stateStr);
        String diffStr = tag.getStringOr(KEY_DIFFICULTY, SudokuDifficulty.EASY.getSerializedName());
        difficulty = SudokuDifficulty.fromSerializedName(diffStr);
        diagonalMode = tag.getBooleanOr(KEY_DIAGONAL, false);
    }
}
