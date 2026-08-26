package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class StaticDigestingRecipe extends StaticProcessingRecipe implements DigestingRecipe {

	private final Ingredient recipeIngredient;
	private final ItemStack recipeResult;

	private final int matchPriority;
	private final NonNullList<Ingredient> vanillaIngredients;

	public StaticDigestingRecipe(ItemStack result, int craftingTimeTicks, int craftingCostNutrients, Ingredient ingredient) {
		super(craftingTimeTicks, craftingCostNutrients);
		recipeIngredient = ingredient;
		recipeResult = result;

		vanillaIngredients = NonNullList.of(Ingredient.EMPTY, recipeIngredient);
		matchPriority = RecipeWithMatchPriority.computeMatchPriority(vanillaIngredients);
	}

	@Override
	public int getMatchPriority() {
		return matchPriority;
	}

	@Override
	public boolean matches(RecipeInput inputInventory, Level worldIn) {
		return recipeIngredient.test(inputInventory.getItem(0));
	}

	@Override
	public ItemStack assemble(RecipeInput inputInventory, HolderLookup.Provider registries) {
		return recipeResult.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height == 1;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return recipeResult;
	}

	@Override
	public Ingredient getIngredient() {
		return recipeIngredient;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return vanillaIngredients;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.DIGESTING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.DIGESTING_RECIPE_TYPE.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.DIGESTER.get());
	}

	public static class Serializer implements RecipeSerializer<StaticDigestingRecipe> {

		public static final MapCodec<StaticDigestingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ItemStack.CODEC.fieldOf(RecipeUtil.JsonKeys.RESULT).forGetter(recipe -> recipe.recipeResult),
				Codec.INT.optionalFieldOf(RecipeUtil.JsonKeys.PROCESSING_TIME, 100).forGetter(recipe -> recipe.craftingTimeTicks),
				Codec.INT.optionalFieldOf(RecipeUtil.JsonKeys.NUTRIENTS_COST, 1).forGetter(recipe -> recipe.craftingCostNutrients),
				Ingredient.CODEC_NONEMPTY.fieldOf(RecipeUtil.JsonKeys.INGREDIENT).forGetter(StaticDigestingRecipe::getIngredient)
		).apply(instance, StaticDigestingRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, StaticDigestingRecipe> STREAM_CODEC = StreamCodec.composite(
				ItemStack.STREAM_CODEC, recipe -> recipe.recipeResult,
				ByteBufCodecs.VAR_INT, recipe -> recipe.craftingTimeTicks,
				ByteBufCodecs.VAR_INT, recipe -> recipe.craftingCostNutrients,
				Ingredient.CONTENTS_STREAM_CODEC, StaticDigestingRecipe::getIngredient,
				StaticDigestingRecipe::new
		);

		@Override
		public MapCodec<StaticDigestingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, StaticDigestingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
