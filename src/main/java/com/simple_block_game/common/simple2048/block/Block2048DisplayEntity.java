package com.simple_block_game.common.simple2048.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.simple2048.data.Value2048;
import org.jetbrains.annotations.NotNull;

public class Block2048DisplayEntity extends BlockEntity {

    private static final String NBT_KEY_DISPLAY_VALUE = "DisplayValue";
    private Value2048 displayValue = Value2048.ZERO;

    public Block2048DisplayEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_2048_DISPLAY_ENTITY.get(), pos, state);
    }

    public int getDisplayValue() {
        return displayValue.getValue();
    }

    public void setDisplayValue(Value2048 newValue) {
        if (newValue == null || this.displayValue == newValue) return;

        this.displayValue = newValue;
        this.setChanged();

        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag customTag = new CompoundTag();
        customTag.putInt(NBT_KEY_DISPLAY_VALUE, this.displayValue.getValue());
        output.store("2048DisplayData", CompoundTag.CODEC, customTag);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag customTag = input.read("2048DisplayData", CompoundTag.CODEC).orElse(new CompoundTag());
        int savedValue = customTag.getIntOr(NBT_KEY_DISPLAY_VALUE, 0);
        this.displayValue = Value2048.fromInt(savedValue);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt(NBT_KEY_DISPLAY_VALUE, this.displayValue.getValue());
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        HolderLookup.Provider registries = this.level != null ? this.level.registryAccess() : RegistryAccess.EMPTY;
        return ClientboundBlockEntityDataPacket.create(this, (be, _) -> be.getUpdateTag(registries));
    }
}
