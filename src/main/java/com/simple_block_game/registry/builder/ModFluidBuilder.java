package com.simple_block_game.registry.builder;

import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;

import com.gto.registrylib.RegistryCore;
import com.gto.registrylib.builders.FluidBuilder;
import com.simple_block_game.registry.GameRegistryCore;
import org.jetbrains.annotations.NotNull;

public class ModFluidBuilder<T extends BaseFlowingFluid, P> extends FluidBuilder<T, P> {

    public static <T extends BaseFlowingFluid, P> ModFluidBuilder<T, P> create(
                                                                               RegistryCore owner, P parent, String name, FluidFactory<T> fluidFactory) {
        var builder = new ModFluidBuilder<>(owner, parent, name, FluidType::new, fluidFactory);
        return (ModFluidBuilder<T, P>) builder.defaultLang().defaultSource().defaultBlock().defaultBucket();
    }

    protected ModFluidBuilder(
                              RegistryCore owner,
                              P parent,
                              String name,
                              FluidTypeFactory typeFactory,
                              FluidFactory<T> fluidFactory) {
        super(owner, parent, name, typeFactory, fluidFactory);
    }

    public ModFluidBuilder<T, P> langCn(@NotNull String name) {
        lang(GameRegistryCore.LANG_ZH_CN, name);
        return this;
    }
}
