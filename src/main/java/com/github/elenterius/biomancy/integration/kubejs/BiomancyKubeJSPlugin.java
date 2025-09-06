package com.github.elenterius.biomancy.integration.kubejs;

import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.crafting.ItemCountRange;
import com.github.elenterius.biomancy.crafting.VariableOutput;
import com.github.elenterius.biomancy.crafting.recipe.RecipeUtil;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import net.minecraft.util.valueproviders.ConstantInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomancyKubeJSPlugin extends KubeJSPlugin {

	public static final Logger LOGGER = LogManager.getLogger("Biomancy KubeJS Plugin");

	@Override
	public void registerClasses(ScriptType type, ClassFilter filter) {
		filter.allow("com.github.elenterius.biomancy");

		filter.deny("com.github.elenterius.biomancy.integration");
		filter.deny("com.github.elenterius.biomancy.mixin");
		filter.deny("com.github.elenterius.biomancy.network");
		filter.deny("com.github.elenterius.spatialdb");
		filter.deny("com.github.elenterius.geckolibextras");
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		event.add("Biomancy", new BiomancyKJSBindings());
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
		LOGGER.info("Registering Recipe Schemas...");

		event.register(ModRecipes.DIGESTING_RECIPE_TYPE.getId(), SimpleRecipeSchemas.DIGESTING_SCHEMA);
		event.register(ModRecipes.BIO_BREWING_RECIPE_TYPE.getId(), SimpleRecipeSchemas.BIO_BREWING_SCHEMA);
		event.register(ModRecipes.BIO_FORGING_RECIPE_TYPE.getId(), BioForgingRecipeSchema.SCHEMA);
		event.register(ModRecipes.DECOMPOSING_RECIPE_TYPE.getId(), DecomposingRecipeSchema.SCHEMA);
	}

	interface RecipeKeys {
		RecipeKey<InputItem> INGREDIENT = ItemComponents.INPUT.key(RecipeUtil.JsonKeys.INGREDIENT);
		RecipeKey<InputItem[]> INGREDIENTS = ItemComponents.INPUT_ARRAY.key(RecipeUtil.JsonKeys.INGREDIENTS);

		RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key(RecipeUtil.JsonKeys.RESULT);

		RecipeKey<Integer> PROCESSING_TIME = NumberComponent.INT.key(RecipeUtil.JsonKeys.PROCESSING_TIME).defaultOptional();
		RecipeKey<Integer> NUTRIENTS_COST = NumberComponent.INT.key(RecipeUtil.JsonKeys.NUTRIENTS_COST).defaultOptional();
	}

	interface SimpleRecipeSchemas {
		RecipeSchema DIGESTING_SCHEMA = new RecipeSchema(RecipeKeys.INGREDIENT, RecipeKeys.RESULT, RecipeKeys.PROCESSING_TIME, RecipeKeys.NUTRIENTS_COST);

		RecipeKey<InputItem> REACTANT = ItemComponents.INPUT.key(RecipeUtil.JsonKeys.REACTANT);
		RecipeSchema BIO_BREWING_SCHEMA = new RecipeSchema(RecipeKeys.INGREDIENTS, REACTANT, RecipeKeys.RESULT, RecipeKeys.PROCESSING_TIME, RecipeKeys.NUTRIENTS_COST);
	}

	interface BioForgingRecipeSchema {
		RecipeComponent<InputItem> INPUT_WITH_COUNT = new RecipeComponentWithParent<>() {
			@Override
			public RecipeComponent<InputItem> parentComponent() {
				return ItemComponents.INPUT;
			}

			@Override
			public JsonElement write(RecipeJS recipe, InputItem input) {
				IngredientStack ingredientStack = new IngredientStack(input.ingredient, input.count);
				return ingredientStack.toJson();
			}

			// not needed because InputItem can read count json keys
			//		@Override
			//		public InputItem read(RecipeJS recipe, Object from) {
			//			if (from instanceof JsonObject json) {
			//				IngredientStack ingredientStack = IngredientStack.fromJson(json);
			//				return InputItem.of(ingredientStack.ingredient(), ingredientStack.count());
			//			}
			//			return parentComponent().read(recipe, from);
			//		}

			@Override
			public String toString() {
				return parentComponent().toString();
			}
		};

		RecipeKey<InputItem[]> INGREDIENTS_WITH_COUNT = INPUT_WITH_COUNT.asArray().key(RecipeUtil.JsonKeys.INGREDIENTS);
		RecipeKey<String> BIO_FORGE_TAB = StringComponent.ID.key(RecipeUtil.JsonKeys.BIO_FORGE_TAB);

		RecipeSchema SCHEMA = new RecipeSchema(INGREDIENTS_WITH_COUNT, RecipeKeys.RESULT, BIO_FORGE_TAB, RecipeKeys.NUTRIENTS_COST);
	}

	interface DecomposingRecipeSchema {
		RecipeComponent<OutputItem> VARIABLE_OUTPUT = new RecipeComponentWithParent<>() {
			@Override
			public RecipeComponent<OutputItem> parentComponent() {
				return ItemComponents.OUTPUT;
			}

			@Override
			public JsonElement write(RecipeJS recipe, OutputItem output) {
				if (output.rolls == null) {
					VariableOutput variableOutput = new VariableOutput(output.item);
					return variableOutput.serialize();
				}
				else if (output.rolls instanceof ConstantInt c) {
					VariableOutput variableOutput = new VariableOutput(output.item.getItem(), c.getValue());
					return variableOutput.serialize();
				}
				else {
					VariableOutput variableOutput = new VariableOutput(output.item.getItem(), output.rolls.getMinValue(), output.rolls.getMaxValue());
					return variableOutput.serialize();
				}
			}

			@Override
			public OutputItem read(RecipeJS recipe, Object from) {
				if (from instanceof JsonObject json) {
					VariableOutput variableOutput = VariableOutput.deserialize(json);
					OutputItem output = OutputItem.of(variableOutput.getItemStack());

					ItemCountRange countRange = variableOutput.getCountRange();
					if (countRange instanceof ItemCountRange.ConstantValue c) {
						return output.withCount(c.value());
					}
					else if (countRange instanceof ItemCountRange.UniformRange range) {
						return output.withRolls(range.min(), range.max());
					}

					// BinomialRange is not supported by OutputItem. Technically we could extend IntProvider, but we won't do that for the KubeJSPlugin.
					return output;
				}

				return parentComponent().read(recipe, from);
			}

			@Override
			public String toString() {
				return parentComponent().toString();
			}
		};

		RecipeKey<OutputItem[]> RESULTS = VARIABLE_OUTPUT.asArray().key(RecipeUtil.JsonKeys.RESULTS);

		RecipeSchema SCHEMA = new RecipeSchema(RecipeKeys.INGREDIENT, RESULTS, RecipeKeys.PROCESSING_TIME, RecipeKeys.NUTRIENTS_COST);
	}

}
