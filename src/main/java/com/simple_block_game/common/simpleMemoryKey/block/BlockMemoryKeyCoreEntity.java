package com.simple_block_game.common.simpleMemoryKey.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyGameState;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyLevel;
import com.simple_block_game.common.simpleMemoryKey.logic.GameMemoryKeyHelper;
import com.simple_block_game.common.simpleMemoryKey.logic.GameMemoryKeyLogic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 记忆键游戏核心方块实体
 */
public class BlockMemoryKeyCoreEntity extends BaseGameBlockEntity {

    private static final String KEY_GAME_STATE = "GameState";
    private static final String KEY_CURRENT_LEVEL = "CurrentLevel";
    private static final String KEY_REMAINING_LIVES = "RemainingLives";
    private static final String KEY_CURRENT_SEQUENCE_INDEX = "CurrentSequenceIndex";
    private static final String KEY_SEQUENCE_DATA = "SequenceData";
    private static final String KEY_FLASHING_INDEX = "FlashingIndex";

    private static final int ERROR_STATE_DURATION_TICKS = 40;  // 2秒
    private static final int LEVEL_SUCCESS_DURATION_TICKS = 20; // 1秒
    private static final int INITIAL_LIVES = 3;

    @Getter
    private MemoryKeyGameState gameState = MemoryKeyGameState.IDLE;

    @Setter
    @Getter
    private MemoryKeyLevel currentLevel = MemoryKeyLevel.LEVEL_1;

    @Getter
    private int remainingLives = INITIAL_LIVES;

    @Setter
    @Getter
    private int currentSequenceIndex = 0;
    @Setter
    private int flashingIndex = -1;

    /**
     * 完整的按键序列
     */
    @Getter
    private List<Integer> sequence = new ArrayList<>();

    /**
     * 演示阶段的当前按键索引
     */
    @Setter
    private int demoCurrentIndex = -1;

    /**
     * 演示阶段的tick计数器
     */
    @Setter
    private int demoTickCounter = 0;

    /**
     * 错误状态的tick计数器（用于显示2秒错误状态）
     */
    @Setter
    private int errorTickCounter = 0;

    public BlockMemoryKeyCoreEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_MEMORY_KEY_CORE_ENTITY.get(), pos, state);
    }

    public void setGameState(MemoryKeyGameState gameState) {
        this.gameState = gameState;
        syncToClient();
    }

    public void setRemainingLives(int remainingLives) {
        this.remainingLives = remainingLives;
        syncToClient();
    }

    /**
     * 同步数据到客户端
     */
    private void syncToClient() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    /**
     * 设置完整的按键序列
     *
     * @param newSequence 新的序列列表
     */
    public void setSequence(List<Integer> newSequence) {
        if (newSequence == null) {
            this.sequence.clear();
        } else {
            this.sequence = new ArrayList<>(newSequence.stream()
                    .filter(v -> v >= 0)
                    .toList());
        }
        syncToClient();
    }

    /**
     * 失去一条生命
     */
    public void loseLife() {
        if (remainingLives > 0) {
            setRemainingLives(Math.max(0, remainingLives - 1));
        }
    }

    public void nextLevel() {
        currentLevel = currentLevel.next();
        currentSequenceIndex = 0;
        flashingIndex = -1;
        demoCurrentIndex = -1;
        demoTickCounter = 0;
        errorTickCounter = 0;
        startDemonstration();
        syncToClient();
    }

    /**
     * 开始演示阶段
     */
    public void startDemonstration() {
        demoCurrentIndex = 0;
        demoTickCounter = 0;
        flashingIndex = -1;
    }

    /**
     * 初始化游戏（用于首次展开）
     * <p>
     * 设置初始状态，但保持序列不变（可能还未生成）。
     */
    public void initialize() {
        // 只重置状态和计数变量，保持序列不变
        this.gameState = MemoryKeyGameState.IDLE;
        this.currentLevel = MemoryKeyLevel.LEVEL_1;
        this.remainingLives = INITIAL_LIVES;
        this.currentSequenceIndex = 0;
        this.flashingIndex = -1;
        this.demoCurrentIndex = -1;
        this.demoTickCounter = 0;
        this.errorTickCounter = 0;

        syncToClient();
    }

    /**
     * 完全重置游戏（从进行中回到未开始状态）
     * <p>
     * 重置所有游戏数据，包括状态、关卡、生命、序列等，
     * 使游戏完全回到初始状态。
     */
    public void completeReset() {
        // 清空序列
        sequence.clear();

        // 重置演示相关变量
        demoCurrentIndex = -1;
        demoTickCounter = 0;
        errorTickCounter = 0;
        flashingIndex = -1;

        // 重置游戏状态
        currentSequenceIndex = 0;
        remainingLives = INITIAL_LIVES;
        currentLevel = MemoryKeyLevel.LEVEL_1;

        // 设置游戏状态为IDLE（内部已调用updateBlockState和setChanged）
        setGameState(MemoryKeyGameState.IDLE);
    }

    /**
     * 实体tick更新（服务端）
     * <p>
     * 处理演示阶段的按键闪烁逻辑和错误状态的延迟处理。
     */
    public void tick() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // 处理错误状态（显示2秒后重新开始演示）
        if (gameState == MemoryKeyGameState.ERROR) {
            handleErrorState();
            return;
        }

        // 处理关卡成功状态（显示1秒后进入下一关演示或通关）
        if (gameState == MemoryKeyGameState.LEVEL_SUCCESS) {
            handleLevelSuccessState();
            return;
        }

        // 处理演示阶段
        if (gameState == MemoryKeyGameState.DEMONSTRATING) {
            handleDemonstrationState(serverLevel);
        }
    }

    /**
     * 处理错误状态
     */
    private void handleErrorState() {
        errorTickCounter++;
        if (errorTickCounter >= ERROR_STATE_DURATION_TICKS) {
            errorTickCounter = 0;
            // 重置当前关卡，开始重新演示
            currentSequenceIndex = 0;
            flashingIndex = -1;
            startDemonstration();
            setGameState(MemoryKeyGameState.DEMONSTRATING);
        }
    }

    /**
     * 处理关卡成功状态
     */
    private void handleLevelSuccessState() {
        errorTickCounter++;
        if (errorTickCounter >= LEVEL_SUCCESS_DURATION_TICKS) {
            errorTickCounter = 0;
            if (GameMemoryKeyLogic.isAllLevelsComplete(currentLevel)) {
                setGameState(MemoryKeyGameState.ALL_SUCCESS);
            } else {
                nextLevel();
                setGameState(MemoryKeyGameState.DEMONSTRATING);
            }
        }
    }

    /**
     * 处理演示阶段
     */
    private void handleDemonstrationState(ServerLevel serverLevel) {
        if (sequence.isEmpty()) {
            return;
        }

        int sequenceLength = currentLevel.getSequenceLength();

        // 演示完成检测
        if (demoCurrentIndex >= sequenceLength) {
            currentSequenceIndex = 0;
            flashingIndex = -1;
            setGameState(MemoryKeyGameState.PLAYER_INPUT);
            return;
        }

        demoTickCounter++;

        // 按键间隔：SEQUENCE_INTERVAL_TICKS = 30tick = 1.5秒
        if (demoTickCounter >= GameMemoryKeyHelper.SEQUENCE_INTERVAL_TICKS) {
            // 触发当前按键闪烁
            if (demoCurrentIndex == -1) demoCurrentIndex++;
            int buttonId = sequence.get(demoCurrentIndex);
            flashingIndex = demoCurrentIndex;
            GameMemoryKeyHelper.triggerButtonFlash(serverLevel, worldPosition, buttonId);
            demoCurrentIndex++;
            demoTickCounter = 0;
            syncToClient();
        }
    }

    @Override
    protected void saveAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY_GAME_STATE, gameState.getValue());
        tag.putInt(KEY_CURRENT_LEVEL, currentLevel.getLevelNumber());
        tag.putInt(KEY_REMAINING_LIVES, remainingLives);
        tag.putInt(KEY_CURRENT_SEQUENCE_INDEX, currentSequenceIndex);
        tag.putInt(KEY_FLASHING_INDEX, flashingIndex);
        tag.putIntArray(KEY_SEQUENCE_DATA, sequence.stream().mapToInt(Integer::intValue).toArray());
        pTag.put("MemoryKeyData", tag);
    }

    @Override
    protected void loadAdditional(@NonNull CompoundTag pTag, HolderLookup.@NonNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        CompoundTag tag = pTag.contains("MemoryKeyData") ? pTag.getCompound("MemoryKeyData") : new CompoundTag();
        gameState = MemoryKeyGameState.fromInt(tag.contains(KEY_GAME_STATE) ? tag.getInt(KEY_GAME_STATE) : 0);
        currentLevel = MemoryKeyLevel.fromLevelNumber(tag.contains(KEY_CURRENT_LEVEL) ? tag.getInt(KEY_CURRENT_LEVEL) : 1);
        remainingLives = tag.contains(KEY_REMAINING_LIVES) ? tag.getInt(KEY_REMAINING_LIVES) : INITIAL_LIVES;
        currentSequenceIndex = tag.contains(KEY_CURRENT_SEQUENCE_INDEX) ? tag.getInt(KEY_CURRENT_SEQUENCE_INDEX) : 0;
        flashingIndex = tag.contains(KEY_FLASHING_INDEX) ? tag.getInt(KEY_FLASHING_INDEX) : -1;

        if (tag.contains(KEY_SEQUENCE_DATA)) {
            sequence.clear();
            for (int value : tag.getIntArray(KEY_SEQUENCE_DATA)) sequence.add(value);
        }
    }
}
