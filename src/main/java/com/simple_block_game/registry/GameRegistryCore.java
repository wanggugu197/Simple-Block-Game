package com.simple_block_game.registry;

import com.simple_block_game.registry.builder.ModBlockBuilder;
import com.simple_block_game.registry.builder.ModEntityBuilder;
import com.simple_block_game.registry.builder.ModFluidBuilder;
import com.simple_block_game.registry.builder.ModItemBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import com.gto.registrylib.RegistryCore;
import com.gto.registrylib.builders.FluidBuilder;
import com.gto.registrylib.composite.ComponentItem;
import com.gto.registrylib.composite.IComponentItem;
import com.gto.registrylib.datagen.ProviderType;
import com.gto.registrylib.datagen.provider.RegistryLibLangProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class GameRegistryCore extends RegistryCore {

    public static final ProviderType<RegistryLibLangProvider> LANG_ZH_CN = ProviderType.registerClientProvider(
            "lang_zh_cn", () -> c -> new RegistryLibLangProvider(c.parent(), c.output(), "zh_cn") {

                @Override
                protected ProviderType<? extends RegistryLibLangProvider> getProviderType() {
                    return GameRegistryCore.LANG_ZH_CN;
                }
            });

    protected GameRegistryCore(String modid) {
        super(modid);
        withLangAlias("zh_cn", LANG_ZH_CN);
    }

    public static GameRegistryCore create(String modid) {
        return new GameRegistryCore(modid);
    }

    @Override
    public <T extends Block, P> ModBlockBuilder<T, P> block(
                                                            @NotNull P parent,
                                                            @NotNull String name,
                                                            @NotNull Function<BlockBehaviour.Properties, T> factory) {
        return ModBlockBuilder.create(this, parent, name, factory);
    }

    @Override
    public <T extends Item> ModItemBuilder<T, RegistryCore> item(
                                                                 @NotNull String name,
                                                                 @NotNull Function<Item.Properties, T> factory) {
        return item(this, name, factory, false);
    }

    @Override
    public ModItemBuilder<Item, RegistryCore> item(@NotNull String name) {
        return item(this, name, Item::new, false);
    }

    @Override
    public <T extends Item & IComponentItem<T>> ModItemBuilder<T, RegistryCore> componentItem(
                                                                                              @NotNull String name,
                                                                                              @NotNull Function<Item.Properties, T> factory) {
        return item(this, name, factory, true);
    }

    @Override
    public ModItemBuilder<ComponentItem, RegistryCore> componentItem(@NotNull String name) {
        return componentItem(name, ComponentItem::new);
    }

    @Override
    public <T extends Item, P> ModItemBuilder<T, P> item(
                                                         @NotNull P parent,
                                                         @NotNull String name,
                                                         @NotNull Function<Item.Properties, T> factory,
                                                         boolean isComponentItem) {
        return ModItemBuilder.create(this, parent, name, factory, isComponentItem);
    }

    @Override
    public <T extends BaseFlowingFluid, P> ModFluidBuilder<T, P> fluid(
                                                                       @NotNull P parent,
                                                                       @NotNull String name,
                                                                       @NotNull Identifier stillTexture,
                                                                       @NotNull Identifier flowingTexture,
                                                                       @NotNull FluidBuilder.FluidFactory<T> fluidFactory) {
        return (ModFluidBuilder<T, P>) super.fluid(parent, name, stillTexture, flowingTexture, fluidFactory);
    }

    @Override
    public <T extends Entity> ModEntityBuilder<T, RegistryCore> entity(
                                                                       @NotNull String name,
                                                                       @NotNull EntityType.EntityFactory<T> factory,
                                                                       @NotNull MobCategory category) {
        return entity(this, name, factory, category);
    }

    @Override
    public <T extends Entity, P> ModEntityBuilder<T, P> entity(
                                                               @NotNull P parent,
                                                               @NotNull String name,
                                                               @NotNull EntityType.EntityFactory<T> factory,
                                                               @NotNull MobCategory category) {
        return ModEntityBuilder.create(this, parent, name, factory, category);
    }

    @Override
    protected <T extends BaseFlowingFluid, P> ModFluidBuilder<T, P> newFluidBuilder(
                                                                                    @NotNull P parent,
                                                                                    @NotNull String name,
                                                                                    @NotNull FluidBuilder.FluidFactory<T> fluidFactory) {
        return ModFluidBuilder.create(this, parent, name, fluidFactory);
    }
}
