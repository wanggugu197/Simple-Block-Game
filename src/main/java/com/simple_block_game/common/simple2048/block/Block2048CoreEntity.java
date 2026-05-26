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
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Block2048CoreEntity extends BlockEntity {

    private static final String NBT_KEY_SCORE = "2048_Score";
    private static final String NBT_KEY_MAX_NUMBER = "2048_MaxNumber";

    private int score = 0;
    private int maxNumber = 0;

    public Block2048CoreEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_2048_CORE_ENTITY.get(), pos, state);
    }

    public void addScore(int add) {
        if (add < 0) return;
        this.score += add;
        this.setChanged();
    }

    public void resetScore() {
        this.score = 0;
        this.setChanged();
    }

    public void setMaxNumber(int newMaxNumber) {
        if (this.maxNumber != newMaxNumber) {
            this.maxNumber = newMaxNumber;
            this.setChanged();
        }
    }

    public void resetMaxNumber() {
        this.maxNumber = 0;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag customTag = new CompoundTag();
        customTag.putInt(NBT_KEY_SCORE, this.score);
        customTag.putInt(NBT_KEY_MAX_NUMBER, this.maxNumber);
        output.store("2048Data", CompoundTag.CODEC, customTag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag customTag = input.read("2048Data", CompoundTag.CODEC).orElse(new CompoundTag());
        this.score = customTag.getIntOr(NBT_KEY_SCORE, 0);
        this.maxNumber = customTag.getIntOr(NBT_KEY_MAX_NUMBER, 0);
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(@NonNull HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt(NBT_KEY_SCORE, this.score);
        tag.putInt(NBT_KEY_MAX_NUMBER, this.maxNumber);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        HolderLookup.Provider registries = this.level != null ? this.level.registryAccess() : RegistryAccess.EMPTY;
        return ClientboundBlockEntityDataPacket.create(this, (be, _) -> be.getUpdateTag(registries));
    }
}
