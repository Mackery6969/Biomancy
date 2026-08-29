package com.github.elenterius.biomancy.datagen.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

	private final DecomposingRecipeProvider decomposingRecipeProvider;
	private final DigestingRecipeProvider digestingRecipeProvider;
	private final BioForgingRecipeProvider bioforgingRecipeProvider;
	private final BioBrewingRecipeProvider biobrewingRecipeProvider;
	private final VanillaRecipeProvider vanillaRecipeProvider;

	public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
		decomposingRecipeProvider = new DecomposingRecipeProvider(output, registries);
		digestingRecipeProvider = new DigestingRecipeProvider(output, registries);
		bioforgingRecipeProvider = new BioForgingRecipeProvider(output, registries);
		biobrewingRecipeProvider = new BioBrewingRecipeProvider(output, registries);
		vanillaRecipeProvider = new VanillaRecipeProvider(output, registries);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {
		decomposingRecipeProvider.buildRecipes(recipeOutput);
		digestingRecipeProvider.buildRecipes(recipeOutput);
		bioforgingRecipeProvider.buildRecipes(recipeOutput);
		biobrewingRecipeProvider.buildRecipes(recipeOutput);
		vanillaRecipeProvider.buildRecipes(recipeOutput);
	}

}
