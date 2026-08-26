package com.github.elenterius.biomancy.integration.kubejs;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.api.tribute.SimpleTribute;
import com.github.elenterius.biomancy.api.tribute.Tributes;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleEvents;
import com.github.elenterius.biomancy.crafting.EssenceIngredient;
import com.github.elenterius.biomancy.crafting.IngredientStack;
import com.github.elenterius.biomancy.crafting.VariableOutput;
import com.github.elenterius.biomancy.crafting.recipe.RecipeUtil;
import com.github.elenterius.biomancy.entity.mob.fleshblob.FleshBlob;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.util.Cast;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.UUID;

public class BiomancyKubeJSPlugin implements KubeJSPlugin {

	public static final Logger LOGGER = LogManager.getLogger("Biomancy KubeJS Plugin");

	@Override
	public void init() {
		NeoForge.EVENT_BUS.addListener(BiomancyKJSEvents::canCradleSpawnMob);
		NeoForge.EVENT_BUS.addListener(BiomancyKJSEvents::onCradleSpawnMob);
	}

	@Override
	public void registerBuilderTypes(BuilderTypeRegistry registry) {
		registry.of(Cast.<ResourceKey<Registry<Serum>>>to(ModSerums.SERUMS.getRegistryKey()), reg -> reg.addDefault(SerumBuilder.class, SerumBuilder::new));
		registry.of(Registries.ITEM, reg -> reg.add(BiomancyMod.rl("basic_serum"), SerumItemBuilder.class, SerumItemBuilder::new));
		registry.of(Cast.<ResourceKey<Registry<BioForgeTab>>>to(ModBioForgeTabs.BIO_FORGE_TABS.getRegistryKey()), reg -> reg.addDefault(BioForgeTabBuilder.class, BioForgeTabBuilder::new));
	}

	@Override
	public void registerEvents(EventGroupRegistry registry) {
		registry.register(BiomancyKJSEvents.GROUP);
	}

	@Override
	public void registerClasses(ClassFilter filter) {
		filter.allow("com.github.elenterius.biomancy");

		filter.deny("com.github.elenterius.biomancy.integration");
		filter.deny("com.github.elenterius.biomancy.mixin");
		filter.deny("com.github.elenterius.biomancy.network");
		filter.deny("com.github.elenterius.spatialdb");
		filter.deny("com.github.elenterius.geckolibextras");
	}

	@Override
	public void registerBindings(BindingRegistry bindings) {
		bindings.add("Biomancy$EssenceIngredient", EssenceIngredientUtil.class);
		bindings.add("Biomancy$EssenceItem", EssenceItemUtil.class);
		bindings.add("Biomancy$Nutrients", Nutrients.class);
		bindings.add("Biomancy$Tributes", Tributes.class);
		bindings.add("Biomancy$SimpleTribute", SimpleTribute.class);
		bindings.add("Biomancy$FleshBlob", FleshBlob.class);
		bindings.add("Biomancy$CradleEvent$CanSpawnMob", PrimordialCradleEvents.CanSpawnMob.class);
		bindings.add("Biomancy$CradleEvent$OnSpawnMob", PrimordialCradleEvents.OnSpawnMob.class);
	}

	@Override
	public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
		LOGGER.info("Registering Recipe Schemas...");

