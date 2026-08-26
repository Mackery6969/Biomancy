package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.crafting.AnyFoodIngredient;
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
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FoodDigestingRecipe extends DynamicProcessingRecipe implements DigestingRecipe {

	private final int multiplier;
	private final ItemStack resultBaseItem;
	private final Ingredient ingredient;

	private final int matchPriority;
	private final NonNullList<Ingredient> vanillaIngredients;

	protected FoodDigestingRecipe(int multiplier, ItemStack resultBaseItem) {
		super(ModRecipes.DIGESTING_RECIPE_TYPE.get());
		this.multiplier = multiplier;
		this.resultBaseItem = resultBaseItem;

		ingredient = new AnyFoodIngredient().toVanilla();

		vanillaIngredients = NonNullList.of(Ingredient.EMPTY, ingredient);
		matchPriority = RecipeWithMatchPriority.computeMatchPriority(vanillaIngredients);
	}

	public static int getFoodNutrition(ItemStack stack) {
		if (stack.isEmpty()) return 0;

		FoodProperties foodProperties = stack.getFoodProperties(null);
		return foodProperties != null ? foodProperties.nutrition() : 0;
	}

	@Override
	public int getMatchPriority() {
		return matchPriority;
	}

	@Override
	public boolean matches(RecipeInput inputInventory, Level pLevel) {
		int nutrition = getFoodNutrition(inputInventory.getItem(0));
		return nutrition > 0;
	}

	@Override
	public ItemStack assemble(RecipeInput inputInventory, HolderLookup.Provider registries) {
		int nutrition = getFoodNutrition(inputInventory.getItem(0));
		if (nutrition <= 0) return ItemStack.EMPTY;

		int count = Mth.clamp(nutrition * multiplier, 1, 64 * 2);
		return resultBaseItem.copyWithCount(count);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height == 1;
	}

	@Override
	public Ingredient getIngredient() {
		return ingredient;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return vanillaIngredients;
	}

	@Override
	public int getCraftingTimeTicks(RecipeInput inputInventory) {
		int nutrition = getFoodNutrition(inputInventory.getItem(0));
		return nutrition > 0 ? Mth.ceil(200 + 190 * Math.log(nutrition)) : 0;
	}

	@Override
	public int getCraftingCostNutrients(RecipeInput inputInventory) {
		float sixtySecondsInTicks = 1200;
		return 1 + Mth.floor(getCraftingTimeTicks(inputInventory) / sixtySecondsInTicks);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.FOOD_DIGESTING_SERIALIZER.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.DIGESTER.get());
	}

	public static class Serializer implements RecipeSerializer<FoodDigestingRecipe> {

		public static final MapCodec<FoodDigestingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.INT.optionalFieldOf("multiplier", 1).forGetter(recipe -> recipe.multiplier),
				ItemStack.CODEC.fieldOf("result_base").forGetter(recipe -> recipe.resultBaseItem)
		).apply(instance, FoodDigestingRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, FoodDigestingRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, recipe -> recipe.multiplier,
				ItemStack.STREAM_CODEC, recipe -> recipe.resultBaseItem,
				FoodDigestingRecipe::new
		);

		@Override
		public MapCodec<FoodDigestingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, FoodDigestingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}

}
