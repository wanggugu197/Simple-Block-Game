package com.simple_block_game.common.simpleSudoku.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseGameBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
        super(SimpleBlockGameRegistration.BLOCK_SUDOKU_DISPLAY_ENTITY.get(), pos, state);
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
    protected void saveAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY_VALUE, value);
        tag.putBoolean(KEY_IS_INITIAL, isInitial);
        pTag.put(DATA_KEY, tag);
    }

    @Override
    protected void loadAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        CompoundTag tag = pTag.contains(DATA_KEY) ? pTag.getCompound(DATA_KEY) : new CompoundTag();
        value = tag.contains(KEY_VALUE) ? tag.getInt(KEY_VALUE) : 0;
        isInitial = tag.getBoolean(KEY_IS_INITIAL);
    }
}
