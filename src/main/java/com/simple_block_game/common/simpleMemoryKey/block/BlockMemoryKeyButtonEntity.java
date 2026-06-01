package com.simple_block_game.common.simpleMemoryKey.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyPosition;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * 记忆键游戏按键方块实体
 */
public class BlockMemoryKeyButtonEntity extends BaseGameBlockEntity {

    private static final String KEY_CORE_POS_X = "CorePosX";
    private static final String KEY_CORE_POS_Y = "CorePosY";
    private static final String KEY_CORE_POS_Z = "CorePosZ";
    private static final String KEY_IS_FLASHING = "IsFlashing";
    private static final String KEY_POSITION_ID = "PositionId";

    public static final int FLASH_DURATION_TICKS = 10;

    @Getter
    private MemoryKeyPosition position;

    @Setter
    @Getter
    private BlockPos corePos;

    @Getter
    private boolean isFlashing = false;
    private int flashTimer = 0;

    public BlockMemoryKeyButtonEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_MEMORY_KEY_BUTTON_ENTITY.get(), pos, state);
    }

    public void setPosition(MemoryKeyPosition position) {
        if (position == null) return;
        this.position = position;
        syncToClient();
    }

    /**
     * 实体tick更新（服务端）
     */
    public void tick() {
        if (isFlashing && flashTimer > 0) {
            flashTimer--;

            if (flashTimer <= 0) {
                stopFlashing();
            }
        }
    }

    /**
     * 开始闪烁
     * <p>
     * 启动计时器，10tick后自动停止闪烁。
     * 此方法由核心逻辑调用，触发按键闪烁效果。
     */
    public void startFlashing() {
        if (!isFlashing) {
            this.isFlashing = true;
            this.flashTimer = FLASH_DURATION_TICKS;
            syncToClient();
        }
    }

    /**
     * 停止闪烁
     */
    public void stopFlashing() {
        if (isFlashing) {
            this.isFlashing = false;
            this.flashTimer = 0;
            syncToClient();
        }
    }

    /**
     * 更新并同步到客户端
     */
    private void syncToClient() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    /**
     * 设置闪烁状态（兼容旧接口，直接调用startFlashing或stopFlashing）
     */
    public void setFlashing(boolean flashing) {
        if (flashing) {
            startFlashing();
        } else {
            stopFlashing();
        }
    }

    @Override
    protected void saveAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        CompoundTag tag = new CompoundTag();
        if (corePos != null) {
            tag.putInt(KEY_CORE_POS_X, corePos.getX());
            tag.putInt(KEY_CORE_POS_Y, corePos.getY());
            tag.putInt(KEY_CORE_POS_Z, corePos.getZ());
        }
        if (position != null) {
            tag.putInt(KEY_POSITION_ID, position.getId());
        }
        tag.putBoolean(KEY_IS_FLASHING, isFlashing);
        pTag.put("MemoryKeyButtonData", tag);
    }

    @Override
    protected void loadAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        CompoundTag tag = pTag.contains("MemoryKeyButtonData") ? pTag.getCompound("MemoryKeyButtonData") : new CompoundTag();
        if (tag.contains(KEY_CORE_POS_X)) {
            setCorePos(new BlockPos(tag.getInt(KEY_CORE_POS_X),
                    tag.getInt(KEY_CORE_POS_Y), tag.getInt(KEY_CORE_POS_Z)));
        }
        if (tag.contains(KEY_POSITION_ID)) {
            setPosition(MemoryKeyPosition.fromId(tag.getInt(KEY_POSITION_ID)));
        }
        setFlashing(tag.getBoolean(KEY_IS_FLASHING));
    }
}
