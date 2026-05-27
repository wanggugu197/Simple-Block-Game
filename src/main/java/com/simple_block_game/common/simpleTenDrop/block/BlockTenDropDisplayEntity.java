package com.simple_block_game.common.simpleTenDrop.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.simpleTenDrop.data.DropletLevel;
import com.simple_block_game.common.simpleTenDrop.data.TenDropGameState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

/**
 * 十滴游戏显示方块实体，存储水滴等级和核心方块位置
 */
public class BlockTenDropDisplayEntity extends BlockEntity {

    private static final String KEY_DATA = "TenDropDisplayData";
    private static final String KEY_LEVEL = "DropletLevel";
    private static final String KEY_CORE_X = "CorePosX";
    private static final String KEY_CORE_Y = "CorePosY";
    private static final String KEY_CORE_Z = "CorePosZ";

    private DropletLevel dropletLevel = DropletLevel.EMPTY;
    @Getter
    private BlockPos corePos;

    public BlockTenDropDisplayEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_TEN_DROP_DISPLAY_ENTITY.get(), pos, state);
    }

    public DropletLevel getDropletLevel() {
        return dropletLevel;
    }

    public int getLevelValue() {
        return dropletLevel.getLevel();
    }

    public void setDropletLevel(DropletLevel newLevel) {
        if (newLevel == null || dropletLevel == newLevel) return;
        dropletLevel = newLevel;
        setChanged();
        syncToClient();
    }

    public void setLevelValue(int value) {
        setDropletLevel(DropletLevel.fromLevel(value));
    }

    public void setCorePos(BlockPos newCorePos) {
        if (newCorePos == corePos) return;
        corePos = newCorePos;
        setChanged();
        syncToClient();
    }

    public boolean handlePlayerClick() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (corePos == null) {
            return false;
        }

        BlockEntity coreEntity = serverLevel.getBlockEntity(corePos);
        if (!(coreEntity instanceof BlockTenDropCoreEntity core)) {
            return false;
        }

        if (core.getGameState() != TenDropGameState.PLAYING) {
            return false;
        }

        if (core.getWaterDrops() <= 0) {
            return false;
        }

        if (dropletLevel == DropletLevel.EMPTY || dropletLevel == DropletLevel.BURST) {
            return false;
        }

        core.setWaterDrops(core.getWaterDrops() - 1);

        DropletLevel newLevel = dropletLevel.nextLevel();
        setDropletLevel(newLevel);

        if (newLevel == DropletLevel.BURST) {
            notifyCoreOfBurst(serverLevel);
        }

        return true;
    }

    private void notifyCoreOfBurst(ServerLevel serverLevel) {
        if (corePos == null) {
            return;
        }

        BlockEntity coreEntity = serverLevel.getBlockEntity(corePos);
        if (coreEntity instanceof BlockTenDropCoreEntity core) {
            core.handleBurstFromDisplay(worldPosition);
        }
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY_LEVEL, dropletLevel.getLevel());
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

        dropletLevel = DropletLevel.fromLevel(tag.getIntOr(KEY_LEVEL, 0));

        if (tag.contains(KEY_CORE_X) && tag.contains(KEY_CORE_Y) && tag.contains(KEY_CORE_Z)) {
            corePos = new BlockPos(
                    tag.getIntOr(KEY_CORE_X, 0),
                    tag.getIntOr(KEY_CORE_Y, 0),
                    tag.getIntOr(KEY_CORE_Z, 0));
        } else {
            corePos = null;
        }
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt(KEY_LEVEL, dropletLevel.getLevel());
        if (corePos != null) {
            tag.putInt(KEY_CORE_X, corePos.getX());
            tag.putInt(KEY_CORE_Y, corePos.getY());
            tag.putInt(KEY_CORE_Z, corePos.getZ());
        }
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        HolderLookup.Provider registries = level != null ? level.registryAccess() : RegistryAccess.EMPTY;
        return ClientboundBlockEntityDataPacket.create(this, (be, _) -> be.getUpdateTag(registries));
    }
}
