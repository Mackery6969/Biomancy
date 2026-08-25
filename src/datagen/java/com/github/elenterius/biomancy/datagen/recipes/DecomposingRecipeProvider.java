package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.block.membrane.Membrane;
import com.github.elenterius.biomancy.datagen.recipes.builder.DatagenIngredient;
import com.github.elenterius.biomancy.datagen.recipes.builder.DecomposingRecipeBuilder;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import net.mcreator.sonsofsins.SonsOfSinsMod;
import net.mcreator.sonsofsins.init.SonsOfSinsModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.orcinus.overweightfarming.OverweightFarming;
import net.orcinus.overweightfarming.init.OFBlocks;
import net.orcinus.overweightfarming.init.OFItems;
import vectorwing.farmersdelight.FarmersDelight;

import java.util.function.Consumer;

public class DecomposingRecipeProvider extends RecipeProvider {

	protected DecomposingRecipeProvider(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		buildBaseRecipes(consumer);
		buildTagRecipes(consumer);
		build119Recipes(consumer);
		build120Recipes(consumer);
		buildSpecialRecipes(consumer);
		buildRecyclingRecipes(consumer);

		buildBiomesOPlentyRecipes(consumer);

		buildFarmersDelightRecipes(consumer);
		buildOverweightFarmingRecipes(consumer);

		buildSonsOfSinsRecipes(consumer);
	}

