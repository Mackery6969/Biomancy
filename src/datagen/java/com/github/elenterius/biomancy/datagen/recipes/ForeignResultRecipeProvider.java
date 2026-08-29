package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModItems;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Emits bio-forging recipes whose result belongs to a mod that is absent at datagen time.
 * Such results cannot round-trip through the recipe codec, so the json is written directly.
 */
public class ForeignResultRecipeProvider implements DataProvider {

	private final PackOutput.PathProvider recipePathProvider;

	public ForeignResultRecipeProvider(PackOutput output) {
		recipePathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		List<CompletableFuture<?>> futures = new ArrayList<>();

		futures.add(bioForging(output, "dramaticdoors", "tall_flesh_door"));
		futures.add(bioForging(output, "dramaticdoors", "tall_full_flesh_door"));

		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}

	private CompletableFuture<?> bioForging(CachedOutput output, String modId, String resultPath) {
		ResourceLocation resultId = ResourceLocation.fromNamespaceAndPath(modId, resultPath);

		JsonObject json = new JsonObject();
		json.addProperty("type", "biomancy:bio_forging");

		JsonArray conditions = new JsonArray();
		JsonObject modLoaded = new JsonObject();
		modLoaded.addProperty("type", "neoforge:mod_loaded");
		modLoaded.addProperty("modid", modId);
		conditions.add(modLoaded);
		json.add("neoforge:conditions", conditions);

		json.addProperty("bio_forge_tab", ModBioForgeTabs.MISC.getId().toString());

		JsonArray ingredients = new JsonArray();
		addIngredient(ingredients, ModItems.FLESH_BITS.get(), 9);
		addIngredient(ingredients, ModItems.BONE_FRAGMENTS.get(), 9);
		addIngredient(ingredients, ModItems.ELASTIC_FIBERS.get(), 6);
		addIngredient(ingredients, ModItems.TOUGH_FIBERS.get(), 3);
		json.add("ingredients", ingredients);

		JsonObject result = new JsonObject();
		result.addProperty("count", 1);
		result.addProperty("id", resultId.toString());
		json.add("result", result);

		Path path = recipePathProvider.json(BiomancyMod.rl(resultPath));
		return DataProvider.saveStable(output, json, path);
	}

	private static void addIngredient(JsonArray ingredients, Item item, int count) {
		JsonObject ingredient = new JsonObject();
		ingredient.addProperty("count", count);
		ingredient.addProperty("item", BuiltInRegistries.ITEM.getKey(item).toString());
		ingredients.add(ingredient);
	}

	@Override
	public String getName() {
		return "Biomancy's Foreign Result Recipes";
	}

}
