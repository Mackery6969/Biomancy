package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.digester.DigesterBlockEntity;
import com.github.elenterius.biomancy.crafting.recipe.DigestingRecipe;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DigestingCategory implements IRecipeCategory<RecipeHolder<DigestingRecipe>> {

	public static final RecipeType<RecipeHolder<DigestingRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(ModRecipes.DIGESTING_RECIPE_TYPE.get());
	private final IDrawable background;
	private final IDrawable icon;

	private final ItemStackHandler inputInventoryHandler;
	private final RecipeWrapper inputInventoryWrapper;

	public DigestingCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.DIGESTER.get()));
		ResourceLocation texture = BiomancyMod.rl("textures/gui/jei/digester_recipe.png");
		background = guiHelper.drawableBuilder(texture, 0, 0, 80, 47).setTextureSize(80, 47).addPadding(0, 4, 0, 0).build();

		inputInventoryHandler = new ItemStackHandler(DigesterBlockEntity.INPUT_SLOTS);
		inputInventoryWrapper = new RecipeWrapper(inputInventoryHandler);
	}

	@Override
	public RecipeType<RecipeHolder<DigestingRecipe>> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Override
	public Component getTitle() {
		return ComponentUtil.translatable("jei.biomancy.recipe.digester");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<DigestingRecipe> recipeHolder, IFocusGroup focuses) {
		DigestingRecipe recipe = recipeHolder.value();
		ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);

		builder.setShapeless();

		if (recipe.isSpecial()) {
			Ingredient ingredient = recipe.getIngredient();
			builder.addSlot(RecipeIngredientRole.INPUT, 1, 4).addIngredients(ingredient);

			List<ItemStack> possibleOutputs = new ArrayList<>();
			for (ItemStack ingredientItem : ingredient.getItems()) {
				inputInventoryHandler.setStackInSlot(0, ingredientItem);
				ItemStack result = recipe.assemble(inputInventoryWrapper, level.registryAccess());
				possibleOutputs.add(result);
			}

			builder.addSlot(RecipeIngredientRole.OUTPUT, 59, 5).addItemStacks(possibleOutputs);
		}
		else {
			builder.addSlot(RecipeIngredientRole.INPUT, 1, 4).addIngredients(recipe.getIngredient());
			builder.addSlot(RecipeIngredientRole.OUTPUT, 59, 5).addItemStack(recipe.getResultItem(level.registryAccess()));
		}
	}

	@Override
	public void draw(RecipeHolder<DigestingRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		DigestingRecipe recipe = recipeHolder.value();
		Font font = Minecraft.getInstance().font;

		IRecipeSlotView slotView = recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT).get(0);
		ItemStack itemStack = slotView.getDisplayedItemStack().orElse(ItemStack.EMPTY);
		inputInventoryHandler.setStackInSlot(0, itemStack);

		int ticks = recipe.getCraftingTimeTicks(inputInventoryWrapper);
		int seconds = ticks > 0 ? ticks / 20 : 0;
		Component timeText = ComponentUtil.translatable("gui.jei.category.smelting.time.seconds", seconds);
		guiGraphics.drawString(font, timeText, 48, 44 - font.lineHeight, ColorStyles.WHITE_ARGB);

		Component costText = ComponentUtil.literal("-" + recipe.getCraftingCostNutrients(inputInventoryWrapper));
		guiGraphics.drawString(font, costText, 15, 44 - font.lineHeight, ColorStyles.WHITE_ARGB);
	}

}
