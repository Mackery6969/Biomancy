package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.datagen.recipes.builder.WikiRecipe;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class WikiRecipesProvider implements DataProvider {

	public static Set<RecipeSerializer<? extends Recipe<?>>> SUPPORTED_VANILLA_RECIPES = Set.of(
			RecipeSerializer.SMELTING_RECIPE, RecipeSerializer.BLASTING_RECIPE, RecipeSerializer.CAMPFIRE_COOKING_RECIPE,
			RecipeSerializer.SMOKING_RECIPE, RecipeSerializer.SHAPED_RECIPE, RecipeSerializer.SHAPELESS_RECIPE,
			RecipeSerializer.SMITHING_TRANSFORM, RecipeSerializer.STONECUTTER
	);

	private final ModRecipeProvider recipeProvider;
	private final Path basePath;

	public WikiRecipesProvider(PackOutput output, ModRecipeProvider recipeProvider) {
		this.recipeProvider = recipeProvider;
		basePath = output.getOutputFolder().resolve(".wiki_data");
	}

	public Path resolveJsonPath(ResourceLocation location) {
		return basePath.resolve(location.getNamespace()).resolve("recipe").resolve(location.getPath() + ".json");
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		Set<ResourceLocation> set = Sets.newHashSet();
		List<CompletableFuture<?>> list = new ArrayList<>();

		recipeProvider.buildRecipes(finishedRecipe -> {
			if (!set.add(finishedRecipe.getId())) {
				throw new IllegalStateException("Duplicate recipe " + finishedRecipe.getId());
			}
			else {
				if (finishedRecipe instanceof WikiRecipe wikiRecipe) {
					list.add(DataProvider.saveStable(output, wikiRecipe.serializeWikiRecipe(), resolveJsonPath(finishedRecipe.getId())));
				}
				else if (SUPPORTED_VANILLA_RECIPES.contains(finishedRecipe.getType())) {
					list.add(DataProvider.saveStable(output, serializeTransformVanilla(finishedRecipe), resolveJsonPath(finishedRecipe.getId())));
				}
			}
		});

		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	private JsonObject serializeTransformVanilla(FinishedRecipe finishedRecipe) {
		JsonObject json = finishedRecipe.serializeRecipe();

		RecipeSerializer<?> type = finishedRecipe.getType();

		if (SUPPORTED_VANILLA_RECIPES.contains(type)) {

			if (type != RecipeSerializer.STONECUTTER && type != RecipeSerializer.BLASTING_RECIPE) {
				if (json.has("ingredient")) {
					WikiRecipe.transform(json.get("ingredient"));
				}
			}

//			if (json.has("ingredients")) {
//				JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
//				for (int i = 0; i < ingredients.size(); i++) {
//					JsonElement jsonElement = ingredients.get(i);
//					WikiRecipe.transform(jsonElement);
////					if (WikiRecipe.transform(jsonElement) instanceof JsonObject obj) {
////						String id = GsonHelper.getAsString(obj, "id");
////						ingredients.set(i, new JsonPrimitive(id));
////					}
//				}
//			}

			if (json.has("result")) {
				JsonElement result = json.get("result");
				if (result instanceof JsonObject) {
					WikiRecipe.transform(result);
				}
				else {
					JsonObject resultOverride = new JsonObject();
					resultOverride.addProperty("id", GsonHelper.getAsString(json, "result"));
					json.add("result", resultOverride);
				}
			}
		}

		return json;
	}

	@Override
	public String getName() {
		return "Wiki Recipe Provider";
	}

}
