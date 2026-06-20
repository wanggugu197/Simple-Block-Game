package com.simple_block_game.data.lang;

import static com.simple_block_game.SimpleBlockGame.REGISTRY;

public class LangHandler {

    public static void addLang(String key, String cn, String en) {
        REGISTRY.lang(key, en);
        REGISTRY.lang("zh_cn", key, cn);
    }

    public static void init() {
        if (!REGISTRY.doDatagen()) return;
        ItemTooltip.init();
        GameMessage.init();
    }
}
