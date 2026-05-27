package com.simple_block_game.common.base.reward;

import com.simple_block_game.SimpleBlockGame;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Objects;

public abstract class BaseGameReward {

    protected static Identifier id(String path) {
        return SimpleBlockGame.parseRL(path);
    }

    protected static void dropLoot(ServerLevel level, Player player, Identifier tableId) {
        if (level == null || player == null || tableId == null) return;
        if (player.isDeadOrDying()) return;

        LootTable table = level.getServer().reloadableRegistries()
                .getLootTable(ResourceKey.create(Registries.LOOT_TABLE, tableId));
        if (table == LootTable.EMPTY) return;

        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .create(LootContextParamSets.CHEST);

        table.getRandomItems(params, level.getRandom().nextLong(), stack -> {
            if (!stack.isEmpty()) {
                Objects.requireNonNull(player.spawnAtLocation(level, stack)).setNoPickUpDelay();
            }
        });
        level.playSound(null, player.blockPosition(), SoundEvents.CHEST_OPEN, SoundSource.PLAYERS, 0.8F, 1.0F);
    }
}
