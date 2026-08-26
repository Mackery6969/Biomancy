package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.advancements.trigger.SacrificedItemTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ModTriggers {

	public static final SacrificedItemTrigger SACRIFICED_ITEM_TRIGGER = new SacrificedItemTrigger();

	private ModTriggers() {}

	public static void register() {
		Registry.register(BuiltInRegistries.TRIGGER_TYPES, BiomancyMod.rl("sacrificed_item"), SACRIFICED_ITEM_TRIGGER);
	}

}
