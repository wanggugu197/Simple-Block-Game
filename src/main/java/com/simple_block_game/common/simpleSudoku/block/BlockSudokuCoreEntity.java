package com.simple_block_game.common.simpleSudoku.block;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleSudoku.data.SudokuDifficulty;
import com.simple_block_game.common.simpleSudoku.data.SudokuGameState;
import com.simple_block_game.common.simpleSudoku.simpleSudokuRegistration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
        super(simpleSudokuRegistration.BLOCK_SUDOKU_CORE_ENTITY.get(), pos, state);
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
    protected void saveAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        CompoundTag tag = new CompoundTag();
        tag.putString(KEY_STATE, gameState.getSerializedName());
        tag.putString(KEY_DIFFICULTY, difficulty.getSerializedName());
        tag.putBoolean(KEY_DIAGONAL, diagonalMode);
        pTag.put(DATA_KEY, tag);
    }

    @Override
    protected void loadAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        CompoundTag tag = pTag.contains(DATA_KEY) ? pTag.getCompound(DATA_KEY) : new CompoundTag();
        String stateStr = tag.contains(KEY_STATE) ? tag.getString(KEY_STATE) : SudokuGameState.IDLE.getSerializedName();
        gameState = SudokuGameState.fromSerializedName(stateStr);
        String diffStr = tag.contains(KEY_DIFFICULTY) ? tag.getString(KEY_DIFFICULTY) : SudokuDifficulty.EASY.getSerializedName();
        difficulty = SudokuDifficulty.fromSerializedName(diffStr);
        diagonalMode = tag.getBoolean(KEY_DIAGONAL);
    }
}
