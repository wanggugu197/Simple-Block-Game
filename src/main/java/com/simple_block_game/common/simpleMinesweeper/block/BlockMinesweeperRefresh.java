package com.simple_block_game.common.simpleMinesweeper.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.simpleMinesweeper.logic.GameMinesweeperHelper;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import static com.simple_block_game.common.simpleMinesweeper.block.BlockMinesweeperCore.handleInitGameData;

public class BlockMinesweeperRefresh extends BaseVerticalBlock {

    public BlockMinesweeperRefresh(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockMinesweeperRefreshEntity(pos, state);
    }

    private static final MapCodec<BlockMinesweeperRefresh> CODEC = simpleCodec(BlockMinesweeperRefresh::new);

    @Override
    protected @NotNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        if (hit.getDirection() != Direction.UP) {
            return InteractionResult.PASS;
        }
        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (!(be instanceof BlockMinesweeperRefreshEntity refreshEntity)) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.refresh_entity_error"));
            return InteractionResult.FAIL;
        }
        BlockPos corePos = refreshEntity.getCorePos();
        if (corePos == null) {
            corePos = findCorePosFromRefreshPos(serverLevel, pos);
            if (corePos == null) {
                player.sendOverlayMessage(Component.translatable("msg.minesweeper.core_not_found"));
                return InteractionResult.FAIL;
            }
            refreshEntity.setCorePos(corePos);
        }
        BlockEntity coreBe = serverLevel.getBlockEntity(corePos);
        if (!(coreBe instanceof BlockMinesweeperCoreEntity coreEntity)) {
            player.sendOverlayMessage(Component.translatable("msg.minesweeper.core_invalid"));
            return InteractionResult.FAIL;
        }
        MinesweeperRefreshArea clickArea = getRefreshClickedArea(hit, pos);
        if (clickArea == MinesweeperRefreshArea.NULL) {
            return InteractionResult.PASS;
        }
        int width = coreEntity.getGridWidth();
        int height = coreEntity.getGridHeight();
        switch (clickArea) {
            case NORTH_EAST -> {
                GameMinesweeperHelper.minimizeMinesweeperLayout(serverLevel, corePos, width, height);
                player.sendOverlayMessage(Component.translatable("msg.minesweeper.minimized"));
            }
            case SOUTH_EAST -> {
                GameMinesweeperHelper.closeMinesweeperLayout(serverLevel, corePos, width, height);
                player.sendOverlayMessage(Component.translatable("msg.minesweeper.closed"));
            }
            case WEST -> {
                handleInitGameData(serverLevel, corePos, player);
                player.sendOverlayMessage(Component.translatable("msg.minesweeper.reset"));
            }
        }
        return InteractionResult.SUCCESS;
    }

    private MinesweeperRefreshArea getRefreshClickedArea(BlockHitResult hit, BlockPos pos) {
        Vec3 worldHitPos = hit.getLocation();
        double localX = worldHitPos.x - pos.getX();
        double localZ = worldHitPos.z - pos.getZ();
        double u = Mth.clamp(localX * 16, 0, 16);
        double v = Mth.clamp(localZ * 16, 0, 16);
        if (u >= 8) {
            if (v < 8) {
                return MinesweeperRefreshArea.NORTH_EAST;
            } else if (v >= 8) {
                return MinesweeperRefreshArea.SOUTH_EAST;
            }
        } else if (u < 8) {
            return MinesweeperRefreshArea.WEST;
        }
        return MinesweeperRefreshArea.NULL;
    }

    private BlockPos findCorePosFromRefreshPos(ServerLevel level, BlockPos refreshPos) {
        for (int xOffset = -100; xOffset <= 0; xOffset++) {
            for (int zOffset = -100; zOffset <= 0; zOffset++) {
                BlockPos checkPos = new BlockPos(
                        refreshPos.getX() + xOffset,
                        refreshPos.getY() - 1,
                        refreshPos.getZ() + zOffset);
                BlockState checkState = level.getBlockState(checkPos);
                if (checkState.getBlock() instanceof BlockMinesweeperCore) {
                    return checkPos;
                }
            }
        }
        return null;
    }

    private enum MinesweeperRefreshArea {
        SOUTH_EAST,
        NORTH_EAST,
        WEST,
        NULL
    }
}
