package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.crafting.recipe.FoodDigestingRecipe;
import com.github.elenterius.biomancy.datagen.recipes.builder.DigestingRecipeBuilder;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.orcinus.overweightfarming.OverweightFarming;
import net.orcinus.overweightfarming.init.OFBlocks;
import net.orcinus.overweightfarming.init.OFItems;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.FoodValues;

import java.util.concurrent.CompletableFuture;

public class DigestingRecipeProvider extends RecipeProvider {

	protected DigestingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	protected static String getItemName(ItemLike itemLike) {
		ResourceLocation key = BuiltInRegistries.ITEM.getKey(itemLike.asItem());
		return key != null ? key.getPath() : "unknown";
	}

	protected static String getTagName(TagKey<Item> tag) {
		return tag.location().getPath();
	}

	protected DigestingRecipeBuilder nutrientPasteRecipe(int count, TagKey<Item> ingredient) {
		return simpleRecipe(ModItems.NUTRIENT_PASTE.get(), count, ingredient);
	}

	protected DigestingRecipeBuilder nutrientPasteRecipe(int count, ItemLike ingredient) {
		return simpleRecipe(ModItems.NUTRIENT_PASTE.get(), count, ingredient);
	}

	protected DigestingRecipeBuilder simpleRecipe(ItemLike result, int count, TagKey<Item> ingredient) {
		return DigestingRecipeBuilder.create(result, count, getTagName(ingredient)).setIngredient(ingredient).unlockedBy(ingredient);
	}

