package com.simple_block_game.common.simple2048.block;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simple2048.data.Value2048;
import com.simple_block_game.common.simple2048.simple2048Registration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;
import lombok.NonNull;

/**
 * 2048 游戏显示方块实体，存储显示数值、编号和核心方块位置
 */
public class Block2048DisplayEntity extends BaseGameBlockEntity {

    private static final String NBT_KEY_VALUE = "DisplayValue";
    private static final String NBT_KEY_INDEX = "DisplayIndex";
    private static final String NBT_KEY_CORE_X = "CorePosX";
    private static final String NBT_KEY_CORE_Y = "CorePosY";
    private static final String NBT_KEY_CORE_Z = "CorePosZ";

    @Getter
    private Value2048 value = Value2048.ZERO;
    @Getter
    private BlockPos corePos = null;

    public Block2048DisplayEntity(BlockPos pos, BlockState state) {
        super(simple2048Registration.BLOCK_2048_DISPLAY_ENTITY.get(), pos, state);
    }

    public int getDisplayValue() {
        return value.getValue();
    }

    public void setDisplayValue(Value2048 newValue) {
        if (newValue == null) return;
        if (value != null && value.equals(newValue)) return;
        value = newValue;
        syncToClient();
    }

    public void setCorePos(BlockPos corePos) {
        this.corePos = corePos;
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
        tag.putInt(NBT_KEY_VALUE, value.getValue());
        if (corePos != null) {
            tag.putInt(NBT_KEY_CORE_X, corePos.getX());
            tag.putInt(NBT_KEY_CORE_Y, corePos.getY());
            tag.putInt(NBT_KEY_CORE_Z, corePos.getZ());
        }
        pTag.put("2048DisplayData", tag);
    }

    @Override
    protected void loadAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        CompoundTag tag = pTag.contains("2048DisplayData") ? pTag.getCompound("2048DisplayData") : new CompoundTag();
        value = Value2048.fromInt(tag.contains(NBT_KEY_VALUE) ? tag.getInt(NBT_KEY_VALUE) : 0);
        if (tag.contains(NBT_KEY_CORE_X) && tag.contains(NBT_KEY_CORE_Y) && tag.contains(NBT_KEY_CORE_Z)) {
            corePos = new BlockPos(tag.getInt(NBT_KEY_CORE_X), tag.getInt(NBT_KEY_CORE_Y), tag.getInt(NBT_KEY_CORE_Z));
        }
    }
}
