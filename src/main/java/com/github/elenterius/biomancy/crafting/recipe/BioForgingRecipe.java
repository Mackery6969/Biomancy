package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import com.github.elenterius.biomancy.util.ItemStackCounter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public class BioForgingRecipe implements Recipe<RecipeInput> {

	public static final byte DEFAULT_CRAFTING_COST_NUTRIENTS = 1;
	public static final int MAX_INGREDIENTS = 5;
	private final BioForgeTab tab;
	private final List<IngredientStack> ingredients;
	private final ItemStack result;

	private final NonNullList<Ingredient> vanillaIngredients;

	private final int cost;

	public BioForgingRecipe(List<IngredientStack> ingredients, ItemStack result, BioForgeTab tab, int craftingCostNutrients) {
		this.tab = tab;
		this.result = result;
		this.ingredients = ingredients;

		List<Ingredient> flatIngredients = RecipeUtil.flattenIngredientStacks(ingredients);
		vanillaIngredients = NonNullList.createWithCapacity(flatIngredients.size());
		vanillaIngredients.addAll(flatIngredients);

		cost = craftingCostNutrients;
	}

	public int getCraftingCostNutrients() {
		return cost;
	}

	public boolean isCraftable(StackedContents itemCounter) {
		for (IngredientStack ingredientStack : ingredients) {
			if (!ingredientStack.hasSufficientCount(itemCounter)) {
				return false;
			}
		}

		return true;
	}

	public boolean isCraftable(ItemStackCounter itemCounter) {
		int[] residuals = new int[ingredients.size()];
		int totalResidual = 0;
		for (int i = 0; i < ingredients.size(); i++) {
			int count = ingredients.get(i).count();
			residuals[i] = count;
			totalResidual += count;
		}

		for (ItemStackCounter.CountedItem countedItem : itemCounter.getItemCounts()) {
			if (totalResidual <= 0) return true;

			int available = countedItem.amount();

			for (int i = 0; i < ingredients.size(); i++) {
				if (available <= 0) break;

				final int residual = residuals[i];
				if (residual > 0 && ingredients.get(i).testItem(countedItem.stack())) {
					final int amount = Math.min(residual, available);
					residuals[i] -= amount;
					available -= amount;
					totalResidual -= amount;
				}
			}
		}

		return totalResidual <= 0;
	}

	@Override
	public boolean matches(RecipeInput input, Level level) {
		int[] countedIngredients = new int[ingredients.size()];
		for (int idx = 0; idx < input.size(); idx++) {
			ItemStack stack = input.getItem(idx);
			if (!stack.isEmpty()) {
				for (int i = 0; i < ingredients.size(); i++) {
					if (ingredients.get(i).testItem(stack)) {
						countedIngredients[i] += stack.getCount();
						break;
					}
				}
			}
		}

		for (int i = 0; i < ingredients.size(); i++) {
			if (countedIngredients[i] < ingredients.get(i).count()) return false;
		}

		return true;
	}

	@Override
	public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
		return result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 0;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return result;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return vanillaIngredients;
	}

	public List<IngredientStack> getIngredientQuantities() {
		return ingredients;
	}

	public BioForgeTab getTab() {
		return tab;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.BIO_FORGING_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipes.BIO_FORGING_RECIPE_TYPE.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.BIO_FORGE.get());
	}

	public static class Serializer implements RecipeSerializer<BioForgingRecipe> {

		private static final Codec<BioForgeTab> TAB_CODEC = Codec.STRING.flatXmap(id -> {
			if (id.equals("biomancy:weapons")) {
				BiomancyMod.LOGGER.warn("A recipe uses the deprecated \"biomancy:weapons\" bio-forge tab instead of \"biomancy:tools\". Using \"biomancy:tools\" fallback, please update your recipe.");
				return DataResult.success(ModBioForgeTabs.TOOLS.get());
			}

			ResourceLocation location = ResourceLocation.tryParse(id);
			BioForgeTab tab = location != null ? ModBioForgeTabs.REGISTRY.get(location) : null;
			return tab != null ? DataResult.success(tab) : DataResult.error(() -> "Unknown Bio-Forge tab '" + id + "'");
		}, tab -> DataResult.success(ModBioForgeTabs.REGISTRY.getKey(tab).toString()));

		public static final MapCodec<BioForgingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				IngredientStack.CODEC.listOf(1, MAX_INGREDIENTS).fieldOf(RecipeUtil.JsonKeys.INGREDIENTS).forGetter(BioForgingRecipe::getIngredientQuantities),
				ItemStack.CODEC.fieldOf(RecipeUtil.JsonKeys.RESULT).forGetter(recipe -> recipe.result),
				TAB_CODEC.fieldOf(RecipeUtil.JsonKeys.BIO_FORGE_TAB).forGetter(BioForgingRecipe::getTab),
				Codec.INT.optionalFieldOf(RecipeUtil.JsonKeys.NUTRIENTS_COST, (int) DEFAULT_CRAFTING_COST_NUTRIENTS).forGetter(BioForgingRecipe::getCraftingCostNutrients)
		).apply(instance, BioForgingRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, BioForgingRecipe> STREAM_CODEC = StreamCodec.composite(
				IngredientStack.STREAM_CODEC.apply(ByteBufCodecs.list()), BioForgingRecipe::getIngredientQuantities,
				ItemStack.STREAM_CODEC, recipe -> recipe.result,
				BioForgeTab.STREAM_CODEC, BioForgingRecipe::getTab,
				ByteBufCodecs.VAR_INT, BioForgingRecipe::getCraftingCostNutrients,
				BioForgingRecipe::new
		);

		@Override
		public MapCodec<BioForgingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BioForgingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}

}
