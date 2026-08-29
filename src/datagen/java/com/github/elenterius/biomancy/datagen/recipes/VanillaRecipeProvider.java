package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.recipe.AcolyteHelmetUpgradeRecipe;
import com.github.elenterius.biomancy.crafting.recipe.BiometricMembraneRecipe;
import com.github.elenterius.biomancy.crafting.recipe.CradleCleansingRecipe;
import com.github.elenterius.biomancy.crafting.recipe.PlayerHeadRecipe;
import com.github.elenterius.biomancy.datagen.recipes.builder.WorkbenchRecipeBuilder;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.github.elenterius.biomancy.item.SimpleBlockItem;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class VanillaRecipeProvider extends RecipeProvider {

	protected VanillaRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	protected static String hasName(ItemLike itemLike) {
		return "has_" + getItemName(itemLike);
	}

	protected static String getItemName(ItemLike itemLike) {
		ResourceLocation key = BuiltInRegistries.ITEM.getKey(itemLike.asItem());
		return key != null ? key.getPath() : "unknown";
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {
		buildPrimaryRecipes(recipeOutput);
		buildFleshBlockRecipes(recipeOutput);
		buildGellingAgentRecipes(recipeOutput);
		buildFoodRecipes(recipeOutput);
		buildMiscRecipes(recipeOutput);

		special(recipeOutput, ModItems.BIOMETRIC_MEMBRANE.get(), BiometricMembraneRecipe::new);
		special(recipeOutput, Items.PLAYER_HEAD, PlayerHeadRecipe::new);
		special(recipeOutput, ModItems.PRIMORDIAL_CRADLE.get(), CradleCleansingRecipe::new);
		special(recipeOutput, ModItems.ACOLYTE_ARMOR_HELMET.get(), AcolyteHelmetUpgradeRecipe::new);
	}

	private void buildPrimaryRecipes(RecipeOutput recipeOutput) {
		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PRIMORDIAL_CORE.get())
				.pattern("PFB")
				.pattern("#E#")
				.pattern("CFM")
				.define('B', Items.BEEF)
				.define('P', Items.PORKCHOP)
				.define('M', Items.MUTTON)
				.define('C', Items.CHICKEN)
				.define('F', Items.ROTTEN_FLESH)
				.define('E', Items.SPIDER_EYE)
				.define('#', Items.ENDER_PEARL)
				.unlockedBy(ModItems.PRIMORDIAL_CORE.get()).save(recipeOutput);

		WorkbenchRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.DESPOIL_SICKLE.get())
				.define('B', Tags.Items.BONES)
				.define('M', ModItemTags.FRESH_RAW_MEATS)
				.pattern("BBM")
				.pattern(" MB")
				.pattern(" BM")
				.unlockedBy(ModItems.PRIMORDIAL_CORE.get()).save(recipeOutput);

		//		WorkbenchRecipeBuilder.shapeless(ModItems.GUIDE_BOOK.get())
		//				.requires(ModItems.MOB_SINEW.get())
		//				.requires(Items.BOOK)
		//				.requires(ModTags.Items.RAW_MEATS)
		//				.requires(ModItems.PRIMORDIAL_LIVING_OCULUS.get())
		//				.requires(ModItems.MOB_FANG.get())
		//				.unlockedBy(hasName(ModItems.PRIMORDIAL_LIVING_FLESH.get()), has(ModItems.PRIMORDIAL_LIVING_FLESH.get())).save(recipeOutput);

		// machines ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PRIMORDIAL_CRADLE.get())
				.define('E', ModItems.PRIMORDIAL_CORE.get())
				.define('M', ModItemTags.FRESH_RAW_MEATS)
				.define('F', ModItemTags.C_FANGS)
				.pattern("F F")
				.pattern("MEM")
				.pattern("MMM")
				.unlockedBy(ModItems.PRIMORDIAL_CORE.get()).save(recipeOutput);

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DECOMPOSER.get())
				.define('M', ModItemTags.FRESH_RAW_MEATS)
				.define('F', ModItemTags.C_FANGS)
				.define('G', ModItems.GENERIC_MOB_GLAND.get())
				.define('E', ModItems.LIVING_FLESH.get())
				.pattern("F F")
				.pattern("MGM")
				.pattern("MEM")
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(recipeOutput);

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BIO_FORGE.get())
				.define('S', Items.SLIME_BALL)
				.define('M', ModItemTags.FRESH_RAW_MEATS)
				.define('C', ModItemTags.C_CLAWS)
				.define('E', ModItems.LIVING_FLESH.get())
				.pattern("C C")
				.pattern("MSM")
				.pattern("MEM")
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(recipeOutput);

		// A recipe for converting between two versions of Flesh Door.
		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FLESH_DOOR.get())
				.requires(ModItems.FULL_FLESH_DOOR.get())
				.unlockedBy(ModItems.FULL_FLESH_DOOR.get())
				.save(recipeOutput, getConversionRecipeId(ModItems.FLESH_DOOR.get(), ModItems.FULL_FLESH_DOOR.get()));

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FULL_FLESH_DOOR.get())
				.requires(ModItems.FLESH_DOOR.get())
				.unlockedBy(ModItems.FLESH_DOOR.get())
				.save(recipeOutput, getConversionRecipeId(ModItems.FULL_FLESH_DOOR.get(), ModItems.FLESH_DOOR.get()));

		WorkbenchRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.PRIMORDIAL_BIO_LANTERN.get())
				.define('B', ModItems.BLOOMBERRY.get())
				.define('V', ModItems.MALIGNANT_FLESH_VEINS.get())
				.define('C', ModItems.TENDON_CHAIN.get())
				.pattern(" C ")
				.pattern("VBV")
				.pattern(" V ")
				.unlockedBy(ModItems.BLOOMBERRY.get())
				.save(recipeOutput);

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLOOMLIGHT.get(), 4)
				.define('B', ModItems.BLOOMBERRY.get())
				.define('V', ModItems.MALIGNANT_FLESH_VEINS.get())
				.pattern("BVB")
				.pattern("VBV")
				.pattern("BVB")
				.unlockedBy(ModItems.BLOOMBERRY.get())
				.save(recipeOutput);

		membrane(recipeOutput, ModItems.IMPERMEABLE_MEMBRANE_PANE.get(), ModItems.IMPERMEABLE_MEMBRANE.get());
		membrane(recipeOutput, ModItems.BABY_PERMEABLE_MEMBRANE_PANE.get(), ModItems.BABY_PERMEABLE_MEMBRANE.get());
		membrane(recipeOutput, ModItems.ADULT_PERMEABLE_MEMBRANE_PANE.get(), ModItems.ADULT_PERMEABLE_MEMBRANE.get());
		membrane(recipeOutput, ModItems.PRIMAL_PERMEABLE_MEMBRANE_PANE.get(), ModItems.PRIMAL_PERMEABLE_MEMBRANE.get());
		membrane(recipeOutput, ModItems.UNDEAD_PERMEABLE_MEMBRANE_PANE.get(), ModItems.UNDEAD_PERMEABLE_MEMBRANE.get());
	}

	private void buildFoodRecipes(RecipeOutput recipeOutput) {
		WorkbenchRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.NUTRIENT_BAR.get())
				.requires(ModItems.NUTRIENT_PASTE.get(), 9)
				.unlockedBy(ModItems.NUTRIENT_PASTE.get())
				.save(recipeOutput);

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.NUTRIENT_PASTE.get(), 9)
				.requires(ModItems.NUTRIENT_BAR.get())
				.unlockedBy(ModItems.NUTRIENT_PASTE.get())
				.save(recipeOutput, getConversionRecipeId(ModItems.NUTRIENT_PASTE.get(), ModItems.NUTRIENT_BAR.get()));
	}

	private void buildGellingAgentRecipes(RecipeOutput recipeOutput) {
		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GELLING_AGENT.get())
				.requires(ModItems.BILE.get(), 4)
				.requires(ModItems.ELASTIC_FIBERS.get(), 5)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get())
				.save(recipeOutput);

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, Items.SLIME_BALL)
				.requires(ModItems.GELLING_AGENT.get(), 1)
				.requires(ModItems.BILE.get(), 2)
				.requires(ModItems.REGENERATIVE_FLUID.get(), 3)
				.requires(Items.LIME_DYE, 1)
				.unlockedBy(ModItems.GELLING_AGENT.get())
				.save(recipeOutput);

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, Items.HONEY_BOTTLE)
				.requires(ModItems.GELLING_AGENT.get(), 1)
				.requires(ModItems.BILE.get(), 2)
				.requires(ModItems.NUTRIENTS.get(), 1)
				.requires(Items.SUGAR, 2)
				.requires(Items.YELLOW_DYE, 1)
				.requires(Items.GLASS_BOTTLE, 1)
				.unlockedBy(ModItems.GELLING_AGENT.get())
				.save(recipeOutput);

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.WATER_GEL_BLOCK.get())
				.requires(Items.WATER_BUCKET)
				.requires(ModItems.GELLING_AGENT.get(), 2)
				.unlockedBy(ModItems.GELLING_AGENT.get())
				.save(recipeOutput);
	}

	private void buildMiscRecipes(RecipeOutput recipeOutput) {
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(ModItems.STONE_POWDER.get()), RecipeCategory.BUILDING_BLOCKS, Items.GLASS_PANE, 0.01f, 100)
				.unlockedBy(hasName(ModItems.STONE_POWDER.get()), has(ModItems.STONE_POWDER.get())).save(recipeOutput, getBlastingRecipeId(Items.GLASS_PANE));

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.DIORITE)
				.requires(Items.COBBLESTONE)
				.requires(ModItems.MINERAL_FRAGMENT.get())
				.requires(ModItems.STONE_POWDER.get())
				.unlockedBy(ModItems.STONE_POWDER.get())
				.save(recipeOutput, getConversionRecipeId(Items.DIORITE, ModItems.STONE_POWDER.get()));

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.GRANITE)
				.requires(Items.DIORITE)
				.requires(ModItems.MINERAL_FRAGMENT.get(), 2)
				.unlockedBy(ModItems.MINERAL_FRAGMENT.get())
				.save(recipeOutput, getConversionRecipeId(Items.GRANITE, ModItems.MINERAL_FRAGMENT.get()));

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Items.RED_SAND)
				.requires(Items.SAND)
				.requires(ModItems.MINERAL_FRAGMENT.get(), 2)
				.unlockedBy(ModItems.MINERAL_FRAGMENT.get())
				.save(recipeOutput, getConversionRecipeId(Items.RED_SAND, ModItems.MINERAL_FRAGMENT.get()));

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, Items.DIRT)
				.define('P', ModItems.ORGANIC_MATTER.get())
				.define('L', ModItems.STONE_POWDER.get())
				.pattern("LPL")
				.pattern("PLP")
				.pattern("LPL")
				.unlockedBy(ModItems.ORGANIC_MATTER.get())
				.save(recipeOutput, getConversionRecipeId(Items.DIRT, ModItems.STONE_POWDER.get()));

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, Items.SAND)
				.define('M', ModItems.MINERAL_FRAGMENT.get())
				.define('L', ModItems.STONE_POWDER.get())
				.pattern("LLL")
				.pattern("LML")
				.pattern("LLL")
				.unlockedBy(ModItems.STONE_POWDER.get())
				.save(recipeOutput, getConversionRecipeId(Items.SAND, ModItems.STONE_POWDER.get()));

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, Items.CLAY_BALL)
				.requires(Items.WATER_BUCKET)
				.requires(ModItems.STONE_POWDER.get(), 8)
				.unlockedBy(ModItems.STONE_POWDER.get())
				.save(recipeOutput, getConversionRecipeId(Items.CLAY_BALL, ModItems.STONE_POWDER.get()));

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GUNPOWDER)
				.requires(Items.CHARCOAL)
				.requires(ModItems.VOLATILE_FLUID.get())
				.requires(Items.BLAZE_POWDER)
				.unlockedBy(ModItems.VOLATILE_FLUID.get())
				.save(recipeOutput, getConversionRecipeId(Items.GUNPOWDER, ModItems.VOLATILE_FLUID.get()));

		WorkbenchRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.GLOW_ITEM_FRAME)
				.define('F', Items.ITEM_FRAME)
				.define('L', ModItems.BIO_LUMENS.get())
				.pattern(" L ")
				.pattern("LFL")
				.pattern(" L ")
				.unlockedBy(ModItems.BIO_LUMENS.get())
				.save(recipeOutput);

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL, 4)
				.requires(ModItems.MOB_FANG.get())
				.unlockedBy(ModItems.MOB_FANG.get())
				.save(recipeOutput);

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GRAY_DYE, 4)
				.requires(ModItems.MOB_CLAW.get())
				.unlockedBy(ModItems.MOB_CLAW.get())
				.save(recipeOutput);

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL, 9)
				.requires(ModItems.PRIMAL_BONE.get())
				.unlockedBy(ModItems.PRIMAL_BONE.get())
				.save(recipeOutput, getConversionRecipeId(Items.BONE_MEAL, ModItems.PRIMAL_BONE.get()));
	}

	private void buildFleshBlockRecipes(RecipeOutput recipeOutput) {
		stairs(recipeOutput, ModItems.FLESH_STAIRS.get(), ModItems.FLESH_BLOCK.get());
		slab(recipeOutput, ModItems.FLESH_SLAB.get(), ModItems.FLESH_BLOCK.get());
		wall(recipeOutput, ModItems.FLESH_WALL.get(), ModItems.FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.FLESH_STAIRS.get(), ModItems.FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.FLESH_SLAB.get(), ModItems.FLESH_BLOCK.get(), 2);
		stonecutting(recipeOutput, ModItems.FLESH_WALL.get(), ModItems.FLESH_BLOCK.get());

		stairs(recipeOutput, ModItems.PACKED_FLESH_STAIRS.get(), ModItems.PACKED_FLESH_BLOCK.get());
		slab(recipeOutput, ModItems.PACKED_FLESH_SLAB.get(), ModItems.PACKED_FLESH_BLOCK.get());
		wall(recipeOutput, ModItems.PACKED_FLESH_WALL.get(), ModItems.PACKED_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.PACKED_FLESH_STAIRS.get(), ModItems.PACKED_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.PACKED_FLESH_SLAB.get(), ModItems.PACKED_FLESH_BLOCK.get(), 2);
		stonecutting(recipeOutput, ModItems.PACKED_FLESH_WALL.get(), ModItems.PACKED_FLESH_BLOCK.get());

		stairs(recipeOutput, ModItems.FIBROUS_FLESH_STAIRS.get(), ModItems.FIBROUS_FLESH_BLOCK.get());
		slab(recipeOutput, ModItems.FIBROUS_FLESH_SLAB.get(), ModItems.FIBROUS_FLESH_BLOCK.get());
		wall(recipeOutput, ModItems.FIBROUS_FLESH_WALL.get(), ModItems.FIBROUS_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.FIBROUS_FLESH_STAIRS.get(), ModItems.FIBROUS_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.FIBROUS_FLESH_SLAB.get(), ModItems.FIBROUS_FLESH_BLOCK.get(), 2);
		stonecutting(recipeOutput, ModItems.FIBROUS_FLESH_WALL.get(), ModItems.FIBROUS_FLESH_BLOCK.get());

		slab(recipeOutput, ModItems.ORNATE_FLESH_SLAB.get(), ModItems.ORNATE_FLESH_BLOCK.get());
		blockFromSlabs(recipeOutput, ModItems.ORNATE_FLESH_BLOCK.get(), ModItems.ORNATE_FLESH_SLAB.get());
		stonecutting(recipeOutput, ModItems.ORNATE_FLESH_SLAB.get(), ModItems.ORNATE_FLESH_BLOCK.get(), 2);

		stairs(recipeOutput, ModItems.PRIMAL_FLESH_STAIRS.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		slab(recipeOutput, ModItems.PRIMAL_FLESH_SLAB.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		blockFromSlabs(recipeOutput, ModItems.PRIMAL_FLESH_BLOCK.get(), ModItems.PRIMAL_FLESH_SLAB.get());
		wall(recipeOutput, ModItems.PRIMAL_FLESH_WALL.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.PRIMAL_FLESH_WALL.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.PRIMAL_FLESH_STAIRS.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.PRIMAL_FLESH_SLAB.get(), ModItems.PRIMAL_FLESH_BLOCK.get(), 2);

		polished(recipeOutput, ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		stairs(recipeOutput, ModItems.SMOOTH_PRIMAL_FLESH_STAIRS.get(), ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get());
		slab(recipeOutput, ModItems.SMOOTH_PRIMAL_FLESH_SLAB.get(), ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get());
		blockFromSlabs(recipeOutput, ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get(), ModItems.SMOOTH_PRIMAL_FLESH_SLAB.get());
		wall(recipeOutput, ModItems.SMOOTH_PRIMAL_FLESH_WALL.get(), ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get(), ModItems.PRIMAL_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.SMOOTH_PRIMAL_FLESH_SLAB.get(), ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get(), 2);
		stonecutting(recipeOutput, ModItems.SMOOTH_PRIMAL_FLESH_STAIRS.get(), ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.SMOOTH_PRIMAL_FLESH_WALL.get(), ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get());

		WorkbenchRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.FIBROUS_PRIMAL_FLESH_BLOCK.get())
				.requires(ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get())
				.unlockedBy(ModItems.SMOOTH_PRIMAL_FLESH_BLOCK.get())
				.save(recipeOutput);
		stairs(recipeOutput, ModItems.FIBROUS_PRIMAL_FLESH_STAIRS.get(), ModItems.FIBROUS_PRIMAL_FLESH_BLOCK.get());
		slab(recipeOutput, ModItems.FIBROUS_PRIMAL_FLESH_SLAB.get(), ModItems.FIBROUS_PRIMAL_FLESH_BLOCK.get());
		blockFromSlabs(recipeOutput, ModItems.FIBROUS_PRIMAL_FLESH_BLOCK.get(), ModItems.FIBROUS_PRIMAL_FLESH_SLAB.get());
		wall(recipeOutput, ModItems.FIBROUS_PRIMAL_FLESH_WALL.get(), ModItems.FIBROUS_PRIMAL_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.FIBROUS_PRIMAL_FLESH_SLAB.get(), ModItems.FIBROUS_PRIMAL_FLESH_BLOCK.get(), 2);
		stonecutting(recipeOutput, ModItems.FIBROUS_PRIMAL_FLESH_STAIRS.get(), ModItems.FIBROUS_PRIMAL_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.FIBROUS_PRIMAL_FLESH_WALL.get(), ModItems.FIBROUS_PRIMAL_FLESH_BLOCK.get());

		WorkbenchRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.POROUS_PRIMAL_FLESH_BLOCK.get(), 4)
				.define('M', ModItems.MALIGNANT_FLESH_BLOCK.get())
				.define('P', ModItems.PRIMAL_FLESH_BLOCK.get())
				.pattern("PM")
				.pattern("MP")
				.unlockedBy(ModItems.PRIMAL_FLESH_BLOCK.get())
				.save(recipeOutput);
		stairs(recipeOutput, ModItems.POROUS_PRIMAL_FLESH_STAIRS.get(), ModItems.POROUS_PRIMAL_FLESH_BLOCK.get());
		slab(recipeOutput, ModItems.POROUS_PRIMAL_FLESH_SLAB.get(), ModItems.POROUS_PRIMAL_FLESH_BLOCK.get());
		blockFromSlabs(recipeOutput, ModItems.POROUS_PRIMAL_FLESH_BLOCK.get(), ModItems.POROUS_PRIMAL_FLESH_SLAB.get());
		wall(recipeOutput, ModItems.POROUS_PRIMAL_FLESH_WALL.get(), ModItems.POROUS_PRIMAL_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.POROUS_PRIMAL_FLESH_WALL.get(), ModItems.POROUS_PRIMAL_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.POROUS_PRIMAL_FLESH_STAIRS.get(), ModItems.POROUS_PRIMAL_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.POROUS_PRIMAL_FLESH_SLAB.get(), ModItems.POROUS_PRIMAL_FLESH_BLOCK.get(), 2);

		stairs(recipeOutput, ModItems.MALIGNANT_FLESH_STAIRS.get(), ModItems.MALIGNANT_FLESH_BLOCK.get());
		slab(recipeOutput, ModItems.MALIGNANT_FLESH_SLAB.get(), ModItems.MALIGNANT_FLESH_BLOCK.get());
		blockFromSlabs(recipeOutput, ModItems.MALIGNANT_FLESH_BLOCK.get(), ModItems.MALIGNANT_FLESH_SLAB.get());
		wall(recipeOutput, ModItems.MALIGNANT_FLESH_WALL.get(), ModItems.MALIGNANT_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.MALIGNANT_FLESH_WALL.get(), ModItems.MALIGNANT_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.MALIGNANT_FLESH_STAIRS.get(), ModItems.MALIGNANT_FLESH_BLOCK.get());
		stonecutting(recipeOutput, ModItems.MALIGNANT_FLESH_SLAB.get(), ModItems.MALIGNANT_FLESH_BLOCK.get(), 2);

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MALIGNANT_FLESH_BLOCK.get())
				.define('F', ModItems.FLESH_BITS.get())
				.define('V', ModItems.MALIGNANT_FLESH_VEINS.get())
				.pattern("VVV")
				.pattern("VFV")
				.pattern("VVV")
				.unlockedBy(ModItems.MALIGNANT_FLESH_VEINS.get())
				.save(recipeOutput);

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PRIMAL_FLESH_BLOCK.get())
				.define('F', ModItems.FLESH_BITS.get())
				.define('V', ModItems.MALIGNANT_FLESH_BLOCK.get())
				.pattern("VVV")
				.pattern("VFV")
				.pattern("VVV")
				.unlockedBy(ModItems.MALIGNANT_FLESH_BLOCK.get())
				.save(recipeOutput);
	}

	private void membrane(RecipeOutput recipeOutput, SimpleBlockItem pane, SimpleBlockItem membrane) {
		WorkbenchRecipeBuilder.shapeless(RecipeCategory.MISC, pane, 2)
				.requires(membrane)
				.unlockedBy(membrane)
				.save(recipeOutput, getConversionRecipeId(pane, membrane));

		WorkbenchRecipeBuilder.shaped(RecipeCategory.MISC, membrane)
				.define('P', pane)
				.pattern("P")
				.pattern("P")
				.unlockedBy(pane)
				.save(recipeOutput, getConversionRecipeId(membrane, pane));
	}

	protected ResourceLocation getSpecialCraftingRecipeId(ItemLike itemLike) {
		return BiomancyMod.rl("special_crafting/" + getItemName(itemLike));
	}

	protected ResourceLocation getCraftingRecipeId(ItemLike itemLike) {
		return BiomancyMod.rl("crafting/" + getItemName(itemLike));
	}

	protected ResourceLocation getRecipeId(ItemLike itemLike) {
		return BiomancyMod.rl(getItemName(itemLike));
	}

	protected ResourceLocation getConversionRecipeId(ItemLike result, ItemLike ingredient) {
		return BiomancyMod.rl(getItemName(result) + "_from_" + getItemName(ingredient));
	}

	protected ResourceLocation getStoneCuttingRecipeId(ItemLike result, ItemLike ingredient) {
		return BiomancyMod.rl("stonecutting/" + getItemName(result) + "_from_" + getItemName(ingredient));
	}

	protected ResourceLocation getSmeltingRecipeId(ItemLike itemLike) {
		return BiomancyMod.rl("smelting/" + getItemName(itemLike));
	}

	protected ResourceLocation getBlastingRecipeId(ItemLike itemLike) {
		return BiomancyMod.rl("blasting/" + getItemName(itemLike));
	}

	protected void special(RecipeOutput recipeOutput, ItemLike result, Function<CraftingBookCategory, Recipe<?>> factory) {
		SpecialRecipeBuilder.special(factory).save(recipeOutput, getSpecialCraftingRecipeId(result).toString());
	}

	protected void polished(RecipeOutput recipeOutput, ItemLike result, ItemLike ingredient) {
		WorkbenchRecipeBuilder.polished(RecipeCategory.BUILDING_BLOCKS, result, ingredient).save(recipeOutput);
	}

	protected void slab(RecipeOutput recipeOutput, BlockItem result, BlockItem ingredient) {
		WorkbenchRecipeBuilder.slab(RecipeCategory.BUILDING_BLOCKS, result, ingredient).save(recipeOutput);
	}

	protected void wall(RecipeOutput recipeOutput, BlockItem result, BlockItem ingredient) {
		WorkbenchRecipeBuilder.wall(RecipeCategory.BUILDING_BLOCKS, result, ingredient).save(recipeOutput);
	}

	protected void stairs(RecipeOutput recipeOutput, ItemLike result, ItemLike ingredient) {
		WorkbenchRecipeBuilder.stairs(RecipeCategory.BUILDING_BLOCKS, result, ingredient).save(recipeOutput);
	}

	protected void stonecutting(RecipeOutput recipeOutput, ItemLike result, ItemLike ingredient) {
		stonecutting(recipeOutput, result, ingredient, 1);
	}

	protected void stonecutting(RecipeOutput recipeOutput, ItemLike result, ItemLike ingredient, int count) {
		SingleItemRecipeBuilder builder = SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, result, count).unlockedBy(getHasName(ingredient), has(ingredient));
		ResourceLocation recipeName = getStoneCuttingRecipeId(result, ingredient);
		builder.save(recipeOutput, recipeName);
	}

	protected void blockFromSlabs(RecipeOutput recipeOutput, Item result, Item slab) {
		WorkbenchRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
				.define('S', slab)
				.pattern(" S ")
				.pattern(" S ")
				.unlockedBy(slab)
				.save(recipeOutput, getRecipeId(result).withSuffix("_from_slabs"));
	}

}
