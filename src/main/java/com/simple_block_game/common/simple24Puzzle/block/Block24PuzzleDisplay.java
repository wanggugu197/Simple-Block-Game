package com.simple_block_game.common.simple24Puzzle.block;

import com.simple_block_game.SimpleBlockGameConfig;
import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.simple24Puzzle.data.GameToken24Puzzle;
import com.simple_block_game.common.simple24Puzzle.logic.Game24PuzzleHelper;

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

/**
 * 24点游戏显示方块
 */
public class Block24PuzzleDisplay extends BaseRotatedBlock {

    public Block24PuzzleDisplay(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static final MapCodec<Block24PuzzleDisplay> CODEC = simpleCodec(Block24PuzzleDisplay::new);

    @Override
    protected @NonNull MapCodec<? extends BaseRotatedBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new Block24PuzzleDisplayEntity(pos, state);
    }

    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                     @NonNull BlockPos pos, @NonNull Player player,
                                                     @NonNull BlockHitResult hit) {
        if (!SimpleBlockGameConfig.enable24PuzzleGame.get()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.FAIL;
        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (!(be instanceof Block24PuzzleDisplayEntity displayEntity)) return InteractionResult.PASS;
        GameToken24Puzzle token = displayEntity.getToken();
        if (token == null) return InteractionResult.PASS;
        BlockPos corePos = displayEntity.getCorePos();
        if (corePos == null) return InteractionResult.PASS;
        Block24PuzzleCoreEntity coreEntity = Game24PuzzleHelper.getCoreEntity(serverLevel, corePos);
        if (coreEntity == null) return InteractionResult.PASS;
        long currentTime = serverLevel.getGameTime();
        if (coreEntity.isComboExpired(currentTime)) {
            coreEntity.resetCombo(currentTime);
        }
        Game24PuzzleHelper.handleTokenInput(coreEntity, token);
        return InteractionResult.SUCCESS;
    }
}
