package com.simple_block_game.common.base.block;

import com.simple_block_game.common.base.data.VerticalRefreshArea;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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

public class BaseVerticalRefreshBlock extends BaseVerticalBlock {

    protected final String coreNotFoundKey;
    protected final String coreInvalidKey;
    protected final String minimizedKey;
    protected final String closedKey;
    protected final String resetKey;

    private final MapCodec<BaseVerticalRefreshBlock> codec;

    private BaseVerticalRefreshBlock(BlockBehaviour.Properties properties,
                                     String coreNotFoundKey, String coreInvalidKey,
                                     String minimizedKey, String closedKey, String resetKey,
                                     MapCodec<BaseVerticalRefreshBlock> codec) {
        super(properties);
        this.coreNotFoundKey = coreNotFoundKey;
        this.coreInvalidKey = coreInvalidKey;
        this.minimizedKey = minimizedKey;
        this.closedKey = closedKey;
        this.resetKey = resetKey;
        this.codec = codec;
    }

    public static BaseVerticalRefreshBlock create(BlockBehaviour.Properties properties,
                                                  String coreNotFoundKey, String coreInvalidKey, String minimizedKey, String closedKey, String resetKey) {
        MapCodec<BaseVerticalRefreshBlock> codec = simpleCodec(p -> new BaseVerticalRefreshBlock(p, coreNotFoundKey, coreInvalidKey, minimizedKey, closedKey, resetKey, null));
        return new BaseVerticalRefreshBlock(properties, coreNotFoundKey, coreInvalidKey, minimizedKey, closedKey, resetKey, codec);
    }

    public static BaseVerticalRefreshBlock create(BlockBehaviour.Properties properties, String gameKey) {
        String prefix = "msg." + gameKey + ".";
        return create(properties,
                prefix + "core_not_found",
                prefix + "core_invalid",
                prefix + "minimized",
                prefix + "closed",
                prefix + "reset");
    }

    protected @NonNull MapCodec<BaseVerticalRefreshBlock> codec() {
        return codec;
    }

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
            player.displayClientMessage(Component.translatable("msg.common.refresh_entity_error"), true);
            return InteractionResult.FAIL;
        }

        BlockPos corePos = refreshEntity.getCorePos();
        if (corePos == null) {
            player.displayClientMessage(Component.translatable(coreNotFoundKey), true);
            return InteractionResult.FAIL;
        }

        BlockState coreState = serverLevel.getBlockState(corePos);
        if (!(coreState.getBlock() instanceof IGameCoreBlock coreBlock)) {
            player.displayClientMessage(Component.translatable(coreInvalidKey), true);
            return InteractionResult.FAIL;
        }

        VerticalRefreshArea area = VerticalRefreshArea.fromHit(hit);
        if (area == VerticalRefreshArea.NULL) return InteractionResult.PASS;

        switch (area) {
            case NORTH_EAST -> {
                coreBlock.minimizeGame(serverLevel, corePos, coreState);
                player.displayClientMessage(Component.translatable(minimizedKey), true);
            }
            case SOUTH_EAST -> {
                coreBlock.closeGame(serverLevel, corePos, coreState);
                player.displayClientMessage(Component.translatable(closedKey), true);
            }
            case WEST -> {
                coreBlock.resetGame(serverLevel, corePos, coreState);
                player.displayClientMessage(Component.translatable(resetKey), true);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
