package com.github.elenterius.biomancy.integration.kubejs;

import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.api.tribute.SimpleTribute;
import com.github.elenterius.biomancy.api.tribute.Tributes;
import com.github.elenterius.biomancy.crafting.EssenceIngredient;
import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.crafting.ItemCountRange;
import com.github.elenterius.biomancy.crafting.VariableOutput;
import com.github.elenterius.biomancy.crafting.recipe.RecipeUtil;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.github.elenterius.biomancy.menu.BioForgeTab;
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
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.util.ClassFilter;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class BiomancyKubeJSPlugin extends KubeJSPlugin {

	public static final Logger LOGGER = LogManager.getLogger("Biomancy KubeJS Plugin");

	public static final RegistryInfo<Serum> SERUM_REGISTRY = RegistryInfo.of(ModSerums.SERUMS.getRegistryKey(), Serum.class);
	public static final RegistryInfo<BioForgeTab> BIO_FORGE_TAB_REGISTRY = RegistryInfo.of(ModBioForgeTabs.BIO_FORGE_TABS.getRegistryKey(), BioForgeTab.class);

	@Override
	public void init() {
		SERUM_REGISTRY.addType("basic", SerumBuilder.class, SerumBuilder::new);
		RegistryInfo.ITEM.addType("biomancy:basic_serum", SerumItemBuilder.class, SerumItemBuilder::new);

		BIO_FORGE_TAB_REGISTRY.addType("basic", BioForgeTabBuilder.class, BioForgeTabBuilder::new);
	}

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
		event.add("Biomancy$EssenceIngredient", EssenceIngredientUtil.class);
		event.add("Biomancy$EssenceItem", EssenceItemUtil.class);
		event.add("Biomancy$Nutrients", Nutrients.class);
		event.add("Biomancy$Tributes", Tributes.class);
		event.add("Biomancy$SimpleTribute", SimpleTribute.class);
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

	interface EssenceIngredientUtil {

		@Info(
				value = "Creates a essence ingredient that matches a specific tier",
				params = {
						@Param(name = "entityType"),
						@Param(name = "tier", value = "The tier that this essence ingredient requires. Valid tiers are: 0, 1, 2 or 3")
				}
		)
		static EssenceIngredient fromTier(EntityType<?> entityType, int tier) {
			BiomancyKubeJSPlugin.LOGGER.warn("Creating EssenceIngredient for {} with tier {}", entityType.getDescriptionId(), tier);
			return EssenceIngredient.of(entityType, tier);
		}

		@Info(
				value = "Creates a essence ingredient that matches any tier",
				params = {@Param(name = "entityType")}
		)
		static EssenceIngredient from(EntityType<?> entityType) {
			BiomancyKubeJSPlugin.LOGGER.warn("Creating EssenceIngredient for {} with tier -1", entityType.getDescriptionId());
			return EssenceIngredient.of(entityType);
		}

	}

	interface EssenceItemUtil {

		@Info(
				value = "Creates a tier 1 essence from the EntityType of a LivingEntity",
				params = {@Param(name = "entityType")}
		)
		static ItemStack from(EntityType<?> entityType) {
			return EssenceItem.fromEntityType(entityType, 1);
		}

		@Info(
				value = "Creates a tier x essence from the EntityType of a LivingEntity",
				params = {
						@Param(name = "entityType"),
						@Param(name = "tier", value = "Quality tier of the essence. Valid tiers are: 1, 2 or 3")
				}
		)
		static ItemStack fromTier(EntityType<?> entityType, int tier) {
			return EssenceItem.fromEntityType(entityType, tier);
		}

		@Info(
				value = "Creates a unique essence from the EntityType of a LivingEntity",
				params = {
						@Param(name = "entityType"),
						@Param(name = "uuid", value = "UUID of the LivingEntity")
				}
		)
		static ItemStack fromUUID(EntityType<?> entityType, UUID uuid) {
			return EssenceItem.fromEntityType(entityType, uuid);
		}

		@Info(
				value = "Creates a tier 1 or 2 essence from a LivingEntity",
				params = {@Param(name = "livingEntity")}
		)
		static ItemStack fromLiving(LivingEntity livingEntity) {
			return EssenceItem.fromEntity(livingEntity, 0, 0);
		}

		@Info(
				value = "Creates a tier x essence from a LivingEntity. The tier depends on the enchantment level of surgical precision.",
				params = {
						@Param(name = "livingEntity"),
						@Param(name = "surgicalPrecisionLevel", value = "Level of surgical precision enchantment"),
						@Param(name = "lootingLevel", value = "Level of looting enchantment")
				}
		)
		static ItemStack fromLivingWith(LivingEntity livingEntity, int surgicalPrecisionLevel, int lootingLevel) {
			return EssenceItem.fromEntity(livingEntity, surgicalPrecisionLevel, lootingLevel);
		}

	}

}
