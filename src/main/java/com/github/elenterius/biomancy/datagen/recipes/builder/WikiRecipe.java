package com.github.elenterius.biomancy.datagen.recipes.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;

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
				in -> input.add(Integer.toString(i.getAndIncrement()), in),
				out -> output.add(Integer.toString(o.getAndIncrement()), out)
		);

		root.add("input", input);
		root.add("output", output);
		return root;
	}

	void serializeWikiRecipeData(Consumer<JsonElement> input, Consumer<JsonElement> output);

}
