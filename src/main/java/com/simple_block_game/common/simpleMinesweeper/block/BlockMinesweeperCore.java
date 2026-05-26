package com.simple_block_game.common.simpleMinesweeper.block;

import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleMinesweeper.data.PresetDifficulty;
import com.simple_block_game.common.simpleMinesweeper.logic.GameMinesweeperHelper;

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
import org.jspecify.annotations.NonNull;

/** 扫雷游戏核心方块 */
public class BlockMinesweeperCore extends BaseVerticalBlock implements IGameCoreBlock {

    public static final BooleanProperty GAME_STARTED = BooleanProperty.create("game_started");
    public static final EnumProperty<PresetDifficulty> DIFFICULTY = EnumProperty.create("difficulty", PresetDifficulty.class);

    private static final MapCodec<BlockMinesweeperCore> CODEC = simpleCodec(BlockMinesweeperCore::new);

    public BlockMinesweeperCore(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(GAME_STARTED, false).setValue(DIFFICULTY, PresetDifficulty.EASY));
    }

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(GAME_STARTED, DIFFICULTY);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockMinesweeperCoreEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        ServerLevel serverLevel = (ServerLevel) level;

        if (hit.getDirection() == Direction.UP) {
            return handleTopClick(serverLevel, state, pos, player, hit);
        } else if (hit.getDirection() != Direction.DOWN) {
            return handleSideClick(serverLevel, state, pos, player, hit);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleTopClick(ServerLevel level, BlockState state, BlockPos pos, Player player, BlockHitResult hit) {
        ClickArea area = getClickArea(hit, pos);
        if (area == ClickArea.NULL) return InteractionResult.PASS;

        boolean started = state.getValue(GAME_STARTED);
        if (area == ClickArea.CENTER_8x8) {
            if (!started) unfoldGame(level, pos, state, player);
            else startGame(level, pos, state, player);
            return InteractionResult.SUCCESS;
        }

        if (started) return InteractionResult.PASS;

        if (area == ClickArea.NORTHWEST_CORNER || area == ClickArea.SOUTHWEST_CORNER) {
            switchDifficulty(level, pos, state, player, area == ClickArea.NORTHWEST_CORNER);
            return InteractionResult.SUCCESS;
        }

        if (state.getValue(DIFFICULTY) != PresetDifficulty.CUSTOM) return InteractionResult.PASS;

        if (area == ClickArea.NORTHEAST_CORNER || area == ClickArea.SOUTHEAST_CORNER) {
            adjustMineCount(level, pos, player, area == ClickArea.NORTHEAST_CORNER);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private InteractionResult handleSideClick(ServerLevel level, BlockState state, BlockPos pos, Player player, BlockHitResult hit) {
        if (state.getValue(GAME_STARTED) || state.getValue(DIFFICULTY) != PresetDifficulty.CUSTOM) {
            return InteractionResult.PASS;
        }
        boolean isX = hit.getDirection() == Direction.NORTH || hit.getDirection() == Direction.SOUTH;
        adjustSize(level, pos, player, isX, hit.getDirection() == Direction.SOUTH || hit.getDirection() == Direction.EAST);
        return InteractionResult.SUCCESS;
    }

    private ClickArea getClickArea(BlockHitResult hit, BlockPos pos) {
        Vec3 worldPos = hit.getLocation();
        double u = Mth.clamp((worldPos.x - pos.getX()) * 16, 0, 16);
        double v = Mth.clamp((worldPos.z - pos.getZ()) * 16, 0, 16);

        if (u >= 4 && u <= 12 && v >= 4 && v <= 12) return ClickArea.CENTER_8x8;
        if (u <= 4 && v <= 4) return ClickArea.SOUTHWEST_CORNER;
        if (u >= 12 && v <= 4) return ClickArea.SOUTHEAST_CORNER;
        if (u <= 4 && v >= 12) return ClickArea.NORTHWEST_CORNER;
        if (u >= 12 && v >= 12) return ClickArea.NORTHEAST_CORNER;
        return ClickArea.NULL;
    }

    private BlockMinesweeperCoreEntity getCore(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof BlockMinesweeperCoreEntity core ? core : null;
    }

    private void sendError(Player player) {
        player.sendOverlayMessage(Component.translatable("msg.common.entity_error"));
    }

    private void switchDifficulty(ServerLevel level, BlockPos pos, BlockState state, Player player, boolean forward) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core == null) {
            sendError(player);
            return;
        }
        PresetDifficulty preset = forward ? core.getPresetDifficulty().next() : core.getPresetDifficulty().prev();
        core.setPresetDifficulty(preset);
        core.setChanged();
        level.setBlock(pos, state.setValue(GAME_STARTED, false).setValue(DIFFICULTY, preset), 3);
        player.sendOverlayMessage(Component.translatable("msg.minesweeper.difficulty_switched",
                Component.translatable(preset.getDisplayName()), preset.getWidth(), preset.getHeight(), preset.getMineCount()));
    }

    private void adjustMineCount(ServerLevel level, BlockPos pos, Player player, boolean add) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core == null) {
            sendError(player);
            return;
        }
        core.adjustMineCount(add);
        player.sendOverlayMessage(Component.translatable("msg.minesweeper.custom_mine",
                core.getGridWidth(), core.getGridHeight(), core.getTotalMineCount()));
    }

    private void adjustSize(ServerLevel level, BlockPos pos, Player player, boolean isX, boolean inc) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core == null) {
            sendError(player);
            return;
        }
        core.adjustSize(isX, inc);
        player.sendOverlayMessage(Component.translatable("msg.minesweeper.custom_size",
                core.getGridWidth(), core.getGridHeight(), core.getTotalMineCount()));
    }

    private enum ClickArea {
        CENTER_8x8,
        SOUTHWEST_CORNER,
        SOUTHEAST_CORNER,
        NORTHWEST_CORNER,
        NORTHEAST_CORNER,
        NULL
    }

    @Override
    public boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos pos, BlockState state) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        return core != null && GameMinesweeperHelper.isAreaEmpty(level, pos, core.getGridWidth(), core.getGridHeight());
    }

    @Override
    public boolean unfoldGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core == null) {
            sendError(player);
            return false;
        }

        int w = core.getGridWidth(), h = core.getGridHeight();
        if (!GameMinesweeperHelper.isAreaEmpty(level, pos, w, h)) {
            player.sendOverlayMessage(Component.translatable("msg.common.obstructed"));
            return false;
        }

        GameMinesweeperHelper.placeLayoutBlocks(level, pos, w, h);
        level.setBlock(pos, state.setValue(GAME_STARTED, true), 3);
        player.sendOverlayMessage(Component.translatable("msg.minesweeper.layout_placed",
                Component.translatable(core.getPresetDifficulty().getDisplayName()), w, h));
        return true;
    }

    @Override
    public void startGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core == null) {
            sendError(player);
            return;
        }

        int w = core.getGridWidth(), h = core.getGridHeight();
        GameMinesweeperHelper.initLayoutEntities(level, pos, w, h);
        core.initGameData();
        core.setChanged();
        GameMinesweeperHelper.resetLayout(level, pos, w, h);
        player.sendOverlayMessage(Component.translatable("msg.minesweeper.game_started",
                Component.translatable(core.getPresetDifficulty().getDisplayName()), core.getTotalMineCount(), w, h));
    }

    @Override
    public void resetGame(ServerLevel level, BlockPos pos, BlockState state) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core != null) GameMinesweeperHelper.resetLayout(level, pos, core.getGridWidth(), core.getGridHeight());
    }

    @Override
    public void minimizeGame(ServerLevel level, BlockPos pos, BlockState state) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core != null) {
            GameMinesweeperHelper.minimizeLayout(level, pos, core.getGridWidth(), core.getGridHeight());
            level.setBlock(pos, state.setValue(GAME_STARTED, false), 3);
        }
    }

    @Override
    public void closeGame(ServerLevel level, BlockPos pos, BlockState state) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core != null) GameMinesweeperHelper.closeLayout(level, pos, core.getGridWidth(), core.getGridHeight());
    }

    @Override
    public boolean isGameUnfolded(BlockState state) {
        return state.getValue(GAME_STARTED);
    }

    @Override
    public BlockEntity getGameCoreEntity(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }
}
