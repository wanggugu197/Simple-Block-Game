package com.simple_block_game;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SimpleBlockGameConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    static {
        initConfig();
        SPEC = BUILDER.build();
    }

    private static void initConfig() {
        BUILDER.push("Simple 2048 Settings");

        BUILDER.pop();
    }
}
