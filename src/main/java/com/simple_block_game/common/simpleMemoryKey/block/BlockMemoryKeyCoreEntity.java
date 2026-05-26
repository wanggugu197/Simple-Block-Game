package com.simple_block_game.common.simpleMemoryKey.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyGameState;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyLevel;
import com.simple_block_game.common.simpleMemoryKey.logic.GameMemoryKeyHelper;
import com.simple_block_game.common.simpleMemoryKey.logic.GameMemoryKeyLogic;

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

import java.util.ArrayList;
import java.util.List;

/** 记忆键游戏核心方块实体 */
public class BlockMemoryKeyCoreEntity extends BlockEntity {

    private static final String KEY_GAME_STATE = "GameState";
    private static final String KEY_CURRENT_LEVEL = "CurrentLevel";
    private static final String KEY_REMAINING_LIVES = "RemainingLives";
    private static final String KEY_CURRENT_SEQUENCE_INDEX = "CurrentSequenceIndex";
    private static final String KEY_SEQUENCE_DATA = "SequenceData";
    private static final String KEY_FLASHING_INDEX = "FlashingIndex";

    @Getter
    private MemoryKeyGameState gameState = MemoryKeyGameState.IDLE;

    @Setter
    @Getter
    private MemoryKeyLevel currentLevel = MemoryKeyLevel.LEVEL_1;

    @Getter
    private int remainingLives = 3;

    public void setGameState(MemoryKeyGameState gameState) {
        this.gameState = gameState;
        updateBlockState();
        setChanged();
    }

    public void setRemainingLives(int remainingLives) {
        this.remainingLives = remainingLives;
        updateBlockState();
        setChanged();
    }

    @Setter
    @Getter
    private int currentSequenceIndex = 0;
    @Setter
    private int flashingIndex = -1;

    /** 完整的按键序列 */
    @Getter
    private List<Integer> sequence = new ArrayList<>();

    /** 演示阶段的当前按键索引 */
    @Setter
    private int demoCurrentIndex = -1;

    /** 演示阶段的tick计数器 */
    @Setter
    private int demoTickCounter = 0;

    /** 错误状态的tick计数器（用于显示2秒错误状态） */
    @Setter
    private int errorTickCounter = 0;

    /** 过渡状态持续时间常量 */
    private static final int ERROR_STATE_DURATION_TICKS = 40;  // 2秒
    private static final int LEVEL_SUCCESS_DURATION_TICKS = 20; // 1秒

    public BlockMemoryKeyCoreEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_MEMORY_KEY_CORE_ENTITY.get(), pos, state);
    }

    /**
     * 更新方块状态，同步游戏状态和生命值到方块属性
     */
    private void updateBlockState() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockState state = serverLevel.getBlockState(worldPosition);
        if (!(state.getBlock() instanceof BlockMemoryKeyCore)) {
            return;
        }

        BlockState newState = state
                .setValue(BlockMemoryKeyCore.GAME_STATE, gameState)
                .setValue(BlockMemoryKeyCore.LIVES, remainingLives);

        serverLevel.setBlock(worldPosition, newState, 3);
        serverLevel.sendBlockUpdated(worldPosition, state, newState, 3);
    }

    /**
     * 设置完整的按键序列
     *
     * @param newSequence 新的序列列表
     */
    public void setSequence(List<Integer> newSequence) {
        this.sequence = new ArrayList<>(newSequence);
        setChanged();
    }

    /**
     * 失去一条生命
     */
    public void loseLife() {
        if (remainingLives > 0) {
            setRemainingLives(remainingLives - 1);
        }
    }

    /**
     * 进入下一关
     * <p>
     * 关卡递增，但保持序列数据不变。
     */
    public void nextLevel() {
        currentLevel = currentLevel.next();
        currentSequenceIndex = 0;
        flashingIndex = -1;
        demoCurrentIndex = -1;
        demoTickCounter = 0;
        errorTickCounter = 0;
        startDemonstration();
        setChanged();
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
        this.remainingLives = 3;
        this.currentSequenceIndex = 0;
        this.flashingIndex = -1;
        this.demoCurrentIndex = -1;
        this.demoTickCounter = 0;
        this.errorTickCounter = 0;

        // 同步到方块状态
        updateBlockState();
        setChanged();
    }

    /** 初始生命数 */
    private static final int INITIAL_LIVES = 3;

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
            if (handleErrorState()) {
                return;
            }
        }

        // 处理关卡成功状态（显示1秒后进入下一关演示或通关）
        if (gameState == MemoryKeyGameState.LEVEL_SUCCESS) {
            if (handleLevelSuccessState()) {
                return;
            }
        }

        // 处理演示阶段
        if (gameState == MemoryKeyGameState.DEMONSTRATING) {
            handleDemonstrationState(serverLevel);
        }
    }

    /**
     * 处理错误状态
     * 
     * @return true表示状态已处理，需要提前返回
     */
    private boolean handleErrorState() {
        errorTickCounter++;

        if (errorTickCounter >= ERROR_STATE_DURATION_TICKS) {
            errorTickCounter = 0;
            // 重置当前关卡，开始重新演示
            currentSequenceIndex = 0;
            flashingIndex = -1;
            startDemonstration();
            setGameState(MemoryKeyGameState.DEMONSTRATING);
        }
        return true;
    }

    /**
     * 处理关卡成功状态
     * 
     * @return true表示状态已处理，需要提前返回
     */
    private boolean handleLevelSuccessState() {
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
        return true;
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
            int buttonId = sequence.get(demoCurrentIndex);
            flashingIndex = demoCurrentIndex;
            GameMemoryKeyHelper.triggerButtonFlash(serverLevel, worldPosition, buttonId);

            demoCurrentIndex++;
            demoTickCounter = 0;
            setChanged();
            serverLevel.sendBlockUpdated(worldPosition, serverLevel.getBlockState(worldPosition), serverLevel.getBlockState(worldPosition), 3);
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        writeToTag(tag);
        output.store("MemoryKeyData", CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read("MemoryKeyData", CompoundTag.CODEC).orElse(new CompoundTag());
        readFromTag(tag);
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        writeToTag(tag);
        return tag;
    }

    /**
     * 将游戏数据写入NBT标签
     */
    private void writeToTag(CompoundTag tag) {
        tag.putString(KEY_GAME_STATE, gameState.getSerializedName());
        tag.putInt(KEY_CURRENT_LEVEL, currentLevel.getLevelNumber());
        tag.putInt(KEY_REMAINING_LIVES, remainingLives);
        tag.putInt(KEY_CURRENT_SEQUENCE_INDEX, currentSequenceIndex);
        tag.putInt(KEY_FLASHING_INDEX, flashingIndex);
        tag.putIntArray(KEY_SEQUENCE_DATA, sequence.stream().mapToInt(Integer::intValue).toArray());
    }

    /**
     * 从NBT标签读取游戏数据
     */
    private void readFromTag(CompoundTag tag) {
        String gameStateStr = tag.getString(KEY_GAME_STATE).orElse(MemoryKeyGameState.IDLE.name());
        try {
            gameState = MemoryKeyGameState.valueOf(gameStateStr);
        } catch (IllegalArgumentException e) {
            gameState = MemoryKeyGameState.IDLE;
        }

        currentLevel = MemoryKeyLevel.fromLevelNumber(tag.getIntOr(KEY_CURRENT_LEVEL, 1));
        remainingLives = tag.getIntOr(KEY_REMAINING_LIVES, INITIAL_LIVES);
        currentSequenceIndex = tag.getIntOr(KEY_CURRENT_SEQUENCE_INDEX, 0);
        flashingIndex = tag.getIntOr(KEY_FLASHING_INDEX, -1);

        tag.getIntArray(KEY_SEQUENCE_DATA).ifPresent(arr -> {
            sequence.clear();
            for (int value : arr) sequence.add(value);
        });
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        HolderLookup.Provider registries = level != null ? level.registryAccess() : RegistryAccess.EMPTY;
        return ClientboundBlockEntityDataPacket.create(this, (be, _) -> be.getUpdateTag(registries));
    }
}