		registry.register(ModRecipes.DIGESTING_RECIPE_TYPE.getId(), SimpleRecipeSchemas.DIGESTING_SCHEMA);
		registry.register(ModRecipes.BIO_BREWING_RECIPE_TYPE.getId(), SimpleRecipeSchemas.BIO_BREWING_SCHEMA);
		registry.register(ModRecipes.BIO_FORGING_RECIPE_TYPE.getId(), BioForgingRecipeSchema.SCHEMA);
		registry.register(ModRecipes.DECOMPOSING_RECIPE_TYPE.getId(), DecomposingRecipeSchema.SCHEMA);
	}

	private static <T> Codec<T> jsonBridgeCodec(java.util.function.Function<T, JsonObject> encoder, java.util.function.Function<JsonObject, T> decoder) {
		return new Codec<>() {
			@Override
			public <O> DataResult<Pair<T, O>> decode(DynamicOps<O> ops, O input) {
				try {
					var json = ops.convertTo(JsonOps.INSTANCE, input);
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

	interface RecipeKeys {
		RecipeKey<Ingredient> INGREDIENT = IngredientComponent.INGREDIENT.instance().inputKey(RecipeUtil.JsonKeys.INGREDIENT);
		RecipeKey<List<Ingredient>> INGREDIENTS = IngredientComponent.INGREDIENT.instance().asList().inputKey(RecipeUtil.JsonKeys.INGREDIENTS);

		RecipeKey<ItemStack> RESULT = ItemStackComponent.ITEM_STACK.instance().outputKey(RecipeUtil.JsonKeys.RESULT);

		RecipeKey<Integer> PROCESSING_TIME = NumberComponent.INT.otherKey(RecipeUtil.JsonKeys.PROCESSING_TIME).defaultOptional();
		RecipeKey<Integer> NUTRIENTS_COST = NumberComponent.INT.otherKey(RecipeUtil.JsonKeys.NUTRIENTS_COST).defaultOptional();
	}

	interface SimpleRecipeSchemas {
		RecipeSchema DIGESTING_SCHEMA = new RecipeSchema(RecipeKeys.INGREDIENT, RecipeKeys.RESULT, RecipeKeys.PROCESSING_TIME, RecipeKeys.NUTRIENTS_COST);

		RecipeKey<Ingredient> REACTANT = IngredientComponent.INGREDIENT.instance().inputKey(RecipeUtil.JsonKeys.REACTANT);
		RecipeSchema BIO_BREWING_SCHEMA = new RecipeSchema(RecipeKeys.INGREDIENTS, REACTANT, RecipeKeys.RESULT, RecipeKeys.PROCESSING_TIME, RecipeKeys.NUTRIENTS_COST);
	}

	interface BioForgingRecipeSchema {
		RecipeComponentType<IngredientStack> INPUT_WITH_COUNT_TYPE = RecipeComponentType.unit(BiomancyMod.rl("ingredient_with_count"), type -> new RecipeComponent<>() {
			@Override
			public RecipeComponentType<?> type() {
				return type;
			}

			@Override
			public Codec<IngredientStack> codec() {
				return jsonBridgeCodec(IngredientStack::toJson, IngredientStack::fromJson);
			}

			@Override
			public dev.latvian.mods.rhino.type.TypeInfo typeInfo() {
				return IngredientComponent.INGREDIENT.instance().typeInfo();
			}

			@Override
			public IngredientStack wrap(RecipeScriptContext cx, Object from) {
				Ingredient ingredient = IngredientComponent.INGREDIENT.instance().wrap(cx, from);
				return new IngredientStack(ingredient, 1);
			}

			@Override
			public boolean isEmpty(IngredientStack value) {
				return value.ingredient().isEmpty();
			}

			@Override
			public String toString() {
				return type.toString();
			}
		});

		RecipeComponent<IngredientStack> INPUT_WITH_COUNT = INPUT_WITH_COUNT_TYPE.instance();

		RecipeKey<List<IngredientStack>> INGREDIENTS_WITH_COUNT = INPUT_WITH_COUNT.asList().inputKey(RecipeUtil.JsonKeys.INGREDIENTS);
		RecipeKey<String> BIO_FORGE_TAB = StringComponent.ID.instance().otherKey(RecipeUtil.JsonKeys.BIO_FORGE_TAB);

		RecipeSchema SCHEMA = new RecipeSchema(INGREDIENTS_WITH_COUNT, RecipeKeys.RESULT, BIO_FORGE_TAB, RecipeKeys.NUTRIENTS_COST);
	}

	interface DecomposingRecipeSchema {
		RecipeComponentType<VariableOutput> VARIABLE_OUTPUT_TYPE = RecipeComponentType.unit(BiomancyMod.rl("variable_output"), type -> new RecipeComponent<>() {
			@Override
			public RecipeComponentType<?> type() {
				return type;
			}

			@Override
			public Codec<VariableOutput> codec() {
				return jsonBridgeCodec(VariableOutput::serialize, VariableOutput::deserialize);
			}

			@Override
			public dev.latvian.mods.rhino.type.TypeInfo typeInfo() {
				return ItemStackComponent.ITEM_STACK.instance().typeInfo();
			}

			@Override
			public VariableOutput wrap(RecipeScriptContext cx, Object from) {
				ItemStack stack = ItemStackComponent.ITEM_STACK.instance().wrap(cx, from);
				return new VariableOutput(stack);
			}

			@Override
			public boolean isEmpty(VariableOutput value) {
				return value.getItem() == net.minecraft.world.item.Items.AIR;
			}

			@Override
			public String toString() {
				return type.toString();
			}
		});

		RecipeComponent<VariableOutput> VARIABLE_OUTPUT = VARIABLE_OUTPUT_TYPE.instance();

		RecipeKey<List<VariableOutput>> RESULTS = VARIABLE_OUTPUT.asList().outputKey(RecipeUtil.JsonKeys.RESULTS);

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
		static Ingredient fromTier(EntityType<?> entityType, int tier) {
			BiomancyKubeJSPlugin.LOGGER.warn("Creating EssenceIngredient for {} with tier {}", entityType.getDescriptionId(), tier);
			return EssenceIngredient.of(entityType, tier);
		}

		@Info(
				value = "Creates a essence ingredient that matches any tier",
				params = {@Param(name = "entityType")}
		)
		static Ingredient from(EntityType<?> entityType) {
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
