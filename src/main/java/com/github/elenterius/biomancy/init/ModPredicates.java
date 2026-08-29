package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.advancements.predicate.FoodItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModPredicates {

	public static final DeferredRegister<ItemSubPredicate.Type<?>> ITEM_SUB_PREDICATE_TYPES = DeferredRegister.create(BuiltInRegistries.ITEM_SUB_PREDICATE_TYPE, BiomancyMod.MOD_ID);

	public static final DeferredHolder<ItemSubPredicate.Type<?>, ItemSubPredicate.Type<FoodItemPredicate>> IS_FOOD_ITEM = ITEM_SUB_PREDICATE_TYPES.register("is_food_item", () -> new ItemSubPredicate.Type<>(FoodItemPredicate.CODEC));

	private ModPredicates() {}

}
