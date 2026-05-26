package com.simple_block_game.common.simple2048.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.simple2048.data.RefreshClickArea;
import com.simple_block_game.common.simple2048.logic.Game2048Helper;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public class Block2048Refresh extends BaseRotatedBlock {

    public Block2048Refresh(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static final MapCodec<Block2048Refresh> CODEC = simpleCodec(Block2048Refresh::new);

    @Override
    protected @NotNull MapCodec<? extends BaseRotatedBlock> codec() {
        return CODEC;
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player,
                                                     @NonNull BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }
        Direction refreshFacing = state.getValue(FACING);
        RefreshClickArea clickArea = getRefreshClickedArea(state, hit);
        BlockPos corePos = Game2048Helper.findCorePosFromRefreshPos(serverLevel, pos, refreshFacing);

        if (corePos == null) {
            player.sendOverlayMessage(Component.translatable("msg.simple2048.core_not_found"));
            return InteractionResult.FAIL;
        }

        switch (clickArea) {
            case LEFT_TOP -> {
                Game2048Helper.minimize2048Layout(serverLevel, corePos, refreshFacing);
                player.sendOverlayMessage(Component.translatable("msg.simple2048.minimized"));
            }
            case RIGHT_TOP -> {
                Game2048Helper.close2048Layout(serverLevel, corePos, refreshFacing);
                player.sendOverlayMessage(Component.translatable("msg.simple2048.closed"));
            }
            case BOTTOM -> {
                Game2048Helper.reset2048Layout(serverLevel, corePos, refreshFacing);
                player.sendOverlayMessage(Component.translatable("msg.simple2048.reset"));
            }
            default -> {
                return InteractionResult.PASS;
            }
        }

        return InteractionResult.SUCCESS;
    }

    public static RefreshClickArea getRefreshClickedArea(BlockState refreshState, BlockHitResult hit) {
        Vec3 worldHitPos = hit.getLocation();
        Direction blockFacing = refreshState.getValue(BaseRotatedBlock.FACING);
        Direction hitFace = hit.getDirection();
        if (hitFace != blockFacing) {
            return RefreshClickArea.NULL;
        }
        BlockPos blockPos = hit.getBlockPos();
        double localX = worldHitPos.x - blockPos.getX();
        double localY = worldHitPos.y - blockPos.getY();
        double localZ = worldHitPos.z - blockPos.getZ();
        switch (blockFacing) {
            case SOUTH:
                double uSouth = Mth.clamp(localX * 16, 0, 16);
                double vSouth = Mth.clamp(localY * 16, 0, 16);
                if (vSouth >= 8) {
                    return uSouth <= 8 ? RefreshClickArea.LEFT_TOP : RefreshClickArea.RIGHT_TOP;
                } else {
                    return RefreshClickArea.BOTTOM;
                }
            case NORTH:
                double uNorth = Mth.clamp(localX * 16, 0, 16);
                double vNorth = Mth.clamp(localY * 16, 0, 16);
                if (vNorth >= 8) {
                    return uNorth <= 8 ? RefreshClickArea.RIGHT_TOP : RefreshClickArea.LEFT_TOP;
                } else {
                    return RefreshClickArea.BOTTOM;
                }
            case EAST:
                double uEast = Mth.clamp(localZ * 16, 0, 16);
                double vEast = Mth.clamp(localY * 16, 0, 16);
                if (vEast >= 8) {
                    return uEast <= 8 ? RefreshClickArea.RIGHT_TOP : RefreshClickArea.LEFT_TOP;
                } else {
                    return RefreshClickArea.BOTTOM;
                }
            case WEST:
                double uWest = Mth.clamp(localZ * 16, 0, 16);
                double vWest = Mth.clamp(localY * 16, 0, 16);
                if (vWest >= 8) {
                    return uWest <= 8 ? RefreshClickArea.LEFT_TOP : RefreshClickArea.RIGHT_TOP;
                } else {
                    return RefreshClickArea.BOTTOM;
                }
            default:
                return RefreshClickArea.NULL;
        }
    }
}
