package com.simple_block_game;

import com.simple_block_game.common.CommonInit;
import com.simple_block_game.registry.GameRegistryCore;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * 简单方块游戏主类
 */
@Mod(SimpleBlockGame.MODID)
public class SimpleBlockGame {

    public static final String MODID = "simple_block_game";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final GameRegistryCore REGISTRY = GameRegistryCore.create(MODID);

    public SimpleBlockGame(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Simple Block Game Registration Init");
        modContainer.registerConfig(ModConfig.Type.COMMON, SimpleBlockGameConfig.SPEC, "Simple-Block-Game-Config.toml");
        CommonInit.init(modEventBus);
    }

    public static Identifier getId(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    public static Identifier getRL(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Identifier parseRL(String location) {
        String namespace = "minecraft";
        String path = location;
        int i = location.indexOf(':');
        if (i >= 0) {
            path = location.substring(i + 1);
            if (i >= 1) namespace = location.substring(0, i);
        }
        return getRL(namespace, path);
    }
}
