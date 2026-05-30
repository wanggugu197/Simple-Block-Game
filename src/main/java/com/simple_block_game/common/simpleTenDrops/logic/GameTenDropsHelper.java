package com.simple_block_game.common.simpleTenDrops.logic;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsCore;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsCoreEntity;
import com.simple_block_game.common.simpleTenDrops.block.BlockTenDropsDisplayEntity;
import com.simple_block_game.common.simpleTenDrops.data.DropletLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static com.simple_block_game.common.simpleTenDrops.logic.GameTenDropsLogic.GRID_SIZE;

public final class GameTenDropsHelper {

    private static final Block FRAME = SimpleBlockGameRegistration.BLOCK_VERTICAL_FRAME.get();
    private static final Block DISPLAY = SimpleBlockGameRegistration.BLOCK_TEN_DROPS_DISPLAY.get();
    private static final Block REFRESH = SimpleBlockGameRegistration.BLOCK_TEN_DROPS_REFRESH.get();
    private static final Block CORE = SimpleBlockGameRegistration.BLOCK_TEN_DROPS_CORE.get();

    private static final Direction[] HORIZONTAL_DIRS = { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };

    private GameTenDropsHelper() {}

    public static boolean processFlyingDroplets(ServerLevel serverLevel, BlockPos corePos, int[][] grid,
                                                BlockTenDropsCoreEntity coreEntity) {
        boolean hasActive = false;

        for (int x = 1; x <= GRID_SIZE; x++) {
            for (int z = 1; z <= GRID_SIZE; z++) {
                BlockPos displayPos = corePos.offset(x, 0, z);
                BlockEntity be = serverLevel.getBlockEntity(displayPos);

                if (!(be instanceof BlockTenDropsDisplayEntity displayEntity) || !displayEntity.hasActiveDroplets()) continue;

                hasActive = true;
                processDropletMovement(serverLevel, displayEntity, displayPos, grid, corePos, coreEntity);
            }
        }
        return hasActive;
    }

    private static void processDropletMovement(ServerLevel serverLevel, BlockTenDropsDisplayEntity displayEntity,
                                               BlockPos displayPos, int[][] grid, BlockPos corePos,
                                               BlockTenDropsCoreEntity coreEntity) {
        for (Direction dir : HORIZONTAL_DIRS) {
            int distance = displayEntity.getDropDistance(dir);
            if (distance == -1) continue;

            int newDistance = distance + 1;
            displayEntity.setDropDistance(dir, newDistance);

            int cellSteps = (newDistance + 2) / BlockTenDropsDisplayEntity.CELL_DISTANCE;
            if (cellSteps > 0) {
                handleDropletReachTarget(serverLevel, displayEntity, displayPos, dir, cellSteps,
                        grid, corePos, coreEntity);
            }
        }
    }

    private static void handleDropletReachTarget(ServerLevel serverLevel, BlockTenDropsDisplayEntity displayEntity,
                                                 BlockPos displayPos, Direction dir, int cellSteps,
                                                 int[][] grid, BlockPos corePos, BlockTenDropsCoreEntity coreEntity) {
        BlockPos targetPos = displayPos.relative(dir, cellSteps);
        BlockEntity targetBe = serverLevel.getBlockEntity(targetPos);

        if (!(targetBe instanceof BlockTenDropsDisplayEntity targetDisplay)) {
            displayEntity.setDropDistance(dir, -1);
            return;
        }

        int targetLevel = targetDisplay.getLevelValue();

        if (targetLevel == 0) {
            return;
        }

        int newLevel = targetLevel + 1;
        targetDisplay.setLevelValue(newLevel);
        updateGridValue(grid, targetPos, corePos, newLevel);
        displayEntity.setDropDistance(dir, -1);

        if (targetDisplay.getLevelValue() >= DropletLevel.BURST.getLevel()) {
            triggerBurst(serverLevel, targetPos, grid, corePos, coreEntity);
        }
    }

    public static void triggerBurst(ServerLevel serverLevel, BlockPos displayPos, int[][] grid,
                                    BlockPos corePos, BlockTenDropsCoreEntity coreEntity) {
        BlockEntity be = serverLevel.getBlockEntity(displayPos);
        if (!(be instanceof BlockTenDropsDisplayEntity displayEntity)) return;

        displayEntity.startBurst();
        updateGridValue(grid, displayPos, corePos, 0);

        if (coreEntity != null) {
            coreEntity.addWaterDrops(1);
        }
    }

    private static void updateGridValue(int[][] grid, BlockPos pos, BlockPos corePos, int value) {
        int gridX = pos.getX() - corePos.getX() - 1;
        int gridY = pos.getZ() - corePos.getZ() - 1;
        if (gridX >= 0 && gridX < GRID_SIZE && gridY >= 0 && gridY < GRID_SIZE) {
            grid[gridY][gridX] = value;
        }
    }

