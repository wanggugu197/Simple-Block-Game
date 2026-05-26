package com.simple_block_game.registry.builder;

import net.minecraft.world.item.Item;

import com.gto.registrylib.RegistryCore;
import com.gto.registrylib.builders.ItemBuilder;
import com.simple_block_game.registry.GameRegistryCore;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ModItemBuilder<T extends Item, P> extends ItemBuilder<T, P> {

    public static <T extends Item, P> ModItemBuilder<T, P> create(
                                                                  RegistryCore owner,
                                                                  P parent,
                                                                  String name,
                                                                  Function<Item.Properties, T> factory,
                                                                  boolean isComponentItem) {
        var builder = new ModItemBuilder<>(owner, parent, name, factory, isComponentItem);
        return (ModItemBuilder<T, P>) builder.defaultLang();
    }

    protected ModItemBuilder(
                             RegistryCore owner,
                             P parent,
                             String name,
                             Function<Item.Properties, T> factory,
                             boolean isComponentItem) {
        super(owner, parent, name, factory, isComponentItem);
    }

    public ModItemBuilder<T, P> langCn(@NotNull String name) {
        lang(GameRegistryCore.LANG_ZH_CN, name);
        return this;
    }
}
