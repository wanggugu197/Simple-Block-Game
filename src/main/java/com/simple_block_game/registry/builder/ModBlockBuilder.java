package com.simple_block_game.registry.builder;

import com.simple_block_game.registry.GameRegistryCore;

import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.gto.registrylib.RegistryCore;
import com.gto.registrylib.builders.BlockBuilder;
import com.gto.registrylib.datagen.generator.RegistryLibBlockModelGenerator;
import com.mojang.math.Quadrant;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

public class ModBlockBuilder<T extends Block, P> extends BlockBuilder<T, P> {

    public static <T extends Block, P> ModBlockBuilder<T, P> create(
                                                                    RegistryCore owner, P parent, String name, Function<BlockBehaviour.Properties, T> factory) {
        var builder = new ModBlockBuilder<>(owner, parent, name, factory);
        return (ModBlockBuilder<T, P>) builder.defaultLoot().defaultLang();
    }

    protected ModBlockBuilder(
                              RegistryCore owner, P parent, String name, Function<BlockBehaviour.Properties, T> factory) {
        super(owner, parent, name, factory);
    }

    public ModBlockBuilder<T, P> langCn(@NotNull String name) {
        lang(GameRegistryCore.LANG_ZH_CN, name);
        return this;
    }

    public ModBlockBuilder<T, P> horizontalBlockstate(@NotNull String modelName) {
        blockstate(() -> (T block, RegistryLibBlockModelGenerator prov) -> prov.generateHorizontalBlock(block, plainVariant(prov.modLoc(modelName))));
        return this;
    }

    public static BlockModelDefinitionGenerator createVerticalBlock(Block block, MultiVariant model) {
        return MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(BlockStateProperties.FACING)
                        .generate(facing -> {
                            if (facing == Direction.UP) {
                                return model;
                            } else {
                                return model.with(variant -> variant.withXRot(Quadrant.R180));
                            }
                        }));
    }
}
