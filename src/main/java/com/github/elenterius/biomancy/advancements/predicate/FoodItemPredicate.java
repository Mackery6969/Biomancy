package com.github.elenterius.biomancy.advancements.predicate;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public record FoodItemPredicate() implements ItemSubPredicate {

	public static final Codec<FoodItemPredicate> CODEC = Codec.unit(FoodItemPredicate::new);

	@Override
	public boolean matches(ItemStack stack) {
		return stack.get(DataComponents.FOOD) != null;
	}

}
