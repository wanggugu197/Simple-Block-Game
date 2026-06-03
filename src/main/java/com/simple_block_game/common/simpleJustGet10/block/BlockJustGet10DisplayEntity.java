package com.simple_block_game.common.simpleJustGet10.block;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleJustGet10.data.ValueJustGet10;
import com.simple_block_game.common.simpleJustGet10.simpleJustGet10Registration;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import lombok.Getter;
import lombok.NonNull;

public class BlockJustGet10DisplayEntity extends BaseGameBlockEntity {

    private static final String KEY_DATA = "JustGet10DisplayData";
    private static final String KEY_VALUE = "DisplayValue";
    private static final String KEY_CORE_X = "CorePosX";
    private static final String KEY_CORE_Y = "CorePosY";
    private static final String KEY_CORE_Z = "CorePosZ";
    private static final String KEY_HIGHLIGHTED = "Highlighted";

    @Getter
    private ValueJustGet10 value = ValueJustGet10.NUM_1;
    @Getter
    private BlockPos corePos;
    @Getter
    private boolean highlighted = false;

    public BlockJustGet10DisplayEntity(BlockPos pos, BlockState state) {
        super(simpleJustGet10Registration.BLOCK_JUST_GET_10_DISPLAY_ENTITY.get(), pos, state);
    }

    public void setValue(ValueJustGet10 value) {
        if (value == null || this.value == value) {
            return;
        }
        this.value = value;
        syncToClient();
    }

    public void setCorePos(BlockPos corePos) {
        this.corePos = corePos;
        syncToClient();
    }

    public void setHighlighted(boolean highlighted) {
        if (this.highlighted == highlighted) {
            return;
        }
        this.highlighted = highlighted;
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
        tag.putInt(KEY_VALUE, value.getValue());
        tag.putBoolean(KEY_HIGHLIGHTED, highlighted);
        if (corePos != null) {
            tag.putInt(KEY_CORE_X, corePos.getX());
            tag.putInt(KEY_CORE_Y, corePos.getY());
            tag.putInt(KEY_CORE_Z, corePos.getZ());
        }
        output.store(KEY_DATA, CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(KEY_DATA, CompoundTag.CODEC).orElse(new CompoundTag());
        int val = tag.getIntOr(KEY_VALUE, 1);
        value = ValueJustGet10.getByValue(val).orElse(ValueJustGet10.NUM_1);
        highlighted = tag.getBooleanOr(KEY_HIGHLIGHTED, false);
        if (tag.contains(KEY_CORE_X) && tag.contains(KEY_CORE_Y) && tag.contains(KEY_CORE_Z)) {
            corePos = new BlockPos(
                    tag.getIntOr(KEY_CORE_X, 0),
                    tag.getIntOr(KEY_CORE_Y, 0),
                    tag.getIntOr(KEY_CORE_Z, 0));
        }
    }
}
