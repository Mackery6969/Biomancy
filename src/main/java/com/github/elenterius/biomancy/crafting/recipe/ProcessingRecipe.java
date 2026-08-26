package com.github.elenterius.biomancy.crafting.recipe;

import net.minecraft.world.item.crafting.RecipeInput;

public sealed interface ProcessingRecipe extends RecipeWithMatchPriority permits DigestingRecipe, DynamicProcessingRecipe, StaticProcessingRecipe {

	int getCraftingTimeTicks(RecipeInput inputInventory);

	int getCraftingCostNutrients(RecipeInput inputInventory);

}
