package com.simple_block_game.common.simpleMemoryKey.logic;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyButtonEntity;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyCore;
import com.simple_block_game.common.simpleMemoryKey.block.BlockMemoryKeyCoreEntity;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyGameState;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyPosition;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** 记忆键游戏布局辅助工具 */
public final class GameMemoryKeyHelper {

    private static final Block FRAME = SimpleBlockGameRegistration.BLOCK_VERTICAL_FRAME.get();
    private static final Block BUTTON = SimpleBlockGameRegistration.BLOCK_MEMORY_KEY_BUTTON.get();
    private static final Block REFRESH = SimpleBlockGameRegistration.BLOCK_MEMORY_KEY_REFRESH.get();
    private static final Block CORE = SimpleBlockGameRegistration.BLOCK_MEMORY_KEY_CORE.get();

    public static final int BUTTON_INTERVAL_TICKS = 20;
    public static final int SEQUENCE_INTERVAL_TICKS = BUTTON_INTERVAL_TICKS + BlockMemoryKeyButtonEntity.FLASH_DURATION_TICKS;

    private GameMemoryKeyHelper() {}

    public static boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos corePos) {
        // 检查按键位置
        for (MemoryKeyPosition pos : MemoryKeyPosition.values()) {
            if (!level.isEmptyBlock(pos.getRelativePos(corePos))) {
                return false;
            }
        }

        // 检查边框位置
        for (BlockPos framePos : getFramePositions(corePos)) {
            if (!level.isEmptyBlock(framePos)) {
                return false;
            }
        }

        // 检查刷新按钮位置
        return level.isEmptyBlock(getRefreshPos(corePos));
    }

    public static void generateMemoryKeyLayout(ServerLevel level, BlockPos corePos) {
        BlockState frameState = SimpleBlockGameRegistration.BLOCK_VERTICAL_FRAME.get().defaultBlockState();
        BlockState refreshState = SimpleBlockGameRegistration.BLOCK_MEMORY_KEY_REFRESH.get().defaultBlockState();

        // 生成8个按键
        for (MemoryKeyPosition pos : MemoryKeyPosition.values()) {
            placeButtonBlock(level, pos.getRelativePos(corePos), corePos, pos);
        }

        // 生成边框位置
        for (BlockPos framePos : getFramePositions(corePos)) {
            if (level.isEmptyBlock(framePos)) {
                level.setBlock(framePos, frameState, 3);
            }
        }

        // 生成刷新按钮
        BlockPos refreshPos = getRefreshPos(corePos);
        if (level.isEmptyBlock(refreshPos)) {
            level.setBlock(refreshPos, refreshState, 3);
            if (level.getBlockEntity(refreshPos) instanceof BlockRefreshEntity refreshEntity) {
                refreshEntity.setCorePos(corePos);
            }
        }
    }

    private static void placeButtonBlock(ServerLevel level, BlockPos buttonPos, BlockPos corePos, MemoryKeyPosition pos) {
        if (!level.isEmptyBlock(buttonPos)) return;

        level.setBlock(buttonPos, BUTTON.defaultBlockState(), 3);

        if (level.getBlockEntity(buttonPos) instanceof BlockMemoryKeyButtonEntity buttonEntity) {
            buttonEntity.setCorePos(corePos);
            buttonEntity.setPosition(pos);
            buttonEntity.setFlashing(false);
            buttonEntity.setChanged();
        }
    }

    /**
     * 最小化记忆键游戏布局
     *
     * @param level   服务端世界
     * @param corePos 核心方块位置
     */
    public static void minimizeMemoryKeyLayout(ServerLevel level, BlockPos corePos) {
        if (corePos == null || !level.isLoaded(corePos)) {
            return;
        }

        // 移除按键方块（使用MemoryKeyPosition提供的位置列表）
        removeBlocksByType(level, MemoryKeyPosition.getButtonPositions(corePos), BUTTON);

        // 移除边框方块
        removeBlocksByType(level, getFramePositions(corePos), FRAME);

        // 移除刷新按钮
        removeBlockIfType(level, getRefreshPos(corePos), REFRESH);

        // 设置核心方块为未展开状态
        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof BlockMemoryKeyCore) {
            level.setBlock(corePos, coreState.setValue(IGameCoreBlock.UNFOLDED, false), 3);
        }
    }

    /**
     * 批量移除指定类型的方块
     *
     * @param level       服务端世界
     * @param positions   位置集合（直接是BlockPos类型）
     * @param targetBlock 目标方块类型
     */
    private static void removeBlocksByType(ServerLevel level, Iterable<BlockPos> positions, Block targetBlock) {
        for (BlockPos pos : positions) {
            removeBlockIfType(level, pos, targetBlock);
        }
    }

    /**
     * 如果方块类型匹配则移除
     *
     * @param level       服务端世界
     * @param pos         方块位置
     * @param targetBlock 目标方块类型
     */
    private static void removeBlockIfType(ServerLevel level, BlockPos pos, Block targetBlock) {
        if (level.getBlockState(pos).getBlock() == targetBlock) {
            level.removeBlock(pos, false);
        }
    }

    public static void destroyMemoryKeyLayout(ServerLevel level, BlockPos corePos) {
        minimizeMemoryKeyLayout(level, corePos);

        if (corePos == null || !level.isLoaded(corePos)) return;

        if (level.getBlockState(corePos).getBlock() instanceof BlockMemoryKeyCore) {
            Vec3 center = Vec3.atCenterOf(corePos);
            ItemEntity item = new ItemEntity(level, center.x, center.y, center.z, new ItemStack(CORE));
            item.setDefaultPickUpDelay();
            level.addFreshEntity(item);
            level.removeBlock(corePos, false);
        }
    }

    public static void resetButtonStates(ServerLevel level, BlockPos corePos) {
        for (MemoryKeyPosition pos : MemoryKeyPosition.values()) {
            BlockPos buttonPos = pos.getRelativePos(corePos);
            BlockEntity be = level.getBlockEntity(buttonPos);

            if (be instanceof BlockMemoryKeyButtonEntity buttonEntity) {
                buttonEntity.setFlashing(false);
            }
        }
    }

    public static void triggerButtonFlash(ServerLevel level, BlockPos corePos, int buttonId) {
        MemoryKeyPosition pos = MemoryKeyPosition.fromId(buttonId);
        BlockPos buttonPos = pos.getRelativePos(corePos);

        BlockEntity be = level.getBlockEntity(buttonPos);
        if (be instanceof BlockMemoryKeyButtonEntity buttonEntity) {
            buttonEntity.startFlashing();
        }
    }

    public static void startGame(ServerLevel level, BlockPos corePos, BlockMemoryKeyCoreEntity coreEntity) {
        if (coreEntity.getSequence().isEmpty()) {
            coreEntity.setSequence(GameMemoryKeyLogic.generateFullSequence());
        }

        resetButtonStates(level, corePos);
        coreEntity.setGameState(MemoryKeyGameState.DEMONSTRATING);
        coreEntity.setCurrentSequenceIndex(0);
        coreEntity.setFlashingIndex(-1);

        // 开始演示，由实体tick处理
        coreEntity.startDemonstration();

        level.sendBlockUpdated(corePos, level.getBlockState(corePos), level.getBlockState(corePos), 3);
    }

    public static void handlePlayerInput(ServerLevel level, BlockPos corePos, BlockMemoryKeyCoreEntity coreEntity, int buttonId, net.minecraft.world.entity.player.Player player) {
        if (coreEntity.getGameState() != MemoryKeyGameState.PLAYER_INPUT) {
            return;
        }

        // 触发按钮闪烁（玩家点击时按钮亮起）
        triggerButtonFlash(level, corePos, buttonId);

        int currentIndex = coreEntity.getCurrentSequenceIndex();
        int expectedButtonId = coreEntity.getSequence().get(currentIndex);

        if (buttonId != expectedButtonId) {
            handleWrongInput(level, corePos, coreEntity, player);
            return;
        }

        handleCorrectInput(level, corePos, coreEntity, player);
    }

    private static void handleCorrectInput(ServerLevel level, BlockPos corePos, BlockMemoryKeyCoreEntity coreEntity, net.minecraft.world.entity.player.Player player) {
        int currentIndex = coreEntity.getCurrentSequenceIndex();
        int sequenceLength = GameMemoryKeyLogic.getSequenceLengthForLevel(coreEntity.getCurrentLevel());

        int newIndex = currentIndex + 1;
        coreEntity.setCurrentSequenceIndex(newIndex);

        if (newIndex >= sequenceLength) {
            if (GameMemoryKeyLogic.isAllLevelsComplete(coreEntity.getCurrentLevel())) {
                // 全部完成，设置为 ALL_SUCCESS
                coreEntity.setGameState(MemoryKeyGameState.ALL_SUCCESS);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("msg.memory_key.all_levels_complete"));
                // 触发通关奖励
                GameMemoryKeyReward.handleReward(level, player, coreEntity.getCurrentLevel(), true);
            } else {
                // 关卡完成，设置为 LEVEL_SUCCESS，由实体tick处理后续逻辑
                coreEntity.setGameState(MemoryKeyGameState.LEVEL_SUCCESS);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("msg.memory_key.level_complete", coreEntity.getCurrentLevel().getLevelNumber()));
            }
        }

        coreEntity.setChanged();
        level.sendBlockUpdated(corePos, level.getBlockState(corePos), level.getBlockState(corePos), 3);
    }

    private static void handleWrongInput(ServerLevel level, BlockPos corePos, BlockMemoryKeyCoreEntity coreEntity, net.minecraft.world.entity.player.Player player) {
        coreEntity.loseLife();

        if (coreEntity.getRemainingLives() <= 0) {
            coreEntity.setGameState(MemoryKeyGameState.GAME_OVER);
            player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("msg.memory_key.game_over", coreEntity.getCurrentLevel().getLevelNumber()));
            // 触发游戏失败奖励（根据到达的关卡）
            GameMemoryKeyReward.handleReward(level, player, coreEntity.getCurrentLevel(), false);
        } else {
            // 设置错误状态，由实体tick处理2秒延迟后重新开始演示
            coreEntity.setGameState(MemoryKeyGameState.ERROR);
            player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("msg.memory_key.wrong_input", coreEntity.getRemainingLives()));
        }

        coreEntity.setChanged();
        level.sendBlockUpdated(corePos, level.getBlockState(corePos), level.getBlockState(corePos), 3);
    }

    /**
     * 获取边框方块位置列表
     *
     * @param corePos 核心方块位置
     * @return 边框位置列表
     */
    private static Iterable<BlockPos> getFramePositions(BlockPos corePos) {
        BlockPos[] framePositions = {
                // 四边中间
                corePos.offset(-2, 0, 0),  // 西外框
                corePos.offset(2, 0, 0),   // 东外框
                corePos.offset(0, 0, -2),  // 北外框
                corePos.offset(0, 0, 2),   // 南外框

                // 西边延伸
                corePos.offset(-2, 0, -1), // 西北边
                corePos.offset(-2, 0, 1),  // 西南边

                // 东边延伸
                corePos.offset(2, 0, -1),  // 东北边
                corePos.offset(2, 0, 1),   // 东南边

                // 北边延伸
                corePos.offset(-1, 0, -2), // 北边西
                corePos.offset(1, 0, -2),  // 北边东

                // 南边延伸
                corePos.offset(-1, 0, 2),  // 南边西
                corePos.offset(1, 0, 2),   // 南边东

                // 新增：角落位置
                corePos.offset(-2, 0, -2), // 西北角
                corePos.offset(-2, 0, 2),  // 西南角
                corePos.offset(2, 0, -2)   // 东北角
                // 东南角被刷新按钮占用
        };
        return () -> java.util.Arrays.stream(framePositions).iterator();
    }

    /**
     * 获取刷新按钮位置
     *
     * @param corePos 核心方块位置
     * @return 刷新按钮位置
     */
    private static BlockPos getRefreshPos(BlockPos corePos) {
        return corePos.offset(2, 0, 2);
    }
}
