package com.simple_block_game.common.simple2048.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

/** 2048游戏核心方块实体，存储分数和最大数字 */
@Getter
public class Block2048CoreEntity extends BlockEntity {

    private static final String KEY_SCORE = "2048_Score";
    private static final String KEY_MAX = "2048_MaxNumber";

    private int score = 0;
    private int maxNumber = 0;

    public Block2048CoreEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_2048_CORE_ENTITY.get(), pos, state);
    }

    public void addScore(int add) {
        if (add < 0) return;
        score += add;
        setChanged();
    }

    public void resetScore() {
        score = 0;
        setChanged();
    }

    public void setMaxNumber(int newMax) {
        if (maxNumber != newMax) {
            maxNumber = newMax;
            setChanged();
        }
    }

    public void resetMaxNumber() {
        maxNumber = 0;
        setChanged();
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY_SCORE, score);
        tag.putInt(KEY_MAX, maxNumber);
        output.store("2048Data", CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read("2048Data", CompoundTag.CODEC).orElse(new CompoundTag());
        score = tag.getIntOr(KEY_SCORE, 0);
        maxNumber = tag.getIntOr(KEY_MAX, 0);
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt(KEY_SCORE, score);
        tag.putInt(KEY_MAX, maxNumber);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        HolderLookup.Provider registries = level != null ? level.registryAccess() : RegistryAccess.EMPTY;
        return ClientboundBlockEntityDataPacket.create(this, (be, _) -> be.getUpdateTag(registries));
    }
}
