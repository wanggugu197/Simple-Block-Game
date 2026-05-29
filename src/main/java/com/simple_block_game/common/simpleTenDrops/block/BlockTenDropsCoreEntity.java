package com.simple_block_game.common.simpleTenDrops.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleTenDrops.data.TenDropsGameState;
import com.simple_block_game.common.simpleTenDrops.logic.GameTenDropsHelper;
import com.simple_block_game.common.simpleTenDrops.logic.GameTenDropsLogic;
import com.simple_block_game.common.simpleTenDrops.logic.GameTenDropsReward;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

import static com.simple_block_game.common.simpleTenDrops.logic.GameTenDropsLogic.GRID_SIZE;

public class BlockTenDropsCoreEntity extends BaseGameBlockEntity {

    private static final String KEY_DATA = "TenDropGameData";
    private static final String KEY_GAME_STATE = "GameState";
    private static final String KEY_WATER_DROPS = "WaterDrops";
    private static final String KEY_GRID_DATA = "GridData";
    private static final String KEY_CURRENT_LEVEL = "CurrentLevel";

    @Getter
    private TenDropsGameState gameState = TenDropsGameState.IDLE;
    @Getter
    private int waterDrops = GameTenDropsLogic.INITIAL_WATER_DROPS;
    private int[][] grid = GameTenDropsLogic.initGrid();
    @Getter
    private int currentLevel = 1;
    @Getter
    @Setter
    private Player currentPlayer;

    private int eliminatedCount = 0;
    private int comboCount = 0;

    public BlockTenDropsCoreEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_TEN_DROPS_CORE_ENTITY.get(), pos, state);
    }

    public void setGameState(TenDropsGameState gameState) {
        this.gameState = gameState;
        syncChanges();
    }

    public void setWaterDrops(int waterDrops) {
        this.waterDrops = Math.max(0, waterDrops);
        syncChanges();
    }

    public void tick() {
        if (!(level instanceof ServerLevel serverLevel) || gameState.isGameEnded() || gameState != TenDropsGameState.BURSTING) return;

        int[] eliminatedRef = { eliminatedCount };
        int[] comboRef = { comboCount };
        boolean hasActiveDroplets = GameTenDropsHelper.processFlyingDroplets(serverLevel, worldPosition, grid, eliminatedRef, comboRef);

        eliminatedCount = eliminatedRef[0];
        comboCount = comboRef[0];

        if (!hasActiveDroplets) checkGameStateAfterBurst(serverLevel);
    }

    private void checkGameStateAfterBurst(ServerLevel serverLevel) {
        if (eliminatedCount > 0) {
            waterDrops = GameTenDropsHelper.checkGameState(serverLevel, worldPosition, grid, waterDrops, eliminatedCount, comboCount, currentLevel);
        } else {
            GameTenDropsHelper.updateDisplay(serverLevel, worldPosition, grid);
        }

        eliminatedCount = 0;
        comboCount = 0;

        if (GameTenDropsLogic.isVictory(grid)) {
            handleVictory(serverLevel);
        } else {
            setGameState(TenDropsGameState.PLAYING);
        }
    }

    public void triggerGameOver() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (GameTenDropsLogic.isVictory(grid)) {
            handleVictory(serverLevel);
        } else {
            setGameState(TenDropsGameState.GAME_OVER);
            GameTenDropsHelper.updateDisplay(serverLevel, worldPosition, grid);
        }
    }

    private void handleVictory(ServerLevel serverLevel) {
        GameTenDropsReward.handleLevelReward(serverLevel, currentPlayer, currentLevel);
        if (currentLevel >= 10) {
            setGameState(TenDropsGameState.VICTORY);
        } else {
            currentLevel++;
            nextLevel();
        }
    }

    public void nextLevel() {
        if (gameState.isGameEnded()) return;

        this.grid = GameTenDropsLogic.generateLevelGrid(currentLevel);
        this.waterDrops = GameTenDropsLogic.INITIAL_WATER_DROPS;

        if (level instanceof ServerLevel serverLevel) {
            GameTenDropsHelper.updateDisplay(serverLevel, worldPosition, grid);
        }

        this.gameState = TenDropsGameState.PLAYING;
        syncChanges();
    }

    public void initialize() {
        this.gameState = TenDropsGameState.IDLE;
        this.currentLevel = 1;
        this.waterDrops = GameTenDropsLogic.INITIAL_WATER_DROPS;
        this.grid = GameTenDropsLogic.initGrid();

        if (level instanceof ServerLevel serverLevel) {
            GameTenDropsHelper.updateDisplay(serverLevel, worldPosition, grid);
        }

        syncChanges();
    }

    public void completeReset() {
        this.currentLevel = 1;
        this.waterDrops = GameTenDropsLogic.INITIAL_WATER_DROPS;
        this.grid = GameTenDropsLogic.initGrid();
        this.gameState = TenDropsGameState.PLAYING;

        if (level instanceof ServerLevel serverLevel) {
            GameTenDropsHelper.updateDisplay(serverLevel, worldPosition, grid);
        }

        syncChanges();
    }

    public void handleBurstFromDisplay(BlockPos displayPos) {
        if (displayPos == null || !(level instanceof ServerLevel serverLevel) || gameState != TenDropsGameState.PLAYING) return;

        setGameState(TenDropsGameState.BURSTING);

        eliminatedCount = 1;
        comboCount = 1;

        int gridX = displayPos.getX() - worldPosition.getX() - 1;
        int gridY = displayPos.getZ() - worldPosition.getZ() - 1;
        if (gridX >= 0 && gridX < GRID_SIZE && gridY >= 0 && gridY < GRID_SIZE) grid[gridY][gridX] = 0;

        BlockEntity be = serverLevel.getBlockEntity(displayPos);
        if (be instanceof BlockTenDropsDisplayEntity displayEntity) displayEntity.startBurst();
    }

    private void syncChanges() {
        setChanged();
        if (!(level instanceof ServerLevel serverLevel)) return;
        serverLevel.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();

        tag.putString(KEY_GAME_STATE, gameState.getSerializedName());
        tag.putInt(KEY_WATER_DROPS, waterDrops);
        tag.putInt(KEY_CURRENT_LEVEL, currentLevel);

        CompoundTag gridTag = new CompoundTag();
        for (int i = 0; i < GRID_SIZE; i++) gridTag.putIntArray("Row" + i, grid[i]);
        tag.put(KEY_GRID_DATA, gridTag);

        output.store(KEY_DATA, CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(KEY_DATA, CompoundTag.CODEC).orElse(new CompoundTag());

        gameState = TenDropsGameState.fromSerializedName(tag.getStringOr(KEY_GAME_STATE, "idle"));
        waterDrops = tag.getIntOr(KEY_WATER_DROPS, GameTenDropsLogic.INITIAL_WATER_DROPS);
        currentLevel = tag.getIntOr(KEY_CURRENT_LEVEL, 1);

        CompoundTag gridTag = tag.getCompoundOrEmpty(KEY_GRID_DATA);
        if (!gridTag.isEmpty()) {
            for (int i = 0; i < GRID_SIZE; i++) {
                Optional<int[]> row = gridTag.getIntArray("Row" + i);
                if (row.isPresent() && row.get().length == GRID_SIZE) grid[i] = row.get().clone();
            }
        }

        if (level instanceof ServerLevel serverLevel) GameTenDropsHelper.updateDisplay(serverLevel, worldPosition, grid);
    }
}
