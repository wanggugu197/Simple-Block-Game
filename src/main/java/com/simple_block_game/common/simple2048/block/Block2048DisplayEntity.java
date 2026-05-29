package com.simple_block_game.common.simple2048.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simple2048.data.Value2048;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import lombok.Getter;
import lombok.NonNull;

/**
 * 2048游戏显示方块实体，存储显示数值
 */
public class Block2048DisplayEntity extends BaseGameBlockEntity {

    private static final String NBT_KEY = "DisplayValue";
    @Getter
    private Value2048 value = Value2048.ZERO;

    public Block2048DisplayEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_2048_DISPLAY_ENTITY.get(), pos, state);
    }

    public int getDisplayValue() {
        return value.getValue();
    }

    public void setDisplayValue(Value2048 newValue) {
        if (newValue == null) return;
        if (value != null && value.equals(newValue)) return;
        value = newValue;
        setChanged();
        syncToClient();
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        tag.putInt(NBT_KEY, value.getValue());
        output.store("2048DisplayData", CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read("2048DisplayData", CompoundTag.CODEC).orElse(new CompoundTag());
        value = Value2048.fromInt(tag.getIntOr(NBT_KEY, 0));
    }
}
