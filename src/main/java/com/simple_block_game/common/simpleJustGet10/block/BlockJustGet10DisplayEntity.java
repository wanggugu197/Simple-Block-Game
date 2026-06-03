package com.simple_block_game.common.simpleJustGet10.block;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleJustGet10.data.ValueJustGet10;
import com.simple_block_game.common.simpleJustGet10.simpleJustGet10Registration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
    protected void saveAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY_VALUE, value.getValue());
        tag.putBoolean(KEY_HIGHLIGHTED, highlighted);
        if (corePos != null) {
            tag.putInt(KEY_CORE_X, corePos.getX());
            tag.putInt(KEY_CORE_Y, corePos.getY());
            tag.putInt(KEY_CORE_Z, corePos.getZ());
        }
        pTag.put(KEY_DATA, tag);
    }

    @Override
    protected void loadAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        CompoundTag tag = pTag.contains(KEY_DATA) ? pTag.getCompound(KEY_DATA) : new CompoundTag();
        int val = tag.getInt(KEY_VALUE);
        value = ValueJustGet10.getByValue(val).orElse(ValueJustGet10.NUM_1);
        highlighted = tag.getBoolean(KEY_HIGHLIGHTED);
        if (tag.contains(KEY_CORE_X) && tag.contains(KEY_CORE_Y) && tag.contains(KEY_CORE_Z)) {
            corePos = new BlockPos(
                    tag.getInt(KEY_CORE_X),
                    tag.getInt(KEY_CORE_Y),
                    tag.getInt(KEY_CORE_Z));
        }
    }
}
