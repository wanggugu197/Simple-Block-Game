package com.simple_block_game.common.simpleTenDrops.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleTenDrops.data.DropletLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

public class BlockTenDropsDisplayEntity extends BaseGameBlockEntity {

    private static final String KEY_DATA = "TenDropsDisplayData";
    private static final String KEY_LEVEL = "DropletLevel";
    private static final String KEY_CORE_X = "CorePosX";
    private static final String KEY_CORE_Y = "CorePosY";
    private static final String KEY_CORE_Z = "CorePosZ";

    private static final String KEY_DROP_NORTH = "DropNorth";
    private static final String KEY_DROP_SOUTH = "DropSouth";
    private static final String KEY_DROP_EAST = "DropEast";
    private static final String KEY_DROP_WEST = "DropWest";

    @Getter
    private DropletLevel dropletLevel = DropletLevel.EMPTY;
    @Getter
    private BlockPos corePos;

    private int dropNorth = -1, dropSouth = -1, dropEast = -1, dropWest = -1;

    public static final int CELL_DISTANCE = 4;

    public BlockTenDropsDisplayEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_TEN_DROPS_DISPLAY_ENTITY.get(), pos, state);
    }

    public int getLevelValue() {
        return dropletLevel.getLevel();
    }

    public void setDropletLevel(DropletLevel newLevel) {
        if (newLevel == null || dropletLevel == newLevel) return;
        dropletLevel = newLevel;
        syncToClient();
    }

    public void setLevelValue(int value) {
        setDropletLevel(DropletLevel.fromLevel(value));
    }

    public void setCorePos(BlockPos newCorePos) {
        if (newCorePos == corePos) return;
        corePos = newCorePos;
        syncToClient();
    }

    public void startBurst() {
        dropNorth = dropSouth = dropEast = dropWest = 0;
        setDropletLevel(DropletLevel.EMPTY);
    }

    public int getDropDistance(Direction dir) {
        return switch (dir) {
            case NORTH -> dropNorth;
            case SOUTH -> dropSouth;
            case EAST -> dropEast;
            case WEST -> dropWest;
            default -> -1;
        };
    }

    public void setDropDistance(Direction dir, int distance) {
        switch (dir) {
            case NORTH -> dropNorth = distance;
            case SOUTH -> dropSouth = distance;
            case EAST -> dropEast = distance;
            case WEST -> dropWest = distance;
        }
        syncToClient();
    }

    public boolean hasActiveDroplets() {
        return dropNorth != -1 || dropSouth != -1 || dropEast != -1 || dropWest != -1;
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

        tag.putInt(KEY_LEVEL, dropletLevel.getLevel());
        if (corePos != null) {
            tag.putInt(KEY_CORE_X, corePos.getX());
            tag.putInt(KEY_CORE_Y, corePos.getY());
            tag.putInt(KEY_CORE_Z, corePos.getZ());
        }
        tag.putInt(KEY_DROP_NORTH, dropNorth);
        tag.putInt(KEY_DROP_SOUTH, dropSouth);
        tag.putInt(KEY_DROP_EAST, dropEast);
        tag.putInt(KEY_DROP_WEST, dropWest);

        output.store(KEY_DATA, CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(KEY_DATA, CompoundTag.CODEC).orElse(new CompoundTag());

        dropletLevel = DropletLevel.fromLevel(tag.getIntOr(KEY_LEVEL, 0));
        corePos = tag.contains(KEY_CORE_X) && tag.contains(KEY_CORE_Y) && tag.contains(KEY_CORE_Z) ? new BlockPos(tag.getIntOr(KEY_CORE_X, 0), tag.getIntOr(KEY_CORE_Y, 0), tag.getIntOr(KEY_CORE_Z, 0)) : null;
        dropNorth = tag.getIntOr(KEY_DROP_NORTH, -1);
        dropSouth = tag.getIntOr(KEY_DROP_SOUTH, -1);
        dropEast = tag.getIntOr(KEY_DROP_EAST, -1);
        dropWest = tag.getIntOr(KEY_DROP_WEST, -1);
    }
}
