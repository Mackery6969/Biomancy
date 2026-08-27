package com.github.elenterius.biomancy.datagen.recipes.builder;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.crafting.recipe.BioBrewingRecipe;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BioBrewingRecipeBuilder implements RecipeBuilder<BioBrewingRecipeBuilder> {

	public static final String RECIPE_SUB_FOLDER = ModRecipes.BIO_BREWING_RECIPE_TYPE.getId().getPath();

	private final ResourceLocation recipeId;
	private final List<ICondition> conditions = new ArrayList<>();
	private final ItemData result;
	private final List<IngredientStack> ingredients = new ArrayList<>();
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	private Ingredient reactant = Ingredient.of(ModItems.VIAL.get());
	private int craftingTimeTicks = -1;
	private int craftingCostNutrients = -1;

	private BioBrewingRecipeBuilder(ResourceLocation recipeId, ItemData result) {
		this.recipeId = ResourceLocation.fromNamespaceAndPath(recipeId.getNamespace(), RECIPE_SUB_FOLDER + "/" + recipeId.getPath());
		this.result = result;
	}

	public static BioBrewingRecipeBuilder create(ResourceLocation recipeId, ItemData result) {
		return new BioBrewingRecipeBuilder(recipeId, result);
	}

	public static BioBrewingRecipeBuilder create(String modId, String outputName, ItemData result) {
		ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(modId, outputName);
		return new BioBrewingRecipeBuilder(rl, result);
	}

	public static BioBrewingRecipeBuilder create(String outputName, ItemData result) {
		ResourceLocation rl = BiomancyMod.rl(outputName);
		return new BioBrewingRecipeBuilder(rl, result);
	}

	public static BioBrewingRecipeBuilder create(ItemData result) {
		ResourceLocation rl = BiomancyMod.rl(result.getItemPath());
		return new BioBrewingRecipeBuilder(rl, result);
	}

	public static BioBrewingRecipeBuilder create(ItemStack stack) {
		return create(new ItemData(stack));
	}

	public static BioBrewingRecipeBuilder create(ItemLike item) {
		return create(new ItemData(item));
	}

	public static BioBrewingRecipeBuilder create(ItemLike item, int count) {
		return create(new ItemData(item, count));
	}

	public BioBrewingRecipeBuilder ifModLoaded(String modId) {
		return withCondition(new ModLoadedCondition(modId));
	}

	public BioBrewingRecipeBuilder ifModMissing(String modId) {
		return withCondition(new NotCondition(new ModLoadedCondition(modId)));
	}

	public BioBrewingRecipeBuilder withCondition(ICondition condition) {
		conditions.add(condition);
		return this;
	}

	public BioBrewingRecipeBuilder setCraftingTime(int timeTicks) {
		if (timeTicks < 0) throw new IllegalArgumentException("Invalid crafting time: " + timeTicks);
		craftingTimeTicks = timeTicks;
		return this;
	}

	public BioBrewingRecipeBuilder setCraftingCost(int costNutrients) {
		if (costNutrients < 0) throw new IllegalArgumentException("Invalid crafting cost: " + costNutrients);
		craftingCostNutrients = costNutrients;
		return this;
	}

	public BioBrewingRecipeBuilder setReactant(ItemLike item) {
		return setReactant(Ingredient.of(item));
	}

	public BioBrewingRecipeBuilder setReactant(TagKey<Item> tag) {
		return setReactant(Ingredient.of(tag));
	}

	public BioBrewingRecipeBuilder setReactant(ItemStack stack) {
		return setReactant(Ingredient.of(stack));
	}

	public BioBrewingRecipeBuilder setReactant(Ingredient ingredient) {
		reactant = ingredient;
		return this;
	}

	public BioBrewingRecipeBuilder addIngredient(TagKey<Item> tag) {
		return addIngredient(Ingredient.of(tag));
	}

	public BioBrewingRecipeBuilder addIngredient(TagKey<Item> tag, int quantity) {
		return addIngredient(Ingredient.of(tag), quantity);
	}

	public BioBrewingRecipeBuilder addIngredient(ItemLike item) {
		return addIngredient(item, 1);
	}

	public BioBrewingRecipeBuilder addIngredient(ItemStack stack) {
		return addIngredient(Ingredient.of(stack), 1);
	}

	public BioBrewingRecipeBuilder addIngredient(Ingredient ingredient) {
		return addIngredient(ingredient, 1);
	}

	public BioBrewingRecipeBuilder addIngredient(ItemLike item, int quantity) {
		addIngredient(Ingredient.of(item), quantity);
		return this;
	}

	public BioBrewingRecipeBuilder addIngredient(Ingredient ingredient, int quantity) {
		ingredients.add(new IngredientStack(ingredient, quantity));
		return this;
	}

	@Override
	public BioBrewingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
		criteria.put(name, criterion);
		return this;
	}

	@Override
	public void save(RecipeOutput recipeOutput, @Nullable RecipeCategory category) {
		validateCriteria();

		if (craftingTimeTicks < 0) {
			craftingTimeTicks = BioBrewingRecipe.DEFAULT_CRAFTING_TIME_TICKS;
		}

		if (craftingCostNutrients < 0) {
			craftingCostNutrients = RecipeCostUtil.getCost(BioBrewingRecipe.DEFAULT_CRAFTING_COST_NUTRIENTS, craftingTimeTicks);
		}

		BioBrewingRecipe recipe = new BioBrewingRecipe(result.toItemStack(), craftingTimeTicks, craftingCostNutrients, ingredients, reactant);

		Advancement.Builder advancementBuilder = recipeOutput.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
		criteria.forEach(advancementBuilder::addCriterion);

		String folderName = RecipeBuilder.getRecipeFolderName(category, BiomancyMod.MOD_ID);
		AdvancementHolder advancementHolder = advancementBuilder.build(recipeId.withPrefix("recipes/" + folderName + "/"));

		recipeOutput.accept(recipeId, recipe, advancementHolder, conditions.toArray(ICondition[]::new));
	}

	private void validateCriteria() {
		if (criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe %s because Criteria are empty.".formatted(recipeId));
		}
	}

}
