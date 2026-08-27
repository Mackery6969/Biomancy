package com.github.elenterius.biomancy.datagen.recipes.builder;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.recipe.StaticDigestingRecipe;
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
import net.minecraft.util.Mth;
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

public final class DigestingRecipeBuilder implements RecipeBuilder<DigestingRecipeBuilder> {

	public static final String RECIPE_SUB_FOLDER = ModRecipes.DIGESTING_RECIPE_TYPE.getId().getPath();

	public static final short DEFAULT_CRAFTING_COST_NUTRIENTS = 2;

	private final ResourceLocation recipeId;
	private final List<ICondition> conditions = new ArrayList<>();
	private final ItemData recipeResult;
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	private @Nullable Ingredient recipeIngredient;
	private int craftingTimeTicks = -1;
	private int craftingTimeModifier = 0;
	private int craftingCostNutrients = -1;
	private int craftingCostModifier = 0;

	private DigestingRecipeBuilder(ResourceLocation recipeId, ItemData result) {
		this.recipeId = ResourceLocation.fromNamespaceAndPath(recipeId.getNamespace(), RECIPE_SUB_FOLDER + "/" + recipeId.getPath());
		recipeResult = result;
	}

	public static DigestingRecipeBuilder create(ResourceLocation recipeId, ItemData result) {
		return new DigestingRecipeBuilder(recipeId, result);
	}

	public static DigestingRecipeBuilder create(String modId, String outputName, ItemData result) {
		ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(modId, outputName + "_from_" + result.getItemPath());
		return new DigestingRecipeBuilder(rl, result);
	}

	public static DigestingRecipeBuilder create(String outputName, ItemData result) {
		ResourceLocation rl = BiomancyMod.rl(outputName + "_from_" + result.getItemPath());
		return new DigestingRecipeBuilder(rl, result);
	}

	public static DigestingRecipeBuilder create(ItemData result) {
		ResourceLocation rl = BiomancyMod.rl(result.getItemPath());
		return new DigestingRecipeBuilder(rl, result);
	}

	public static DigestingRecipeBuilder create(ItemData result, String postSuffix) {
		ResourceLocation rl = BiomancyMod.rl(result.getItemPath() + "_from_" + postSuffix);
		return new DigestingRecipeBuilder(rl, result);
	}

	public static DigestingRecipeBuilder create(ItemStack stack) {
		return create(new ItemData(stack));
	}

	public static DigestingRecipeBuilder create(ItemLike item) {
		return create(new ItemData(item));
	}

	public static DigestingRecipeBuilder create(ItemLike item, int count) {
		return create(new ItemData(item, count));
	}

	public static DigestingRecipeBuilder create(ItemLike item, int count, String suffix) {
		return create(new ItemData(item, count), suffix);
	}

	public DigestingRecipeBuilder ifModLoaded(String modId) {
		return withCondition(new ModLoadedCondition(modId));
	}

	public DigestingRecipeBuilder ifModMissing(String modId) {
		return withCondition(new NotCondition(new ModLoadedCondition(modId)));
	}

	public DigestingRecipeBuilder withCondition(ICondition condition) {
		conditions.add(condition);
		return this;
	}

	public DigestingRecipeBuilder setIngredient(ItemLike item) {
		return setIngredient(Ingredient.of(item));
	}

	public DigestingRecipeBuilder setIngredient(TagKey<Item> tag) {
		return setIngredient(Ingredient.of(tag));
	}

	public DigestingRecipeBuilder setIngredient(ItemStack stack) {
		return setIngredient(Ingredient.of(stack));
	}

	public DigestingRecipeBuilder setIngredient(Ingredient ingredient) {
		this.recipeIngredient = ingredient;
		return this;
	}

	@Override
	public DigestingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
		criteria.put(name, criterion);
		return this;
	}

	public DigestingRecipeBuilder setCraftingTime(int time) {
		if (time < 0) throw new IllegalArgumentException("Invalid crafting time: " + time);
		craftingTimeTicks = time;
		return this;
	}

	public DigestingRecipeBuilder addCraftingTimeModifier(int modifier) {
		craftingTimeModifier = modifier;
		return this;
	}

	public DigestingRecipeBuilder setCraftingCost(int costNutrients) {
		if (costNutrients < 0) throw new IllegalArgumentException("Invalid crafting cost: " + costNutrients);
		craftingCostNutrients = costNutrients;
		return this;
	}

	public DigestingRecipeBuilder addCraftingCostModifier(int modifier) {
		craftingCostModifier = modifier;
		return this;
	}

	@Override
	public void save(RecipeOutput recipeOutput, @Nullable RecipeCategory category) {
		validateCriteria();

		if (craftingTimeTicks < 0) {
			if (recipeResult.getRegistryName().equals(ModItems.NUTRIENT_PASTE.getId())) {
				craftingTimeTicks = Mth.ceil(200 + 190 * Math.log(recipeResult.getCount()));
			}
			else if (recipeResult.getRegistryName().equals(ModItems.NUTRIENT_BAR.getId())) {
				craftingTimeTicks = Mth.ceil(200 + 190 * Math.log(recipeResult.getCount() * 9d));
			}
		}

		if (craftingCostNutrients < 0) {
			craftingCostNutrients = RecipeCostUtil.getCost(DEFAULT_CRAFTING_COST_NUTRIENTS, craftingTimeTicks);
		}

		craftingTimeTicks += craftingTimeModifier;
		craftingCostNutrients += craftingCostModifier;

		if (craftingTimeTicks < 0) throw new IllegalArgumentException("Invalid crafting time: " + craftingTimeTicks);
		if (craftingCostNutrients < 0) throw new IllegalArgumentException("Invalid crafting cost: " + craftingCostNutrients);

		StaticDigestingRecipe recipe = new StaticDigestingRecipe(recipeResult.toItemStack(), craftingTimeTicks, craftingCostNutrients, recipeIngredient);

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
