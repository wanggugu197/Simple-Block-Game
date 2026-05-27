package com.simple_block_game.common.base.block;

import com.simple_block_game.common.base.data.RotatedRefreshArea;

import net.minecraft.core.BlockPos;
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

public class BaseRotatedRefreshBlock extends BaseRotatedBlock {

    protected final String coreNotFoundKey;
    protected final String coreInvalidKey;
    protected final String minimizedKey;
    protected final String closedKey;
    protected final String resetKey;

    private final MapCodec<BaseRotatedRefreshBlock> codec;

    private BaseRotatedRefreshBlock(BlockBehaviour.Properties properties,
                                    String coreNotFoundKey, String coreInvalidKey,
                                    String minimizedKey, String closedKey, String resetKey,
                                    MapCodec<BaseRotatedRefreshBlock> codec) {
        super(properties);
        this.coreNotFoundKey = coreNotFoundKey;
        this.coreInvalidKey = coreInvalidKey;
        this.minimizedKey = minimizedKey;
        this.closedKey = closedKey;
        this.resetKey = resetKey;
        this.codec = codec;
    }

    public static BaseRotatedRefreshBlock create(BlockBehaviour.Properties properties,
                                                 String coreNotFoundKey, String coreInvalidKey, String minimizedKey, String closedKey, String resetKey) {
        MapCodec<BaseRotatedRefreshBlock> codec = simpleCodec(p -> new BaseRotatedRefreshBlock(p, coreNotFoundKey, coreInvalidKey, minimizedKey, closedKey, resetKey, null));
        return new BaseRotatedRefreshBlock(properties, coreNotFoundKey, coreInvalidKey, minimizedKey, closedKey, resetKey, codec);
    }

    public static BaseRotatedRefreshBlock create(BlockBehaviour.Properties properties, String gameKey) {
        String prefix = "msg." + gameKey + ".";
        return create(properties,
                prefix + "core_not_found",
                prefix + "core_invalid",
                prefix + "minimized",
                prefix + "closed",
                prefix + "reset");
    }

    protected @NonNull MapCodec<BaseRotatedRefreshBlock> codec() {
        return codec;
    }

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
            player.sendOverlayMessage(Component.translatable("msg.common.refresh_entity_error"));
            return InteractionResult.FAIL;
        }

        BlockPos corePos = refreshEntity.getCorePos();
        if (corePos == null) {
            player.sendOverlayMessage(Component.translatable(coreNotFoundKey));
            return InteractionResult.FAIL;
        }

        BlockState coreState = serverLevel.getBlockState(corePos);
        if (!(coreState.getBlock() instanceof IGameCoreBlock coreBlock)) {
            player.sendOverlayMessage(Component.translatable(coreInvalidKey));
            return InteractionResult.FAIL;
        }

        switch (clickArea) {
            case LEFT_TOP -> {
                coreBlock.minimizeGame(serverLevel, corePos, coreState);
                player.sendOverlayMessage(Component.translatable(minimizedKey));
            }
            case RIGHT_TOP -> {
                coreBlock.closeGame(serverLevel, corePos, coreState);
                player.sendOverlayMessage(Component.translatable(closedKey));
            }
            case BOTTOM -> {
                coreBlock.resetGame(serverLevel, corePos, coreState);
                player.sendOverlayMessage(Component.translatable(resetKey));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
