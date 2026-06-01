package com.simple_block_game.datagen;

import com.simple_block_game.data.lang.LangHandler;
import com.simple_block_game.data.lang.lang.SimplifiedChineseLanguageProvider;
import com.simple_block_game.data.lang.lang.TraditionalChineseLanguageProvider;

import com.tterrag.registrate.providers.ProviderType;

import static com.simple_block_game.SimpleBlockGame.isDataGen;
import static com.simple_block_game.registry.SimpleBlockGameRegistration.REGISTRATE;

public final class Datagen {

    public static void init() {
        if (isDataGen()) {
            // REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, ResourceFarmBlockTags::init);
            // REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, ResourceFarmItemTags::init);
            REGISTRATE.addDataGenerator(ProviderType.LANG, LangHandler::enInitialize);
            REGISTRATE.addDataGenerator(SimplifiedChineseLanguageProvider.LANG, LangHandler::cnInitialize);
            REGISTRATE.addDataGenerator(TraditionalChineseLanguageProvider.LANG, LangHandler::twInitialize);
        }
    }
}
