package com.simple_block_game.common.base.block;

import com.simple_block_game.common.base.data.RotatedRefreshArea;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

/** 水平旋转刷新方块基类，处理游戏控制（最小化/关闭/重置） */
public abstract class BaseRotatedRefreshBlock extends BaseRotatedBlock {

    protected final String coreNotFoundKey;
    protected final String minimizedKey;
    protected final String closedKey;
    protected final String resetKey;

    protected BaseRotatedRefreshBlock(BlockBehaviour.Properties properties,
                                      String coreNotFoundKey, String minimizedKey, String closedKey, String resetKey) {
        super(properties);
        this.coreNotFoundKey = coreNotFoundKey;
        this.minimizedKey = minimizedKey;
        this.closedKey = closedKey;
        this.resetKey = resetKey;
    }

    protected abstract @NonNull MapCodec<? extends BaseRotatedRefreshBlock> codec();

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockRefreshEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                     @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.FAIL;

        var clickArea = RotatedRefreshArea.fromHit(state, hit);
        if (clickArea == RotatedRefreshArea.NULL) return InteractionResult.PASS;

        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (!(be instanceof BlockRefreshEntity refreshEntity)) {
            player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable("msg.common.refresh_entity_error"));
            return InteractionResult.FAIL;
        }

        BlockPos corePos = refreshEntity.getCorePos();
        if (corePos == null) {
            player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable(coreNotFoundKey));
            return InteractionResult.FAIL;
        }

        BlockState coreState = serverLevel.getBlockState(corePos);
        if (!(coreState.getBlock() instanceof IGameCoreBlock coreBlock)) {
            return InteractionResult.PASS;
        }

        switch (clickArea) {
            case LEFT_TOP -> {
                coreBlock.minimizeGame(serverLevel, corePos, coreState);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable(minimizedKey));
            }
            case RIGHT_TOP -> {
                coreBlock.closeGame(serverLevel, corePos, coreState);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable(closedKey));
            }
            case BOTTOM -> {
                coreBlock.resetGame(serverLevel, corePos, coreState);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable(resetKey));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
