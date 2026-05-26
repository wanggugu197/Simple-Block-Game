package com.simple_block_game.common.base.block;

import com.simple_block_game.common.base.data.VerticalRefreshArea;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

/** 垂直固定刷新方块基类，处理游戏控制（最小化/关闭/重置） */
public abstract class BaseVerticalRefreshBlock extends BaseVerticalBlock {

    protected final String coreNotFoundKey;
    protected final String coreInvalidKey;
    protected final String minimizedKey;
    protected final String closedKey;
    protected final String resetKey;

    protected BaseVerticalRefreshBlock(BlockBehaviour.Properties properties,
                                       String coreNotFoundKey, String coreInvalidKey,
                                       String minimizedKey, String closedKey, String resetKey) {
        super(properties);
        this.coreNotFoundKey = coreNotFoundKey;
        this.coreInvalidKey = coreInvalidKey;
        this.minimizedKey = minimizedKey;
        this.closedKey = closedKey;
        this.resetKey = resetKey;
    }

    protected abstract @NonNull MapCodec<? extends BaseVerticalRefreshBlock> codec();

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockRefreshEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                     @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (level.isClientSide() || hit.getDirection() != Direction.UP) {
            return hit.getDirection() == Direction.UP ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        ServerLevel serverLevel = (ServerLevel) level;

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
            player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable(coreInvalidKey));
            return InteractionResult.FAIL;
        }

        VerticalRefreshArea area = VerticalRefreshArea.fromHit(hit);
        if (area == VerticalRefreshArea.NULL) return InteractionResult.PASS;

        switch (area) {
            case NORTH_EAST -> {
                coreBlock.minimizeGame(serverLevel, corePos, coreState);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable(minimizedKey));
            }
            case SOUTH_EAST -> {
                coreBlock.closeGame(serverLevel, corePos, coreState);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable(closedKey));
            }
            case WEST -> {
                coreBlock.resetGame(serverLevel, corePos, coreState);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable(resetKey));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
