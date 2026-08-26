package com.github.elenterius.biomancy.crafting.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public abstract class SimpleRecipeType<T extends Recipe<RecipeInput>> implements RecipeType<T> {

	private final String identifier;

	public SimpleRecipeType(String identifier) {
		this.identifier = identifier;
	}

	public String getId() {
		return identifier;
	}

	@Override
	public String toString() {
		return identifier;
	}

	public static class AdvancedRecipeType<R extends Recipe<RecipeInput>> extends SimpleRecipeType<R> {

		public AdvancedRecipeType(String identifier) {
			super(identifier);
		}

		public Optional<R> getRecipeById(Level level, ResourceLocation id) {
			RecipeManager recipeManager = level.getRecipeManager();
			return recipeManager.byKey(id).map(this::castRecipeHolder).map(RecipeHolder::value);
		}

		public Optional<R> getFirstRecipeFor(Level level, RecipeInput inputInventory) {
			RecipeManager recipeManager = level.getRecipeManager();
			return recipeManager.getRecipeFor(this, inputInventory, level).map(RecipeHolder::value);
		}

		/**
		 * It is recommended to cache the returned recipe.
		 *
		 * @return recipe biased towards item-value ingredients
		 */
		public Optional<RecipeHolder<R>> getBestRecipeFor(Level level, RecipeInput inputInventory) {
			Collection<RecipeHolder<R>> recipes = level.getRecipeManager().getAllRecipesFor(this);

			RecipeHolder<R> topRecipe = null;
			int topPriority = Integer.MIN_VALUE;

			for (RecipeHolder<R> recipeHolder : recipes) {
				if (!recipeHolder.value().matches(inputInventory, level)) continue;

				int currentPriority = RecipeWithMatchPriority.getOrComputeMatchPriority(recipeHolder.value());
				if (currentPriority > topPriority) {
					topRecipe = recipeHolder;
					topPriority = currentPriority;
				}
			}

			return Optional.ofNullable(topRecipe);
		}

		private @Nullable RecipeHolder<R> castRecipeHolder(RecipeHolder<?> recipeHolder) {
			//noinspection unchecked
			return (RecipeHolder<R>) recipeHolder;
		}

		private boolean matches(R recipe, ItemStack stack) {
			for (Ingredient ingredient : recipe.getIngredients()) {
				if (ingredient.test(stack)) return true;
			}
			return false;
		}

		public Optional<R> getFirstRecipeForIngredient(Level level, ItemStack stack) {
			RecipeManager recipeManager = level.getRecipeManager();
			return recipeManager.getAllRecipesFor(this).stream()
					.map(RecipeHolder::value)
					.filter(recipe -> matches(recipe, stack))
					.findFirst();
		}

		/**
		 * It is recommended to cache the returned recipe.
		 *
		 * @return recipe biased towards item-value ingredients
		 */
		public Optional<R> getBestRecipeForIngredient(Level level, ItemStack stack) {
			Collection<RecipeHolder<R>> recipes = level.getRecipeManager().getAllRecipesFor(this);

			R topRecipe = null;
			int topPriority = Integer.MIN_VALUE;

			for (RecipeHolder<R> recipeHolder : recipes) {
				R recipe = recipeHolder.value();
				if (!matches(recipe, stack)) continue;

				int currentPriority = RecipeWithMatchPriority.getOrComputeMatchPriority(recipe);
				if (currentPriority > topPriority) {
					topRecipe = recipe;
					topPriority = currentPriority;
				}
			}

			return Optional.ofNullable(topRecipe);
		}

		public Optional<RecipeHolder<R>> getFirstRecipeForIngredient(Level level, ItemStack stack, @Nullable ResourceLocation lastRecipeId) {
			RecipeManager recipeManager = level.getRecipeManager();

			if (lastRecipeId != null) {
				Optional<RecipeHolder<R>> cached = recipeManager.byKey(lastRecipeId).map(this::castRecipeHolder);
				if (cached.isPresent() && matches(cached.get().value(), stack)) {
					return cached;
				}
			}

			return recipeManager.getAllRecipesFor(this).stream()
					.filter(recipeHolder -> matches(recipeHolder.value(), stack))
					.findFirst();
		}

		/**
		 * It is recommended to cache the returned recipe.
		 *
		 * @return recipe biased towards item-value ingredients
		 */
		public Optional<RecipeHolder<R>> getBestRecipeForIngredient(Level level, ItemStack stack, @Nullable ResourceLocation lastRecipeId) {
			RecipeManager recipeManager = level.getRecipeManager();

			if (lastRecipeId != null) {
				Optional<RecipeHolder<R>> cached = recipeManager.byKey(lastRecipeId).map(this::castRecipeHolder);
				if (cached.isPresent() && matches(cached.get().value(), stack)) {
					return cached;
				}
			}

			Collection<RecipeHolder<R>> recipes = recipeManager.getAllRecipesFor(this);

			RecipeHolder<R> topRecipe = null;
			int topPriority = Integer.MIN_VALUE;

			for (RecipeHolder<R> recipeHolder : recipes) {
				if (!matches(recipeHolder.value(), stack)) continue;

				int currentPriority = RecipeWithMatchPriority.getOrComputeMatchPriority(recipeHolder.value());
				if (currentPriority > topPriority) {
					topRecipe = recipeHolder;
					topPriority = currentPriority;
				}
			}

			return Optional.ofNullable(topRecipe);
		}

	}

}
