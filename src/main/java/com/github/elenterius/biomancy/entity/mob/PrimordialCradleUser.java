package com.github.elenterius.biomancy.entity.mob;

import com.github.elenterius.biomancy.entity.mob.ai.goal.FindItemGoal;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Set;
import java.util.function.Predicate;

public interface PrimordialCradleUser {

	Set<Item> SPECIAL_ITEMS_TO_HOLD = Set.of(ModItems.LIVING_FLESH.get(), ModItems.CREATOR_MIX.get(), Items.ROTTEN_FLESH, Items.SPIDER_EYE);
	Predicate<ItemEntity> SPECIAL_ITEM_ENTITY_FILTER = itemEntity -> {
		if (!FindItemGoal.ITEM_ENTITY_FILTER.test(itemEntity)) return false;

		ItemStack stack = itemEntity.getItem();
		if (SPECIAL_ITEMS_TO_HOLD.contains(stack.getItem())) return true;
		return stack.getFoodProperties(null) != null && (stack.is(ModItemTags.FRESH_RAW_MEATS) || stack.is(ModItemTags.COOKED_MEATS));
	};

	ItemStack getTributeItemForCradle();

	boolean hasTributeForCradle();

}
