package com.simple_block_game.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

import static com.simple_block_game.SimpleBlockGame.REGISTRYLIB;
import static com.simple_block_game.common.SimpleBlockGameRegistration.*;

public class SimpleBlockGameRecipe {

    public static void init() {}

    /**
     * 无条件解锁的TriggerInstance
     */
    private static final InventoryChangeTrigger.TriggerInstance UNCONDITIONAL_TRIGGER_INSTANCE = new InventoryChangeTrigger.TriggerInstance(Optional.empty(),
            InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of());

    /**
     * 封装无条件解锁的Criterion
     */
    private static final Criterion<InventoryChangeTrigger.TriggerInstance> UNCONDITIONAL_CRITERION = CriteriaTriggers.INVENTORY_CHANGED
            .createCriterion(UNCONDITIONAL_TRIGGER_INSTANCE);

    static {
        REGISTRYLIB.addRecipeData(prov -> {
            prov.shaped(RecipeCategory.COMBAT, BLOCK_2048_CORE)
                    .pattern("QQQ")
                    .pattern("QIQ")
                    .pattern("QQQ")
                    .define('Q', Items.QUARTZ_PILLAR)
                    .define('I', Items.IRON_BLOCK)
                    .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                    .save(prov, "make_2048_core");
            prov.shaped(RecipeCategory.COMBAT, BLOCK_MINESWEEPER_CORE)
                    .pattern("QQQ")
                    .pattern("QIQ")
                    .pattern("QQQ")
                    .define('Q', Items.QUARTZ_PILLAR)
                    .define('I', Items.TNT)
                    .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                    .save(prov, "make_minesweeper_core");
            prov.shaped(RecipeCategory.COMBAT, BLOCK_MEMORY_KEY_CORE)
                    .pattern("QQQ")
                    .pattern("QIQ")
                    .pattern("QQQ")
                    .define('Q', Items.QUARTZ_PILLAR)
                    .define('I', Items.REDSTONE_BLOCK)
                    .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
                    .save(prov, "make_memory_key_core");
        });
    }
}
