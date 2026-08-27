package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.block.digester.DigesterBlockEntity;
import com.github.elenterius.biomancy.crafting.recipe.DigestingRecipe;
import com.github.elenterius.biomancy.crafting.recipe.FoodDigestingRecipe;
import com.github.elenterius.biomancy.crafting.recipe.StaticDigestingRecipe;
import com.github.elenterius.biomancy.init.ModRecipes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;

public final class DigestingRecipes {

	private DigestingRecipes() {}

	public static List<RecipeHolder<DigestingRecipe>> getRecipes(ClientLevel level) {
		List<RecipeHolder<DigestingRecipe>> allRecipes = level.getRecipeManager().getAllRecipesFor(ModRecipes.DIGESTING_RECIPE_TYPE.get());

		List<RecipeHolder<DigestingRecipe>> resolvedRecipes = new ArrayList<>();

		for (RecipeHolder<DigestingRecipe> recipeHolder : allRecipes) {
			DigestingRecipe recipe = recipeHolder.value();
			if (recipe instanceof FoodDigestingRecipe dynamicRecipe) {
				resolvedRecipes.addAll(convertToStaticRecipes(level, recipeHolder.id(), dynamicRecipe));
			}
			else {
				resolvedRecipes.add(recipeHolder);
			}
		}

		return resolvedRecipes;
	}

	private static List<RecipeHolder<DigestingRecipe>> convertToStaticRecipes(ClientLevel level, ResourceLocation recipeId, FoodDigestingRecipe dynamicRecipe) {
		List<RecipeHolder<DigestingRecipe>> staticRecipes = new ArrayList<>();

		ItemStackHandler itemStackHandler = new ItemStackHandler(DigesterBlockEntity.INPUT_SLOTS);
		RecipeWrapper inputInventory = new RecipeWrapper(itemStackHandler);

		for (ItemStack ingredientItem : dynamicRecipe.getIngredient().getItems()) {
			itemStackHandler.setStackInSlot(0, ingredientItem);

			ItemStack result = dynamicRecipe.assemble(inputInventory, level.registryAccess());
			int craftingTimeTicks = dynamicRecipe.getCraftingTimeTicks(inputInventory);
			int craftingCostNutrients = dynamicRecipe.getCraftingCostNutrients(inputInventory);
			Ingredient ingredient = Ingredient.of(ingredientItem);

			StaticDigestingRecipe recipe = new StaticDigestingRecipe(result, craftingTimeTicks, craftingCostNutrients, ingredient);

			String suffix = BuiltInRegistries.ITEM.getKey(ingredientItem.getItem()).toLanguageKey();
			staticRecipes.add(new RecipeHolder<>(recipeId.withSuffix("_jei_" + suffix), recipe));
		}

		return staticRecipes;
	}

}
