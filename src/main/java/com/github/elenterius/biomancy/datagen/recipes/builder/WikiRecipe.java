package com.github.elenterius.biomancy.datagen.recipes.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.util.GsonHelper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public interface WikiRecipe extends FinishedRecipe {

	default JsonObject serializeWikiRecipe() {
		JsonObject root = new JsonObject();
		root.addProperty("type", BuiltInRegistries.RECIPE_SERIALIZER.getKey(getType()).toString());

		JsonObject input = new JsonObject();
		AtomicInteger i = new AtomicInteger();

		JsonObject output = new JsonObject();
		AtomicInteger o = new AtomicInteger();

		serializeWikiRecipeData(
				in -> input.add(Integer.toString(i.getAndIncrement()), transform(in)),
				out -> output.add(Integer.toString(o.getAndIncrement()), transform(out))
		);

		root.add("input", input);
		root.add("output", output);
		return root;
	}

	static JsonElement transform(JsonElement json) {
		if (json instanceof JsonObject jsonObject) {
			if (jsonObject.has("item")) {
				if (jsonObject.has("id")) throw new RuntimeException("JSON already contains 'id' member!");

				JsonElement item = jsonObject.get("item");
				jsonObject.remove("item");

				if (item instanceof JsonObject) {
					if (jsonObject.has("type")) {
						String type = GsonHelper.getAsString(jsonObject, "type");
						if (type.equals("biomancy:essence")) {
							jsonObject.remove("type");
							jsonObject.addProperty("id", type);
							JsonElement nbt = jsonObject.get("predicate_tag");
							jsonObject.remove("predicate_tag");
							jsonObject.add("nbt", nbt);
						}
						else throw new RuntimeException("Unhandled ingredient type: " + type);
					}
					else throw new RuntimeException("Unknown item type: " + item);
				}
				else {
					jsonObject.add("id", item);
				}
			}
			else if (jsonObject.has("tag")) {
				if (jsonObject.has("id")) throw new RuntimeException("JSON already contains 'id' member!");

				String tag = GsonHelper.getAsString(jsonObject, "tag");
				jsonObject.remove("tag");
				jsonObject.addProperty("id", "#" + tag);
			}
		}
		return json;
	}

	void serializeWikiRecipeData(Consumer<JsonElement> input, Consumer<JsonElement> output);

}
