package com.simple_block_game.common.simple24Puzzle.block;

import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simple24Puzzle.data.GameToken24Puzzle;
import com.simple_block_game.common.simple24Puzzle.logic.Game24PuzzleHelper;
import com.simple_block_game.common.simple24Puzzle.simple24PuzzleRegistration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 24点游戏核心方块实体，存储谜题Token和游戏状态
 */
public class Block24PuzzleCoreEntity extends BaseGameBlockEntity {

    private static final String NBT_KEY_START_TIME = "StartTime";
    private static final String NBT_KEY_COMPLETED_COUNT = "CompletedCount";
    private static final String NBT_KEY_LAST_SUCCESS_TIME = "LastSuccessTime";
    private static final String NBT_KEY_CURRENT_NUMBERS = "CurrentNumbers";
    private static final String NBT_KEY_INPUT_TOKENS = "InputTokens";

    private static final long COMBO_TIMEOUT_TICKS = 6000L;

    @Getter
    private long startTime;
    @Getter
    private int completedCount;
    @Getter
    private long lastSuccessTime;
    @Getter
    private List<GameToken24Puzzle> currentNumbers = new ArrayList<>();
    @Getter
    private List<GameToken24Puzzle> inputTokens = new ArrayList<>();

    public Block24PuzzleCoreEntity(BlockPos pos, BlockState state) {
        super(simple24PuzzleRegistration.BLOCK_24PUZZLE_CORE_ENTITY.get(), pos, state);
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        syncToClient();
    }

    public void incrementCompletedCount(Level level) {
        long currentTime = level.getGameTime();
        if (isComboExpired(currentTime)) {
            resetCombo(currentTime);
        }
        this.completedCount++;
        this.lastSuccessTime = currentTime;
        syncToClient();
    }

    public boolean isComboExpired(long currentTime) {
        return lastSuccessTime == 0 || currentTime - lastSuccessTime > COMBO_TIMEOUT_TICKS;
    }

    public void resetCombo(long currentTime) {
        this.startTime = currentTime;
        this.completedCount = 0;
        this.lastSuccessTime = currentTime;
    }

    public void setCurrentNumbers(ServerLevel level, BlockPos corePos, Direction facing, List<GameToken24Puzzle> numbers) {
        this.currentNumbers = numbers == null ? new ArrayList<>() : new ArrayList<>(numbers);
        Game24PuzzleHelper.showPuzzleNumbers(level, corePos, facing, currentNumbers);
        syncToClient();
    }

    public void setInputTokens(List<GameToken24Puzzle> tokens) {
        this.inputTokens = tokens == null ? new ArrayList<>() : new ArrayList<>(tokens);
        syncToClient();
    }

    public void addInputToken(GameToken24Puzzle token) {
        inputTokens.add(token);
        syncToClient();
    }

    public void subtractInputToken() {
        if (!inputTokens.isEmpty()) {
            inputTokens.removeLast();
            syncToClient();
        }
    }

    public void resetInputToken() {
        inputTokens.clear();
        syncToClient();
    }

    public void reset() {
        this.startTime = 0;
        this.completedCount = 0;
        this.lastSuccessTime = 0;
        this.currentNumbers = new ArrayList<>();
        this.inputTokens = new ArrayList<>();
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
        tag.putLong(NBT_KEY_START_TIME, startTime);
        tag.putInt(NBT_KEY_COMPLETED_COUNT, completedCount);
        tag.putLong(NBT_KEY_LAST_SUCCESS_TIME, lastSuccessTime);
        tag.putIntArray(NBT_KEY_CURRENT_NUMBERS, currentNumbers.stream().mapToInt(Enum::ordinal).toArray());
        tag.putIntArray(NBT_KEY_INPUT_TOKENS, inputTokens.stream().mapToInt(Enum::ordinal).toArray());
        pTag.put("24PuzzleData", tag);
    }

    @Override
    protected void loadAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        CompoundTag tag = pTag.contains("24PuzzleData") ? pTag.getCompound("24PuzzleData") : new CompoundTag();
        startTime = tag.getLong(NBT_KEY_START_TIME);
        completedCount = tag.getInt(NBT_KEY_COMPLETED_COUNT);
        lastSuccessTime = tag.getLong(NBT_KEY_LAST_SUCCESS_TIME);

        int[] numberIds = tag.getIntArray(NBT_KEY_CURRENT_NUMBERS);
        currentNumbers = new ArrayList<>();
        for (int id : numberIds) {
            GameToken24Puzzle[] values = GameToken24Puzzle.values();
            if (id >= 0 && id < values.length) {
                currentNumbers.add(values[id]);
            }
        }

        int[] inputIds = tag.getIntArray(NBT_KEY_INPUT_TOKENS);
        inputTokens = new ArrayList<>();
        for (int id : inputIds) {
            GameToken24Puzzle[] values = GameToken24Puzzle.values();
            if (id >= 0 && id < values.length) {
                inputTokens.add(values[id]);
            }
        }
    }
}
