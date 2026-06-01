package com.simple_block_game.registry;

import com.simple_block_game.SimpleBlockGame;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import com.tterrag.registrate.Registrate;

public class SimpleBlockGameRegistration {

    public static final Registrate REGISTRATE = Registrate.create(SimpleBlockGame.MODID);

    static {
        SimpleBlockGameRegistration.REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    public static void init() {}
}
