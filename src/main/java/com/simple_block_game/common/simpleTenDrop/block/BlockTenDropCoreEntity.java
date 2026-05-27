package com.simple_block_game.common.simpleTenDrop.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.simpleTenDrop.data.TenDropGameState;
import com.simple_block_game.common.simpleTenDrop.logic.GameTenDropHelper;
import com.simple_block_game.common.simpleTenDrop.logic.GameTenDropLogic;

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

import java.util.Optional;

/**
 * 十滴游戏核心方块实体，管理游戏状态、水滴数量、网格数据和当前关卡
 */
public class BlockTenDropCoreEntity extends BlockEntity {

    private static final String KEY_DATA = "TenDropGameData";
    private static final String KEY_GAME_STATE = "GameState";
    private static final String KEY_WATER_DROPS = "WaterDrops";
    private static final String KEY_GRID_DATA = "GridData";
    private static final String KEY_CURRENT_LEVEL = "CurrentLevel";

    private static final int MAX_VISUAL_WATER_DROPS = 11;
    private static final int MIN_WATER_DROPS = 0;

    @Getter
    private TenDropGameState gameState = TenDropGameState.IDLE;
    private int waterDrops = GameTenDropLogic.INITIAL_WATER_DROPS;
    private int[][] grid = GameTenDropLogic.initGrid();
    private int currentLevel = 1;

