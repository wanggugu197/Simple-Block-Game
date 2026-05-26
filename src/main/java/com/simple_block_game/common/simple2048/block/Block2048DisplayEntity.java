package com.simple_block_game.common.simple2048.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.simple2048.data.Value2048;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.jspecify.annotations.NonNull;

/** 2048游戏显示方块实体，存储显示数值 */
public class Block2048DisplayEntity extends BlockEntity {

    private static final String NBT_KEY = "DisplayValue";
    private Value2048 value = Value2048.ZERO;

    public Block2048DisplayEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_2048_DISPLAY_ENTITY.get(), pos, state);
    }

    public int getDisplayValue() {
        return value.getValue();
    }

    public void setDisplayValue(Value2048 newValue) {
        if (newValue == null || value == newValue) return;
        value = newValue;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt(NBT_KEY, value.getValue());
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        HolderLookup.Provider registries = level != null ? level.registryAccess() : RegistryAccess.EMPTY;
        return ClientboundBlockEntityDataPacket.create(this, (be, _) -> be.getUpdateTag(registries));
    }
}
