package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.BioForgeScreen;
import com.github.elenterius.biomancy.client.gui.BioLabScreen;
import com.github.elenterius.biomancy.client.gui.DecomposerScreen;
import com.github.elenterius.biomancy.client.gui.DigesterScreen;
import com.github.elenterius.biomancy.crafting.recipe.PotionSerumRecipes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.menu.BioLabMenu;
import com.github.elenterius.biomancy.menu.DecomposerMenu;
import com.github.elenterius.biomancy.menu.DigesterMenu;
import com.github.elenterius.biomancy.menu.ISlotZone;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class BiomancyJeiPlugin implements IModPlugin {

	private static final ResourceLocation ID = BiomancyMod.rl("plugin");

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(ModItems.POTION_SERUM.get(), PotionSerumSubtypeInterpreter.INSTANCE);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new DecomposingCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new BioBrewingCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new DigestingCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new BioForgingCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
		RecipeManager recipeManager = level.getRecipeManager();

		registration.addRecipes(DecomposingCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(ModRecipes.DECOMPOSING_RECIPE_TYPE.get()));
		registration.addRecipes(BioBrewingCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(ModRecipes.BIO_BREWING_RECIPE_TYPE.get()));
		registration.addRecipes(BioBrewingCategory.RECIPE_TYPE, PotionSerumRecipes.getRecipes(level));

		registration.addRecipes(DigestingCategory.RECIPE_TYPE, DigestingRecipes.getRecipes(level));
		registration.addRecipes(BioForgingCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(ModRecipes.BIO_FORGING_RECIPE_TYPE.get()));

		registration.addRecipes(RecipeTypes.CRAFTING, wrapAsRecipeHolders("biometric_membrane", SpecialCraftingRecipeMaker.createBiometricMembraneRecipes()));
		registration.addRecipes(RecipeTypes.CRAFTING, wrapAsRecipeHolders("cradle_cleansing", SpecialCraftingRecipeMaker.createCradleCleansingRecipes()));
		registration.addRecipes(RecipeTypes.CRAFTING, wrapAsRecipeHolders("player_head", SpecialCraftingRecipeMaker.createPlayerHeadRecipes()));
		registration.addRecipes(RecipeTypes.CRAFTING, wrapAsRecipeHolders("helmet_upgrade", SpecialCraftingRecipeMaker.createHelmetUpgradeRecipes()));
	}

	private static List<RecipeHolder<CraftingRecipe>> wrapAsRecipeHolders(String category, List<CraftingRecipe> recipes) {
		List<RecipeHolder<CraftingRecipe>> holders = new ArrayList<>(recipes.size());
		for (int i = 0; i < recipes.size(); i++) {
			holders.add(new RecipeHolder<>(BiomancyMod.rl("jei/" + category + "/" + i), recipes.get(i)));
		}
		return holders;
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ModItems.BIO_FORGE.get()), BioForgingCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.BIO_LAB.get()), BioBrewingCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.DIGESTER.get()), DigestingCategory.RECIPE_TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.DECOMPOSER.get()), DecomposingCategory.RECIPE_TYPE);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(DecomposerScreen.class, 176 - 5 - 10, 4, 10, 10, DecomposingCategory.RECIPE_TYPE);
		registration.addRecipeClickArea(BioLabScreen.class, 176 - 5 - 10, 4, 10, 10, BioBrewingCategory.RECIPE_TYPE);
		registration.addRecipeClickArea(DigesterScreen.class, 176 - 5 - 10, 4, 10, 10, DigestingCategory.RECIPE_TYPE);
		registration.addRecipeClickArea(BioForgeScreen.class, 292 - 5 - 10, 4, 10, 10, BioForgingCategory.RECIPE_TYPE);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registerInputSlots(registration, ModMenuTypes.DECOMPOSER.get(), DecomposerMenu.class, DecomposingCategory.RECIPE_TYPE, DecomposerMenu.SlotZone.INPUT_ZONE);
		registerInputSlots(registration, ModMenuTypes.BIO_LAB.get(), BioLabMenu.class, BioBrewingCategory.RECIPE_TYPE, BioLabMenu.SlotZone.INPUT_ZONE);
		registerInputSlots(registration, ModMenuTypes.DIGESTER.get(), DigesterMenu.class, DigestingCategory.RECIPE_TYPE, DigesterMenu.SlotZone.INPUT_ZONE);
	}

	private <C extends AbstractContainerMenu, R> void registerInputSlots(IRecipeTransferRegistration registration, @Nullable MenuType<C> menuType, Class<? extends C> containerClass, RecipeType<R> recipeType, ISlotZone slotZone) {
		registration.addRecipeTransferHandler(containerClass, menuType, recipeType, slotZone.getFirstIndex(), slotZone.getSlotCount(), 0, 36);
	}


}
