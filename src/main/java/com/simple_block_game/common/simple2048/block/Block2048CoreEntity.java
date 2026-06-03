package com.simple_block_game.common.simple2048.block;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simple2048.simple2048Registration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;
import lombok.NonNull;

/**
 * 2048游戏核心方块实体，存储分数和最大数字
 */
public class Block2048CoreEntity extends BaseGameBlockEntity {

    private static final String KEY_SCORE = "2048_Score";
    private static final String KEY_MAX = "2048_MaxNumber";

    @Getter
    private int score = 0;
    @Getter
    private int maxNumber = 0;

    public Block2048CoreEntity(BlockPos pos, BlockState state) {
        super(simple2048Registration.BLOCK_2048_CORE_ENTITY.get(), pos, state);
    }

    public void addScore(int add) {
        if (add <= 0) return;
        score = Math.min(Integer.MAX_VALUE, score + add);
        syncToClient();
    }

    public void resetScore() {
        score = 0;
        syncToClient();
    }

    public void setMaxNumber(int newMax) {
        if (newMax < 0) return;
        if (maxNumber != newMax) {
            maxNumber = newMax;
            syncToClient();
        }
    }

    public void resetMaxNumber() {
        maxNumber = 0;
        syncToClient();
    }

    public void syncToClient() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY_SCORE, score);
        tag.putInt(KEY_MAX, maxNumber);
        pTag.put("2048Data", tag);
    }

    @Override
    protected void loadAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        CompoundTag tag = pTag.contains("2048Data") ? pTag.getCompound("2048Data") : new CompoundTag();
        score = tag.contains(KEY_SCORE) ? tag.getInt(KEY_SCORE) : 0;
        maxNumber = tag.contains(KEY_MAX) ? tag.getInt(KEY_MAX) : 0;
    }
}
