package com.github.elenterius.biomancy.api.livingtool;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface SimpleLivingTool extends LivingTool {

	@Override
	default void onNutrientsChanged(ItemStack livingTool, int oldValue, int newValue) {}

	@Override
	default int getLivingToolActionCost(ItemStack livingTool, ItemAbility itemAbility) {
		return 1;
	}

}
