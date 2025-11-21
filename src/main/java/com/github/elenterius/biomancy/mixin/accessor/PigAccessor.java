package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Pig.class)
public interface PigAccessor {

	@Accessor("FOOD_ITEMS")
	static @NonNull Ingredient biomancy$FOOD_ITEMS() {
		//noinspection DataFlowIssue
		return null;
	}

}
