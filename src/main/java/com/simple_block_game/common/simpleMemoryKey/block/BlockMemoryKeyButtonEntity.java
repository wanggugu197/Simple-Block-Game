package com.simple_block_game.common.simpleMemoryKey.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyPosition;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

/** 记忆键游戏按键方块实体 */
public class BlockMemoryKeyButtonEntity extends BlockEntity {

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

    private boolean isFlashing = false;
    private int flashTimer = 0;

    public BlockMemoryKeyButtonEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_MEMORY_KEY_BUTTON_ENTITY.get(), pos, state);
    }

    public void setPosition(MemoryKeyPosition position) {
        this.position = position;
        // 同步到方块状态
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            if (state.hasProperty(BlockMemoryKeyButton.POSITION)) {
                level.setBlock(worldPosition, state.setValue(BlockMemoryKeyButton.POSITION, position), 3);
            }
        }
        setChanged();
    }

    /** 实体tick更新（服务端） */
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
            updateBlockState();
        }
    }

    /**
     * 停止闪烁
     */
    public void stopFlashing() {
        if (isFlashing) {
            this.isFlashing = false;
            this.flashTimer = 0;
            updateBlockState();
        }
    }

    /**
     * 更新方块状态并同步到客户端
     */
    private void updateBlockState() {
        if (level instanceof ServerLevel serverLevel) {
            BlockState state = level.getBlockState(worldPosition);
            if (state.hasProperty(BlockMemoryKeyButton.FLASHING)) {
                BlockState newState = state.setValue(BlockMemoryKeyButton.FLASHING, isFlashing);
                level.setBlock(worldPosition, newState, 3);
                serverLevel.sendBlockUpdated(worldPosition, state, newState, 3);
            }
        }
        setChanged();
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
        writeToTag(tag);
        output.store("MemoryKeyButtonData", CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read("MemoryKeyButtonData", CompoundTag.CODEC).orElse(new CompoundTag());
        readFromTag(tag);
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        writeToTag(tag);
        return tag;
    }

    /** 将数据写入NBT标签 */
    private void writeToTag(CompoundTag tag) {
        if (corePos != null) {
            tag.putInt(KEY_CORE_POS_X, corePos.getX());
            tag.putInt(KEY_CORE_POS_Y, corePos.getY());
            tag.putInt(KEY_CORE_POS_Z, corePos.getZ());
        }
        if (position != null) {
            tag.putInt(KEY_POSITION_ID, position.getId());
        }
        tag.putBoolean(KEY_IS_FLASHING, isFlashing);
    }

    /** 从NBT标签读取数据 */
    private void readFromTag(CompoundTag tag) {
        if (tag.contains(KEY_CORE_POS_X)) {
            setCorePos(new BlockPos(tag.getIntOr(KEY_CORE_POS_X, 0),
                    tag.getIntOr(KEY_CORE_POS_Y, 0), tag.getIntOr(KEY_CORE_POS_Z, 0)));
        }
        if (tag.contains(KEY_POSITION_ID)) {
            setPosition(MemoryKeyPosition.fromId(tag.getIntOr(KEY_POSITION_ID, 0)));
        }
        setFlashing(tag.getBooleanOr(KEY_IS_FLASHING, false));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        HolderLookup.Provider registries = level != null ? level.registryAccess() : RegistryAccess.EMPTY;
        return ClientboundBlockEntityDataPacket.create(this, (be, _) -> be.getUpdateTag(registries));
    }
}
