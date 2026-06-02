package com.simple_block_game.common.simpleSudoku.block;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleSudoku.simpleSudokuRegistration;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import lombok.Getter;
import lombok.NonNull;

public class BlockSudokuDisplayEntity extends BaseGameBlockEntity {

    private static final String DATA_KEY = "SudokuDisplayData";
    private static final String KEY_VALUE = "Value";
    private static final String KEY_IS_INITIAL = "IsInitial";

    @Getter
    private int value = 0;
    @Getter
    private boolean isInitial = false;

    public BlockSudokuDisplayEntity(BlockPos pos, BlockState state) {
        super(simpleSudokuRegistration.BLOCK_SUDOKU_DISPLAY_ENTITY.get(), pos, state);
    }

    public void setValue(int value) {
        if (this.value == value) return;
        this.value = value;
        syncToClient();
    }

    public void setInitial(boolean initial) {
        isInitial = initial;
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
        tag.putInt(KEY_VALUE, value);
        tag.putBoolean(KEY_IS_INITIAL, isInitial);
        output.store(DATA_KEY, CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(DATA_KEY, CompoundTag.CODEC).orElse(new CompoundTag());
        value = tag.getIntOr(KEY_VALUE, 0);
        isInitial = tag.getBooleanOr(KEY_IS_INITIAL, false);
    }
}
