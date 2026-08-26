package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.datagen.recipes.builder.RecipeCostUtil;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.mixin.accessor.PotionBrewingAccessor;
import com.github.elenterius.biomancy.serum.PotionSerum;
import com.github.elenterius.biomancy.util.ItemStackCounter;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class PotionSerumRecipes {

	public static final String NAME_PREFIX = "potion_serum_";

	private static @Nullable List<RecipeHolder<BioBrewingRecipe>> recipes;
	private static @Nullable Map<ResourceLocation, RecipeHolder<BioBrewingRecipe>> recipesById;
	private static @Nullable Set<Holder<Potion>> potions;

	private PotionSerumRecipes() {}

	public static List<RecipeHolder<BioBrewingRecipe>> getRecipes(Level level) {
		computeIfAbsent(level);
		return recipes;
	}

	public static Set<Holder<Potion>> getPotions(Level level) {
		computeIfAbsent(level);
		return potions;
	}

	public static Optional<RecipeHolder<BioBrewingRecipe>> byId(@Nullable ResourceLocation recipeId, Level level) {
		if (recipeId == null) return Optional.empty();
		computeIfAbsent(level);
		return Optional.ofNullable(recipesById.get(recipeId));
	}

	public static @Nullable RecipeHolder<BioBrewingRecipe> getRecipeFor(Level level, RecipeInput inputInventory) {
		computeIfAbsent(level);

		RecipeHolder<BioBrewingRecipe> topRecipe = null;
		int topPriority = Integer.MIN_VALUE;

		for (RecipeHolder<BioBrewingRecipe> recipeHolder : recipes) {
			BioBrewingRecipe recipe = recipeHolder.value();
			if (!recipe.matches(inputInventory, level)) continue;

			int currentPriority = recipe.getMatchPriority();
			if (currentPriority > topPriority) {
				topRecipe = recipeHolder;
				topPriority = currentPriority;
			}
		}

		return topRecipe;
	}

	private static synchronized void computeIfAbsent(Level level) {
		if (recipes != null) return;

		PotionBrewing potionBrewing = level.potionBrewing();

		Set<Holder<Potion>> bestPotions = getBestPotions();
		List<PotionRecipe> bestPotionRecipes = new ArrayList<>();
		Map<Holder<Potion>, List<PotionRecipe>> recipeLookup = new HashMap<>();

		for (PotionBrewing.Mix<Potion> mix : ((PotionBrewingAccessor) potionBrewing).biomancy$potionMixes()) {
			PotionRecipe recipe = new PotionRecipe(mix.ingredient(), mix.from(), mix.to());
			recipeLookup.computeIfAbsent(recipe.result, potion -> new ArrayList<>()).add(recipe);
			if (bestPotions.contains(recipe.result)) {
				bestPotionRecipes.add(recipe);
			}
		}

		for (IBrewingRecipe iBrewingRecipe : potionBrewing.getRecipes()) {
			if (iBrewingRecipe instanceof BrewingRecipe brewingRecipe) {
				Holder<Potion> result = getPotion(brewingRecipe.getOutput()).orElse(Potions.WATER);
				Ingredient ingredient = brewingRecipe.getIngredient();
				Set<Holder<Potion>> possibleReactants = getPossiblePotions(brewingRecipe.getInput());
				for (Holder<Potion> reactant : possibleReactants) {
					PotionRecipe recipe = new PotionRecipe(ingredient, reactant, result);
					recipeLookup.computeIfAbsent(recipe.result, potion -> new ArrayList<>()).add(recipe);
					if (bestPotions.contains(result)) {
						bestPotionRecipes.add(recipe);
					}
				}
			}
		}

		List<RecipeHolder<BioBrewingRecipe>> newRecipes = new ArrayList<>();
		Set<Holder<Potion>> newPotions = new HashSet<>();
		int index = 0;

		for (PotionRecipe bestPotionRecipe : bestPotionRecipes) {
			List<ResolvedRecipe> resolvedRecipes = resolveRecipes(bestPotionRecipe, recipeLookup);

			//TODO: remap (fermented) spider eye to decay additive?
			//TODO: remap glistening melon to healing additive?

			if (resolvedRecipes.size() <= 2) {
				for (ResolvedRecipe resolvedRecipe : resolvedRecipes) {
					newRecipes.add(createRecipe(NAME_PREFIX + index++, resolvedRecipe.ingredients, resolvedRecipe.reactant.getDefaultInstance(), bestPotionRecipe.result));
					newPotions.add(bestPotionRecipe.result);
				}
				continue;
			}

			//REDUCE TOO MANY RECIPE VARIATIONS DOWN TO ONE

			ItemStackCounter itemCounter = new ItemStackCounter();
			for (ResolvedRecipe resolvedRecipe : resolvedRecipes) {
				for (Ingredient ingredient : resolvedRecipe.ingredients) {
					itemCounter.accountStacks(ingredient.getItems());
				}
			}

			List<Ingredient> ingredients = itemCounter.getItemsSorted(4, false).stream().map(Ingredient::of).toList();

			newRecipes.add(createRecipe(NAME_PREFIX + index++, ingredients, ModItems.UNSTABLE_COMPOUND.get().getDefaultInstance(), bestPotionRecipe.result));
			newPotions.add(bestPotionRecipe.result);
		}

		recipes = Collections.unmodifiableList(newRecipes);
		recipesById = recipes.stream().collect(Collectors.toUnmodifiableMap(RecipeHolder::id, recipeHolder -> recipeHolder, (a, b) -> b));
		potions = Collections.unmodifiableSet(newPotions);
	}

	private record PotionRecipe(Ingredient ingredient, Holder<Potion> potionIngredient, Holder<Potion> result) {}

	private record ResolvedRecipe(List<Ingredient> ingredients, Item reactant) {}

	private static List<ResolvedRecipe> resolveRecipes(PotionRecipe recipeRoot, Map<Holder<Potion>, List<PotionRecipe>> recipeLookup) {
		List<ResolvedRecipe> accumulator = new ArrayList<>();
		resolveRecursive(recipeRoot, new ArrayList<>(), recipeLookup, accumulator::add);
		return accumulator;
	}

	private static void resolveRecursive(PotionRecipe current, List<Ingredient> currentIngredients, Map<Holder<Potion>, List<PotionRecipe>> recipeLookup, Consumer<ResolvedRecipe> accumulator) {
		if (current.potionIngredient.is(Potions.WATER)) {
			List<Ingredient> ingredients = new ArrayList<>(currentIngredients);
			ingredients.add(current.ingredient);
			accumulator.accept(new ResolvedRecipe(ingredients, ModItems.ORGANIC_COMPOUND.get()));
			return;
		}

		if (current.potionIngredient.is(Potions.MUNDANE)) {
			List<Ingredient> ingredients = new ArrayList<>(currentIngredients);
			ingredients.add(current.ingredient);
			if (ingredients.size() < 4) {
				ingredients.add(Ingredient.of(ModItems.BILE.get()));
				accumulator.accept(new ResolvedRecipe(ingredients, ModItems.VIAL.get()));
			}
			else {
				accumulator.accept(new ResolvedRecipe(ingredients, ModItems.ORGANIC_COMPOUND.get()));
			}

			return;
		}

		if (recipeLookup.getOrDefault(current.potionIngredient, List.of()).isEmpty()) {
			List<Ingredient> ingredients = new ArrayList<>(currentIngredients);
			ingredients.add(current.ingredient);
			accumulator.accept(new ResolvedRecipe(ingredients, ModItems.EXOTIC_COMPOUND.get()));
			return;
		}

		if (currentIngredients.size() >= 4) {
			return; //TODO: handle this in a better way by replacing many cheap items with one expensive item
		}

		List<PotionRecipe> subRecipes = recipeLookup.get(current.potionIngredient);
		for (PotionRecipe subRecipe : subRecipes) {
			List<Ingredient> ingredients = new ArrayList<>(currentIngredients);
			ingredients.add(current.ingredient);
			resolveRecursive(subRecipe, ingredients, recipeLookup, accumulator);
		}
	}

	private static Set<Holder<Potion>> getBestPotions() {
		Map<Holder<MobEffect>, Map<Holder<Potion>, MobEffectInstance>> primaryPotionEffects = new HashMap<>();
		for (Holder.Reference<Potion> potionHolder : BuiltInRegistries.POTION.holders().toList()) {
			MobEffectInstance instance = PotionSerum.getPrimaryEffectInstance(potionHolder.value().getEffects());
			if (instance == null) continue;
			primaryPotionEffects.computeIfAbsent(instance.getEffect(), k -> new HashMap<>()).put(potionHolder, instance);
		}

		Set<Holder<Potion>> bestPotions = new HashSet<>();

		for (Map<Holder<Potion>, MobEffectInstance> candidates : primaryPotionEffects.values()) {
			MobEffectInstance instance = PotionSerum.getPrimaryEffectInstance(candidates.values());
			candidates.entrySet().stream()
					.filter(entry -> entry.getValue() == instance)
					.findAny()
					.map(Map.Entry::getKey)
					.ifPresent(bestPotions::add);
		}

		return bestPotions;
	}

	private static Optional<Holder<Potion>> getPotion(ItemStack stack) {
		if (stack.getItem() != Items.POTION) return Optional.empty();
		PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
		return contents != null ? contents.potion() : Optional.empty();
	}

	private static Set<Holder<Potion>> getPossiblePotions(Ingredient ingredient) {
		Set<Holder<Potion>> possiblePotions = new HashSet<>();

		for (ItemStack stack : ingredient.getItems()) {
			if (stack.getItem() != Items.POTION) continue;
			getPotion(stack).ifPresent(possiblePotions::add);
		}

		return possiblePotions;
	}

	private static RecipeHolder<BioBrewingRecipe> createRecipe(String name, List<Ingredient> ingredients, ItemStack reactant, Holder<Potion> result) {
		ResourceLocation id = BiomancyMod.rl(name);

		List<IngredientStack> ingredientStacks = new ArrayList<>();
		for (Ingredient ingredient : ingredients) {
			ingredientStacks.add(new IngredientStack(ingredient, 1));
		}

		int craftingTimeTicks = BioBrewingRecipe.DEFAULT_CRAFTING_TIME_TICKS + ingredients.size() * 20;
		int craftingCostNutrients = RecipeCostUtil.getCost(BioBrewingRecipe.DEFAULT_CRAFTING_COST_NUTRIENTS, craftingTimeTicks);

		BioBrewingRecipe recipe = new BioBrewingRecipe(
				ModItems.POTION_SERUM.get().getInstanceFrom(result),
				craftingTimeTicks,
				craftingCostNutrients,
				ingredientStacks,
				Ingredient.of(reactant)
		);

		return new RecipeHolder<>(id, recipe);
	}

}
