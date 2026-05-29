package com.simple_block_game.common.simpleMinesweeper.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseGameBlockEntity;
import com.simple_block_game.common.simpleMinesweeper.data.PresetDifficulty;
import com.simple_block_game.common.simpleMinesweeper.logic.GameMinesweeperLogic;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * 扫雷游戏核心方块实体，存储雷区布局和游戏状态
 */
public class BlockMinesweeperCoreEntity extends BaseGameBlockEntity {

    private static final String KEY_DATA = "MinesweeperCoreData";
    private static final String KEY_MINE_COUNT = "TotalMineCount";
    private static final String KEY_FLAG_COUNT = "CurrentFlagCount";
    private static final String KEY_PRESET = "PresetDifficulty";
    private static final String KEY_WIDTH = "GridWidth";
    private static final String KEY_HEIGHT = "GridHeight";
    private static final String KEY_GAME_OVER = "GameOver";
    private static final String KEY_MINE_GRID = "MineGrid";
    private static final String KEY_GRID_WIDTH = "Width";
    private static final String KEY_GRID_HEIGHT = "Height";
    private static final String KEY_GRID_DATA = "Data";

    private int flagCount;
    @Getter
    private boolean[][] mineGrid;
    private PresetDifficulty preset = PresetDifficulty.EASY;
    private int width = 9;
    private int height = 9;
    private int mineCount = 10;
    @Setter
    @Getter
    private boolean gameOver;

    public BlockMinesweeperCoreEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_MINESWEEPER_CORE_ENTITY.get(), pos, state);
    }

    public int getCurrentFlagCount() {
        return flagCount;
    }

    public PresetDifficulty getPresetDifficulty() {
        return preset;
    }

    public int getGridWidth() {
        return width;
    }

    public int getGridHeight() {
        return height;
    }

    public int getTotalMineCount() {
        return mineCount;
    }

    public void initGameData() {
        flagCount = 0;
        mineGrid = new boolean[height][width];
        gameOver = false;
        setChanged();
    }

    public void setMineGrid(int startX, int startZ) {
        mineGrid = GameMinesweeperLogic.generateMineGrid(width, height, mineCount, startX, startZ);
    }

    public void setPresetDifficulty(PresetDifficulty preset) {
        if (preset == null) return;
        this.preset = preset;
        width = Math.max(1, preset.getWidth());
        height = Math.max(1, preset.getHeight());
        mineCount = Math.max(1, preset.getMineCount());
        setChanged();
    }

    public void adjustSize(boolean isXAxis, boolean increase) {
        if (width <= 0 || height <= 0) return;

        float content = getMineContent();
        if (isXAxis) {
            width = Mth.clamp(increase ? width + 1 : width - 1, 9, 256);
        } else {
            height = Mth.clamp(increase ? height + 1 : height - 1, 9, 256);
        }
        mineCount = (int) Mth.clamp(width * height * content, width * height * 0.1f, width * height * 0.4f);
        setChanged();
    }

    public void adjustMineCount(boolean add) {
        if (width <= 0 || height <= 0) return;

        int increment = Math.max(1, (int) (width * height * 0.005f));
        mineCount = Mth.clamp(add ? mineCount + increment : mineCount - increment,
                Math.max(1, (int) (width * height * 0.1f)),
                Math.min(width * height - 1, (int) (width * height * 0.4f)));
        setChanged();
    }

    public float getMineContent() {
        return (float) mineCount / (width * height);
    }

    public void setCurrentFlagCount(int count) {
        flagCount = count;
        setChanged();
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY_MINE_COUNT, mineCount);
        tag.putInt(KEY_FLAG_COUNT, flagCount);
        tag.putString(KEY_PRESET, preset.name());
        tag.putInt(KEY_WIDTH, width);
        tag.putInt(KEY_HEIGHT, height);
        tag.putBoolean(KEY_GAME_OVER, gameOver);

        if (mineGrid != null && mineGrid.length > 0 && mineGrid[0].length > 0) {
            CompoundTag gridTag = new CompoundTag();
            gridTag.putInt(KEY_GRID_WIDTH, mineGrid[0].length);
            gridTag.putInt(KEY_GRID_HEIGHT, mineGrid.length);
            StringBuilder sb = new StringBuilder();
            for (boolean[] row : mineGrid) {
                for (boolean hasMine : row) sb.append(hasMine ? "1" : "0");
            }
            gridTag.putString(KEY_GRID_DATA, sb.toString());
            tag.put(KEY_MINE_GRID, gridTag);
        }
        output.store(KEY_DATA, CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(KEY_DATA, CompoundTag.CODEC).orElse(new CompoundTag());
        mineCount = tag.getIntOr(KEY_MINE_COUNT, 10);
        flagCount = tag.getIntOr(KEY_FLAG_COUNT, 0);

        try {
            preset = PresetDifficulty.valueOf(tag.getStringOr(KEY_PRESET, "EASY"));
        } catch (IllegalArgumentException e) {
            preset = PresetDifficulty.EASY;
        }

        width = tag.getIntOr(KEY_WIDTH, 9);
        height = tag.getIntOr(KEY_HEIGHT, 9);
        gameOver = tag.getBooleanOr(KEY_GAME_OVER, false);

        if (tag.contains(KEY_MINE_GRID)) {
            CompoundTag gridTag = tag.getCompoundOrEmpty(KEY_MINE_GRID);
            int w = gridTag.getIntOr(KEY_GRID_WIDTH, 0);
            int h = gridTag.getIntOr(KEY_GRID_HEIGHT, 0);
            String data = gridTag.getStringOr(KEY_GRID_DATA, "");

            if (data.length() == w * h && w > 0 && h > 0) {
                mineGrid = new boolean[h][w];
                int idx = 0;
                for (int z = 0; z < h; z++) {
                    for (int x = 0; x < w; x++) {
                        mineGrid[z][x] = data.charAt(idx++) == '1';
                    }
                }
            } else {
                mineGrid = new boolean[height][width];
            }
        }
    }
}
