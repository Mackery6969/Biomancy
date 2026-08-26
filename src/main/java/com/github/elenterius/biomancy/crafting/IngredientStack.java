package com.github.elenterius.biomancy.crafting;

import com.github.elenterius.biomancy.crafting.recipe.RecipeUtil;
import com.github.elenterius.biomancy.util.ItemStackCounter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public record IngredientStack(Ingredient ingredient, int count) {

	public static final String ALT_INGREDIENT_KEY = "alt"; //legacy support, unused by Biomancy
	public static final String COUNT_KEY = "count";

	public static final Codec<IngredientStack> CODEC = RecipeUtil.jsonBridgeCodec(IngredientStack::toJson, IngredientStack::fromJson);
	public static final StreamCodec<RegistryFriendlyByteBuf, IngredientStack> STREAM_CODEC = StreamCodec.of((buf, value) -> value.toNetwork(buf), IngredientStack::fromNetwork);

	public ItemStack[] getItems() {
		return ingredient.getItems();
	}

	public boolean testItem(@Nullable ItemStack stack) {
		return ingredient.test(stack);
	}

	public boolean hasSufficientCount(StackedContents itemCounter) {
		IntList stackingIds = ingredient.getStackingIds();

		int n = 0;
		for (int i = 0; i < stackingIds.size(); i++) {
			n += itemCounter.contents.get(stackingIds.getInt(i));
			if (n >= count) return true;
		}

		return false;
	}

	public boolean hasSufficientCount(ItemStackCounter itemCounter) {
		List<ItemStackCounter.CountedItem> itemCounts = itemCounter.getItemCounts();

		int n = 0;
		for (ItemStackCounter.CountedItem countedItem : itemCounts) {
			if (ingredient.test(countedItem.stack())) n += countedItem.amount();
			if (n >= count) return true;
		}

		return false;
	}

	public List<ItemStack> getItemsWithCount() {
		if (count == 1) return List.of(ingredient.getItems());
		return Arrays.stream(ingredient.getItems()).map(this::copyItemStackWithCount).toList();
	}

	private ItemStack copyItemStackWithCount(ItemStack stack) {
		if (count == 0) return ItemStack.EMPTY;
		ItemStack copy = stack.copy();
		copy.setCount(count);
		return copy;
	}

	public JsonObject toJson() {
		JsonElement ingredientJson = Ingredient.CODEC_NONEMPTY.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow();

		if (ingredientJson.isJsonArray()) {
			JsonObject json = new JsonObject();
			json.add(ALT_INGREDIENT_KEY, ingredientJson);
			if (count > 1) json.addProperty(COUNT_KEY, count);
			return json;
		}

		JsonObject json = ingredientJson.getAsJsonObject();
		if (count > 1) json.addProperty(COUNT_KEY, count);
		return json;
	}

	public static IngredientStack fromJson(JsonObject json) {
		Ingredient ingredient = readIngredient(json);
		int count = GsonHelper.getAsInt(json, COUNT_KEY, 1);
		return new IngredientStack(ingredient, count);
	}

	private static Ingredient readIngredient(JsonObject json) {
		JsonElement ingredientJson = GsonHelper.isArrayNode(json, ALT_INGREDIENT_KEY) ? GsonHelper.getAsJsonArray(json, ALT_INGREDIENT_KEY) : json;
		return Ingredient.CODEC_NONEMPTY.parse(JsonOps.INSTANCE, ingredientJson).getOrThrow();
	}

	public static IngredientStack fromNetwork(RegistryFriendlyByteBuf buffer) {
		Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
		int count = buffer.readVarInt();

		return new IngredientStack(ingredient, count);
	}

	public void toNetwork(RegistryFriendlyByteBuf buffer) {
		Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
		buffer.writeVarInt(count);
	}

}
