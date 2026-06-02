package com.simple_block_game.common.simpleMemoryKey.block;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyPosition;
import com.simple_block_game.common.simpleMemoryKey.simpleMemoryKeyRegistration;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
        super(simpleMemoryKeyRegistration.BLOCK_MEMORY_KEY_BUTTON_ENTITY.get(), pos, state);
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
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
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
        output.store("MemoryKeyButtonData", CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read("MemoryKeyButtonData", CompoundTag.CODEC).orElse(new CompoundTag());
        if (tag.contains(KEY_CORE_POS_X)) {
            setCorePos(new BlockPos(tag.getIntOr(KEY_CORE_POS_X, 0),
                    tag.getIntOr(KEY_CORE_POS_Y, 0), tag.getIntOr(KEY_CORE_POS_Z, 0)));
        }
        if (tag.contains(KEY_POSITION_ID)) {
            setPosition(MemoryKeyPosition.fromId(tag.getIntOr(KEY_POSITION_ID, 0)));
        }
        setFlashing(tag.getBooleanOr(KEY_IS_FLASHING, false));
    }
}
