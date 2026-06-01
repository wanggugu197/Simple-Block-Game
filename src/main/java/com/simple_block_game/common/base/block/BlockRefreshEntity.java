package com.simple_block_game.common.base.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import lombok.NonNull;

import java.util.Objects;

public class BlockRefreshEntity extends BaseGameBlockEntity {

    private BlockPos corePos;

    private static final String KEY_REFRESH_DATA = "RefreshData";
    private static final String NBT_KEY_CORE_POS_X = "CorePosX";
    private static final String NBT_KEY_CORE_POS_Y = "CorePosY";
    private static final String NBT_KEY_CORE_POS_Z = "CorePosZ";

    public BlockRefreshEntity(BlockEntityType<?> block, BlockPos pos, BlockState state) {
        super(block, pos, state);
    }

    public BlockRefreshEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_REFRESH_ENTITY.get(), pos, state);
    }

    @NonNull
    public BlockPos getCorePos() {
        return corePos;
    }

    public void setCorePos(BlockPos corePos) {
        if (Objects.equals(corePos, this.corePos)) return;
        this.corePos = corePos;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        CompoundTag customTag = new CompoundTag();
        if (this.corePos != null) {
            customTag.putInt(NBT_KEY_CORE_POS_X, this.corePos.getX());
            customTag.putInt(NBT_KEY_CORE_POS_Y, this.corePos.getY());
            customTag.putInt(NBT_KEY_CORE_POS_Z, this.corePos.getZ());
        }
        pTag.put(KEY_REFRESH_DATA, customTag);
    }

    @Override
    protected void loadAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains(KEY_REFRESH_DATA, CompoundTag.TAG_COMPOUND)) {
            CompoundTag customTag = pTag.getCompound(KEY_REFRESH_DATA);
            if (customTag.contains(NBT_KEY_CORE_POS_X) && customTag.contains(NBT_KEY_CORE_POS_Y) && customTag.contains(NBT_KEY_CORE_POS_Z)) {
                this.corePos = new BlockPos(
                        customTag.getInt(NBT_KEY_CORE_POS_X),
                        customTag.getInt(NBT_KEY_CORE_POS_Y),
                        customTag.getInt(NBT_KEY_CORE_POS_Z));
            } else {
                this.corePos = null;
            }
        } else {
            this.corePos = null;
        }
    }
}
