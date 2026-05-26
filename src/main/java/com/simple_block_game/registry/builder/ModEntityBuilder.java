package com.simple_block_game.registry.builder;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import com.gto.registrylib.RegistryCore;
import com.gto.registrylib.builders.EntityBuilder;
import com.simple_block_game.registry.GameRegistryCore;
import org.jetbrains.annotations.NotNull;

public class ModEntityBuilder<T extends Entity, P> extends EntityBuilder<T, P> {

    public static <T extends Entity, P> ModEntityBuilder<T, P> create(
                                                                      RegistryCore owner,
                                                                      P parent,
                                                                      String name,
                                                                      EntityType.EntityFactory<T> factory,
                                                                      MobCategory category) {
        var builder = new ModEntityBuilder<>(owner, parent, name, factory, category);
        return (ModEntityBuilder<T, P>) builder.defaultLang();
    }

    protected ModEntityBuilder(
                               RegistryCore core,
                               P parent,
                               String name,
                               EntityType.EntityFactory<T> factory,
                               MobCategory category) {
        super(core, parent, name, factory, category);
    }

    public ModEntityBuilder<T, P> langCn(@NotNull String name) {
        lang(GameRegistryCore.LANG_ZH_CN, name);
        return this;
    }
}
