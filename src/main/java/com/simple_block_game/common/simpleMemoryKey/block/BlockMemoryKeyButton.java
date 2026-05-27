package com.simple_block_game.common.simpleMemoryKey.block;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.block.BaseVerticalBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

/** 记忆键游戏的按键方块，支持闪烁效果和玩家输入 */
public class BlockMemoryKeyButton extends BaseVerticalBlock {

    public BlockMemoryKeyButton(BlockBehaviour.Properties properties) {
        super(properties);
    }

    private static final MapCodec<BlockMemoryKeyButton> CODEC = simpleCodec(BlockMemoryKeyButton::new);

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
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
     */
    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos,
                                                     @NonNull Player player, @NonNull BlockHitResult hit) {
        if (!SimpleBlockGameConfig.enableMemoryKeyGame.get()) return InteractionResult.PASS;

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