	private void buildBaseRecipes(Consumer<FinishedRecipe> consumer) {
		DecomposingRecipeBuilder.create().setIngredient(Items.GRASS_BLOCK).addExtraCraftingTime(3 * 20).addExtraCraftingCost(1).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.STONE_POWDER.get(), 0, 1).unlockedBy(Items.GRASS_BLOCK).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DIRT).addExtraCraftingTime(3 * 20).addExtraCraftingCost(1).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1).addOutput(ModItems.STONE_POWDER.get(), 0, 1).unlockedBy(Items.DIRT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.COARSE_DIRT).addExtraCraftingTime(3 * 20).addExtraCraftingCost(1).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.COARSE_DIRT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.PODZOL).addExtraCraftingTime(3 * 20).addExtraCraftingCost(1).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.STONE_POWDER.get(), 0, 1).unlockedBy(Items.PODZOL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.ROOTED_DIRT).addExtraCraftingTime(3 * 20).addExtraCraftingCost(1).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1).addOutput(ModItems.STONE_POWDER.get(), 0, 1).unlockedBy(Items.ROOTED_DIRT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.SAND).addExtraCraftingTime(3 * 20).addExtraCraftingCost(1).addOutput(ModItems.STONE_POWDER.get(), 1, 3).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.SAND).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.RED_SAND).addExtraCraftingTime(3 * 20).addExtraCraftingCost(1).addOutput(ModItems.STONE_POWDER.get(), 1, 3).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(Items.RED_SAND).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.GRAVEL).addExtraCraftingTime(10 * 20).addExtraCraftingCost(1).addOutput(ModItems.STONE_POWDER.get(), 3, 6).unlockedBy(Items.GRAVEL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.SPONGE).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4).unlockedBy(Items.SPONGE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.SEA_PICKLE).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).addOutput(ModItems.BIO_LUMENS.get(), 1, 2).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.SEA_PICKLE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.LILY_OF_THE_VALLEY).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.TOXIN_EXTRACT.get(), 0, 1).unlockedBy(Items.LILY_OF_THE_VALLEY).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.OXEYE_DAISY).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.REGENERATIVE_FLUID.get(), -2, 1).unlockedBy(Items.OXEYE_DAISY).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.WITHER_ROSE).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).addOutput(ModItems.WITHERING_OOZE.get(), 3, 5).unlockedBy(Items.WITHER_ROSE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.SPORE_BLOSSOM).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.BIO_LUMENS.get(), 3, 5).unlockedBy(Items.SPORE_BLOSSOM).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.BROWN_MUSHROOM).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.BROWN_MUSHROOM).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.RED_MUSHROOM).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.RED_MUSHROOM).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.CRIMSON_FUNGUS).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).unlockedBy(Items.CRIMSON_FUNGUS).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.WARPED_FUNGUS).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).unlockedBy(Items.WARPED_FUNGUS).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.CRIMSON_ROOTS).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).unlockedBy(Items.CRIMSON_ROOTS).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.WARPED_ROOTS).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).unlockedBy(Items.WARPED_ROOTS).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.NETHER_SPROUTS).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.NETHER_SPROUTS).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.SUGAR_CANE).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(Items.SUGAR, 1, 2).unlockedBy(Items.SUGAR_CANE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.KELP).addOutput(ModItems.ORGANIC_MATTER.get(), -1, 1).unlockedBy(Items.KELP).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.BAMBOO).addExtraCraftingTime(2 * 20).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.BAMBOO).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.CHORUS_FLOWER).addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 5).addOutput(ModItems.EXOTIC_DUST.get(), 2, 4).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 5).unlockedBy(Items.CHORUS_FLOWER).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.CLAY).addOutput(ModItems.STONE_POWDER.get(), 1, 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(Items.CLAY).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.GLOWSTONE).addExtraCraftingTime(5 * 20).addExtraCraftingCost(1).addOutput(ModItems.STONE_POWDER.get(), 2, 4).addOutput(ModItems.EXOTIC_DUST.get(), 1, 4).addOutput(ModItems.BIO_LUMENS.get(), -4, 4).unlockedBy(Items.GLOWSTONE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.GLOW_LICHEN).addOutput(ModItems.BIO_LUMENS.get(), 1, 2).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1).unlockedBy(Items.GLOW_LICHEN).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DRAGON_EGG).addExtraCraftingCost(4).addOutput(ModItems.EXOTIC_DUST.get(), 97, 128).addOutput(ModItems.BIO_LUMENS.get(), 6, 10).addOutput(ModItems.HORMONE_SECRETION.get(), 17, 23).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 4).unlockedBy(Items.DRAGON_EGG).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.TURTLE_EGG).addOutput(ModItems.HORMONE_SECRETION.get(), 1, 2).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1).unlockedBy(Items.TURTLE_EGG).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.TUBE_CORAL).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.REGENERATIVE_FLUID.get(), 0, 1).unlockedBy(Items.TUBE_CORAL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.BRAIN_CORAL).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.REGENERATIVE_FLUID.get(), 0, 1).unlockedBy(Items.BRAIN_CORAL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.BUBBLE_CORAL).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.REGENERATIVE_FLUID.get(), 0, 1).unlockedBy(Items.BUBBLE_CORAL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.FIRE_CORAL).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.REGENERATIVE_FLUID.get(), 0, 1).unlockedBy(Items.FIRE_CORAL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.HORN_CORAL).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.REGENERATIVE_FLUID.get(), 0, 1).unlockedBy(Items.HORN_CORAL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DEAD_BRAIN_CORAL).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_BRAIN_CORAL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DEAD_BUBBLE_CORAL).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_BUBBLE_CORAL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DEAD_FIRE_CORAL).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_FIRE_CORAL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DEAD_HORN_CORAL).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_HORN_CORAL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DEAD_TUBE_CORAL).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_TUBE_CORAL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.TUBE_CORAL_FAN).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.REGENERATIVE_FLUID.get(), 0, 1).unlockedBy(Items.TUBE_CORAL_FAN).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.BRAIN_CORAL_FAN).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.REGENERATIVE_FLUID.get(), 0, 1).unlockedBy(Items.BRAIN_CORAL_FAN).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.BUBBLE_CORAL_FAN).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.REGENERATIVE_FLUID.get(), 0, 1).unlockedBy(Items.BUBBLE_CORAL_FAN).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.FIRE_CORAL_FAN).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.REGENERATIVE_FLUID.get(), 0, 1).unlockedBy(Items.FIRE_CORAL_FAN).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.HORN_CORAL_FAN).addOutput(ModItems.ORGANIC_MATTER.get(), 1).addOutput(ModItems.REGENERATIVE_FLUID.get(), 0, 1).unlockedBy(Items.HORN_CORAL_FAN).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DEAD_TUBE_CORAL_FAN).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_TUBE_CORAL_FAN).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DEAD_BRAIN_CORAL_FAN).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_BRAIN_CORAL_FAN).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DEAD_BUBBLE_CORAL_FAN).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_BUBBLE_CORAL_FAN).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DEAD_FIRE_CORAL_FAN).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_FIRE_CORAL_FAN).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DEAD_HORN_CORAL_FAN).addOutput(ModItems.STONE_POWDER.get(), 1).unlockedBy(Items.DEAD_HORN_CORAL_FAN).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.REDSTONE).addOutput(ModItems.BIO_LUMENS.get(), -2, 1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.REDSTONE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.TURTLE_HELMET).addOutput(ModItems.TOUGH_FIBERS.get(), 15, 25).addOutput(ModItems.MINERAL_FRAGMENT.get(), 9, 15).unlockedBy(Items.TURTLE_HELMET).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.SCUTE).addOutput(ModItems.TOUGH_FIBERS.get(), 3, 5).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 3).unlockedBy(Items.SCUTE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.APPLE).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.APPLE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DIAMOND).addExtraCraftingCost(1).addOutput(ModItems.GEM_FRAGMENTS.get(), 4, 8).unlockedBy(Items.DIAMOND).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.EMERALD).addExtraCraftingCost(1).addOutput(ModItems.GEM_FRAGMENTS.get(), 5, 9).unlockedBy(Items.EMERALD).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.LAPIS_LAZULI).addExtraCraftingCost(1).addOutput(ModItems.GEM_FRAGMENTS.get(), 0, 1).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).unlockedBy(Items.LAPIS_LAZULI).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.QUARTZ).addExtraCraftingCost(1).addOutput(ModItems.GEM_FRAGMENTS.get(), 1, 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(Items.QUARTZ).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.AMETHYST_SHARD).addExtraCraftingCost(1).addOutput(ModItems.GEM_FRAGMENTS.get(), 3, 5).unlockedBy(Items.AMETHYST_SHARD).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.RAW_IRON).addExtraCraftingCost(1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).addOutput(ModItems.STONE_POWDER.get(), 1, 2).unlockedBy(Items.RAW_IRON).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.IRON_INGOT).addExtraCraftingCost(2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).unlockedBy(Items.IRON_INGOT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.RAW_COPPER).addExtraCraftingCost(1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).addOutput(ModItems.STONE_POWDER.get(), 1, 2).unlockedBy(Items.RAW_COPPER).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.COPPER_INGOT).addExtraCraftingCost(2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).unlockedBy(Items.COPPER_INGOT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.RAW_GOLD).addExtraCraftingCost(1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).addOutput(ModItems.STONE_POWDER.get(), 1, 2).unlockedBy(Items.RAW_GOLD).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.GOLD_INGOT).addExtraCraftingCost(2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).unlockedBy(Items.GOLD_INGOT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.NETHERITE_INGOT).addExtraCraftingCost(2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 43, 72).unlockedBy(Items.NETHERITE_INGOT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.NETHERITE_SCRAP).addExtraCraftingCost(1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 9).unlockedBy(Items.NETHERITE_SCRAP).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.FLINT).addOutput(ModItems.STONE_POWDER.get(), 1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.FLINT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.PORKCHOP).addOutput(ModItems.FLESH_BITS.get(), 3, 5).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.PORKCHOP).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.GOLDEN_APPLE).addExtraCraftingCost(1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 37, 63).addOutput(ModItems.ORGANIC_MATTER.get(), 4, 6).addOutput(ModItems.REGENERATIVE_FLUID.get(), 3, 6).unlockedBy(Items.GOLDEN_APPLE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.ENCHANTED_GOLDEN_APPLE).addExtraCraftingCost(1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 43, 72).addOutput(ModItems.REGENERATIVE_FLUID.get(), 12, 20).addOutput(ModItems.EXOTIC_DUST.get(), 6, 10).unlockedBy(Items.ENCHANTED_GOLDEN_APPLE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.CLAY_BALL).addOutput(ModItems.STONE_POWDER.get(), 1, 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.CLAY_BALL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.SLIME_BALL).addOutput(ModItems.REGENERATIVE_FLUID.get(), 2, 3).addOutput(ModItems.BILE.get(), 1, 2).unlockedBy(Items.SLIME_BALL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.SLIME_BLOCK).addExtraCraftingCost(3).addOutput(ModItems.REGENERATIVE_FLUID.get(), 2 * 9, 3 * 9).addOutput(ModItems.BILE.get(), 10, 18).unlockedBy(Items.SLIME_BLOCK).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.MAGMA_CREAM).addOutput(ModItems.REGENERATIVE_FLUID.get(), 1, 2).addOutput(ModItems.BILE.get(), 1, 3).addOutput(ModItems.BIO_LUMENS.get(), 1, 3).addOutput(ModItems.VOLATILE_FLUID.get(), 0, 2).unlockedBy(Items.MAGMA_CREAM).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.GLOWSTONE_DUST).addOutput(ModItems.STONE_POWDER.get(), 1).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).addOutput(ModItems.BIO_LUMENS.get(), -1, 1).unlockedBy(Items.GLOWSTONE_DUST).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.COD).addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.COD).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.SALMON).addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.SALMON).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.TROPICAL_FISH).addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.TROPICAL_FISH).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.PUFFERFISH).addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).addOutput(ModItems.TOXIN_EXTRACT.get(), 1, 3).unlockedBy(Items.PUFFERFISH).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.INK_SAC).addOutput(ModItems.BILE.get(), 1, 2).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.INK_SAC).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.GLOW_INK_SAC).addOutput(ModItems.BIO_LUMENS.get(), 3, 5).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.GLOW_INK_SAC).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.COCOA_BEANS).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4).unlockedBy(Items.COCOA_BEANS).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.BONE_MEAL).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2).unlockedBy(Items.BONE_MEAL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.CAKE).addOutput(ModItems.ORGANIC_MATTER.get(), 10, 18).unlockedBy(Items.CAKE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.COOKIE).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4).unlockedBy(Items.COOKIE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.MELON_SLICE).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4).unlockedBy(Items.MELON_SLICE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DRIED_KELP).addOutput(ModItems.ORGANIC_MATTER.get(), -2, 2).unlockedBy(Items.DRIED_KELP).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DRIED_KELP_BLOCK).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 3).unlockedBy(Items.DRIED_KELP_BLOCK).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.BEEF).addOutput(ModItems.FLESH_BITS.get(), 3, 6).unlockedBy(Items.BEEF).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.CHICKEN).addOutput(ModItems.FLESH_BITS.get(), 3, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 4).addOutput(ModItems.ELASTIC_FIBERS.get(), 2, 3).unlockedBy(Items.CHICKEN).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.ROTTEN_FLESH).addOutput(ModItems.FLESH_BITS.get(), 1, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 0, 1).unlockedBy(Items.ROTTEN_FLESH).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.ENDER_PEARL).addOutput(ModItems.EXOTIC_DUST.get(), 2, 3).unlockedBy(Items.ENDER_PEARL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.BLAZE_ROD).addOutput(ModItems.BIO_LUMENS.get(), 2, 4).addOutput(ModItems.EXOTIC_DUST.get(), 2).unlockedBy(Items.BLAZE_ROD).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.BLAZE_POWDER).addOutput(ModItems.BIO_LUMENS.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 1).unlockedBy(Items.BLAZE_POWDER).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.GHAST_TEAR).addOutput(ModItems.HORMONE_SECRETION.get(), 4, 8).addOutput(ModItems.BILE.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 1, 2).unlockedBy(Items.GHAST_TEAR).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.GOLD_NUGGET).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.GOLD_NUGGET).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.NETHER_WART).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 0, 1).unlockedBy(Items.NETHER_WART).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.SPIDER_EYE).addOutput(ModItems.BILE.get(), 0, 1).addOutput(ModItems.FLESH_BITS.get(), 1).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.SPIDER_EYE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.FERMENTED_SPIDER_EYE).addOutput(ModItems.FLESH_BITS.get(), 1).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).unlockedBy(Items.FERMENTED_SPIDER_EYE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.ENDER_EYE).addOutput(ModItems.EXOTIC_DUST.get(), 5, 6).unlockedBy(Items.ENDER_EYE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.GLISTERING_MELON_SLICE).addExtraCraftingCost(1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 6).addOutput(ModItems.ORGANIC_MATTER.get(), 2).unlockedBy(Items.GLISTERING_MELON_SLICE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.CARROT).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.CARROT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.POTATO).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Items.POTATO).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.BAKED_POTATO).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 5).unlockedBy(Items.BAKED_POTATO).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.POISONOUS_POTATO).addOutput(ModItems.TOXIN_EXTRACT.get(), 2, 4).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 3).unlockedBy(Items.POISONOUS_POTATO).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.GOLDEN_CARROT).addExtraCraftingCost(1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 4, 8).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4).unlockedBy(Items.GOLDEN_CARROT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.SKELETON_SKULL).addOutput(ModItems.BONE_FRAGMENTS.get(), 28, 48).addOutput(ModItems.MINERAL_FRAGMENT.get(), 4, 7).unlockedBy(Items.SKELETON_SKULL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.WITHER_SKELETON_SKULL).addOutput(ModItems.BONE_FRAGMENTS.get(), 28, 48).addOutput(ModItems.WITHERING_OOZE.get(), 8, 16).addOutput(ModItems.MINERAL_FRAGMENT.get(), 4, 7).unlockedBy(Items.WITHER_SKELETON_SKULL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.PLAYER_HEAD).addOutput(ModItems.FLESH_BITS.get(), 19, 32).addOutput(ModItems.ELASTIC_FIBERS.get(), 5, 9).addOutput(Items.SKELETON_SKULL, 1).unlockedBy(Items.PLAYER_HEAD).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.ZOMBIE_HEAD).addOutput(ModItems.FLESH_BITS.get(), 14, 24).addOutput(ModItems.ELASTIC_FIBERS.get(), 5, 9).addOutput(Items.SKELETON_SKULL, 1).unlockedBy(Items.ZOMBIE_HEAD).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.CREEPER_HEAD).addOutput(ModItems.FLESH_BITS.get(), 19, 32).addOutput(ModItems.ELASTIC_FIBERS.get(), 5, 9).addOutput(Items.SKELETON_SKULL, 1).unlockedBy(Items.CREEPER_HEAD).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.DRAGON_HEAD).addOutput(ModItems.FLESH_BITS.get(), 50).addOutput(ModItems.EXOTIC_DUST.get(), 50).addOutput(ModItems.TOUGH_FIBERS.get(), 25).addOutput(ModItems.MINERAL_FRAGMENT.get(), 20).addOutput(ModItems.BONE_FRAGMENTS.get(), 50).unlockedBy(Items.DRAGON_HEAD).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.NETHER_STAR).addOutput(ModItems.EXOTIC_DUST.get(), 50).addOutput(ModItems.BIO_LUMENS.get(), 25).addOutput(ModItems.GEM_FRAGMENTS.get(), 20).unlockedBy(Items.NETHER_STAR).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.PRISMARINE_SHARD).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(Items.PRISMARINE_SHARD).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.PRISMARINE_CRYSTALS).addOutput(ModItems.GEM_FRAGMENTS.get(), 1, 3).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).addOutput(ModItems.BIO_LUMENS.get(), 0, 1).unlockedBy(Items.PRISMARINE_CRYSTALS).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.RABBIT).addOutput(ModItems.FLESH_BITS.get(), 3, 6).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3).unlockedBy(Items.RABBIT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.RABBIT_FOOT).addOutput(ModItems.ELASTIC_FIBERS.get(), 3, 5).addOutput(ModItems.FLESH_BITS.get(), 2, 3).addOutput(ModItems.BONE_FRAGMENTS.get(), 1, 2).unlockedBy(Items.RABBIT_FOOT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.RABBIT_HIDE).addOutput(ModItems.TOUGH_FIBERS.get(), 0, 1).unlockedBy(Items.RABBIT_HIDE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.MUTTON).addOutput(ModItems.FLESH_BITS.get(), 2, 4).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 3).unlockedBy(Items.MUTTON).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.CHORUS_FRUIT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 3).addOutput(ModItems.EXOTIC_DUST.get(), 1, 2).addOutput(ModItems.BILE.get(), 0, 1).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.CHORUS_FRUIT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.POPPED_CHORUS_FRUIT).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 1, 2).addOutput(ModItems.BILE.get(), 0, 1).addOutput(ModItems.ORGANIC_MATTER.get(), 1).unlockedBy(Items.POPPED_CHORUS_FRUIT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.TOTEM_OF_UNDYING).addExtraCraftingCost(2).addOutput(ModItems.EXOTIC_DUST.get(), 25).addOutput(ModItems.GEM_FRAGMENTS.get(), 15).addOutput(ModItems.MINERAL_FRAGMENT.get(), 10).unlockedBy(Items.TOTEM_OF_UNDYING).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.SHULKER_SHELL).addOutput(ModItems.MINERAL_FRAGMENT.get(), 6, 10).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 7).addOutput(ModItems.STONE_POWDER.get(), 1, 2).unlockedBy(Items.SHULKER_SHELL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.IRON_NUGGET).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Items.IRON_NUGGET).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.PHANTOM_MEMBRANE).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 7).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).addOutput(ModItems.EXOTIC_DUST.get(), 1, 3).unlockedBy(Items.PHANTOM_MEMBRANE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.NAUTILUS_SHELL).addOutput(ModItems.MINERAL_FRAGMENT.get(), 6, 10).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 7).unlockedBy(Items.NAUTILUS_SHELL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.HEART_OF_THE_SEA).addOutput(ModItems.GEM_FRAGMENTS.get(), 8).addOutput(ModItems.EXOTIC_DUST.get(), 15).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5).unlockedBy(Items.HEART_OF_THE_SEA).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.GLOW_BERRIES).addOutput(ModItems.BIO_LUMENS.get(), 0, 1).addOutput(ModItems.ORGANIC_MATTER.get(), -1, 1).unlockedBy(Items.GLOW_BERRIES).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.SHROOMLIGHT).addExtraCraftingCost(1).addOutput(ModItems.BIO_LUMENS.get(), 5, 9).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 3).unlockedBy(Items.SHROOMLIGHT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.POINTED_DRIPSTONE).addOutput(ModItems.STONE_POWDER.get(), 1, 2).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(Items.POINTED_DRIPSTONE).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.MOB_FANG).addOutput(ModItems.MINERAL_FRAGMENT.get(), 2, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 4, 6).unlockedBy(ModItems.MOB_FANG).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.MOB_CLAW).addOutput(ModItems.MINERAL_FRAGMENT.get(), 3, 5).addOutput(ModItems.TOUGH_FIBERS.get(), 4, 6).unlockedBy(ModItems.MOB_CLAW).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.MOB_SINEW).addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8).addOutput(ModItems.FLESH_BITS.get(), 1, 2).unlockedBy(ModItems.MOB_SINEW).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.MOB_MARROW).addOutput(ModItems.HORMONE_SECRETION.get(), 1, 4).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 4).addOutput(ModItems.MINERAL_FRAGMENT.get(), 1, 2).unlockedBy(ModItems.MOB_MARROW).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.WITHERED_MOB_MARROW).addOutput(ModItems.WITHERING_OOZE.get(), 3, 5).addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 4).unlockedBy(ModItems.WITHERED_MOB_MARROW).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.GENERIC_MOB_GLAND).addOutput(ModItems.BILE.get(), 4, 6).addOutput(ModItems.FLESH_BITS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3).unlockedBy(ModItems.GENERIC_MOB_GLAND).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.TOXIN_GLAND).addOutput(ModItems.TOXIN_EXTRACT.get(), 2, 5).addOutput(ModItems.FLESH_BITS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3).unlockedBy(ModItems.TOXIN_GLAND).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.VOLATILE_GLAND).addOutput(ModItems.VOLATILE_FLUID.get(), 2, 5).addOutput(ModItems.FLESH_BITS.get(), 2, 3).addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 3).unlockedBy(ModItems.VOLATILE_GLAND).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.ACIDIC_EGG).addOutput(ModItems.ACID_EXTRACT.get(), -2, 1).addOutput(ModItems.BILE.get(), 2, 4).unlockedBy(ModItems.ACIDIC_EGG).save(consumer);
	}

	private void buildTagRecipes(Consumer<FinishedRecipe> consumer) {
		DecomposingRecipeBuilder.create().setIngredient(Tags.Items.STRING).addOutput(ModItems.MINERAL_FRAGMENT.get(), -1, 1).unlockedBy(Tags.Items.STRING).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Tags.Items.FEATHERS).addOutput(ModItems.TOUGH_FIBERS.get(), 0, 1).addOutput(ModItems.MINERAL_FRAGMENT.get(), 0, 1).unlockedBy(Tags.Items.FEATHERS).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Tags.Items.LEATHER).addOutput(ModItems.TOUGH_FIBERS.get(), 1, 4).unlockedBy(Tags.Items.LEATHER).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Tags.Items.EGGS).addOutput(ModItems.HORMONE_SECRETION.get(), 0, 1).addOutput(ModItems.ORGANIC_MATTER.get(), 0, 2).unlockedBy(Tags.Items.EGGS).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Tags.Items.SEEDS).addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2).unlockedBy(Tags.Items.SEEDS).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Tags.Items.CROPS).addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4).unlockedBy(Tags.Items.CROPS).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(Tags.Items.BONES).addOutput(ModItems.BONE_FRAGMENTS.get(), 3, 6).unlockedBy(Tags.Items.BONES).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItemTags.C_WITHER_BONES).addOutput(ModItems.BONE_FRAGMENTS.get(), 3, 6).addOutput(ModItems.WITHERING_OOZE.get(), 3, 5).unlockedBy(ModItemTags.C_WITHER_BONES).save(consumer);
	}

	private void build119Recipes(Consumer<FinishedRecipe> consumer) {
		DecomposingRecipeBuilder.create().setIngredient(Items.ECHO_SHARD).addOutput(ModItems.EXOTIC_DUST.get(), 8, 12).unlockedBy(Items.ECHO_SHARD).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.GOAT_HORN).addOutput(ModItems.MINERAL_FRAGMENT.get(), 5, 7).addOutput(ModItems.TOUGH_FIBERS.get(), 6, 8).unlockedBy(Items.GOAT_HORN).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.PEARLESCENT_FROGLIGHT).addExtraCraftingCost(1).addOutput(ModItems.BIO_LUMENS.get(), 5, 9).addOutput(ModItems.BILE.get(), 2, 3).unlockedBy(Items.PEARLESCENT_FROGLIGHT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.VERDANT_FROGLIGHT).addExtraCraftingCost(1).addOutput(ModItems.BIO_LUMENS.get(), 5, 9).addOutput(ModItems.BILE.get(), 2, 3).unlockedBy(Items.VERDANT_FROGLIGHT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.OCHRE_FROGLIGHT).addExtraCraftingCost(1).addOutput(ModItems.BIO_LUMENS.get(), 5, 9).addOutput(ModItems.BILE.get(), 2, 3).unlockedBy(Items.OCHRE_FROGLIGHT).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(Items.FROGSPAWN).addOutput(ModItems.BILE.get(), 0, 1).unlockedBy(Items.FROGSPAWN).save(consumer);
	}

	private void build120Recipes(Consumer<FinishedRecipe> consumer) {
		DecomposingRecipeBuilder.create().setIngredient(Items.PITCHER_POD)
				.addOutput(ModItems.BILE.get(), 1, 3)
				.addOutput(ModItems.EXOTIC_DUST.get(), 0, 3)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4)
				.unlockedBy(Items.PITCHER_POD).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(Items.PITCHER_PLANT)
				.addOutput(ModItems.EXOTIC_DUST.get(), 0, 3)
				.addOutput(ModItems.TOXIN_EXTRACT.get(), 0, 2)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4)
				.unlockedBy(Items.PITCHER_PLANT).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(Items.TORCHFLOWER)
				.addOutput(ModItems.BIO_LUMENS.get(), 0, 2)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2)
				.unlockedBy(Items.TORCHFLOWER).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(Items.TORCHFLOWER_SEEDS)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 1, 2)
				.unlockedBy(Items.TORCHFLOWER_SEEDS).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(Items.SNIFFER_EGG)
				.addOutput(ModItems.STONE_POWDER.get(), 0, 4)
				.addOutput(ModItems.HORMONE_SECRETION.get(), 1, 4)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 1, 4)
				.addOutput(ModItems.EXOTIC_DUST.get(), 2, 6)
				.unlockedBy(Items.SNIFFER_EGG).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(Items.PIGLIN_HEAD)
				.addOutput(ModItems.FLESH_BITS.get(), 22, 36)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 8, 12)
				.addOutput(Items.SKELETON_SKULL, 1)
				.unlockedBy(Items.PIGLIN_HEAD).save(consumer);
	}

	private void buildRecyclingRecipes(Consumer<FinishedRecipe> consumer) {
		final int blockCost = BioForgingRecipeProvider.blockCost;
		final int slabCost = BioForgingRecipeProvider.slabCost;
		final int stairsCost = BioForgingRecipeProvider.stairsCost;
		final int wallCost = BioForgingRecipeProvider.wallCost;

		DecomposingRecipeBuilder.create().setIngredient(ModItems.FLESH_BLOCK).addRecyclingOutput(ModItems.FLESH_BITS.get(), blockCost).unlockedBy(ModItems.FLESH_BLOCK).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.FLESH_STAIRS).addRecyclingOutput(ModItems.FLESH_BITS.get(), stairsCost).unlockedBy(ModItems.FLESH_STAIRS).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.FLESH_SLAB).addRecyclingOutput(ModItems.FLESH_BITS.get(), slabCost).unlockedBy(ModItems.FLESH_SLAB).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.FLESH_WALL).addRecyclingOutput(ModItems.FLESH_BITS.get(), wallCost).unlockedBy(ModItems.FLESH_WALL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.PACKED_FLESH_BLOCK).addRecyclingOutput(ModItems.FLESH_BITS.get(), blockCost * 2).addRecyclingOutput(ModItems.TOUGH_FIBERS.get(), blockCost).unlockedBy(ModItems.PACKED_FLESH_BLOCK).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.PACKED_FLESH_STAIRS).addRecyclingOutput(ModItems.FLESH_BITS.get(), stairsCost * 2).addRecyclingOutput(ModItems.TOUGH_FIBERS.get(), stairsCost).unlockedBy(ModItems.PACKED_FLESH_STAIRS).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.PACKED_FLESH_SLAB).addRecyclingOutput(ModItems.FLESH_BITS.get(), slabCost * 2).addRecyclingOutput(ModItems.TOUGH_FIBERS.get(), slabCost).unlockedBy(ModItems.PACKED_FLESH_SLAB).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.PACKED_FLESH_WALL).addRecyclingOutput(ModItems.FLESH_BITS.get(), wallCost * 2).addRecyclingOutput(ModItems.TOUGH_FIBERS.get(), wallCost).unlockedBy(ModItems.PACKED_FLESH_WALL).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.FIBROUS_FLESH_BLOCK).addRecyclingOutput(ModItems.FLESH_BITS.get(), blockCost / 2).addRecyclingOutput(ModItems.ELASTIC_FIBERS.get(), blockCost).unlockedBy(ModItems.FIBROUS_FLESH_BLOCK).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.FIBROUS_FLESH_STAIRS).addRecyclingOutput(ModItems.FLESH_BITS.get(), stairsCost / 2).addRecyclingOutput(ModItems.ELASTIC_FIBERS.get(), stairsCost).unlockedBy(ModItems.FIBROUS_FLESH_STAIRS).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.FIBROUS_FLESH_SLAB).addRecyclingOutput(ModItems.FLESH_BITS.get(), slabCost / 2).addRecyclingOutput(ModItems.ELASTIC_FIBERS.get(), slabCost).unlockedBy(ModItems.FIBROUS_FLESH_SLAB).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.FIBROUS_FLESH_WALL).addRecyclingOutput(ModItems.FLESH_BITS.get(), wallCost / 2).addRecyclingOutput(ModItems.ELASTIC_FIBERS.get(), wallCost).unlockedBy(ModItems.FIBROUS_FLESH_WALL).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(ModItems.CHISELED_FLESH_BLOCK.get())
				.addRecyclingOutput(ModItems.FLESH_BITS.get(), blockCost)
				.addRecyclingOutput(ModItems.BONE_FRAGMENTS.get(), 2)
				.unlockedBy(ModItems.CHISELED_FLESH_BLOCK.get()).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(ModItems.ORNATE_FLESH_BLOCK.get())
				.addRecyclingOutput(ModItems.FLESH_BITS.get(), blockCost)
				.addRecyclingOutput(ModItems.BONE_FRAGMENTS.get(), 4)
				.unlockedBy(ModItems.ORNATE_FLESH_BLOCK.get()).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(ModItems.ORNATE_FLESH_SLAB.get())
				.addRecyclingOutput(ModItems.FLESH_BITS.get(), slabCost)
				.addRecyclingOutput(ModItems.BONE_FRAGMENTS.get(), 2)
				.unlockedBy(ModItems.ORNATE_FLESH_SLAB.get()).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(ModItems.TUBULAR_FLESH_BLOCK.get())
				.addRecyclingOutput(ModItems.FLESH_BITS.get(), blockCost / 2)
				.addRecyclingOutput(ModItems.ELASTIC_FIBERS.get(), blockCost)
				.unlockedBy(ModItems.TUBULAR_FLESH_BLOCK.get()).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(ModItems.FLESH_PILLAR.get())
				.addRecyclingOutput(ModItems.FLESH_BITS.get(), blockCost / 2)
				.addRecyclingOutput(ModItems.BONE_FRAGMENTS.get(), blockCost / 2)
				.unlockedBy(ModItems.FLESH_PILLAR.get()).save(consumer);

		ModBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).filter(Membrane.class::isInstance).forEach(
				block -> DecomposingRecipeBuilder.create().setIngredient(block).addOutput(ModItems.BILE.get(), 0, 2).unlockedBy(block).save(consumer)
		);

		DecomposingRecipeBuilder.create().setIngredient(ModItems.MALIGNANT_FLESH_BLOCK.get()).addOutput(ModItems.FLESH_BITS.get(), -3, 1).unlockedBy(ModItems.MALIGNANT_FLESH_BLOCK.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.MALIGNANT_FLESH_SLAB.get()).addOutput(ModItems.FLESH_BITS.get(), -6, 1).unlockedBy(ModItems.MALIGNANT_FLESH_SLAB.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.MALIGNANT_FLESH_STAIRS.get()).addOutput(ModItems.FLESH_BITS.get(), -4, 1).unlockedBy(ModItems.MALIGNANT_FLESH_STAIRS.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.MALIGNANT_FLESH_WALL.get()).addOutput(ModItems.FLESH_BITS.get(), -3, 1).unlockedBy(ModItems.MALIGNANT_FLESH_WALL.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.PRIMAL_FLESH_BLOCK.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 2).unlockedBy(ModItems.PRIMAL_FLESH_BLOCK.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.PRIMAL_FLESH_SLAB.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 1).unlockedBy(ModItems.PRIMAL_FLESH_SLAB.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.PRIMAL_FLESH_STAIRS.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 1).unlockedBy(ModItems.PRIMAL_FLESH_STAIRS.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.PRIMAL_FLESH_WALL.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 2).unlockedBy(ModItems.PRIMAL_FLESH_WALL.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 2).unlockedBy(ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.SMOOTH_PRIMAL_FLESH_SLAB.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 1).unlockedBy(ModItems.SMOOTH_PRIMAL_FLESH_SLAB.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.SMOOTH_PRIMAL_FLESH_STAIRS.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 1).unlockedBy(ModItems.SMOOTH_PRIMAL_FLESH_STAIRS.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.SMOOTH_PRIMAL_FLESH_WALL.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 2).unlockedBy(ModItems.SMOOTH_PRIMAL_FLESH_WALL.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.FIBROUS_PRIMAL_FLESH_BLOCK.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 2).unlockedBy(ModItems.FIBROUS_PRIMAL_FLESH_BLOCK.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.FIBROUS_PRIMAL_FLESH_SLAB.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 1).unlockedBy(ModItems.FIBROUS_PRIMAL_FLESH_SLAB.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.FIBROUS_PRIMAL_FLESH_STAIRS.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 1).unlockedBy(ModItems.FIBROUS_PRIMAL_FLESH_STAIRS.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.FIBROUS_PRIMAL_FLESH_WALL.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 2).unlockedBy(ModItems.FIBROUS_PRIMAL_FLESH_WALL.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.POROUS_PRIMAL_FLESH_BLOCK.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 2).unlockedBy(ModItems.POROUS_PRIMAL_FLESH_BLOCK.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.POROUS_PRIMAL_FLESH_SLAB.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 1).unlockedBy(ModItems.POROUS_PRIMAL_FLESH_SLAB.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.POROUS_PRIMAL_FLESH_STAIRS.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 1).unlockedBy(ModItems.POROUS_PRIMAL_FLESH_STAIRS.get()).save(consumer);
		DecomposingRecipeBuilder.create().setIngredient(ModItems.POROUS_PRIMAL_FLESH_WALL.get()).addOutput(ModItems.FLESH_BITS.get(), 0, 2).unlockedBy(ModItems.POROUS_PRIMAL_FLESH_WALL.get()).save(consumer);
	}

	private void buildSpecialRecipes(Consumer<FinishedRecipe> consumer) {
		DecomposingRecipeBuilder.create().setIngredient(ModItems.LIVING_FLESH)
				.addOutput(ModItems.FLESH_BITS.get(), 3, 6)
				.addOutput(ModItems.EXOTIC_DUST.get(), 0, 2)
				.unlockedBy(ModItems.LIVING_FLESH).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(ModItems.PRIMAL_ORIFICE)
				.addOutput(ModItems.FLESH_BITS.get(), 0, 2)
				.addOutput(ModItems.BILE.get(), 1, 4)
				.unlockedBy(ModItems.PRIMAL_BLOOM).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(ModItems.PRIMAL_BLOOM)
				.addOutput(ModItems.FLESH_BITS.get(), 0, 2)
				.addOutput(ModItems.EXOTIC_DUST.get(), 2, 3)
				.addOutput(ModItems.BILE.get(), 1, 3)
				.unlockedBy(ModItems.PRIMAL_BLOOM).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(ModItems.BLOOMBERRY)
				.addOutput(ModItems.BIO_LUMENS.get(), 0, 2)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1)
				.addOutput(ModItems.EXOTIC_DUST.get(), 0, 3)
				.addOutput(ModItems.BILE.get(), 1, 2)
				.unlockedBy(ModItems.BLOOMBERRY).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(ModItems.BLOOMLIGHT)
				.addExtraCraftingCost(1)
				.addExtraCraftingTime(2 * 20)
				.addOutput(ModItems.BIO_LUMENS.get(), 5, 9)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2, 3)
				.addOutput(ModItems.EXOTIC_DUST.get(), 0, 4)
				.addOutput(ModItems.BILE.get(), 1, 2)
				.unlockedBy(Items.SHROOMLIGHT).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(ModItems.NUTRIENT_PASTE)
				.addOutput(ModItems.NUTRIENTS.get(), 5)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 0, 1)
				.unlockedBy(Items.SHROOMLIGHT).save(consumer);
	}

	private DecomposingRecipeBuilder createBiomesOPlentyRecipe() {
		return DecomposingRecipeBuilder.create().ifModLoaded("biomesoplenty");
	}

	private void buildBiomesOPlentyRecipes(Consumer<FinishedRecipe> consumer) {
		createBiomesOPlentyRecipe()
				.setIngredient(new DatagenIngredient("biomesoplenty:flesh_tendons"))
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8)
				.addOutput(ModItems.FLESH_BITS.get(), 0, 1)
				.unlockedBy(ModItems.ELASTIC_FIBERS).save(consumer);

		createBiomesOPlentyRecipe()
				.setIngredient(new DatagenIngredient("biomesoplenty:flesh_tendons_strand"))
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 2, 4)
				.addOutput(ModItems.FLESH_BITS.get(), 0, 1)
				.unlockedBy(ModItems.ELASTIC_FIBERS).save(consumer);

		createBiomesOPlentyRecipe()
				.setIngredient(new DatagenIngredient("biomesoplenty:flesh"))
				.addOutput(ModItems.FLESH_BITS.get(), 1, 2)
				.unlockedBy(ModItems.FLESH_BITS).save(consumer);

		createBiomesOPlentyRecipe()
				.setIngredient(new DatagenIngredient("biomesoplenty:porous_flesh"))
				.addOutput(ModItems.FLESH_BITS.get(), 1, 2)
				.unlockedBy(ModItems.FLESH_BITS).save(consumer);

		createBiomesOPlentyRecipe()
				.setIngredient(new DatagenIngredient("biomesoplenty:hair"))
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), -1, 1)
				.unlockedBy(ModItems.MINERAL_FRAGMENT).save(consumer);

		createBiomesOPlentyRecipe()
				.setIngredient(new DatagenIngredient("biomesoplenty:eyebulb"))
				.addOutput(ModItems.BILE.get(), 1, 2)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.unlockedBy(ModItems.BILE).save(consumer);

		createBiomesOPlentyRecipe()
				.setIngredient(new DatagenIngredient("biomesoplenty:pus_bubble"))
				.addOutput(ModItems.BILE.get(), 4, 6)
				.unlockedBy(ModItems.BILE).save(consumer);
	}

	private DecomposingRecipeBuilder farmersDelightRecipe() {
		return DecomposingRecipeBuilder.create().ifModLoaded(FarmersDelight.MODID);
	}

	private DecomposingRecipeBuilder overweightFarmingRecipe() {
		return DecomposingRecipeBuilder.create().ifModLoaded(OverweightFarming.MODID);
	}

	private DecomposingRecipeBuilder sonsOfSinsRecipe() {
		return DecomposingRecipeBuilder.create().ifModLoaded(SonsOfSinsMod.MODID);
	}

	private void buildFarmersDelightRecipes(Consumer<FinishedRecipe> consumer) {
		class FarmersDelightItems extends vectorwing.farmersdelight.common.registry.ModItems {} //alias workaround

		farmersDelightRecipe()
				.setIngredient(FarmersDelightItems.BROWN_MUSHROOM_COLONY)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 5, 2 * 5)
				.unlockedBy(FarmersDelightItems.BROWN_MUSHROOM_COLONY).save(consumer);

		farmersDelightRecipe()
				.setIngredient(FarmersDelightItems.RED_MUSHROOM_COLONY)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 5, 2 * 5)
				.unlockedBy(FarmersDelightItems.RED_MUSHROOM_COLONY).save(consumer);

		farmersDelightRecipe()
				.setIngredient(FarmersDelightItems.HAM)
				.addOutput(ModItems.FLESH_BITS.get(), 3 * 2, 5 * 2)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 2 * 2, 3 * 2)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 2, 2 * 2)
				.unlockedBy(FarmersDelightItems.HAM).save(consumer);
	}

	private void buildOverweightFarmingRecipes(Consumer<FinishedRecipe> consumer) {
		overweightFarmingRecipe()
				.setIngredient(OFItems.VEGETABLE_PEELS.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2, 4)
				.unlockedBy(OFItems.VEGETABLE_PEELS.get()).save(consumer);

		overweightFarmingRecipe()
				.setIngredient(OFBlocks.OVERWEIGHT_COCOA.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2 * 10, 4 * 10)
				.unlockedBy(OFBlocks.OVERWEIGHT_COCOA.get()).save(consumer);

		overweightFarmingRecipe()
				.setIngredient(OFBlocks.PEELED_OVERWEIGHT_COCOA.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2 * 10 - 2, 4 * 10 - 4)
				.unlockedBy(OFBlocks.OVERWEIGHT_COCOA.get()).save(consumer);

		overweightFarmingRecipe()
				.setIngredient(OFBlocks.OVERWEIGHT_APPLE.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 10, 2 * 10)
				.unlockedBy(OFBlocks.OVERWEIGHT_APPLE.get()).save(consumer);

		overweightFarmingRecipe()
				.setIngredient(OFBlocks.OVERWEIGHT_BEETROOT.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2 * 10, 4 * 10)
				.unlockedBy(OFBlocks.OVERWEIGHT_BEETROOT.get()).save(consumer);

		overweightFarmingRecipe()
				.setIngredient(OFBlocks.OVERWEIGHT_CARROT.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 10, 2 * 10)
				.unlockedBy(OFBlocks.OVERWEIGHT_CARROT.get()).save(consumer);

		overweightFarmingRecipe()
				.setIngredient(OFBlocks.OVERWEIGHT_POTATO.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 10, 2 * 10)
				.unlockedBy(OFBlocks.OVERWEIGHT_POTATO.get()).save(consumer);

		overweightFarmingRecipe()
				.setIngredient(OFBlocks.OVERWEIGHT_POISONOUS_POTATO.get())
				.addOutput(ModItems.TOXIN_EXTRACT.get(), 2 * 10, 4 * 10)
				.addOutput(ModItems.ORGANIC_MATTER.get(), 10, 3 * 10)
				.unlockedBy(OFBlocks.OVERWEIGHT_POISONOUS_POTATO.get()).save(consumer);

		overweightFarmingRecipe()
				.setIngredient(OFBlocks.OVERWEIGHT_BAKED_POTATO.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 2 * 10, 5 * 10)
				.unlockedBy(OFBlocks.OVERWEIGHT_BAKED_POTATO.get()).save(consumer);

		overweightFarmingRecipe()
				.setIngredient(OFBlocks.OVERWEIGHT_NETHER_WART.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 10, 2 * 10)
				.addOutput(ModItems.EXOTIC_DUST.get(), 4, 10)
				.unlockedBy(OFBlocks.OVERWEIGHT_NETHER_WART.get()).save(consumer);

		overweightFarmingRecipe().ifModLoaded(FarmersDelight.MODID)
				.setIngredient(OFBlocks.OVERWEIGHT_CABBAGE.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 10, 2 * 10)
				.unlockedBy(OFBlocks.OVERWEIGHT_CABBAGE.get()).save(consumer);

		overweightFarmingRecipe().ifModLoaded(FarmersDelight.MODID)
				.setIngredient(OFBlocks.OVERWEIGHT_ONION.get())
				.addOutput(ModItems.ORGANIC_MATTER.get(), 10, 2 * 10)
				.unlockedBy(OFBlocks.OVERWEIGHT_ONION.get()).save(consumer);
	}


	private void buildSonsOfSinsRecipes(Consumer<FinishedRecipe> consumer) {
		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.RIBS)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 5)
				.unlockedBy(SonsOfSinsModItems.RIBS).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.CREEPER_RIBS)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 5)
				.addOutput(ModItems.VOLATILE_FLUID.get(), 2, 3)
				.unlockedBy(SonsOfSinsModItems.CREEPER_RIBS).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.CHARGED_CREEPER_RIBS)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 1, 2)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 2, 5)
				.addOutput(ModItems.VOLATILE_FLUID.get(), 2 * 3, 3 * 3)
				.addOutput(ModItems.EXOTIC_DUST.get(), 1, 2)
				.unlockedBy(SonsOfSinsModItems.CHARGED_CREEPER_RIBS).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.MUSCLE)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8)
				.addOutput(ModItems.FLESH_BITS.get(), 1, 2)
				.unlockedBy(SonsOfSinsModItems.MUSCLE).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.RAVAGER_MUSCLE)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 1, 2)
				.addOutput(ModItems.FLESH_BITS.get(), 2, 4)
				.unlockedBy(SonsOfSinsModItems.RAVAGER_MUSCLE).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.ENDERMAN_MUSCLE)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8)
				.addOutput(ModItems.FLESH_BITS.get(), 1, 2)
				.addOutput(ModItems.EXOTIC_DUST.get(), 2, 3)
				.unlockedBy(SonsOfSinsModItems.ENDERMAN_MUSCLE).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.STRIDER_MUSCLE)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8)
				.addOutput(ModItems.TOUGH_FIBERS.get(), 2, 3)
				.addOutput(ModItems.STONE_POWDER.get(), 2, 4)
				.addOutput(ModItems.FLESH_BITS.get(), 0, 1)
				.unlockedBy(SonsOfSinsModItems.STRIDER_MUSCLE).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.HEART)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8)
				.addOutput(ModItems.FLESH_BITS.get(), 1, 2)
				.unlockedBy(SonsOfSinsModItems.HEART).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.SPIDER_HEART)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8)
				.addOutput(ModItems.FLESH_BITS.get(), 1, 2)
				.unlockedBy(SonsOfSinsModItems.SPIDER_HEART).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.BLAZING_HEART)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8)
				.addOutput(ModItems.FLESH_BITS.get(), 1, 2)
				.addOutput(ModItems.EXOTIC_DUST.get(), 2 * 3)
				.addOutput(ModItems.BIO_LUMENS.get(), 2 * 3, 4 * 3)
				.unlockedBy(SonsOfSinsModItems.BLAZING_HEART).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.ICE_HEART)
				.addOutput(ModItems.ELASTIC_FIBERS.get(), 4, 8)
				.addOutput(ModItems.FLESH_BITS.get(), 1, 2)
				.addOutput(ModItems.EXOTIC_DUST.get(), 2 * 3)
				.unlockedBy(SonsOfSinsModItems.ICE_HEART).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.BLOODY_BONE)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 3, 6)
				.unlockedBy(SonsOfSinsModItems.BLOODY_BONE).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.FLESH_OF_DEMISE)
				.addOutput(ModItems.BONE_FRAGMENTS.get(), 3, 6)
				.addOutput(ModItems.FLESH_BITS.get(), -1, 1)
				.unlockedBy(SonsOfSinsModItems.BLOODY_BONE).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.SLIME_REAR)
				.addOutput(ModItems.REGENERATIVE_FLUID.get(), 2, 3)
				.addOutput(ModItems.BILE.get(), 1, 2)
				.unlockedBy(SonsOfSinsModItems.SLIME_REAR).save(consumer);

		DecomposingRecipeBuilder.create().setIngredient(SonsOfSinsModItems.BLOCK_OF_SLIME_REAR)
				.addExtraCraftingCost(3)
				.addOutput(ModItems.REGENERATIVE_FLUID.get(), 2 * 9, 3 * 9)
				.addOutput(ModItems.BILE.get(), 10, 18)
				.unlockedBy(SonsOfSinsModItems.BLOCK_OF_SLIME_REAR).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.GOLEM_CUIRASS)
				.addExtraCraftingCost(2)
				.addOutput(ModItems.MINERAL_FRAGMENT.get(), 4, 6)
				.unlockedBy(SonsOfSinsModItems.GOLEM_CUIRASS).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.ETHER_ASHES)
				.addOutput(ModItems.EXOTIC_DUST.get(), 0, 2)
				.unlockedBy(SonsOfSinsModItems.ETHER_ASHES).save(consumer);

		sonsOfSinsRecipe()
				.setIngredient(SonsOfSinsModItems.CRYSTALLIZED_ETHER)
				.addOutput(ModItems.EXOTIC_DUST.get(), 1, 3)
				.unlockedBy(SonsOfSinsModItems.CRYSTALLIZED_ETHER).save(consumer);
	}

}
