package com.simple_block_game.common.simpleMinesweeper.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.simpleMinesweeper.data.PresetDifficulty;
import com.simple_block_game.common.simpleMinesweeper.logic.GameMinesweeperLogic;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
public class BlockMinesweeperCoreEntity extends BlockEntity {

    private int currentFlagCount;
    private boolean[][] mineGrid;

    private PresetDifficulty presetDifficulty = PresetDifficulty.EASY;
    @Setter
    private int gridWidth = 9;
    @Setter
    private int gridHeight = 9;
    @Setter
    private int totalMineCount = 10;

    @Setter
    private boolean gameOver = false;

    private static final String KEY_MINESWEEPER_DATA = "MinesweeperCoreData";
    private static final String NBT_KEY_TOTAL_MINE_COUNT = "TotalMineCount";
    private static final String NBT_KEY_CURRENT_FLAG_COUNT = "CurrentFlagCount";
    private static final String NBT_KEY_PRESET_DIFFICULTY = "PresetDifficulty";
    private static final String NBT_KEY_GRID_WIDTH = "GridWidth";
    private static final String NBT_KEY_GRID_HEIGHT = "GridHeight";
    private static final String NBT_KEY_GAME_OVER = "GameOver";
    private static final String NBT_KEY_MINE_GRID = "MineGrid";
    private static final String NBT_KEY_MINE_GRID_WIDTH = "Width";
    private static final String NBT_KEY_MINE_GRID_HEIGHT = "Height";
    private static final String NBT_KEY_MINE_GRID_DATA = "Data";

    public BlockMinesweeperCoreEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_MINESWEEPER_CORE_ENTITY.get(), pos, state);
    }

    public void initGameData() {
        this.currentFlagCount = 0;
        this.mineGrid = new boolean[gridHeight][gridWidth];
        this.gameOver = false;
        this.setChanged();
    }

    public void setMineGrid(int startX, int startZ) {
        this.mineGrid = GameMinesweeperLogic.generateMineGrid(gridWidth, gridHeight, totalMineCount, startX, startZ);
    }

    public void setPresetDifficulty(PresetDifficulty preset) {
        this.presetDifficulty = preset;
        this.gridWidth = preset.getWidth();
        this.gridHeight = preset.getHeight();
        this.totalMineCount = preset.getMineCount();
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            if (state.getValue(BlockMinesweeperCore.DIFFICULTY) != preset) {
                this.level.setBlock(this.worldPosition, state.setValue(BlockMinesweeperCore.DIFFICULTY, preset), 3);
            }
        }
    }

    public void adjustSize(boolean isXAxis, boolean increase) {
        float content = getMineContent();
        if (isXAxis) {
            gridWidth = Mth.clamp(increase ? gridWidth + 1 : gridWidth - 1, 9, 256);
        } else {
            gridHeight = Mth.clamp(increase ? gridHeight + 1 : gridHeight - 1, 9, 256);
        }
        totalMineCount = (int) Mth.clamp(gridWidth * gridHeight * content, gridWidth * gridHeight * 0.1f, gridWidth * gridHeight * 0.4f);
    }

    public void adjustMineCount(boolean add) {
        int increment = (int) (gridWidth * gridHeight * 0.005f);
        if (add) totalMineCount += increment;
        else totalMineCount -= increment;
        totalMineCount = (int) Mth.clamp(totalMineCount, gridWidth * gridHeight * 0.1f, gridWidth * gridHeight * 0.4f);
    }

    public float getMineContent() {
        return (float) totalMineCount / (gridWidth * gridHeight);
    }

    public void setCurrentFlagCount(int count) {
        this.currentFlagCount = count;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag customTag = new CompoundTag();
        customTag.putInt(NBT_KEY_TOTAL_MINE_COUNT, this.totalMineCount);
        customTag.putInt(NBT_KEY_CURRENT_FLAG_COUNT, this.currentFlagCount);
        customTag.putString(NBT_KEY_PRESET_DIFFICULTY, this.presetDifficulty.name());
        customTag.putInt(NBT_KEY_GRID_WIDTH, this.gridWidth);
        customTag.putInt(NBT_KEY_GRID_HEIGHT, this.gridHeight);
        customTag.putBoolean(NBT_KEY_GAME_OVER, this.gameOver);
        if (mineGrid != null && mineGrid.length > 0 && mineGrid[0].length > 0) {
            CompoundTag mineGridTag = new CompoundTag();
            mineGridTag.putInt(NBT_KEY_MINE_GRID_WIDTH, mineGrid[0].length);
            mineGridTag.putInt(NBT_KEY_MINE_GRID_HEIGHT, mineGrid.length);
            StringBuilder sb = new StringBuilder();
            for (boolean[] row : mineGrid) {
                for (boolean hasMine : row) {
                    sb.append(hasMine ? "1" : "0");
                }
            }
            mineGridTag.putString(NBT_KEY_MINE_GRID_DATA, sb.toString());
            customTag.put(NBT_KEY_MINE_GRID, mineGridTag);
        }
        output.store(KEY_MINESWEEPER_DATA, CompoundTag.CODEC, customTag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag customTag = input.read(KEY_MINESWEEPER_DATA, CompoundTag.CODEC).orElse(new CompoundTag());
        this.totalMineCount = customTag.getIntOr(NBT_KEY_TOTAL_MINE_COUNT, 10);
        this.currentFlagCount = customTag.getIntOr(NBT_KEY_CURRENT_FLAG_COUNT, 0);
        String presetName = customTag.getStringOr(NBT_KEY_PRESET_DIFFICULTY, "easy");
        try {
            this.presetDifficulty = PresetDifficulty.valueOf(presetName);
        } catch (IllegalArgumentException e) {
            this.presetDifficulty = PresetDifficulty.EASY;
        }
        this.gridWidth = customTag.getIntOr(NBT_KEY_GRID_WIDTH, 9);
        this.gridHeight = customTag.getIntOr(NBT_KEY_GRID_HEIGHT, 9);
        this.gameOver = customTag.getBooleanOr(NBT_KEY_GAME_OVER, false);
        if (customTag.contains(NBT_KEY_MINE_GRID)) {
            CompoundTag mineGridTag = customTag.getCompoundOrEmpty(NBT_KEY_MINE_GRID);
            int width = mineGridTag.getIntOr(NBT_KEY_MINE_GRID_WIDTH, 0);
            int height = mineGridTag.getIntOr(NBT_KEY_MINE_GRID_HEIGHT, 0);
            String data = mineGridTag.getStringOr(NBT_KEY_MINE_GRID_DATA, "");
            if (data.length() == width * height && width > 0 && height > 0) {
                this.mineGrid = new boolean[height][width];
                int index = 0;
                for (int z = 0; z < height; z++) {
                    for (int x = 0; x < width; x++) {
                        this.mineGrid[z][x] = data.charAt(index++) == '1';
                    }
                }
            } else {
                this.mineGrid = new boolean[gridHeight][gridWidth];
            }
        }
    }

    @Nullable
    @Override
    public Packet<@NonNull ClientGamePacketListener> getUpdatePacket() {
        HolderLookup.Provider registries = this.level != null ? this.level.registryAccess() : RegistryAccess.EMPTY;
        return ClientboundBlockEntityDataPacket.create(this, (blockEntity, _) -> blockEntity.getUpdateTag(registries));
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(@NonNull HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt(NBT_KEY_TOTAL_MINE_COUNT, this.totalMineCount);
        tag.putInt(NBT_KEY_CURRENT_FLAG_COUNT, this.currentFlagCount);
        tag.putString(NBT_KEY_PRESET_DIFFICULTY, this.presetDifficulty.name());
        tag.putInt(NBT_KEY_GRID_WIDTH, this.gridWidth);
        tag.putInt(NBT_KEY_GRID_HEIGHT, this.gridHeight);
        tag.putBoolean(NBT_KEY_GAME_OVER, this.gameOver);
        return tag;
    }
}
