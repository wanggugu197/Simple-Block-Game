package com.simple_block_game.registry.builder;

import com.simple_block_game.registry.GameRegistryCore;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.gto.registrylib.RegistryCore;
import com.gto.registrylib.builders.BlockBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

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
}
