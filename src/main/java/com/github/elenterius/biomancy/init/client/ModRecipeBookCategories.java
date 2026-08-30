package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.recipe.BioForgingRecipe;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModRecipeBookTypes;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID, value = Dist.CLIENT)
public final class ModRecipeBookCategories {

	public static final Supplier<List<ItemStack>> DUMMY_ICON_SUPPLIER = () -> List.of(new ItemStack(Items.BARRIER));

	private ModRecipeBookCategories() {}

	@SuppressWarnings("unused") // referenced by META-INF/enumextensions.json
	public static Object getIconSupplierParameter(int idx, Class<?> type) {
		return type.cast(switch (idx) {
			case 0 -> DUMMY_ICON_SUPPLIER;
			default -> throw new IllegalArgumentException("Unexpected parameter index: " + idx);
		});
	}

	public static RecipeBookCategories getRecipeBookCategories(BioForgeTab category) {
		return BioForgeCategories.TAB_TO_CATEGORY.getOrDefault(category.enumId(), RecipeBookCategories.UNKNOWN);
	}

	@SubscribeEvent
	public static void registerRecipeBooks(RegisterRecipeBookCategoriesEvent event) {
		BioForgeCategories.register(event);

		event.registerRecipeCategoryFinder(ModRecipes.BIO_BREWING_RECIPE_TYPE.get(), rc -> RecipeBookCategories.UNKNOWN);
		event.registerRecipeCategoryFinder(ModRecipes.DECOMPOSING_RECIPE_TYPE.get(), rc -> RecipeBookCategories.UNKNOWN);
		event.registerRecipeCategoryFinder(ModRecipes.DIGESTING_RECIPE_TYPE.get(), rc -> RecipeBookCategories.UNKNOWN);
	}

	private static final class BioForgeCategories {
		//inner class prevents pre-mature initialization from the EventBusSubscriber annotation

		private static final int SLOT_COUNT = 24;

		private static final Map<String, RecipeBookCategories> TAB_TO_CATEGORY = new HashMap<>();
		public static final RecipeBookCategories SEARCH_CATEGORY = createAndRegisterSearchCategory();

		public static final Function<RecipeHolder<?>, RecipeBookCategories> RECIPE_CATEGORY_FINDER = recipeHolder -> {
			if (recipeHolder.value() instanceof BioForgingRecipe bioForgingRecipe) {
				return TAB_TO_CATEGORY.get(bioForgingRecipe.getTab().enumId());
			}
			return null;
		};

		private BioForgeCategories() {}

		private static RecipeBookCategories slot(int index) {
			return RecipeBookCategories.valueOf("BIOMANCY_BIO_FORGE_TAB_" + index);
		}

		private static RecipeBookCategories createAndRegisterSearchCategory() {
			BioForgeTab tab = ModBioForgeTabs.SEARCH.get();
			String name = tab.enumId();
			RecipeBookCategories category = slot(0);
			TAB_TO_CATEGORY.put(name, category);
			return category;
		}

		private static void registerCategories() {
			int nextSlot = 1;
			for (Map.Entry<ResourceKey<BioForgeTab>, BioForgeTab> entry : ModBioForgeTabs.REGISTRY.entrySet()) {
				BioForgeTab tab = entry.getValue();

				if (tab == ModBioForgeTabs.SEARCH.get()) continue;

				if (nextSlot >= SLOT_COUNT) {
					BiomancyMod.LOGGER.warn("Ran out of Bio-Forge recipe book category slots, tab '{}' will not be filterable in the recipe book", tab.enumId());
					continue;
				}

				TAB_TO_CATEGORY.put(tab.enumId(), slot(nextSlot++));
			}
		}

		private static void register(RegisterRecipeBookCategoriesEvent event) {
			registerCategories();

			List<RecipeBookCategories> categories = TAB_TO_CATEGORY.values().stream().toList();

			event.registerBookCategories(ModRecipeBookTypes.BIO_FORGE, categories);
			event.registerAggregateCategory(BioForgeCategories.SEARCH_CATEGORY, categories);
			event.registerRecipeCategoryFinder(ModRecipes.BIO_FORGING_RECIPE_TYPE.get(), RECIPE_CATEGORY_FINDER);
		}
	}
}