    public static boolean handleDisplayClick(ServerLevel serverLevel, BlockTenDropsDisplayEntity displayEntity, Player player) {
        if (displayEntity == null) return false;

        BlockPos corePos = displayEntity.getCorePos();
        if (corePos == null) return false;

        BlockEntity coreEntity = serverLevel.getBlockEntity(corePos);
        if (!(coreEntity instanceof BlockTenDropsCoreEntity core)) return false;

        if (!core.getGameState().isInteractive()) {
            player.sendOverlayMessage(Component.translatable("msg.ten_drops.invalid_click"));
            return false;
        }

        if (displayEntity.getDropletLevel() == DropletLevel.BURST) {
            player.sendOverlayMessage(Component.translatable("msg.ten_drops.invalid_click"));
            return false;
        }

        core.setCurrentPlayer(player);
        core.setWaterDrops(core.getWaterDrops() - 1);

        DropletLevel newLevel = displayEntity.getDropletLevel().nextLevel();
        displayEntity.setDropletLevel(newLevel);

        if (newLevel == DropletLevel.BURST) {
            core.handleBurstFromDisplay(displayEntity.getBlockPos());
        } else if (core.getWaterDrops() <= 0) {
            core.triggerGameOver();
        }

        return true;
    }

    public static boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos corePos) {
        if (corePos == null || !level.isLoaded(corePos)) return false;

        for (int x = 0; x <= GRID_SIZE + 1; x++) {
            for (int z = 0; z <= GRID_SIZE + 1; z++) {
                BlockPos pos = corePos.offset(x, 0, z);
                if (!pos.equals(corePos) && !level.isEmptyBlock(pos)) return false;
            }
        }
        return level.isEmptyBlock(corePos.offset(GRID_SIZE + 1, 0, GRID_SIZE + 1));
    }

    public static void generateLayout(ServerLevel level, BlockPos corePos) {
        BlockState displayState = DISPLAY.defaultBlockState();
        BlockState frameState = FRAME.defaultBlockState();

        for (int x = 0; x <= GRID_SIZE + 1; x++) {
            for (int z = 0; z <= GRID_SIZE + 1; z++) {
                BlockPos pos = corePos.offset(x, 0, z);
                if (pos.equals(corePos)) continue;

                boolean isDisplay = x > 0 && x <= GRID_SIZE && z > 0 && z <= GRID_SIZE;
                level.setBlock(pos, isDisplay ? displayState : frameState, Block.UPDATE_ALL);

                if (isDisplay) initDisplayEntity(level, pos, corePos);
            }
        }

        BlockPos refreshPos = corePos.offset(GRID_SIZE + 1, 0, GRID_SIZE + 1);
        level.setBlock(refreshPos, REFRESH.defaultBlockState(), Block.UPDATE_ALL);
        initRefreshEntity(level, refreshPos, corePos);
    }

    public static void minimizeLayout(ServerLevel level, BlockPos corePos) {
        if (corePos == null || !level.isLoaded(corePos)) return;

        for (int x = 0; x <= GRID_SIZE + 1; x++) {
            for (int z = 0; z <= GRID_SIZE + 1; z++) {
                Block block = level.getBlockState(corePos.offset(x, 0, z)).getBlock();
                if (block == FRAME || block == DISPLAY) level.removeBlock(corePos.offset(x, 0, z), false);
            }
        }

        if (level.getBlockState(corePos.offset(GRID_SIZE + 1, 0, GRID_SIZE + 1)).getBlock() == REFRESH) {
            level.removeBlock(corePos.offset(GRID_SIZE + 1, 0, GRID_SIZE + 1), false);
        }
    }

    public static void destroyLayout(ServerLevel level, BlockPos corePos) {
        minimizeLayout(level, corePos);
        if (corePos == null || !level.isLoaded(corePos)) return;

        if (level.getBlockState(corePos).getBlock() instanceof BlockTenDropsCore) {
            Vec3 center = Vec3.atCenterOf(corePos);
            ItemEntity item = new ItemEntity(level, center.x, center.y, center.z, new ItemStack(CORE));
            item.setDefaultPickUpDelay();
            level.addFreshEntity(item);
            level.removeBlock(corePos, false);
        }
    }

    public static void updateDisplay(ServerLevel level, BlockPos corePos, int[][] grid) {
        for (int z = 0; z < GRID_SIZE; z++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                BlockPos displayPos = corePos.offset(x + 1, 0, z + 1);
                if (level.getBlockEntity(displayPos) instanceof BlockTenDropsDisplayEntity entity) {
                    entity.setLevelValue(grid[z][x]);
                }
            }
        }
    }

    private static void initDisplayEntity(ServerLevel level, BlockPos pos, BlockPos corePos) {
        if (!level.isLoaded(pos)) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockTenDropsDisplayEntity entity) {
            entity.setCorePos(corePos);
            entity.setDropletLevel(DropletLevel.EMPTY);
        }
    }

    private static void initRefreshEntity(ServerLevel level, BlockPos pos, BlockPos corePos) {
        if (!level.isLoaded(pos)) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockRefreshEntity entity) {
            entity.setCorePos(corePos);
        }
    }
}
