package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.crafting.VariableOutput;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.NonNullList;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class RecipeUtil {

	public static final class JsonKeys {
		public static final String INGREDIENT = "ingredient";
		public static final String INGREDIENTS = "ingredients";
		public static final String REACTANT = "reactant";
		public static final String RESULT = "result";
		public static final String RESULTS = "results";
		public static final String PROCESSING_TIME = "processingTime";
		public static final String NUTRIENTS_COST = "nutrientsCost";
		public static final String BIO_FORGE_TAB = BioForgeTab.JSON_KEY;

		// misc recipe stuff
		public static final String GROUP = "group";
		public static final String CONDITIONS = "conditions";

		// item related keys
		public static final String COUNT = "count";
		public static final String TAG = "tag";
		public static final String ID = "id";

		private JsonKeys() {}
	}

	public static final class TagKeys {
		public static final String FORGE_CAPS = "ForgeCaps";

		private TagKeys() {}
	}

	private RecipeUtil() {}

	public static Ingredient readIngredient(JsonObject json, String memberName) {
		JsonElement ingredientJson = GsonHelper.isArrayNode(json, memberName) ? GsonHelper.getAsJsonArray(json, memberName) : GsonHelper.getAsJsonObject(json, memberName);
		return Ingredient.CODEC_NONEMPTY.parse(JsonOps.INSTANCE, ingredientJson).getOrThrow();
	}

	public static NonNullList<Ingredient> readIngredients(JsonArray jsonArray) {
		NonNullList<Ingredient> list = NonNullList.create();
		for (int i = 0; i < jsonArray.size(); i++) {
			Ingredient ingredient = Ingredient.CODEC_NONEMPTY.parse(JsonOps.INSTANCE, jsonArray.get(i)).getOrThrow();
			if (!ingredient.isEmpty()) {
				list.add(ingredient);
			}
		}
		return list;
	}

	public static List<VariableOutput> readVariableProductionOutputs(JsonArray jsonArray) {
		List<VariableOutput> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			list.add(VariableOutput.deserialize(jsonArray.get(i).getAsJsonObject()));
		}
		return list;
	}

	public static List<IngredientStack> readIngredientStacks(JsonArray jsonArray) {
		List<IngredientStack> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); ++i) {
			IngredientStack ingredientStack = IngredientStack.fromJson(jsonArray.get(i).getAsJsonObject());
			if (!ingredientStack.ingredient().isEmpty()) {
				list.add(ingredientStack);
			}
		}
		return list;
	}

	public static List<Ingredient> flattenIngredientStacks(List<IngredientStack> ingredients) {
		List<Ingredient> flatIngredients = new ArrayList<>();
		for (IngredientStack ingredientStack : ingredients) {
			Ingredient ingredient = ingredientStack.ingredient();
			for (int i = 0; i < ingredientStack.count(); i++) {
				flatIngredients.add(ingredient); //insert the same ingredient instances
			}
		}
		return flatIngredients;
	}

	public static <T> Codec<T> jsonBridgeCodec(Function<T, JsonObject> encoder, Function<JsonObject, T> decoder) {
		return new Codec<>() {
			@Override
			public <O> DataResult<Pair<T, O>> decode(DynamicOps<O> ops, O input) {
				try {
					JsonElement json = ops.convertTo(JsonOps.INSTANCE, input);
					return DataResult.success(Pair.of(decoder.apply(json.getAsJsonObject()), ops.empty()));
				}
				catch (Exception ex) {
					return DataResult.error(ex::getMessage);
				}
			}

			@Override
			public <O> DataResult<O> encode(T input, DynamicOps<O> ops, O prefix) {
				return DataResult.success(JsonOps.INSTANCE.convertTo(ops, encoder.apply(input)));
			}
		};
	}

}
