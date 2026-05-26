package com.simple_block_game.common.simple2048.block;

import com.simple_block_game.common.base.block.BaseRotatedBlock;
import com.simple_block_game.common.simple2048.data.Value2048;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import com.mojang.serialization.MapCodec;
import lombok.NonNull;

/** 2048游戏显示方块 */
public class Block2048Display extends BaseRotatedBlock {

    public static final EnumProperty<@NonNull Value2048> DISPLAY_VALUE = EnumProperty.create("display_value", Value2048.class);

    public Block2048Display(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(DISPLAY_VALUE, Value2048.ZERO));
    }

    public static final MapCodec<Block2048Display> CODEC = simpleCodec(Block2048Display::new);

    @Override
    protected @NonNull MapCodec<? extends BaseRotatedBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(@NonNull StateDefinition.Builder<Block, @NonNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DISPLAY_VALUE);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new Block2048DisplayEntity(pos, state);
    }

    public static void setDisplayValue(BlockGetter level, BlockPos pos, int newValue) {
        if (level == null || pos == null) return;

        Value2048 value = Value2048.fromInt(newValue);

        if (level.getBlockEntity(pos) instanceof Block2048DisplayEntity entity) {
            entity.setDisplayValue(value);
        }

        if (level instanceof Level realLevel && !realLevel.isClientSide()) {
            BlockState state = realLevel.getBlockState(pos);
            if (state.hasProperty(DISPLAY_VALUE)) {
                realLevel.setBlock(pos, state.setValue(DISPLAY_VALUE, value), 3);
            }
        }
    }

    public static int getDisplayValue(BlockGetter level, BlockPos pos) {
        if (level == null || pos == null) return 0;

        if (level.getBlockEntity(pos) instanceof Block2048DisplayEntity entity) {
            return entity.getDisplayValue();
        }

        return 0;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level,
                                                                  @NonNull BlockState state,
                                                                  @NonNull BlockEntityType<T> type) {
        return null;
    }
}
