package com.simple_block_game.common.simpleMemoryKey.block;

import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyGameState;
import com.simple_block_game.common.simpleMemoryKey.logic.GameMemoryKeyHelper;

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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.NonNull;

/** 记忆键游戏核心方块 */
public class BlockMemoryKeyCore extends BaseVerticalBlock implements IGameCoreBlock {

    private static final MapCodec<BlockMemoryKeyCore> CODEC = simpleCodec(BlockMemoryKeyCore::new);

    public static final EnumProperty<MemoryKeyGameState> GAME_STATE = EnumProperty.create("state", MemoryKeyGameState.class);
    public static final IntegerProperty LIVES = IntegerProperty.create("lives", 0, 3);

    public BlockMemoryKeyCore(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(UNFOLDED, false)
                .setValue(GAME_STATE, MemoryKeyGameState.IDLE)
                .setValue(LIVES, 3));
    }

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UNFOLDED, GAME_STATE, LIVES);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockMemoryKeyCoreEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
        if (level.isClientSide()) return null;

        return (_, _, _, entity) -> {
            if (entity instanceof BlockMemoryKeyCoreEntity coreEntity) {
                coreEntity.tick();
            }
        };
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (level.isClientSide() || hit.getDirection() != Direction.UP) {
            return hit.getDirection() == Direction.UP ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        ClickArea area = getClickArea(hit, pos);

        if (area == ClickArea.CENTER_8x8) {
            if (!state.getValue(UNFOLDED)) {
                unfoldGame(serverLevel, pos, state, player);
            } else {
                BlockMemoryKeyCoreEntity core = getCore(serverLevel, pos);
                if (core != null && core.getGameState() == MemoryKeyGameState.IDLE) {
                    startGame(serverLevel, pos, state, player);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private ClickArea getClickArea(BlockHitResult hit, BlockPos pos) {
        Vec3 worldPos = hit.getLocation();
        double u = Mth.clamp((worldPos.x - pos.getX()) * 16, 0, 16);
        double v = Mth.clamp((worldPos.z - pos.getZ()) * 16, 0, 16);

        if (u >= 4 && u <= 12 && v >= 4 && v <= 12) return ClickArea.CENTER_8x8;
        if (u <= 4 && v >= 12) return ClickArea.NORTHWEST_CORNER;
        return ClickArea.NULL;
    }

    private BlockMemoryKeyCoreEntity getCore(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof BlockMemoryKeyCoreEntity core ? core : null;
    }

    private void sendError(Player player) {
        player.sendOverlayMessage(Component.translatable("msg.common.entity_error"));
    }

    private enum ClickArea {
        CENTER_8x8,
        NORTHWEST_CORNER,
        NULL
    }

    @Override
    public boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos pos, BlockState state) {
        return GameMemoryKeyHelper.checkLayoutAreaIsEmpty(level, pos);
    }

    /**
     * 展开记忆键游戏布局
     * <p>
     * 在核心方块周围生成8个按键方块、12个边框方块和1个刷新控制方块。
     * 展开前会检查布局区域是否为空，避免与其他方块重叠。
     *
     * @param level  服务端世界
     * @param pos    核心方块位置
     * @param state  核心方块状态
     * @param player 执行操作的玩家
     * @return 是否成功展开
     */
    @Override
    public boolean unfoldGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        if (!checkLayoutAreaIsEmpty(level, pos, state)) {
            player.sendOverlayMessage(Component.translatable("msg.common.obstructed"));
            return false;
        }
        GameMemoryKeyHelper.generateMemoryKeyLayout(level, pos);
        level.setBlock(pos, state.setValue(UNFOLDED, true), 3);
        // 初始化游戏：从未开始到准备开始状态
        initializeGame(level, pos);
        player.sendOverlayMessage(Component.translatable("msg.memory_key.game_ready"));
        return true;
    }

    @Override
    public void startGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        BlockMemoryKeyCoreEntity core = getCore(level, pos);
        if (core == null) {
            sendError(player);
            return;
        }

        GameMemoryKeyHelper.startGame(level, pos, core);
        player.sendOverlayMessage(Component.translatable("msg.memory_key.watch_sequence"));
    }

    /**
     * 初始化游戏（从未开始到准备开始状态）
     * <p>
     * 用于游戏展开时的第一次初始化，设置初始状态但不清除序列。
     */
    private void initializeGame(ServerLevel level, BlockPos pos) {
        BlockMemoryKeyCoreEntity core = getCore(level, pos);
        if (core != null) {
            core.initialize();
            syncBlockState(level, pos);
        }
    }

    @Override
    public void resetGame(ServerLevel level, BlockPos pos, BlockState state) {
        BlockMemoryKeyCoreEntity core = getCore(level, pos);
        if (core != null) {
            core.completeReset();
            GameMemoryKeyHelper.resetButtonStates(level, pos);
            syncBlockState(level, pos);
        }
    }

    /**
     * 同步方块状态到客户端
     *
     * @param level 服务端世界
     * @param pos   方块位置
     */
    private void syncBlockState(ServerLevel level, BlockPos pos) {
        BlockState currentState = level.getBlockState(pos);

        if (!currentState.getValue(UNFOLDED)) {
            currentState = currentState.setValue(UNFOLDED, true);
            level.setBlock(pos, currentState, 3);
        }

        level.sendBlockUpdated(pos, currentState, level.getBlockState(pos), 3);
    }

    @Override
    public void minimizeGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameMemoryKeyHelper.minimizeMemoryKeyLayout(level, pos);
    }

    @Override
    public void closeGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameMemoryKeyHelper.destroyMemoryKeyLayout(level, pos);
    }

    @Override
    public boolean isGameUnfolded(BlockState state) {
        return state.getValue(UNFOLDED);
    }

    @Override
    public BlockEntity getGameCoreEntity(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    public static void handleButtonClick(ServerLevel level, BlockPos corePos, int buttonId, Player player) {
        BlockEntity be = level.getBlockEntity(corePos);
        if (!(be instanceof BlockMemoryKeyCoreEntity core)) return;
        GameMemoryKeyHelper.handlePlayerInput(level, corePos, core, buttonId, player);
    }
}