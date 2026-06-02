package com.simple_block_game.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;

import java.util.List;
import java.util.Optional;

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
    public static final Criterion<InventoryChangeTrigger.TriggerInstance> UNCONDITIONAL_CRITERION = CriteriaTriggers.INVENTORY_CHANGED
            .createCriterion(UNCONDITIONAL_TRIGGER_INSTANCE);
}
