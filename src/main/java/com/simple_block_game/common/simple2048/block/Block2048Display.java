package com.simple_block_game.common.simple2048.block;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.simple2048.data.Quadrant;
import com.simple_block_game.common.simple2048.data.Value2048;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

/**
 * 2048 游戏显示方块
 */
public class Block2048Display extends BaseRotatedBlock {

    public Block2048Display(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static final MapCodec<Block2048Display> CODEC = simpleCodec(Block2048Display::new);

    @Override
    protected @NonNull MapCodec<? extends BaseRotatedBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new Block2048DisplayEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                     @NonNull BlockPos pos, @NonNull Player player,
                                                     @NonNull BlockHitResult hit) {
        if (!SimpleBlockGameConfig.enable2048Game.get()) return InteractionResult.PASS;

        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.FAIL;

        BlockPos corePos = Block2048Display.getCorePos(serverLevel, pos);
        if (corePos == null) return InteractionResult.PASS;

        BlockState coreState = serverLevel.getBlockState(corePos);
        if (!(coreState.getBlock() instanceof Block2048Core coreBlock)) return InteractionResult.PASS;

        Quadrant direction = coreBlock.getQuadrant(coreState, hit);

        if (direction == Quadrant.NULL) return InteractionResult.PASS;

        coreBlock.handleGameMove(serverLevel, corePos, coreState, player, direction);
        return InteractionResult.SUCCESS;
    }

    public static void setDisplayValue(BlockGetter level, BlockPos pos, int newValue) {
        if (level == null || pos == null) return;
        Value2048 value = Value2048.fromInt(newValue);
        if (level.getBlockEntity(pos) instanceof Block2048DisplayEntity entity) {
            entity.setDisplayValue(value);
        }
    }

    public static int getDisplayValue(BlockGetter level, BlockPos pos) {
        if (level == null || pos == null) return 0;
        if (level.getBlockEntity(pos) instanceof Block2048DisplayEntity entity) {
            return entity.getDisplayValue();
        }
        return 0;
    }

    public static void setCorePos(BlockGetter level, BlockPos pos, BlockPos corePos) {
        if (level == null || pos == null) return;
        if (level.getBlockEntity(pos) instanceof Block2048DisplayEntity entity) {
            entity.setCorePos(corePos);
        }
    }

    public static BlockPos getCorePos(BlockGetter level, BlockPos pos) {
        if (level == null || pos == null) return null;
        if (level.getBlockEntity(pos) instanceof Block2048DisplayEntity entity) {
            return entity.getCorePos();
        }
        return null;
    }
}
