package com.simple_block_game.util.multiVersion;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class MultiVersionHelper {

    public static void sendPlayerMessage(Player player, Component component, boolean override) {
        player.displayClientMessage(component, override);
    }
}
