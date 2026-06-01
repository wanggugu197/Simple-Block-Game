package com.simple_block_game.common.simpleMinesweeper.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleMinesweeper.data.MinesweeperState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
        super(SimpleBlockGameRegistration.BLOCK_MINESWEEPER_DISPLAY_ENTITY.get(), pos, state);
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
    protected void saveAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        CompoundTag tag = new CompoundTag();
        tag.putString(KEY_STATE, state.getSerializedName());
        if (corePos != null) {
            tag.putInt(KEY_CORE_X, corePos.getX());
            tag.putInt(KEY_CORE_Y, corePos.getY());
            tag.putInt(KEY_CORE_Z, corePos.getZ());
        }
        pTag.put(KEY_DATA, tag);
    }

    @Override
    protected void loadAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        CompoundTag tag = pTag.contains(KEY_DATA) ? pTag.getCompound(KEY_DATA) : new CompoundTag();

        state = MinesweeperState.fromSerializedName(tag.contains(KEY_STATE) ? tag.getString(KEY_STATE) : "unopened");

        if (tag.contains(KEY_CORE_X) && tag.contains(KEY_CORE_Y) && tag.contains(KEY_CORE_Z)) {
            corePos = new BlockPos(tag.getInt(KEY_CORE_X), tag.getInt(KEY_CORE_Y), tag.getInt(KEY_CORE_Z));
        } else {
            corePos = null;
        }
    }
}
