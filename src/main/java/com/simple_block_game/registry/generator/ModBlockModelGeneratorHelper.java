package com.simple_block_game.registry.generator;

import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.gto.registrylib.datagen.generator.RegistryLibBlockModelGenerator;
import com.mojang.math.Quadrant;

import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;
import static net.minecraft.core.Direction.UP;

public class ModBlockModelGeneratorHelper {

    public static void createVerticalBlock(Block block, RegistryLibBlockModelGenerator prov, String id) {
        MultiVariant model = plainVariant(prov.modLoc(id));
        prov.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(BlockStateProperties.VERTICAL_DIRECTION)
                        .generate(facing -> model.with(variant -> variant.withXRot(facing == UP ? Quadrant.R0 : Quadrant.R180)))));
    }

    public static void createHorizontalBlock(Block block, RegistryLibBlockModelGenerator prov, String id) {
        MultiVariant model = plainVariant(prov.modLoc(id));
        prov.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING)
                        .generate(facing -> model.with(variant -> switch (facing) {
                            case NORTH -> variant.withYRot(Quadrant.R0);
                            case EAST -> variant.withYRot(Quadrant.R90);
                            case SOUTH -> variant.withYRot(Quadrant.R180);
                            case WEST -> variant.withYRot(Quadrant.R270);
                            default -> throw new IllegalStateException("Unexpected value: " + facing);
                        }))));
    }
}
