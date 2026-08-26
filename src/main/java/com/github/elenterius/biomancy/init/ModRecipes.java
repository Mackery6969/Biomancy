package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.recipe.*;
import com.github.elenterius.biomancy.crafting.recipe.SimpleRecipeType.AdvancedRecipeType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;
import java.util.function.Supplier;

public final class ModRecipes {

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, BiomancyMod.MOD_ID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, BiomancyMod.MOD_ID);

	public static final DeferredHolder<RecipeType<?>, AdvancedRecipeType<DecomposingRecipe>> DECOMPOSING_RECIPE_TYPE = registerRecipeType("decomposing");
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DecomposingRecipe>> DECOMPOSING_SERIALIZER = registerRecipeSerializer(DECOMPOSING_RECIPE_TYPE, DecomposingRecipe.Serializer::new);
	public static final DeferredHolder<RecipeType<?>, AdvancedRecipeType<BioBrewingRecipe>> BIO_BREWING_RECIPE_TYPE = registerRecipeType("bio_brewing");
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BioBrewingRecipe>> BIO_BREWING_SERIALIZER = registerRecipeSerializer(BIO_BREWING_RECIPE_TYPE, BioBrewingRecipe.Serializer::new);
	public static final DeferredHolder<RecipeType<?>, AdvancedRecipeType<BioForgingRecipe>> BIO_FORGING_RECIPE_TYPE = registerRecipeType("bio_forging");
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BioForgingRecipe>> BIO_FORGING_SERIALIZER = registerRecipeSerializer(BIO_FORGING_RECIPE_TYPE, BioForgingRecipe.Serializer::new);
	public static final DeferredHolder<RecipeType<?>, AdvancedRecipeType<DigestingRecipe>> DIGESTING_RECIPE_TYPE = registerRecipeType("digesting");
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<StaticDigestingRecipe>> DIGESTING_SERIALIZER = registerRecipeSerializer(DIGESTING_RECIPE_TYPE, StaticDigestingRecipe.Serializer::new);

	// DYNAMIC
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FoodDigestingRecipe>> FOOD_DIGESTING_SERIALIZER = registerDynamicRecipeSerializer(DIGESTING_RECIPE_TYPE, "food", FoodDigestingRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BiometricMembraneRecipe>> BIOMETRIC_MEMBRANE_CRAFTING_SERIALIZER = registerDynamicCraftingRecipeSerializer(RecipeType.CRAFTING, "biometric_membrane", () -> new SimpleCraftingRecipeSerializer<>(BiometricMembraneRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CradleCleansingRecipe>> CRADLE_CLEANSING_SERIALIZER = registerDynamicCraftingRecipeSerializer(RecipeType.CRAFTING, "cradle_cleansing", () -> new SimpleCraftingRecipeSerializer<>(CradleCleansingRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PlayerHeadRecipe>> PLAYER_HEAD_SERIALIZER = registerDynamicCraftingRecipeSerializer(RecipeType.CRAFTING, "player_head", () -> new SimpleCraftingRecipeSerializer<>(PlayerHeadRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AcolyteHelmetUpgradeRecipe>> ACOLYTE_HELMET_UPGRADE_SERIALIZER = registerDynamicCraftingRecipeSerializer(RecipeType.CRAFTING, "acolyte_helmet_upgrade", () -> new SimpleCraftingRecipeSerializer<>(AcolyteHelmetUpgradeRecipe::new));

	private ModRecipes() {}

	public static void registerComposterRecipes() {
		ComposterBlock.COMPOSTABLES.putIfAbsent(ModItems.ORGANIC_MATTER.get(), 0.25f);
	}

	public static void registerBrewingRecipes() {
		registerBrewingRecipe(ModItems.TOXIN_EXTRACT.get(), Potions.AWKWARD, Potions.POISON);
		registerBrewingRecipe(ModItems.TOXIN_GLAND.get(), Potions.MUNDANE, Potions.LONG_POISON);
		registerBrewingRecipe(ModItems.TOXIN_GLAND.get(), Potions.THICK, Potions.STRONG_POISON);
		registerBrewingRecipe(ModItems.WITHERING_OOZE.get(), Potions.POISON, Potions.HARMING);
		registerBrewingRecipe(ModItems.WITHERING_OOZE.get(), Potions.STRONG_POISON, Potions.STRONG_HARMING);
		registerBrewingRecipe(ModItems.BLOOMBERRY.get(), Potions.MUNDANE, ModPotions.PRIMORDIAL_INFESTATION);
		registerBrewingRecipe(Items.REDSTONE, ModPotions.PRIMORDIAL_INFESTATION, ModPotions.LONG_PRIMORDIAL_INFESTATION);
	}

	private static void registerBrewingRecipe(Item reactant, Holder<Potion> potionBase, Holder<Potion> potionResult) {
		BrewingRecipeRegistry.addRecipe(new BrewingRecipe(createPotionIngredient(potionBase), Ingredient.of(reactant), createPotionStack(potionResult)));
	}

	private static ItemStack createPotionStack(Holder<Potion> potion) {
		return PotionContents.createItemStack(Items.POTION, potion);
	}

	private static Ingredient createPotionIngredient(Holder<Potion> potion) {
		return DataComponentIngredient.of(true, createPotionStack(potion));
	}

	private static <T extends RecipeType<?>, R extends Recipe<Container>> DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> registerRecipeSerializer(DeferredHolder<RecipeType<?>, T> recipeType, Supplier<RecipeSerializer<R>> serializerSupplier) {
		return RECIPE_SERIALIZERS.register(recipeType.getId().getPath(), serializerSupplier);
	}

	private static <T extends RecipeType<?>, R extends Recipe<Container>> DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> registerDynamicRecipeSerializer(DeferredHolder<RecipeType<?>, T> recipeType, String name, Supplier<RecipeSerializer<R>> serializerSupplier) {
		String prefix = recipeType.getId().getPath() + "_dynamic_";
		return RECIPE_SERIALIZERS.register(prefix + name, serializerSupplier);
	}

	private static <T extends CraftingRecipe, R extends CraftingRecipe> DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> registerDynamicCraftingRecipeSerializer(RecipeType<T> recipeType, String name, Supplier<RecipeSerializer<R>> serializerSupplier) {
		String prefix = Objects.requireNonNull(ResourceLocation.tryParse(recipeType.toString())).getPath() + "_dynamic_";
		return RECIPE_SERIALIZERS.register(prefix + name, serializerSupplier);
	}

	private static <R extends CraftingRecipe> DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> registerCraftingRecipeSerializer(String name, Supplier<RecipeSerializer<R>> serializer) {
		return RECIPE_SERIALIZERS.register(name, serializer);
	}

	private static <T extends Recipe<Container>> DeferredHolder<RecipeType<?>, AdvancedRecipeType<T>> registerRecipeType(String namespacedId) {
		return RECIPE_TYPES.register(namespacedId, () -> new AdvancedRecipeType<>(BiomancyMod.rlStr(namespacedId)));
	}

}
