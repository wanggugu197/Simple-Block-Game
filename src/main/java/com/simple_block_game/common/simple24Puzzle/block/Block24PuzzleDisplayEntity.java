package com.simple_block_game.common.simple24Puzzle.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simple24Puzzle.data.GameToken24Puzzle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;
import lombok.NonNull;

/**
 * 24点游戏显示方块实体，存储显示Token和核心方块位置
 */
public class Block24PuzzleDisplayEntity extends BaseGameBlockEntity {

    private static final String NBT_KEY_TOKEN = "DisplayToken";
    private static final String NBT_KEY_CORE_X = "CorePosX";
    private static final String NBT_KEY_CORE_Y = "CorePosY";
    private static final String NBT_KEY_CORE_Z = "CorePosZ";

    @Getter
    private GameToken24Puzzle token;
    @Getter
    private BlockPos corePos;

    public Block24PuzzleDisplayEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_24PUZZLE_DISPLAY_ENTITY.get(), pos, state);
    }

    public void setToken(GameToken24Puzzle newToken) {
        if (token != null && token.equals(newToken)) return;
        token = newToken;
        syncToClient();
    }

    public void setCorePos(BlockPos corePos) {
        this.corePos = corePos;
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
        if (token != null) {
            tag.putInt(NBT_KEY_TOKEN, token.ordinal());
        }
        if (corePos != null) {
            tag.putInt(NBT_KEY_CORE_X, corePos.getX());
            tag.putInt(NBT_KEY_CORE_Y, corePos.getY());
            tag.putInt(NBT_KEY_CORE_Z, corePos.getZ());
        }
        pTag.put("24PuzzleDisplayData", tag);
    }

    @Override
    protected void loadAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        CompoundTag tag = pTag.contains("24PuzzleDisplayData") ? pTag.getCompound("24PuzzleDisplayData") : new CompoundTag();
        if (tag.contains(NBT_KEY_TOKEN)) {
            int tokenId = tag.getInt(NBT_KEY_TOKEN);
            GameToken24Puzzle[] values = GameToken24Puzzle.values();
            if (tokenId >= 0 && tokenId < values.length) {
                this.token = values[tokenId];
            }
        }
        if (tag.contains(NBT_KEY_CORE_X) && tag.contains(NBT_KEY_CORE_Y) && tag.contains(NBT_KEY_CORE_Z)) {
            this.corePos = new BlockPos(
                    tag.getInt(NBT_KEY_CORE_X),
                    tag.getInt(NBT_KEY_CORE_Y),
                    tag.getInt(NBT_KEY_CORE_Z));
        }
    }
}
