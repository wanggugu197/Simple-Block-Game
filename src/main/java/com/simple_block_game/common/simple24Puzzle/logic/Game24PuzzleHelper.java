package com.simple_block_game.common.simple24Puzzle.logic;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleCore;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleCoreEntity;
import com.simple_block_game.common.simple24Puzzle.block.Block24PuzzleDisplayEntity;
import com.simple_block_game.common.simple24Puzzle.data.GameToken24Puzzle;
import com.simple_block_game.common.simple24Puzzle.simple24PuzzleRegistration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

/**
 * 24点游戏辅助工具类
 */
public final class Game24PuzzleHelper {

    private static final int DISPLAY_WIDTH = 6;
    private static final int DISPLAY_HEIGHT = 3;
    private static final int LAYOUT_WIDTH = DISPLAY_WIDTH + 2;
    private static final int LAYOUT_HEIGHT = DISPLAY_HEIGHT + 2;

    private static final GameToken24Puzzle[] BOTTOM_ROW_TOKENS = {
            GameToken24Puzzle.ADD,
            GameToken24Puzzle.SUB,
            GameToken24Puzzle.MUL,
            GameToken24Puzzle.DIV,
            GameToken24Puzzle.L_BRACKET,
            GameToken24Puzzle.R_BRACKET
    };

    private Game24PuzzleHelper() {}

