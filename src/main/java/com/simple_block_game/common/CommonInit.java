package com.simple_block_game.common;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

import com.simple_block_game.data.lang.LangHandler;

public class CommonInit {

    private static IEventBus modBus;

    public static void init(IEventBus modBus) {
        CommonInit.modBus = modBus;
        SimpleBlockGameRegistration.init();
        SimpleBlockGameRecipe.init();
        LangHandler.init();
        modBus.addListener(CommonInit::commonSetup);
        modBus.addListener(CommonInit::modConstruct);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {}

    private static void modConstruct(FMLConstructModEvent event) {}
}
