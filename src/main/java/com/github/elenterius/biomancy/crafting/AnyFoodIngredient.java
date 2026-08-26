package com.github.elenterius.biomancy.crafting;

import com.github.elenterius.biomancy.init.ModIngredientTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.function.Predicate;
import java.util.stream.Stream;

public record AnyFoodIngredient() implements ICustomIngredient {

	public static final MapCodec<AnyFoodIngredient> CODEC = MapCodec.unit(AnyFoodIngredient::new);

	private static final Predicate<FoodProperties> NUTRITION_PREDICATE = foodProperties -> foodProperties != null && foodProperties.nutrition() > 0;

	@Override
	public boolean test(ItemStack stack) {
		if (stack.isEmpty()) return false;
		return NUTRITION_PREDICATE.test(stack.getFoodProperties(null));
	}

	@Override
	public Stream<ItemStack> getItems() {
		return BuiltInRegistries.ITEM.stream()
				.map(ItemStack::new)
				.filter(stack -> NUTRITION_PREDICATE.test(stack.getFoodProperties(null)));
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IngredientType<?> getType() {
		return ModIngredientTypes.ANY_FOOD.get();
	}

}
