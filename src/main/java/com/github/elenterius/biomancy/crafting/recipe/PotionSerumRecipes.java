package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.datagen.recipes.builder.RecipeCostUtil;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.mixin.accessor.PotionBrewingAccessor;
import com.github.elenterius.biomancy.serum.PotionSerum;
import com.github.elenterius.biomancy.util.ItemStackCounter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public final class PotionSerumRecipes {

	public static final List<BioBrewingRecipe> RECIPES;
	public static final Set<Potion> POTIONS;

	static {
		Set<Potion> bestPotions = getBestPotions();
		List<PotionRecipe> bestPotionRecipes = new ArrayList<>();
		Map<Potion, List<PotionRecipe>> recipeLookup = new HashMap<>();

		for (PotionBrewing.Mix<Potion> mix : PotionBrewingAccessor.biomancy$POTION_MIXES()) {
			PotionRecipe recipe = new PotionRecipe(mix.ingredient, mix.from.value(), mix.to.value());
			recipeLookup.computeIfAbsent(recipe.result, potion -> new ArrayList<>()).add(recipe);
			if (bestPotions.contains(recipe.result)) {
				bestPotionRecipes.add(recipe);
			}
		}

		for (IBrewingRecipe iBrewingRecipe : BrewingRecipeRegistry.getRecipes()) {
			if (iBrewingRecipe instanceof BrewingRecipe brewingRecipe) {
				Potion result = getPotion(brewingRecipe.getOutput());
				Ingredient ingredient = brewingRecipe.getIngredient();
				Set<Potion> possibleReactants = getPossiblePotions(brewingRecipe.getInput());
				for (Potion reactant : possibleReactants) {
					PotionRecipe recipe = new PotionRecipe(ingredient, reactant, result);
					recipeLookup.computeIfAbsent(recipe.result, potion -> new ArrayList<>()).add(recipe);
					if (bestPotions.contains(result)) {
						bestPotionRecipes.add(recipe);
					}
				}
			}
		}

		List<BioBrewingRecipe> recipes = new ArrayList<>();
		Set<Potion> potions = new HashSet<>();
		int index = 0;

		for (PotionRecipe bestPotionRecipe : bestPotionRecipes) {
			List<ResolvedRecipe> resolvedRecipes = resolveRecipes(bestPotionRecipe, recipeLookup);

			//TODO: remap (fermented) spider eye to decay additive?
			//TODO: remap glistening melon to healing additive?

			if (resolvedRecipes.size() <= 2) {
				for (ResolvedRecipe resolvedRecipe : resolvedRecipes) {
					recipes.add(createRecipe("potion_serum_" + index++, resolvedRecipe.ingredients, resolvedRecipe.reactant.getDefaultInstance(), bestPotionRecipe.result));
					potions.add(bestPotionRecipe.result);
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

			recipes.add(createRecipe("potion_serum_" + index++, ingredients, ModItems.UNSTABLE_COMPOUND.get().getDefaultInstance(), bestPotionRecipe.result));
			potions.add(bestPotionRecipe.result);
		}

		RECIPES = Collections.unmodifiableList(recipes);
		POTIONS = Collections.unmodifiableSet(potions);
	}

	private PotionSerumRecipes() {}

	public static @Nullable BioBrewingRecipe getRecipeFor(Level level, Container inputInventory) {
		BioBrewingRecipe topRecipe = null;
		int topPriority = Integer.MIN_VALUE;

		for (BioBrewingRecipe recipe : RECIPES) {
			if (!recipe.matches(inputInventory, level)) continue;

			int currentPriority = recipe.getMatchPriority();
			if (currentPriority > topPriority) {
				topRecipe = recipe;
				topPriority = currentPriority;
			}
		}

		return topRecipe;
	}

	private record PotionRecipe(Ingredient ingredient, Potion potionIngredient, Potion result) {}

	private record ResolvedRecipe(List<Ingredient> ingredients, Item reactant) {}

	private static List<ResolvedRecipe> resolveRecipes(PotionRecipe recipeRoot, Map<Potion, List<PotionRecipe>> recipeLookup) {
		List<ResolvedRecipe> accumulator = new ArrayList<>();
		resolveRecursive(recipeRoot, new ArrayList<>(), recipeLookup, accumulator::add);
		return accumulator;
	}

	private static void resolveRecursive(PotionRecipe current, List<Ingredient> currentIngredients, Map<Potion, List<PotionRecipe>> recipeLookup, Consumer<ResolvedRecipe> accumulator) {
		if (current.potionIngredient == Potions.WATER) {
			List<Ingredient> ingredients = new ArrayList<>(currentIngredients);
			ingredients.add(current.ingredient);
			accumulator.accept(new ResolvedRecipe(ingredients, ModItems.ORGANIC_COMPOUND.get()));
			return;
		}

		if (current.potionIngredient == Potions.MUNDANE) {
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

		if (current.potionIngredient == Potions.EMPTY || recipeLookup.getOrDefault(current.potionIngredient, List.of()).isEmpty()) {
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

	private static Set<Potion> getBestPotions() {
		Map<MobEffect, Map<Potion, MobEffectInstance>> primaryPotionEffects = new HashMap<>();
		for (Potion potion : ForgeRegistries.POTIONS.getValues()) {
			MobEffectInstance instance = PotionSerum.getPrimaryEffectInstance(potion.getEffects());
			if (instance == null) continue;
			primaryPotionEffects.computeIfAbsent(instance.getEffect(), k -> new HashMap<>()).put(potion, instance);
		}

		Set<Potion> bestPotions = new HashSet<>();

		for (Map<Potion, MobEffectInstance> candidates : primaryPotionEffects.values()) {
			MobEffectInstance instance = PotionSerum.getPrimaryEffectInstance(candidates.values());
			candidates.entrySet().stream()
					.filter(entry -> entry.getValue() == instance)
					.findAny()
					.map(Map.Entry::getKey)
					.ifPresent(bestPotions::add);
		}

		return bestPotions;
	}

	private static Potion getPotion(ItemStack stack) {
		if (stack.getItem() != Items.POTION) return Potions.EMPTY;
		return PotionUtils.getPotion(stack);
	}

	private static Set<Potion> getPossiblePotions(Ingredient ingredient) {
		Set<Potion> possiblePotions = new HashSet<>();

		for (ItemStack stack : ingredient.getItems()) {
			if (stack.getItem() != Items.POTION) continue;

			Potion potion = PotionUtils.getPotion(stack);
			if (potion != Potions.EMPTY) possiblePotions.add(potion);
		}

		return possiblePotions;
	}

	private static BioBrewingRecipe createRecipe(String name, List<Ingredient> ingredients, ItemStack reactant, Potion result) {
		ResourceLocation id = BiomancyMod.createRL(name);

		List<IngredientStack> ingredientStacks = new ArrayList<>();
		for (Ingredient ingredient : ingredients) {
			ingredientStacks.add(new IngredientStack(ingredient, 1));
		}

		int craftingTimeTicks = BioBrewingRecipe.DEFAULT_CRAFTING_TIME_TICKS + ingredients.size() * 20;
		int craftingCostNutrients = RecipeCostUtil.getCost(BioBrewingRecipe.DEFAULT_CRAFTING_COST_NUTRIENTS, craftingTimeTicks);

		return new BioBrewingRecipe(
				id,
				ModItems.POTION_SERUM.get().getInstanceFrom(result),
				craftingTimeTicks,
				craftingCostNutrients,
				ingredientStacks,
				Ingredient.of(reactant)
		);
	}

}