    /** 检查布局区域是否为空，确保展开游戏时有足够空间 */
    public static boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        Direction.Axis axis = getAxis(coreFacing);
        int dir = getDirection(coreFacing);
        for (int i = 0; i < LAYOUT_WIDTH; i++) {
            for (int j = 0; j < LAYOUT_HEIGHT; j++) {
                BlockPos pos = calcPos(corePos, axis, dir, i, j);
                if (!pos.equals(corePos) && !level.isEmptyBlock(pos)) return false;
            }
        }
        return true;
    }

    public static void generateLayout(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        Direction.Axis axis = getAxis(coreFacing);
        int dir = getDirection(coreFacing);
        BlockState displayState = simple24PuzzleRegistration.BLOCK_24PUZZLE_DISPLAY.get()
                .defaultBlockState().setValue(BaseRotatedBlock.FACING, coreFacing);
        BlockState refreshState = simple24PuzzleRegistration.BLOCK_24PUZZLE_REFRESH.get()
                .defaultBlockState().setValue(BaseRotatedBlock.FACING, coreFacing);
        BlockState frameState = SimpleBlockGameRegistration.BLOCK_ROTATED_FRAME.get()
                .defaultBlockState().setValue(BaseRotatedBlock.FACING, coreFacing);
        for (int i = 0; i < LAYOUT_WIDTH; i++) {
            for (int j = 0; j < LAYOUT_HEIGHT; j++) {
                BlockPos pos = calcPos(corePos, axis, dir, i, j);
                if (pos.equals(corePos)) continue;
                BlockState state = getBlockState(i, j, displayState, refreshState, frameState);
                if (!level.isEmptyBlock(pos)) continue;
                level.setBlock(pos, state, Block.UPDATE_ALL);
                if ((i >= 1 && i <= DISPLAY_WIDTH && j >= 1 && j <= DISPLAY_HEIGHT) && level.getBlockEntity(pos) instanceof Block24PuzzleDisplayEntity entity) {
                    entity.setCorePos(corePos);
                    if (j == 1) entity.setToken(BOTTOM_ROW_TOKENS[i - 1]);
                }
                if ((i == LAYOUT_WIDTH - 1 && j == LAYOUT_HEIGHT - 1) && level.getBlockEntity(pos) instanceof BlockRefreshEntity refreshEntity) {
                    refreshEntity.setCorePos(corePos);
                }
            }
        }
    }

    /** 最小化布局，移除所有游戏方块并重置核心方块状态 */
    public static void minimizeLayout(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        if (corePos == null || !level.isLoaded(corePos)) return;
        Direction.Axis axis = getAxis(coreFacing);
        int dir = getDirection(coreFacing);
        for (int i = 0; i < LAYOUT_WIDTH; i++) {
            for (int j = 0; j < LAYOUT_HEIGHT; j++) {
                BlockPos pos = calcPos(corePos, axis, dir, i, j);
                if (pos.equals(corePos)) continue;
                Block block = level.getBlockState(pos).getBlock();
                if (is24PuzzleBlock(block)) level.removeBlock(pos, false);
            }
        }
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof Block24PuzzleCore) {
            level.setBlock(corePos, coreState.setValue(IGameCoreBlock.UNFOLDED, false), Block.UPDATE_ALL);
            resetCoreEntity(level, corePos);
        }
    }

    /** 关闭布局，最小化后掉落核心方块物品 */
    public static void closeLayout(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        minimizeLayout(level, corePos, coreFacing);
        if (corePos == null || !level.isLoaded(corePos)) return;
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof Block24PuzzleCore) {
            Vec3 dropPos = Vec3.atCenterOf(corePos);
            level.addFreshEntity(new ItemEntity(level, dropPos.x, dropPos.y, dropPos.z,
                    new ItemStack(simple24PuzzleRegistration.BLOCK_24PUZZLE_CORE.get())));
            level.removeBlock(corePos, false);
        }
    }

    /** 处理游戏交互，读取输入并验证答案 */
    public static boolean handleGameInteraction(ServerLevel level, BlockPos corePos) {
        Block24PuzzleCoreEntity coreEntity = getCoreEntity(level, corePos);
        if (coreEntity == null) return false;
        List<GameToken24Puzzle> inputTokens = coreEntity.getInputTokens();
        List<GameToken24Puzzle> puzzleTokens = coreEntity.getCurrentNumbers();
        if (inputTokens == null || inputTokens.isEmpty()) return false;
        coreEntity.setInputTokens(inputTokens);
        boolean success = Game24PuzzleLogic.verifyResult(inputTokens, puzzleTokens);
        if (success) {
            coreEntity.incrementCompletedCount(level);
            return true;
        }
        return false;
    }

    /** 开始新游戏，生成谜题并初始化状态 */
    public static void startGame(ServerLevel level, BlockPos corePos, Direction facing) {
        Block24PuzzleCoreEntity coreEntity = getCoreEntity(level, corePos);
        if (coreEntity == null) return;
        List<GameToken24Puzzle> puzzle = Game24PuzzleLogic.generateValidPuzzle();
        coreEntity.setCurrentNumbers(level, corePos, facing, puzzle);
        coreEntity.setInputTokens(new ArrayList<>());
        if (coreEntity.getStartTime() == 0) {
            coreEntity.setStartTime(level.getGameTime());
        }
    }

    /** 在第3行中间4个位置显示题目数字 */
    public static void showPuzzleNumbers(ServerLevel level, BlockPos corePos, Direction facing, List<GameToken24Puzzle> puzzle) {
        if (puzzle == null || puzzle.size() != 4) return;
        Direction.Axis axis = getAxis(facing);
        int dir = getDirection(facing);
        int startCol = 2;
        for (int i = 0; i < 4; i++) {
            BlockPos pos = calcPos(corePos, axis, dir, startCol + i, 3);
            if (level.getBlockEntity(pos) instanceof Block24PuzzleDisplayEntity entity) {
                entity.setToken(puzzle.get(i));
            }
        }
    }

    /** 重置游戏，生成新谜题 */
    public static void resetGame(ServerLevel level, BlockPos corePos, Direction facing) {
        Block24PuzzleCoreEntity coreEntity = getCoreEntity(level, corePos);
        if (coreEntity != null) {
            List<GameToken24Puzzle> puzzle = Game24PuzzleLogic.generateValidPuzzle();
            coreEntity.setCurrentNumbers(level, corePos, facing, puzzle);
            coreEntity.setInputTokens(new ArrayList<>());
        }
    }

    /** 重置核心实体状态 */
    private static void resetCoreEntity(ServerLevel level, BlockPos corePos) {
        BlockEntity be = level.getBlockEntity(corePos);
        if (be instanceof Block24PuzzleCoreEntity coreEntity) {
            coreEntity.reset();
        }
    }

    /** 获取核心方块实体 */
    public static Block24PuzzleCoreEntity getCoreEntity(ServerLevel level, BlockPos corePos) {
        BlockEntity be = level.getBlockEntity(corePos);
        return be instanceof Block24PuzzleCoreEntity coreEntity ? coreEntity : null;
    }

    /**
     * 处理Token输入，点击行3数字或行1运算符时调用
     */
    public static void handleTokenInput(Block24PuzzleCoreEntity coreEntity, GameToken24Puzzle token) {
        if (token == null) return;
        if (coreEntity == null) return;
        coreEntity.addInputToken(token);
    }

    /** 根据朝向获取轴方向 */
    private static Direction.Axis getAxis(Direction facing) {
        return switch (facing) {
            case NORTH, SOUTH -> Direction.Axis.X;
            case EAST, WEST -> Direction.Axis.Z;
            default -> Direction.Axis.Y;
        };
    }

    /** 根据朝向获取方向系数（+1或-1） */
    private static int getDirection(Direction facing) {
        return switch (facing) {
            case NORTH, EAST -> -1;
            case SOUTH, WEST -> +1;
            default -> 1;
        };
    }

    /** 根据核心位置、轴、方向系数计算方块位置 */
    private static BlockPos calcPos(BlockPos corePos, Direction.Axis axis, int dir, int i, int j) {
        int offset = i * dir;
        return switch (axis) {
            case X -> new BlockPos(corePos.getX() + offset, corePos.getY() + j, corePos.getZ());
            case Z -> new BlockPos(corePos.getX(), corePos.getY() + j, corePos.getZ() + offset);
            default -> corePos;
        };
    }

    /** 根据坐标获取对应的方块状态（显示/刷新/框架） */
    private static BlockState getBlockState(int i, int j, BlockState display, BlockState refresh, BlockState frame) {
        if (i >= 1 && i <= DISPLAY_WIDTH && j >= 1 && j <= DISPLAY_HEIGHT) return display;
        if (i == LAYOUT_WIDTH - 1 && j == LAYOUT_HEIGHT - 1) return refresh;
        return frame;
    }

    /** 判断是否为24点游戏相关方块 */
    private static boolean is24PuzzleBlock(Block block) {
        return block == SimpleBlockGameRegistration.BLOCK_ROTATED_FRAME.get() ||
                block == simple24PuzzleRegistration.BLOCK_24PUZZLE_DISPLAY.get() ||
                block == simple24PuzzleRegistration.BLOCK_24PUZZLE_REFRESH.get();
    }

    /**
     * 获取格式化的耗时显示
     */
    public static String getFormattedTime(long elapsedTicks) {
        long totalSeconds = elapsedTicks / TICKS_PER_SECOND;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
