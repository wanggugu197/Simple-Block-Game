package com.simple_block_game.common.simpleMinesweeper.block;

import com.simple_block_game.SimpleBlockGameConfig;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

/**
 * 扫雷游戏核心方块
 */
public class BlockMinesweeperCore extends BaseVerticalBlock implements IGameCoreBlock {

    public BlockMinesweeperCore(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(UNFOLDED, false));
    }

    private static final MapCodec<BlockMinesweeperCore> CODEC = simpleCodec(BlockMinesweeperCore::new);

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UNFOLDED);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockMinesweeperCoreEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (!SimpleBlockGameConfig.enableMinesweeperGame.get()) return InteractionResult.PASS;

        if (level.isClientSide()) return InteractionResult.SUCCESS;
        ServerLevel serverLevel = (ServerLevel) level;

        if (hit.getDirection() == Direction.UP) {
            return handleTopClick(serverLevel, state, pos, player, hit);
        } else if (hit.getDirection() != Direction.DOWN) {
            return handleSideClick(serverLevel, pos, player, hit);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleTopClick(ServerLevel level, BlockState state, BlockPos pos, Player player, BlockHitResult hit) {
        ClickArea area = getClickArea(hit, pos);
        if (area == ClickArea.NULL) return InteractionResult.PASS;

        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core == null) {
            sendError(player);
            return InteractionResult.PASS;
        }

        boolean started = core.isGameOver() || core.getMineGrid() != null && core.getMineGrid().length > 0;
        if (area == ClickArea.CENTER_8x8) {
            if (!started) unfoldGame(level, pos, state, player);
            else startGame(level, pos, state, player);
            return InteractionResult.SUCCESS;
        }

        if (started) return InteractionResult.PASS;

        if (area == ClickArea.NORTHWEST_CORNER || area == ClickArea.SOUTHWEST_CORNER) {
            switchDifficulty(level, pos, player, area == ClickArea.NORTHWEST_CORNER);
            return InteractionResult.SUCCESS;
        }

        if (core.getPresetDifficulty() != PresetDifficulty.CUSTOM) return InteractionResult.PASS;

        if (area == ClickArea.NORTHEAST_CORNER || area == ClickArea.SOUTHEAST_CORNER) {
            adjustMineCount(level, pos, player, area == ClickArea.NORTHEAST_CORNER);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private InteractionResult handleSideClick(ServerLevel level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core == null) {
            sendError(player);
            return InteractionResult.PASS;
        }
        boolean started = core.isGameOver() || core.getMineGrid() != null && core.getMineGrid().length > 0;
        if (started || core.getPresetDifficulty() != PresetDifficulty.CUSTOM) {
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
        player.displayClientMessage(Component.translatable("msg.common.entity_error"), true);
    }

    private void switchDifficulty(ServerLevel level, BlockPos pos, Player player, boolean forward) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core == null) {
            sendError(player);
            return;
        }
        PresetDifficulty preset = forward ? core.getPresetDifficulty().next() : core.getPresetDifficulty().prev();
        core.setPresetDifficulty(preset);
        player.displayClientMessage(Component.translatable("msg.minesweeper.difficulty_switched",
                Component.translatable(preset.getDisplayName()), preset.getWidth(), preset.getHeight(), preset.getMineCount()), true);
    }

    private void adjustMineCount(ServerLevel level, BlockPos pos, Player player, boolean add) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core == null) {
            sendError(player);
            return;
        }
        core.adjustMineCount(add);
        player.displayClientMessage(Component.translatable("msg.minesweeper.custom_mine",
                core.getGridWidth(), core.getGridHeight(), core.getTotalMineCount()), true);
    }

    private void adjustSize(ServerLevel level, BlockPos pos, Player player, boolean isX, boolean inc) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core == null) {
            sendError(player);
            return;
        }
        core.adjustSize(isX, inc);
        player.displayClientMessage(Component.translatable("msg.minesweeper.custom_size",
                core.getGridWidth(), core.getGridHeight(), core.getTotalMineCount()), true);
    }

    private enum ClickArea {
        CENTER_8x8,
        SOUTHWEST_CORNER,
        SOUTHEAST_CORNER,
        NORTHWEST_CORNER,
        NORTHEAST_CORNER,
        NULL
    }

    public static void reset(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BlockMinesweeperCoreEntity coreEntity)) return;
        coreEntity.initGameData();
    }

    @Override
    public boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos pos, BlockState state) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        return core != null && GameMinesweeperHelper.checkLayoutAreaIsEmpty(level, pos, core.getGridWidth(), core.getGridHeight());
    }

    @Override
    public boolean unfoldGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core == null) {
            sendError(player);
            return false;
        }

        if (!checkLayoutAreaIsEmpty(level, pos, state)) {
            player.displayClientMessage(Component.translatable("msg.common.obstructed"), true);
            return false;
        }

        GameMinesweeperHelper.generateLayout(level, pos, core);
        level.setBlock(pos, state.setValue(UNFOLDED, true), Block.UPDATE_ALL);
        player.displayClientMessage(Component.translatable("msg.minesweeper.layout_placed",
                Component.translatable(core.getPresetDifficulty().getDisplayName()), core.getGridWidth(), core.getGridHeight()), true);
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
        core.initGameData();
        GameMinesweeperHelper.resetLayout(level, pos, w, h);
        player.displayClientMessage(Component.translatable("msg.minesweeper.game_started",
                Component.translatable(core.getPresetDifficulty().getDisplayName()), core.getTotalMineCount(), w, h), true);
    }

    @Override
    public void resetGame(ServerLevel level, BlockPos pos, BlockState state) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core == null) return;
        GameMinesweeperHelper.resetLayout(level, pos, core.getGridWidth(), core.getGridHeight());
        core.initGameData();
    }

    @Override
    public void minimizeGame(ServerLevel level, BlockPos pos, BlockState state) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core != null) {
            GameMinesweeperHelper.minimizeLayout(level, pos, core.getGridWidth(), core.getGridHeight());
            core.clearGameData();
        }
        if (state.getBlock() instanceof BlockMinesweeperCore) {
            level.setBlock(pos, state.setValue(IGameCoreBlock.UNFOLDED, false), Block.UPDATE_ALL);
        }
    }

    @Override
    public void closeGame(ServerLevel level, BlockPos pos, BlockState state) {
        BlockMinesweeperCoreEntity core = getCore(level, pos);
        if (core != null) GameMinesweeperHelper.closeLayout(level, pos, core.getGridWidth(), core.getGridHeight());
    }

    @Override
    public BlockEntity getGameCoreEntity(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }
}