    public BlockTenDropCoreEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_TEN_DROP_CORE_ENTITY.get(), pos, state);
    }

    public void setGameState(TenDropGameState gameState) {
        this.gameState = gameState;
        setChanged();
        updateBlockState();
    }

    public int getWaterDrops() {
        return waterDrops;
    }

    public void setWaterDrops(int waterDrops) {
        this.waterDrops = Math.max(MIN_WATER_DROPS, waterDrops);
        setChanged();
        updateBlockState();
    }

    public int[][] getGrid() {
        return GameTenDropLogic.copyGrid(grid);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void tick() {
        if (!(level instanceof ServerLevel)) {
            return;
        }
    }

    public void handlePlayerClick(int x, int y) {
        if (gameState != TenDropGameState.PLAYING) {
            return;
        }

        GameTenDropLogic.GameResult result = GameTenDropLogic.processClick(grid, waterDrops, x, y, currentLevel);

        this.grid = result.newGrid();
        this.waterDrops = Math.max(MIN_WATER_DROPS, result.waterDrops());

        if (level instanceof ServerLevel serverLevel) {
            GameTenDropHelper.updateDisplay(serverLevel, worldPosition, grid);
        }

        if (result.victory()) {
            handleVictory();
        } else if (result.gameOver()) {
            setGameState(TenDropGameState.GAME_OVER);
        }

        setChanged();
        updateBlockState();
    }

    private void handleVictory() {
        setGameState(TenDropGameState.VICTORY);
        waterDrops += GameTenDropLogic.getLevelClearReward();
        currentLevel++;
        setChanged();
        updateBlockState();
    }

    public void nextLevel() {
        this.grid = GameTenDropLogic.generateNextLevelGrid(currentLevel);
        this.waterDrops = GameTenDropLogic.getInitialWaterDrops(currentLevel);

        if (level instanceof ServerLevel serverLevel) {
            GameTenDropHelper.updateDisplay(serverLevel, worldPosition, grid);
        }

        this.gameState = TenDropGameState.PLAYING;
        setChanged();
        updateBlockState();
    }

    public void initialize() {
        this.gameState = TenDropGameState.IDLE;
        this.currentLevel = 1;
        this.waterDrops = GameTenDropLogic.getInitialWaterDrops(currentLevel);
        this.grid = GameTenDropLogic.initGrid();

        if (level instanceof ServerLevel serverLevel) {
            GameTenDropHelper.updateDisplay(serverLevel, worldPosition, grid);
        }

        setChanged();
        updateBlockState();
    }

    public void completeReset() {
        initialize();
    }

    public void handleBurstFromDisplay(BlockPos displayPos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (gameState != TenDropGameState.PLAYING) {
            return;
        }

        setGameState(TenDropGameState.BURSTING);

        int relativeX = displayPos.getX() - worldPosition.getX();
        int relativeZ = displayPos.getZ() - worldPosition.getZ();

        int gridX = relativeX + GameTenDropLogic.GRID_SIZE / 2;
        int gridY = relativeZ + GameTenDropLogic.GRID_SIZE / 2;

        if (gridX >= 0 && gridX < GameTenDropLogic.GRID_SIZE &&
                gridY >= 0 && gridY < GameTenDropLogic.GRID_SIZE) {

            GameTenDropLogic.GameResult result = GameTenDropLogic.processClick(
                    grid, waterDrops, gridX, gridY, currentLevel);

            this.grid = result.newGrid();
            this.waterDrops = Math.max(MIN_WATER_DROPS, result.waterDrops());

            GameTenDropHelper.updateDisplay(serverLevel, worldPosition, grid);

            if (result.victory()) {
                handleVictory();
            } else if (result.gameOver()) {
                setGameState(TenDropGameState.GAME_OVER);
            } else {
                setGameState(TenDropGameState.PLAYING);
            }
        } else {
            setGameState(TenDropGameState.PLAYING);
        }

        setChanged();
        updateBlockState();
    }

    private void updateBlockState() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockState state = serverLevel.getBlockState(worldPosition);

        int visualWaterDrops = waterDrops > MAX_VISUAL_WATER_DROPS - 1 ? MAX_VISUAL_WATER_DROPS : waterDrops;
        state = state.setValue(BlockTenDropCore.WATER_DROPS, visualWaterDrops);

        serverLevel.setBlock(worldPosition, state, 3);
        serverLevel.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        writeToTag(tag);
        output.store(KEY_DATA, CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(KEY_DATA, CompoundTag.CODEC).orElse(new CompoundTag());
        readFromTag(tag);

        if (level instanceof ServerLevel serverLevel) {
            GameTenDropHelper.updateDisplay(serverLevel, worldPosition, grid);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        writeToTag(tag);
        return tag;
    }

    private void writeToTag(CompoundTag tag) {
        tag.putString(KEY_GAME_STATE, gameState.getSerializedName());
        tag.putInt(KEY_WATER_DROPS, waterDrops);
        tag.putInt(KEY_CURRENT_LEVEL, currentLevel);

        CompoundTag gridTag = new CompoundTag();
        for (int i = 0; i < GameTenDropLogic.GRID_SIZE; i++) {
            gridTag.putIntArray("Row" + i, grid[i]);
        }
        tag.put(KEY_GRID_DATA, gridTag);
    }

    private void readFromTag(CompoundTag tag) {
        String gameStateStr = tag.getStringOr(KEY_GAME_STATE, "idle");
        gameState = TenDropGameState.fromSerializedName(gameStateStr);

        waterDrops = tag.getIntOr(KEY_WATER_DROPS, GameTenDropLogic.INITIAL_WATER_DROPS);
        currentLevel = tag.getIntOr(KEY_CURRENT_LEVEL, 1);

        CompoundTag gridTag = tag.getCompoundOrEmpty(KEY_GRID_DATA);
        if (!gridTag.isEmpty()) {
            for (int i = 0; i < GameTenDropLogic.GRID_SIZE; i++) {
                Optional<int[]> row = gridTag.getIntArray("Row" + i);
                if (row.get().length == GameTenDropLogic.GRID_SIZE) {
                    grid[i] = row.get().clone();
                }
            }
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        HolderLookup.Provider registries = level != null ? level.registryAccess() : RegistryAccess.EMPTY;
        return ClientboundBlockEntityDataPacket.create(this, (be, _) -> be.getUpdateTag(registries));
    }
}
