package com.simple_block_game.util.generator;

import com.simple_block_game.SimpleBlockGame;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class ModBlockModelGeneratorHelper {

    public static void registerHorizontalBlock(Block block, BlockStateProvider prov, String modelPath) {
        ModelFile model = new ModelFile.UncheckedModelFile(SimpleBlockGame.getId(modelPath));

        // 纯手动映射 4 个方向与对应的 Y 轴旋转角度
        prov.getVariantBuilder(block)
                .partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setModels(new ConfiguredModel(model, 0, 0, false))
                .partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
                .setModels(new ConfiguredModel(model, 0, 90, false))
                .partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
                .setModels(new ConfiguredModel(model, 0, 180, false))
                .partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
                .setModels(new ConfiguredModel(model, 0, 270, false));
    }

    public static void registerVerticalBlock(Block block, BlockStateProvider prov, String modelPath) {
        ModelFile model = new ModelFile.UncheckedModelFile(SimpleBlockGame.getId(modelPath));
        prov.getVariantBuilder(block)
                .partialState().with(BlockStateProperties.VERTICAL_DIRECTION, Direction.UP)
                .setModels(new ConfiguredModel(model, 0, 0, false))
                .partialState().with(BlockStateProperties.VERTICAL_DIRECTION, Direction.DOWN)
                .setModels(new ConfiguredModel(model, 180, 0, false));
    }
}