	protected DigestingRecipeBuilder simpleRecipe(ItemLike result, int count, ItemLike ingredient) {
		return DigestingRecipeBuilder.create(result, count, getItemName(ingredient)).setIngredient(ingredient).unlockedBy(ingredient);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {
		buildFromFoodRecipes(recipeOutput);
		buildFromOrganicRecipes(recipeOutput);

		buildFarmersDelightRecipes(recipeOutput);
		buildOverweightFarmingRecipes(recipeOutput);
	}

	private void buildFromFoodRecipes(RecipeOutput recipeOutput) {
		FoodDigestingRecipe.RecipeBuilder.save(recipeOutput, 1, ModItems.NUTRIENT_PASTE.get()); //dynamic recipe that handles all food items
	}

	private void buildFromOrganicRecipes(RecipeOutput recipeOutput) {
		nutrientPasteRecipe(1, Items.SHORT_GRASS).save(recipeOutput);
		nutrientPasteRecipe(1, Items.SEAGRASS).save(recipeOutput);
		nutrientPasteRecipe(1, Items.VINE).save(recipeOutput);
		nutrientPasteRecipe(1, Items.FERN).save(recipeOutput);
		nutrientPasteRecipe(1, Items.HANGING_ROOTS).save(recipeOutput);

		nutrientPasteRecipe(1, Items.BAMBOO).addCraftingTimeModifier(60).save(recipeOutput);

		int sugarNutrition = 1;
		nutrientPasteRecipe(sugarNutrition, Items.SUGAR_CANE).addCraftingTimeModifier(20).save(recipeOutput);

		int seedNutrition = 1;
		nutrientPasteRecipe(seedNutrition, Tags.Items.SEEDS).addCraftingTimeModifier(40).save(recipeOutput);

		nutrientPasteRecipe(1, ItemTags.SMALL_FLOWERS).addCraftingTimeModifier(-20).save(recipeOutput);
		nutrientPasteRecipe(1, ItemTags.LEAVES).addCraftingTimeModifier(25).save(recipeOutput);
		nutrientPasteRecipe(1, ItemTags.SAPLINGS).addCraftingTimeModifier(15).save(recipeOutput);

		nutrientPasteRecipe(1, Items.MOSS_CARPET).addCraftingTimeModifier(-20).save(recipeOutput);
		nutrientPasteRecipe(2, Items.MOSS_BLOCK).setCraftingCost(3).addCraftingTimeModifier(20).save(recipeOutput);

		nutrientPasteRecipe(2, Items.NETHER_WART).setCraftingCost(2).addCraftingTimeModifier(-40).save(recipeOutput);
		nutrientPasteRecipe(2, Items.CACTUS).setCraftingCost(3).save(recipeOutput);
		nutrientPasteRecipe(2, Items.LARGE_FERN).save(recipeOutput);
		nutrientPasteRecipe(2, Items.TALL_GRASS).save(recipeOutput);
		nutrientPasteRecipe(2, ItemTags.TALL_FLOWERS).save(recipeOutput);
		nutrientPasteRecipe(2, Items.NETHER_SPROUTS).save(recipeOutput);
		nutrientPasteRecipe(2, Items.WEEPING_VINES).save(recipeOutput);
		nutrientPasteRecipe(2, Items.TWISTING_VINES).save(recipeOutput);
		nutrientPasteRecipe(2, Items.WARPED_ROOTS).save(recipeOutput);
		nutrientPasteRecipe(2, Items.CRIMSON_ROOTS).save(recipeOutput);
		nutrientPasteRecipe(2, Items.LILY_PAD).save(recipeOutput);

		nutrientPasteRecipe(2, Items.SMALL_DRIPLEAF).save(recipeOutput);
		nutrientPasteRecipe(4, Items.BIG_DRIPLEAF).setCraftingCost(3).save(recipeOutput);

		nutrientPasteRecipe(3, Items.HONEYCOMB).save(recipeOutput);
		nutrientPasteRecipe(3, Items.SEA_PICKLE).save(recipeOutput);
		nutrientPasteRecipe(3, Items.WARPED_WART_BLOCK).setCraftingCost(3).save(recipeOutput);
		nutrientPasteRecipe(9 * 2, Items.NETHER_WART_BLOCK).setCraftingCost(9 * 2 - 2).save(recipeOutput);
		nutrientPasteRecipe(4, Items.SHROOMLIGHT).setCraftingCost(3).save(recipeOutput);

		nutrientPasteRecipe(Foods.DRIED_KELP.nutrition(), Items.KELP).setCraftingCost(3).addCraftingTimeModifier(35).save(recipeOutput);
		nutrientPasteRecipe(9 * Foods.DRIED_KELP.nutrition(), Items.DRIED_KELP_BLOCK).addCraftingTimeModifier(-Math.round(20 * 4.5f)).save(recipeOutput);

		int wheatNutrition = Math.max(1, Foods.BREAD.nutrition() / 3);
		nutrientPasteRecipe(wheatNutrition, Items.WHEAT).save(recipeOutput);
		nutrientPasteRecipe(9 * wheatNutrition, Items.HAY_BLOCK).addCraftingTimeModifier(40).save(recipeOutput);

		nutrientPasteRecipe(4, Items.COCOA_BEANS).setCraftingCost(3).addCraftingTimeModifier(60).save(recipeOutput);

		int eggNutrition = 1;
		nutrientPasteRecipe(eggNutrition, Items.EGG).addCraftingTimeModifier(20).save(recipeOutput);
		nutrientPasteRecipe(eggNutrition, ModItems.ACIDIC_EGG.get()).addCraftingTimeModifier(20).save(recipeOutput);
		nutrientPasteRecipe(2, Items.TURTLE_EGG).save(recipeOutput);
		nutrientPasteRecipe(6, Items.SNIFFER_EGG).setCraftingCost(3).save(recipeOutput);

		int mushroomNutrition = Math.max(1, Foods.MUSHROOM_STEW.nutrition() / 2);
		nutrientPasteRecipe(mushroomNutrition, Tags.Items.MUSHROOMS).save(recipeOutput);
		nutrientPasteRecipe(mushroomNutrition, Items.WARPED_FUNGUS).save(recipeOutput);
		nutrientPasteRecipe(mushroomNutrition, Items.CRIMSON_FUNGUS).save(recipeOutput);
		nutrientPasteRecipe(mushroomNutrition * 2, Items.RED_MUSHROOM_BLOCK).setCraftingCost(3).save(recipeOutput);
		nutrientPasteRecipe(mushroomNutrition * 2, Items.BROWN_MUSHROOM_BLOCK).setCraftingCost(3).save(recipeOutput);
		nutrientPasteRecipe(2, Items.MUSHROOM_STEM).setCraftingCost(3).save(recipeOutput);

		int milkNutrition = 2;

		int cakeNutrition = 3 * wheatNutrition + 2 * sugarNutrition + eggNutrition + 3 * milkNutrition;
		nutrientPasteRecipe(cakeNutrition, Items.CAKE).save(recipeOutput);

		nutrientPasteRecipe(7 * Foods.MELON_SLICE.nutrition(), Items.MELON).setCraftingCost(3).save(recipeOutput);

		int pumpkinNutrition = Foods.PUMPKIN_PIE.nutrition() - sugarNutrition - eggNutrition;
		nutrientPasteRecipe(pumpkinNutrition, Items.PUMPKIN).setCraftingCost(3).save(recipeOutput);
		nutrientPasteRecipe(pumpkinNutrition - 4 * seedNutrition, Items.CARVED_PUMPKIN).addCraftingTimeModifier(-100).save(recipeOutput);
	}

	private DigestingRecipeBuilder farmersDelightRecipe(int count, ItemLike ingredient) {
		return nutrientPasteRecipe(count, ingredient).ifModLoaded(FarmersDelight.MODID);
	}

	private DigestingRecipeBuilder overweightFarmingRecipe(int count, ItemLike ingredient) {
		return nutrientPasteRecipe(count, ingredient).ifModLoaded(OverweightFarming.MODID);
	}

	private void buildFarmersDelightRecipes(RecipeOutput recipeOutput) {
		class FarmersDelightItems extends vectorwing.farmersdelight.common.registry.ModItems {}

		farmersDelightRecipe(1, FarmersDelightItems.ROTTEN_TOMATO.get()).addCraftingTimeModifier(50).save(recipeOutput);

		int mushroomNutrition = Math.max(1, Foods.MUSHROOM_STEW.nutrition() / 2);
		farmersDelightRecipe(mushroomNutrition * 5, FarmersDelightItems.BROWN_MUSHROOM_COLONY.get()).save(recipeOutput);
		farmersDelightRecipe(mushroomNutrition * 5, FarmersDelightItems.RED_MUSHROOM_COLONY.get()).save(recipeOutput);

		farmersDelightRecipe(Foods.MELON_SLICE.nutrition() * 4, FarmersDelightItems.MELON_JUICE.get()).setCraftingCost(3).save(recipeOutput);
		farmersDelightRecipe(Foods.APPLE.nutrition() * 2 + 1, FarmersDelightItems.APPLE_CIDER.get()).setCraftingCost(3).save(recipeOutput);
		farmersDelightRecipe(4 * 2 + 2, FarmersDelightItems.HOT_COCOA.get()).setCraftingCost(3).addCraftingTimeModifier(30).save(recipeOutput);
	}

	private void buildOverweightFarmingRecipes(RecipeOutput recipeOutput) {
		int peelNutrition = 1;
		overweightFarmingRecipe(peelNutrition, OFItems.VEGETABLE_PEELS.get()).save(recipeOutput);

		int seedNutrition = 1;
		overweightFarmingRecipe(7 * Foods.MELON_SLICE.nutrition() - peelNutrition, OFBlocks.SEEDED_PEELED_MELON.get()).setCraftingCost(3).addCraftingTimeModifier(25).save(recipeOutput);
		overweightFarmingRecipe(7 * Foods.MELON_SLICE.nutrition() - peelNutrition - seedNutrition, OFBlocks.HALF_SEEDED_PEELED_MELON.get()).setCraftingCost(3).addCraftingTimeModifier(20).save(recipeOutput);
		overweightFarmingRecipe(7 * Foods.MELON_SLICE.nutrition() - peelNutrition - seedNutrition * 2, OFBlocks.SEEDLESS_PEELED_MELON.get()).setCraftingCost(3).addCraftingTimeModifier(15).save(recipeOutput);

		overweightFarmingRecipe(Foods.CARROT.nutrition() * 10, OFBlocks.OVERWEIGHT_CARROT.get()).setCraftingCost(3).addCraftingTimeModifier(30).save(recipeOutput);
		overweightFarmingRecipe(Foods.BEETROOT.nutrition() * 10, OFBlocks.OVERWEIGHT_BEETROOT.get()).setCraftingCost(3).addCraftingTimeModifier(30).save(recipeOutput);
		overweightFarmingRecipe(Foods.POTATO.nutrition() * 10, OFBlocks.OVERWEIGHT_POTATO.get()).setCraftingCost(3).addCraftingTimeModifier(30).save(recipeOutput);
		overweightFarmingRecipe(Foods.POISONOUS_POTATO.nutrition() * 10, OFBlocks.OVERWEIGHT_POISONOUS_POTATO.get()).setCraftingCost(3).addCraftingTimeModifier(30).save(recipeOutput);
		overweightFarmingRecipe(Foods.BAKED_POTATO.nutrition() * 10, OFBlocks.OVERWEIGHT_BAKED_POTATO.get()).setCraftingCost(3).addCraftingTimeModifier(30).save(recipeOutput);

		overweightFarmingRecipe(4 * 10, OFBlocks.OVERWEIGHT_COCOA.get()).setCraftingCost(3).addCraftingTimeModifier(60).save(recipeOutput);
		overweightFarmingRecipe(4 * 10 - peelNutrition, OFBlocks.PEELED_OVERWEIGHT_COCOA.get()).setCraftingCost(3).addCraftingTimeModifier(45).save(recipeOutput);
		overweightFarmingRecipe(2 * 10, OFBlocks.OVERWEIGHT_NETHER_WART.get()).setCraftingCost(2 * 10 - 2).addCraftingTimeModifier(30).save(recipeOutput);

		overweightFarmingRecipe(Foods.APPLE.nutrition() * 10, OFBlocks.OVERWEIGHT_APPLE.get()).setCraftingCost(3).addCraftingTimeModifier(30).save(recipeOutput);
		overweightFarmingRecipe(Foods.GOLDEN_APPLE.nutrition() * 10, OFBlocks.OVERWEIGHT_GOLDEN_APPLE.get()).setCraftingCost(3).addCraftingTimeModifier(60).save(recipeOutput);

		overweightFarmingRecipe(FoodValues.ONION.nutrition() * 10, OFBlocks.OVERWEIGHT_ONION.get()).ifModLoaded(FarmersDelight.MODID)
				.setCraftingCost(3).addCraftingTimeModifier(30)
				.save(recipeOutput);
		overweightFarmingRecipe(FoodValues.CABBAGE.nutrition() * 10, OFBlocks.OVERWEIGHT_CABBAGE.get()).ifModLoaded(FarmersDelight.MODID)
				.setCraftingCost(3).addCraftingTimeModifier(30)
				.save(recipeOutput);

		overweightFarmingRecipe(10, OFBlocks.OVERWEIGHT_KIWI.get()).ifModLoaded("hedgehog")
				.setCraftingCost(3).addCraftingTimeModifier(30)
				.save(recipeOutput);
		overweightFarmingRecipe(10 - peelNutrition, OFBlocks.PEELED_OVERWEIGHT_KIWI.get()).ifModLoaded("hedgehog")
				.setCraftingCost(3).addCraftingTimeModifier(20)
				.save(recipeOutput);

		overweightFarmingRecipe(10, OFBlocks.OVERWEIGHT_GINGER.get()).ifModLoaded("snowyspirit")
				.setCraftingCost(3).addCraftingTimeModifier(30)
				.save(recipeOutput);
		overweightFarmingRecipe(10 - peelNutrition, OFBlocks.PEELED_OVERWEIGHT_GINGER.get()).ifModLoaded("snowyspirit")
				.setCraftingCost(3).addCraftingTimeModifier(25)
				.save(recipeOutput);
	}

}
