package com.simple_block_game.common.simpleMemoryKey.block;

import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.simpleMemoryKey.data.MemoryKeyPosition;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.NonNull;

/** 记忆键游戏的按键方块，支持闪烁效果和玩家输入 */
public class BlockMemoryKeyButton extends BaseVerticalBlock {

    private static final MapCodec<BlockMemoryKeyButton> CODEC = simpleCodec(BlockMemoryKeyButton::new);

    public static final EnumProperty<MemoryKeyPosition> POSITION = EnumProperty.create("position", MemoryKeyPosition.class);
    public static final BooleanProperty FLASHING = BooleanProperty.create("flashing");

    public BlockMemoryKeyButton(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POSITION, MemoryKeyPosition.NORTH)
                .setValue(FLASHING, false));
    }

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POSITION, FLASHING);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockMemoryKeyButtonEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
        if (level.isClientSide()) return null;

        return (_, _, _, entity) -> {
            if (entity instanceof BlockMemoryKeyButtonEntity buttonEntity) {
                buttonEntity.tick();
            }
        };
    }

    /**
     * 处理玩家点击事件
     * <p>
     * 获取核心方块位置和按键位置，调用核心逻辑处理按键输入。
     *
     * @param state  方块状态
     * @param level  世界
     * @param pos    方块位置
     * @param player 玩家
     * @param hit    点击结果
     * @return 交互结果
     */
    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos,
                                                     @NonNull Player player, @NonNull BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BlockMemoryKeyButtonEntity buttonEntity)) {
            return InteractionResult.PASS;
        }

        BlockPos corePos = buttonEntity.getCorePos();
        if (corePos == null) {
            return InteractionResult.PASS;
        }

        BlockMemoryKeyCore.handleButtonClick((ServerLevel) level, corePos, buttonEntity.getPosition().getId(), player);
        return InteractionResult.SUCCESS;
    }
}
