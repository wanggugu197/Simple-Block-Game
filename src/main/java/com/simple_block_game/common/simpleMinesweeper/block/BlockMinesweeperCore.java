package com.simple_block_game.common.simpleMinesweeper.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.simpleMinesweeper.data.PresetDifficulty;
import com.simple_block_game.common.simpleMinesweeper.logic.GameMinesweeperHelper;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockMinesweeperCore extends BaseVerticalBlock {

    public static final BooleanProperty GAME_STARTED = BooleanProperty.create("game_started");
    public static final EnumProperty<@NonNull PresetDifficulty> DIFFICULTY = EnumProperty.create("difficulty", PresetDifficulty.class);

    public BlockMinesweeperCore(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(GAME_STARTED, false)
                .setValue(DIFFICULTY, PresetDifficulty.EASY));
    }

    private static final MapCodec<BlockMinesweeperCore> CODEC = simpleCodec(BlockMinesweeperCore::new);

    @Override
    protected @NotNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(@NonNull StateDefinition.Builder<Block, @NonNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(GAME_STARTED);
        builder.add(DIFFICULTY);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockMinesweeperCoreEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }
        if (hit.getDirection() == Direction.UP) {
            ClickArea clickArea = getClickedArea(hit, pos);
            switch (clickArea) {
                case CENTER_8x8:
                    if (!state.getValue(GAME_STARTED)) handlePlaceLayoutBlocks(serverLevel, pos, state, player);
                    else handleInitGameData(serverLevel, pos, player);
                    break;
                case NORTHWEST_CORNER, SOUTHWEST_CORNER:
                    if (state.getValue(GAME_STARTED)) break;
                    boolean forward = clickArea == ClickArea.NORTHWEST_CORNER;
                    handleSwitchPresetDifficulty(serverLevel, pos, state, player, forward);
                    break;
                case NORTHEAST_CORNER, SOUTHEAST_CORNER:
                    if (state.getValue(GAME_STARTED)) break;
                    if (state.getValue(DIFFICULTY) != PresetDifficulty.CUSTOM) break;
                    handleAdjustMineCount(serverLevel, pos, player, clickArea == ClickArea.NORTHEAST_CORNER);
                    break;
                case NULL:
                    return InteractionResult.PASS;
            }
        } else if (hit.getDirection() != Direction.DOWN) {
            if (state.getValue(GAME_STARTED)) return InteractionResult.PASS;
            if (state.getValue(DIFFICULTY) != PresetDifficulty.CUSTOM) return InteractionResult.PASS;
            boolean isXAxis = hit.getDirection() == Direction.NORTH || hit.getDirection() == Direction.SOUTH;
            boolean increase = hit.getDirection() == Direction.SOUTH || hit.getDirection() == Direction.EAST;
            handleAdjustSize(serverLevel, pos, player, isXAxis, increase);
        }
        return InteractionResult.SUCCESS;
    }

    private static ClickArea getClickedArea(BlockHitResult hit, BlockPos pos) {
        Vec3 worldHitPos = hit.getLocation();
        double localX = worldHitPos.x - pos.getX();
        double localZ = worldHitPos.z - pos.getZ();
        double u = Mth.clamp(localX * 16, 0, 16);
        double v = Mth.clamp(localZ * 16, 0, 16);
        if (u >= 4 && u <= 12 && v >= 4 && v <= 12) {
            return ClickArea.CENTER_8x8;
        }
        if (u <= 4 && v <= 4) {
            return ClickArea.SOUTHWEST_CORNER;
        }
        if (u >= 12 && v <= 4) {
            return ClickArea.SOUTHEAST_CORNER;
        }
        if (u <= 4 && v >= 12) {
            return ClickArea.NORTHWEST_CORNER;
        }
        if (u >= 12 && v >= 12) {
            return ClickArea.NORTHEAST_CORNER;
        }
        return ClickArea.NULL;
    }

    /**
     * 第一步：仅放置扫雷布局方块（GAME_STARTED=false时触发）
     */
    public static void handlePlaceLayoutBlocks(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (!(be instanceof BlockMinesweeperCoreEntity coreEntity)) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.entity_error"));
            return;
        }
        PresetDifficulty currentPreset = coreEntity.getPresetDifficulty();
        int width = coreEntity.getGridWidth();
        int height = coreEntity.getGridHeight();
        if (!GameMinesweeperHelper.checkLayoutAreaIsEmpty(serverLevel, pos, width, height)) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.layout_obstructed"));
            return;
        }
        GameMinesweeperHelper.placeMinesweeperBlocks(serverLevel, pos, width, height);
        serverLevel.setBlock(pos, state.setValue(GAME_STARTED, true), 3);
        player.sendOverlayMessage(Component.translatable("msg.minesweeper.layout_placed",
                Component.translatable(currentPreset.getDisplayName()), width, height));
    }

    /**
     * 第二步：初始化游戏数据
     */
    public static void handleInitGameData(ServerLevel serverLevel, BlockPos pos, Player player) {
        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (!(be instanceof BlockMinesweeperCoreEntity coreEntity)) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.entity_error"));
            return;
        }
        PresetDifficulty currentPreset = coreEntity.getPresetDifficulty();
        int width = coreEntity.getGridWidth();
        int height = coreEntity.getGridHeight();
        int mineCount = coreEntity.getTotalMineCount();
        GameMinesweeperHelper.initMinesweeperEntities(serverLevel, pos, width, height);
        coreEntity.initGameData();
        coreEntity.setChanged();
        GameMinesweeperHelper.resetMinesweeperLayout(serverLevel, pos, width, height);
        player.sendOverlayMessage(Component.translatable("msg.minesweeper.game_started",
                Component.translatable(currentPreset.getDisplayName()), mineCount, width, height));
    }

    public static void handleSwitchPresetDifficulty(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player, boolean forward) {
        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (!(be instanceof BlockMinesweeperCoreEntity coreEntity)) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.entity_error"));
            return;
        }
        PresetDifficulty currentPreset = coreEntity.getPresetDifficulty();
        PresetDifficulty newPreset = forward ? currentPreset.next() : currentPreset.prev();
        coreEntity.setPresetDifficulty(newPreset);
        coreEntity.setChanged();
        serverLevel.setBlock(pos, state.setValue(GAME_STARTED, false).setValue(DIFFICULTY, newPreset), 3);
        player.sendOverlayMessage(Component.translatable("msg.minesweeper.difficulty_switched",
                Component.translatable(newPreset.getDisplayName()), newPreset.getWidth(), newPreset.getHeight(), newPreset.getMineCount()));
    }

    public static void handleAdjustMineCount(ServerLevel serverLevel, BlockPos pos, Player player, boolean add) {
        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (!(be instanceof BlockMinesweeperCoreEntity coreEntity)) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.entity_error"));
            return;
        }
        coreEntity.adjustMineCount(add);
        player.sendOverlayMessage(Component.translatable("msg.minesweeper.custom_mine",
                coreEntity.getGridWidth(), coreEntity.getGridHeight(), coreEntity.getTotalMineCount()));
    }

    public static void handleAdjustSize(ServerLevel serverLevel, BlockPos pos, Player player, boolean isXAxis, boolean increase) {
        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (!(be instanceof BlockMinesweeperCoreEntity coreEntity)) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.entity_error"));
            return;
        }
        coreEntity.adjustSize(isXAxis, increase);
        player.sendOverlayMessage(Component.translatable("msg.minesweeper.custom_size",
                coreEntity.getGridWidth(), coreEntity.getGridHeight(), coreEntity.getTotalMineCount()));
    }

    private enum ClickArea {
        CENTER_8x8,
        SOUTHWEST_CORNER,
        SOUTHEAST_CORNER,
        NORTHWEST_CORNER,
        NORTHEAST_CORNER,
        NULL
    }
}
