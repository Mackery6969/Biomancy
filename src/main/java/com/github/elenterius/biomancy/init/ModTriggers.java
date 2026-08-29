package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.advancements.trigger.SacrificedItemTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModTriggers {

	public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES = DeferredRegister.create(Registries.TRIGGER_TYPE, BiomancyMod.MOD_ID);

	public static final DeferredHolder<CriterionTrigger<?>, SacrificedItemTrigger> SACRIFICED_ITEM_TRIGGER = TRIGGER_TYPES.register("sacrificed_item", SacrificedItemTrigger::new);

	private ModTriggers() {}

}
