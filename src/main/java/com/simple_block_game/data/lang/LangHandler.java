package com.simple_block_game.data.lang;

import static com.simple_block_game.SimpleBlockGame.REGISTRYLIB;

public class LangHandler {

    public static void addLang(String key, String cn, String en) {
        REGISTRYLIB.lang("en_us", key, en);
        REGISTRYLIB.lang("zh_cn", key, cn);
    }

    public static void init() {
        if (!REGISTRYLIB.doDatagen()) return;
        ItemTooltip.init();
        GameMessage.init();
    }
}
