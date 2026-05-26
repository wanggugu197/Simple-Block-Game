package com.simple_block_game.common.simple2048.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.simple2048.block.Block2048Core;
import com.simple_block_game.common.simple2048.block.Block2048Display;

import static com.simple_block_game.common.simple2048.logic.Game2048Logic.GRID_SIZE;

public class Game2048Helper {

    private Game2048Helper() {}

    /**
     * 检查2048布局区域是否全为空气
     */
    public static boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        Direction.Axis mainAxis = getMainExtendAxis(coreFacing);
        int extendDirection = getExtendDirection(coreFacing);
        for (int mainOffset = 0; mainOffset < GRID_SIZE + 2; mainOffset++) {
            for (int yOffset = 0; yOffset < GRID_SIZE + 2; yOffset++) {
                BlockPos targetPos = calculateTargetPos(corePos, mainAxis, extendDirection, mainOffset, yOffset);
                if (targetPos.equals(corePos)) continue;
                if (!level.isEmptyBlock(targetPos)) return false;
            }
        }
        return true;
    }

    /**
     * 生成2048的布局
     */
    public static void generate2048Layout(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        Direction.Axis mainAxis = getMainExtendAxis(coreFacing);
        int extendDirection = getExtendDirection(coreFacing);
        for (int mainOffset = 0; mainOffset < GRID_SIZE + 2; mainOffset++) {
            for (int yOffset = 0; yOffset < GRID_SIZE + 2; yOffset++) {
                BlockPos targetPos = calculateTargetPos(corePos, mainAxis, extendDirection, mainOffset, yOffset);
                if (targetPos.equals(corePos)) continue;
                BlockState placeState = getBlockStateByOffset(mainOffset, yOffset, coreFacing);
                if (level.isEmptyBlock(targetPos)) {
                    level.setBlock(targetPos, placeState, 3);
                }
            }
        }
    }

    /**
     * 根据FACING获取布局的主延伸轴
     */
    private static Direction.Axis getMainExtendAxis(Direction coreFacing) {
        return switch (coreFacing) {
            case NORTH, SOUTH -> Direction.Axis.X;
            case EAST, WEST -> Direction.Axis.Z;
            default -> Direction.Axis.Y;
        };
    }

    /**
     * 获取布局的延伸方向
     */
    private static int getExtendDirection(Direction coreFacing) {
        return switch (coreFacing) {
            case NORTH, EAST -> -1;
            case SOUTH, WEST -> +1;
            default -> 1;
        };
    }

    /**
     * 根据主轴/偏移量计算目标坐标
     */
    private static BlockPos calculateTargetPos(BlockPos corePos, Direction.Axis mainAxis, int extendDirection, int mainOffset, int yOffset) {
        int coreX = corePos.getX();
        int coreY = corePos.getY();
        int coreZ = corePos.getZ();
        int offset = mainOffset * extendDirection;
        return switch (mainAxis) {
            case X -> new BlockPos(coreX + offset, coreY + yOffset, coreZ);
            case Z -> new BlockPos(coreX, coreY + yOffset, coreZ + offset);
            default -> corePos;
        };
    }

    /**
     * 根据偏移量确定要放置的方块类型
     */
    private static BlockState getBlockStateByOffset(int mainOffset, int yOffset, Direction coreFacing) {
        String offsetKey = mainOffset + "," + yOffset;
        return switch (offsetKey) {
            case "1,1", "1,2", "1,3", "1,4", "2,1", "2,2", "2,3", "2,4", "3,1", "3,2", "3,3", "3,4", "4,1", "4,2", "4,3", "4,4" -> SimpleBlockGameRegistration.BLOCK_2048_DISPLAY
                    .get().defaultBlockState().setValue(BaseRotatedBlock.FACING, coreFacing);
            case "5,5" -> SimpleBlockGameRegistration.BLOCK_2048_REFRESH.get().defaultBlockState().setValue(BaseRotatedBlock.FACING, coreFacing);
            default -> SimpleBlockGameRegistration.BLOCK_ROTATED_FRAME.get().defaultBlockState().setValue(BaseRotatedBlock.FACING, coreFacing);
        };
    }

    /**
     * 从Refresh方块位置反推核心方块位置
     */
    public static BlockPos findCorePosFromRefreshPos(ServerLevel level, BlockPos refreshPos, Direction refreshFacing) {
        Direction.Axis mainAxis = getMainExtendAxis(refreshFacing);
        int extendDirection = getExtendDirection(refreshFacing);
        int offset = (GRID_SIZE + 1) * extendDirection;
        int coreX = refreshPos.getX();
        int coreY = refreshPos.getY() - (GRID_SIZE + 1);
        int coreZ = refreshPos.getZ();
        switch (mainAxis) {
            case X -> coreX -= offset;
            case Z -> coreZ -= offset;
        }
        BlockPos corePos = new BlockPos(coreX, coreY, coreZ);
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof Block2048Core) {
            return corePos;
        }
        return null;
    }

    /**
     * 最小化操作
     */
    public static void minimize2048Layout(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        if (corePos == null || !level.isLoaded(corePos)) {
            return;
        }
        Direction.Axis mainAxis = getMainExtendAxis(coreFacing);
        int extendDirection = getExtendDirection(coreFacing);
        for (int mainOffset = 0; mainOffset < GRID_SIZE + 2; mainOffset++) {
            for (int yOffset = 0; yOffset < GRID_SIZE + 2; yOffset++) {
                BlockPos targetPos = calculateTargetPos(corePos, mainAxis, extendDirection, mainOffset, yOffset);
                if (targetPos.equals(corePos)) continue;
                BlockState targetState = level.getBlockState(targetPos);
                Block targetBlock = targetState.getBlock();
                if (targetBlock == SimpleBlockGameRegistration.BLOCK_ROTATED_FRAME.get() ||
                        targetBlock == SimpleBlockGameRegistration.BLOCK_2048_DISPLAY.get() ||
                        targetBlock == SimpleBlockGameRegistration.BLOCK_2048_REFRESH.get()) {
                    level.removeBlock(targetPos, false);
                }
            }
        }
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof Block2048Core) {
            level.setBlock(corePos, coreState.setValue(Block2048Core.UNFOLDED, false), 3);
            Block2048Core.reset(level, corePos);
        }
    }

    /**
     * 关闭操作
     */
    public static void close2048Layout(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        minimize2048Layout(level, corePos, coreFacing);
        if (corePos == null || !level.isLoaded(corePos)) {
            return;
        }
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof Block2048Core) {
            ItemStack coreItemStack = new ItemStack(SimpleBlockGameRegistration.BLOCK_2048_CORE.get());
            Vec3 dropPos = Vec3.atCenterOf(corePos);
            ItemEntity itemEntity = new ItemEntity(level, dropPos.x, dropPos.y, dropPos.z, coreItemStack);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
            level.removeBlock(corePos, false);
        }
    }

    /**
     * 重置操作
     */
    public static void reset2048Layout(ServerLevel level, BlockPos corePos, Direction coreFacing) {
        if (corePos == null || !level.isLoaded(corePos)) {
            return;
        }
        int[][] initGrid = Game2048Logic.initGrid();
        writeDisplayGrid(level, corePos, coreFacing, initGrid);
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof Block2048Core) {
            Block2048Core.reset(level, corePos);
        }
    }

    /**
     * 读取Display方块的数组
     */
    public static int[][] readDisplayGrid(ServerLevel level, BlockPos corePos, Direction facing) {
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        Direction.Axis mainAxis = Game2048Helper.getMainExtendAxis(facing);
        int extendDirection = Game2048Helper.getExtendDirection(facing);
        for (int row = 0; row < GRID_SIZE; row++) {
            int yOffset = GRID_SIZE - row;
            for (int col = 0; col < GRID_SIZE; col++) {
                int mainOffset = col + 1;
                BlockPos targetPos = Game2048Helper.calculateTargetPos(
                        corePos, mainAxis, extendDirection, mainOffset, yOffset);
                grid[row][col] = Block2048Display.getDisplayValue(level, targetPos);
            }
        }
        return grid;
    }

    /**
     * 将数组写入Display方块
     */
    public static void writeDisplayGrid(ServerLevel level, BlockPos corePos, Direction facing, int[][] grid) {
        Direction.Axis mainAxis = Game2048Helper.getMainExtendAxis(facing);
        int extendDirection = Game2048Helper.getExtendDirection(facing);
        for (int row = 0; row < GRID_SIZE; row++) {
            int yOffset = GRID_SIZE - row;
            for (int col = 0; col < GRID_SIZE; col++) {
                int mainOffset = col + 1;
                BlockPos targetPos = Game2048Helper.calculateTargetPos(
                        corePos, mainAxis, extendDirection, mainOffset, yOffset);
                Block2048Display.setDisplayValue(level, targetPos, grid[row][col]);
            }
        }
    }
}
