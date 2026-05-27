package com.simple_block_game.common.base.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
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
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag customTag = new CompoundTag();
        if (corePos != null) {
            customTag.putInt(NBT_KEY_CORE_POS_X, corePos.getX());
            customTag.putInt(NBT_KEY_CORE_POS_Y, corePos.getY());
            customTag.putInt(NBT_KEY_CORE_POS_Z, corePos.getZ());
        }
        output.store(KEY_REFRESH_DATA, CompoundTag.CODEC, customTag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag customTag = input.read(KEY_REFRESH_DATA, CompoundTag.CODEC).orElse(new CompoundTag());
        if (customTag.contains(NBT_KEY_CORE_POS_X) && customTag.contains(NBT_KEY_CORE_POS_Y) && customTag.contains(NBT_KEY_CORE_POS_Z)) {
            corePos = new BlockPos(
                    customTag.getIntOr(NBT_KEY_CORE_POS_X, 0),
                    customTag.getIntOr(NBT_KEY_CORE_POS_Y, 0),
                    customTag.getIntOr(NBT_KEY_CORE_POS_Z, 0));
        } else {
            corePos = null;
        }
    }
}
