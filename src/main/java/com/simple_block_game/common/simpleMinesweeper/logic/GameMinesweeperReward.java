package com.simple_block_game.common.simpleMinesweeper.logic;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import com.simple_block_game.SimpleBlockGame;
import it.unimi.dsi.fastutil.floats.Float2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectSortedMap;
import lombok.NonNull;

public class GameMinesweeperReward {

    public static final Float2ObjectRBTreeMap<Identifier> reward = new Float2ObjectRBTreeMap<>();

    static {
        reward.put(0.08f, SimpleBlockGame.parseRL("minecraft:chests/desert_pyramid"));
        reward.put(0.15f, SimpleBlockGame.parseRL("minecraft:chests/abandoned_mineshaft"));
        reward.put(0.22f, SimpleBlockGame.parseRL("minecraft:chests/pillager_outpost"));
        reward.put(0.29f, SimpleBlockGame.parseRL("minecraft:chests/woodland_mansion"));
        reward.put(0.35f, SimpleBlockGame.parseRL("minecraft:chests/bastion_treasure"));
        reward.put(0.40f, SimpleBlockGame.parseRL("minecraft:chests/end_city_treasure"));
    }

    public static void handleReward(@NonNull ServerLevel serverLevel, @NonNull Player player, float inputValue) {
        if (player.isDeadOrDying()) return;
        Float2ObjectSortedMap<Identifier> subMap = reward.headMap(inputValue);
        if (subMap.isEmpty()) return;
        float matchedThreshold = subMap.lastFloatKey();
        Identifier lootTableId = reward.get(matchedThreshold);
        if (lootTableId != null) {
            simulateLootBoxOpening(serverLevel, player, lootTableId);
        }
    }

    private static void simulateLootBoxOpening(@NonNull ServerLevel serverLevel, @NonNull Player player, @NonNull Identifier lootTableId) {
        if (player.isDeadOrDying()) return;
        LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootTableId));
        if (lootTable == LootTable.EMPTY) return;
        LootParams lootParams = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .create(LootContextParamSets.CHEST);
        lootTable.getRandomItems(lootParams, serverLevel.getRandom().nextLong(), itemStack -> {
            if (itemStack.isEmpty()) return;
            ItemEntity itemEntity = player.spawnAtLocation(serverLevel, itemStack);
            if (itemEntity != null) itemEntity.setNoPickUpDelay();
        });
        serverLevel.playSound(null, player.blockPosition(), SoundEvents.CHEST_OPEN, SoundSource.PLAYERS, 0.8F, 1.0F);
    }
}
