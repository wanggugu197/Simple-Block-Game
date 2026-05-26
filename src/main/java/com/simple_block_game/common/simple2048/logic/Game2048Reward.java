package com.simple_block_game.common.simple2048.logic;

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
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.NonNull;

public class Game2048Reward {

    public static final Int2ObjectOpenHashMap<Identifier> ScoreReward = new Int2ObjectOpenHashMap<>();
    public static final Int2ObjectOpenHashMap<Identifier> MaxNumberReward = new Int2ObjectOpenHashMap<>();

    static {
        ScoreReward.put(500, SimpleBlockGame.parseRL("minecraft:chests/igloo_chest"));
        ScoreReward.put(1000, SimpleBlockGame.parseRL("minecraft:chests/shipwreck_treasure"));
        ScoreReward.put(1500, SimpleBlockGame.parseRL("minecraft:chests/underwater_ruin_big"));
        ScoreReward.put(2000, SimpleBlockGame.parseRL("minecraft:chests/desert_pyramid"));
        ScoreReward.put(3000, SimpleBlockGame.parseRL("minecraft:chests/abandoned_mineshaft"));
        ScoreReward.put(4000, SimpleBlockGame.parseRL("minecraft:chests/jungle_temple"));
        ScoreReward.put(5000, SimpleBlockGame.parseRL("minecraft:chests/pillager_outpost"));
        ScoreReward.put(7500, SimpleBlockGame.parseRL("minecraft:chests/stronghold_library"));
        ScoreReward.put(10000, SimpleBlockGame.parseRL("minecraft:chests/bastion_other"));
        ScoreReward.put(15000, SimpleBlockGame.parseRL("minecraft:chests/bastion_treasure"));
        ScoreReward.put(20000, SimpleBlockGame.parseRL("minecraft:chests/woodland_mansion"));
        ScoreReward.put(25000, SimpleBlockGame.parseRL("minecraft:chests/ancient_city_ice_box"));
        ScoreReward.put(30000, SimpleBlockGame.parseRL("minecraft:chests/ancient_city"));
        ScoreReward.put(50000, SimpleBlockGame.parseRL("minecraft:chests/end_city_treasure"));

        MaxNumberReward.put(1024, SimpleBlockGame.parseRL("minecraft:chests/simple_dungeon"));
        MaxNumberReward.put(2048, SimpleBlockGame.parseRL("minecraft:chests/village/village_weaponsmith"));
        MaxNumberReward.put(4096, SimpleBlockGame.parseRL("minecraft:chests/woodland_mansion"));
        MaxNumberReward.put(8192, SimpleBlockGame.parseRL("minecraft:chests/ancient_city"));
        MaxNumberReward.put(16384, SimpleBlockGame.parseRL("minecraft:chests/bastion_treasure"));
        MaxNumberReward.put(32768, SimpleBlockGame.parseRL("minecraft:chests/buried_treasure"));
        MaxNumberReward.put(65536, SimpleBlockGame.parseRL("minecraft:chests/end_city_treasure"));
    }

    public static void handleScoreReward(
                                         @NonNull ServerLevel serverLevel,
                                         @NonNull Player player,
                                         int originalScore,
                                         int newScore) {
        if (originalScore >= newScore) return;
        for (int scoreThreshold : ScoreReward.keySet()) {
            if (scoreThreshold > originalScore && scoreThreshold <= newScore) {
                Identifier lootTableId = ScoreReward.get(scoreThreshold);
                if (lootTableId != null) {
                    simulateLootBoxOpening(serverLevel, player, lootTableId);
                }
            }
        }
    }

    public static void handleMaxNumberReward(
                                             @NonNull ServerLevel serverLevel,
                                             @NonNull Player player,
                                             int originalMax,
                                             int newMax) {
        if (originalMax >= newMax) return;
        for (int numberThreshold : MaxNumberReward.keySet()) {
            if (numberThreshold > originalMax && numberThreshold <= newMax) {
                Identifier lootTableId = MaxNumberReward.get(numberThreshold);
                if (lootTableId != null) {
                    simulateLootBoxOpening(serverLevel, player, lootTableId);
                }
            }
        }
    }

    private static void simulateLootBoxOpening(
                                               @NonNull ServerLevel serverLevel,
                                               @NonNull Player player,
                                               @NonNull Identifier lootTableId) {
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
