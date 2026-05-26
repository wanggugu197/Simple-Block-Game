package com.simple_block_game.common.simpleMinesweeper.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import lombok.NonNull;

import java.util.Objects;

import javax.annotation.Nullable;

public class BlockMinesweeperRefreshEntity extends BlockEntity {

    private BlockPos corePos;

    private static final String KEY_MINESWEEPER_REFRESH_DATA = "MinesweeperRefreshData";
    private static final String NBT_KEY_CORE_POS_X = "CorePosX";
    private static final String NBT_KEY_CORE_POS_Y = "CorePosY";
    private static final String NBT_KEY_CORE_POS_Z = "CorePosZ";

    public BlockMinesweeperRefreshEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_MINESWEEPER_REFRESH_ENTITY.get(), pos, state);
    }

    @Nullable
    public BlockPos getCorePos() {
        return corePos;
    }

    public void setCorePos(BlockPos corePos) {
        if (Objects.equals(corePos, this.corePos)) {
            return;
        }
        this.corePos = corePos;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
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
        output.store(KEY_MINESWEEPER_REFRESH_DATA, CompoundTag.CODEC, customTag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag customTag = input.read(KEY_MINESWEEPER_REFRESH_DATA, CompoundTag.CODEC).orElse(new CompoundTag());
        if (customTag.contains(NBT_KEY_CORE_POS_X) && customTag.contains(NBT_KEY_CORE_POS_Y) && customTag.contains(NBT_KEY_CORE_POS_Z)) {
            this.corePos = new BlockPos(
                    customTag.getIntOr(NBT_KEY_CORE_POS_X, 0),
                    customTag.getIntOr(NBT_KEY_CORE_POS_Y, 0),
                    customTag.getIntOr(NBT_KEY_CORE_POS_Z, 0));
        } else {
            this.corePos = null;
        }
    }

    @Nullable
    @Override
    public Packet<@NonNull ClientGamePacketListener> getUpdatePacket() {
        HolderLookup.Provider registries = this.level != null ? this.level.registryAccess() : RegistryAccess.EMPTY;
        return ClientboundBlockEntityDataPacket.create(this, (blockEntity, _) -> blockEntity.getUpdateTag(registries));
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(@NonNull HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (corePos != null) {
            tag.putInt(NBT_KEY_CORE_POS_X, corePos.getX());
            tag.putInt(NBT_KEY_CORE_POS_Y, corePos.getY());
            tag.putInt(NBT_KEY_CORE_POS_Z, corePos.getZ());
        }
        return tag;
    }
}
