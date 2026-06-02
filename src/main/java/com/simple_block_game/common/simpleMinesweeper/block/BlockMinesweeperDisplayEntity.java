package com.simple_block_game.common.simpleMinesweeper.block;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleMinesweeper.data.MinesweeperState;
import com.simple_block_game.common.simpleMinesweeper.simpleMinesweeperRegistration;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import lombok.Getter;
import lombok.NonNull;

/**
 * 扫雷游戏显示方块实体，存储显示状态和核心方块位置
 */
public class BlockMinesweeperDisplayEntity extends BaseGameBlockEntity {

    @Getter
    private MinesweeperState state = MinesweeperState.UNOPENED;
    @Getter
    private BlockPos corePos;

    private static final String KEY_DATA = "MinesweeperDisplayData";
    private static final String KEY_STATE = "DisplayState";
    private static final String KEY_CORE_X = "CorePosX";
    private static final String KEY_CORE_Y = "CorePosY";
    private static final String KEY_CORE_Z = "CorePosZ";

    public BlockMinesweeperDisplayEntity(BlockPos pos, BlockState state) {
        super(simpleMinesweeperRegistration.BLOCK_MINESWEEPER_DISPLAY_ENTITY.get(), pos, state);
    }

    public void setDisplayState(MinesweeperState newState) {
        if (newState == null) return;
        if (state != null && state.equals(newState)) return;
        state = newState;
        syncToClient();
    }

    public void setCorePos(BlockPos newCorePos) {
        if (newCorePos == corePos) return;
        corePos = newCorePos;
        syncToClient();
    }

    private void syncToClient() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        tag.putString(KEY_STATE, state.getSerializedName());
        if (corePos != null) {
            tag.putInt(KEY_CORE_X, corePos.getX());
            tag.putInt(KEY_CORE_Y, corePos.getY());
            tag.putInt(KEY_CORE_Z, corePos.getZ());
        }
        output.store(KEY_DATA, CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(KEY_DATA, CompoundTag.CODEC).orElse(new CompoundTag());

        state = MinesweeperState.fromSerializedName(tag.getStringOr(KEY_STATE, "unopened"));

        if (tag.contains(KEY_CORE_X) && tag.contains(KEY_CORE_Y) && tag.contains(KEY_CORE_Z)) {
            corePos = new BlockPos(tag.getIntOr(KEY_CORE_X, 0), tag.getIntOr(KEY_CORE_Y, 0), tag.getIntOr(KEY_CORE_Z, 0));
        } else {
            corePos = null;
        }
    }
}
