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

public final class GameMemoryKeyHelper {

    private static final Block FRAME = SimpleBlockGameRegistration.BLOCK_VERTICAL_FRAME.get();
    private static final Block BUTTON = SimpleBlockGameRegistration.BLOCK_MEMORY_KEY_BUTTON.get();
    private static final Block REFRESH = SimpleBlockGameRegistration.BLOCK_MEMORY_KEY_REFRESH.get();
    private static final Block CORE = SimpleBlockGameRegistration.BLOCK_MEMORY_KEY_CORE.get();

    public static final int BUTTON_INTERVAL_TICKS = 20;
    public static final int SEQUENCE_INTERVAL_TICKS = BUTTON_INTERVAL_TICKS + BlockMemoryKeyButtonEntity.FLASH_DURATION_TICKS;

    private GameMemoryKeyHelper() {}

    public static boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos corePos) {
        if (corePos == null || !level.isLoaded(corePos)) return false;

        for (MemoryKeyPosition pos : MemoryKeyPosition.values()) {
            if (!level.isEmptyBlock(pos.getRelativePos(corePos))) {
                return false;
            }
        }

        for (BlockPos framePos : getFramePositions(corePos)) {
            if (!level.isEmptyBlock(framePos)) {
                return false;
            }
        }

        return level.isEmptyBlock(getRefreshPos(corePos));
    }

    public static void generateLayout(ServerLevel level, BlockPos corePos) {
        BlockState frameState = SimpleBlockGameRegistration.BLOCK_VERTICAL_FRAME.get().defaultBlockState();
        BlockState refreshState = SimpleBlockGameRegistration.BLOCK_MEMORY_KEY_REFRESH.get().defaultBlockState();

        for (MemoryKeyPosition pos : MemoryKeyPosition.values()) {
            placeButtonBlock(level, pos.getRelativePos(corePos), corePos, pos);
        }

        for (BlockPos framePos : getFramePositions(corePos)) {
            if (level.isEmptyBlock(framePos)) {
                level.setBlock(framePos, frameState, Block.UPDATE_ALL);
            }
        }

        BlockPos refreshPos = getRefreshPos(corePos);
        if (level.isEmptyBlock(refreshPos)) {
            level.setBlock(refreshPos, refreshState, Block.UPDATE_ALL);
            if (level.getBlockEntity(refreshPos) instanceof BlockRefreshEntity refreshEntity) {
                refreshEntity.setCorePos(corePos);
            }
        }
    }

    public static void minimizeLayout(ServerLevel level, BlockPos corePos) {
        if (corePos == null || !level.isLoaded(corePos)) return;

        removeBlocksByType(level, MemoryKeyPosition.getButtonPositions(corePos), BUTTON);
        removeBlocksByType(level, getFramePositions(corePos), FRAME);
        removeBlockIfType(level, getRefreshPos(corePos), REFRESH);

        BlockState coreState = level.getBlockState(corePos);
        if (coreState.getBlock() instanceof BlockMemoryKeyCore) {
            level.setBlock(corePos, coreState.setValue(IGameCoreBlock.UNFOLDED, false), Block.UPDATE_ALL);
        }
    }

    public static void closeLayout(ServerLevel level, BlockPos corePos) {
        minimizeLayout(level, corePos);
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
        if (corePos == null || !level.isLoaded(corePos)) return;

        MemoryKeyPosition pos = MemoryKeyPosition.fromId(buttonId);
        if (pos == null) return;

        BlockPos buttonPos = pos.getRelativePos(corePos);
        if (!level.isLoaded(buttonPos)) return;

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
        coreEntity.startDemonstration();

        level.sendBlockUpdated(corePos, level.getBlockState(corePos), level.getBlockState(corePos), 3);
    }

    public static void handlePlayerInput(ServerLevel level, BlockPos corePos, BlockMemoryKeyCoreEntity coreEntity, int buttonId, net.minecraft.world.entity.player.Player player) {
        if (coreEntity.getGameState() != MemoryKeyGameState.PLAYER_INPUT) {
            return;
        }

        triggerButtonFlash(level, corePos, buttonId);

        int currentIndex = coreEntity.getCurrentSequenceIndex();
        int expectedButtonId = coreEntity.getSequence().get(currentIndex);

        if (buttonId != expectedButtonId) {
            handleWrongInput(level, coreEntity, player);
            return;
        }

        handleCorrectInput(level, coreEntity, player);
    }

    private static void handleCorrectInput(ServerLevel level, BlockMemoryKeyCoreEntity coreEntity, net.minecraft.world.entity.player.Player player) {
        int currentIndex = coreEntity.getCurrentSequenceIndex();
        int sequenceLength = GameMemoryKeyLogic.getSequenceLengthForLevel(coreEntity.getCurrentLevel());

        int newIndex = currentIndex + 1;
        coreEntity.setCurrentSequenceIndex(newIndex);

        if (newIndex >= sequenceLength) {
            if (GameMemoryKeyLogic.isAllLevelsComplete(coreEntity.getCurrentLevel())) {
                coreEntity.setGameState(MemoryKeyGameState.ALL_SUCCESS);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("msg.memory_key.all_levels_complete"));
                GameMemoryKeyReward.handleReward(level, player, coreEntity.getCurrentLevel(), true);
            } else {
                coreEntity.setGameState(MemoryKeyGameState.LEVEL_SUCCESS);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("msg.memory_key.level_complete", coreEntity.getCurrentLevel().getLevelNumber()));
            }
        }
    }

    private static void handleWrongInput(ServerLevel level, BlockMemoryKeyCoreEntity coreEntity, net.minecraft.world.entity.player.Player player) {
        coreEntity.loseLife();

        if (coreEntity.getRemainingLives() <= 0) {
            coreEntity.setGameState(MemoryKeyGameState.GAME_OVER);
            player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("msg.memory_key.game_over", coreEntity.getCurrentLevel().getLevelNumber()));
            GameMemoryKeyReward.handleReward(level, player, coreEntity.getCurrentLevel(), false);
        } else {
            coreEntity.setGameState(MemoryKeyGameState.ERROR);
            player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("msg.memory_key.wrong_input", coreEntity.getRemainingLives()));
        }
    }

    private static void placeButtonBlock(ServerLevel level, BlockPos buttonPos, BlockPos corePos, MemoryKeyPosition pos) {
        if (!level.isEmptyBlock(buttonPos)) return;

        level.setBlock(buttonPos, BUTTON.defaultBlockState(), Block.UPDATE_ALL);

        if (level.getBlockEntity(buttonPos) instanceof BlockMemoryKeyButtonEntity buttonEntity) {
            buttonEntity.setCorePos(corePos);
            buttonEntity.setPosition(pos);
            buttonEntity.setFlashing(false);
        }
    }

    private static void removeBlocksByType(ServerLevel level, Iterable<BlockPos> positions, Block targetBlock) {
        for (BlockPos pos : positions) {
            removeBlockIfType(level, pos, targetBlock);
        }
    }

    private static void removeBlockIfType(ServerLevel level, BlockPos pos, Block targetBlock) {
        if (level.getBlockState(pos).getBlock() == targetBlock) {
            level.removeBlock(pos, false);
        }
    }

    private static Iterable<BlockPos> getFramePositions(BlockPos corePos) {
        BlockPos[] framePositions = {
                corePos.offset(-2, 0, 0),
                corePos.offset(2, 0, 0),
                corePos.offset(0, 0, -2),
                corePos.offset(0, 0, 2),
                corePos.offset(-2, 0, -1),
                corePos.offset(-2, 0, 1),
                corePos.offset(2, 0, -1),
                corePos.offset(2, 0, 1),
                corePos.offset(-1, 0, -2),
                corePos.offset(1, 0, -2),
                corePos.offset(-1, 0, 2),
                corePos.offset(1, 0, 2),
                corePos.offset(-2, 0, -2),
                corePos.offset(-2, 0, 2),
                corePos.offset(2, 0, -2)
        };
        return () -> java.util.Arrays.stream(framePositions).iterator();
    }

    private static BlockPos getRefreshPos(BlockPos corePos) {
        return corePos.offset(2, 0, 2);
    }
}
