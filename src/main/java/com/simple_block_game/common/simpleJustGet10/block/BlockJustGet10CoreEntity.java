package com.simple_block_game.common.simpleJustGet10.block;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleJustGet10.data.JustGet10GameState;
import com.simple_block_game.common.simpleJustGet10.data.ValueJustGet10;
import com.simple_block_game.common.simpleJustGet10.simpleJustGet10Registration;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import lombok.Getter;
import lombok.NonNull;

public class BlockJustGet10CoreEntity extends BaseGameBlockEntity {

    private static final String KEY_DATA = "JustGet10GameData";
    private static final String KEY_STATE = "GameState";
    private static final String KEY_SCORE = "Score";
    private static final String KEY_MAX = "MaxValue";

    @Getter
    private JustGet10GameState gameState = JustGet10GameState.IDLE;
    @Getter
    private int score = 0;
    @Getter
    private ValueJustGet10 maxValue = ValueJustGet10.NUM_1;

    public BlockJustGet10CoreEntity(BlockPos pos, BlockState state) {
        super(simpleJustGet10Registration.BLOCK_JUST_GET_10_CORE_ENTITY.get(), pos, state);
    }

    public void setGameState(JustGet10GameState state) {
        this.gameState = state;
        sync();
    }

    public void addScore(int points) {
        if (points > 0) {
            score = Math.min(Integer.MAX_VALUE, score + points);
            sync();
        }
    }

    public void setMaxValue(ValueJustGet10 val) {
        if (val != null && val.getValue() > maxValue.getValue()) {
            maxValue = val;
            sync();
        }
    }

    public void resetScore() {
        score = 0;
        sync();
    }

    public void resetMaxValue() {
        maxValue = ValueJustGet10.NUM_1;
        sync();
    }

    public void completeReset() {
        gameState = JustGet10GameState.IDLE;
        score = 0;
        maxValue = ValueJustGet10.NUM_1;
        sync();
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        tag.putString(KEY_STATE, gameState.getSerializedName());
        tag.putInt(KEY_SCORE, score);
        tag.putInt(KEY_MAX, maxValue.getValue());
        output.store(KEY_DATA, CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(KEY_DATA, CompoundTag.CODEC).orElse(new CompoundTag());
        gameState = JustGet10GameState.fromSerializedName(tag.getStringOr(KEY_STATE, "idle"));
        score = tag.getIntOr(KEY_SCORE, 0);
        maxValue = ValueJustGet10.getByValue(tag.getIntOr(KEY_MAX, 1)).orElse(ValueJustGet10.NUM_1);
    }
}
