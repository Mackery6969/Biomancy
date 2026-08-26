package com.github.elenterius.biomancy.crafting.recipe;

import net.minecraft.world.item.crafting.RecipeInput;

public abstract non-sealed class StaticProcessingRecipe implements ProcessingRecipe {

	protected final int craftingTimeTicks;
	protected final int craftingCostNutrients;

	protected StaticProcessingRecipe(int craftingTimeTicks, int craftingCostNutrients) {
		this.craftingTimeTicks = craftingTimeTicks;
		this.craftingCostNutrients = craftingCostNutrients;
	}

	@Override
	public final int getCraftingTimeTicks(RecipeInput inputInventory) {
		return craftingTimeTicks;
	}

	@Override
	public final int getCraftingCostNutrients(RecipeInput inputInventory) {
		return craftingCostNutrients;
	}

}
