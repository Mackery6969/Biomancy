package com.github.elenterius.biomancy.crafting.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Recipe with dynamic cost, time and/or result
 */
public abstract non-sealed class DynamicProcessingRecipe implements ProcessingRecipe {

	private final RecipeType<?> type;

	protected DynamicProcessingRecipe(RecipeType<?> type) {
		this.type = type;
	}

	@Override
	public final boolean isSpecial() {
		return true;
	}

	@Override
	public final ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public final RecipeType<?> getType() {
		return type;
	}

}
