package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.digester.DigesterBlockEntity;
import com.github.elenterius.biomancy.crafting.ItemCountRange;
import com.github.elenterius.biomancy.crafting.VariableOutput;
import com.github.elenterius.biomancy.crafting.recipe.DecomposingRecipe;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;

public class DecomposingCategory implements IRecipeCategory<DecomposingRecipe> {

	@SuppressWarnings("DataFlowIssue")
	public static final RecipeType<DecomposingRecipe> RECIPE_TYPE = new RecipeType<>(ModRecipes.DECOMPOSING_RECIPE_TYPE.getId(), DecomposingRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;

	private final RecipeWrapper inputInventoryWrapper;

	public DecomposingCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.DECOMPOSER.get()));
		background = guiHelper.drawableBuilder(BiomancyMod.createRL("textures/gui/jei/decomposer_recipe.png"), 0, 0, 132, 64).setTextureSize(132, 64).build();

		inputInventoryWrapper = new RecipeWrapper(new ItemStackHandler(DigesterBlockEntity.INPUT_SLOTS));
	}

	@Override
	public RecipeType<DecomposingRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Override
	public Component getTitle() {
		return ComponentUtil.translatable("jei.biomancy.recipe.decomposer");
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
	public void setRecipe(IRecipeLayoutBuilder builder, DecomposingRecipe recipe, IFocusGroup focuses) {
		int gap = 10;
		int offset = 16 + gap;

		int x0 = 5;
		int y0 = 9;
		int x1 = 55;
		int y1 = y0 + offset; // 35

		builder.addSlot(RecipeIngredientRole.INPUT, x0, y0).addItemStacks(recipe.getIngredientQuantity().getItemsWithCount());

		List<VariableOutput> outputs = recipe.getOutputs();
		addOutputSlot(builder, x1, y0, outputs, 0);
		addOutputSlot(builder, x1, y1, outputs, 1);
		addOutputSlot(builder, x1 + offset, y0, outputs, 2);
		addOutputSlot(builder, x1 + offset, y1, outputs, 3);
		addOutputSlot(builder, x1 + offset * 2, y0, outputs, 4);
		addOutputSlot(builder, x1 + offset * 2, y1, outputs, 5);
	}

	private void addOutputSlot(IRecipeLayoutBuilder builder, int x, int y, List<VariableOutput> outputs, int index) {
		assert index >= 0;
		assert index < DecomposingRecipe.MAX_OUTPUTS;

		IRecipeSlotBuilder slotBuilder = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y);
		if (index < outputs.size()) {
			VariableOutput output = outputs.get(index);
			ItemStack stack = output.getItemStack();
			slotBuilder.addItemStack(stack);

			final ItemCountRange countRange = output.getCountRange();
			slotBuilder.addTooltipCallback((slotView, tooltip) -> {
				tooltip.add(ComponentUtil.EMPTY_LINE);

				if (countRange instanceof ItemCountRange.UniformRange uniform) {
					tooltip.add(ComponentUtil.literal("Uniform Output Distribution").withStyle(TextStyles.MUTED_PURPLE));

					float onAverage = averageAmount(uniform.min(), uniform.max());
					tooltip.add(ComponentUtil.literal("Average: " + String.format("%.1f", onAverage) + " items").withStyle(TextStyles.GRAY));

					tooltip.add(ComponentUtil.literal("Range: [%,d, ... , %,d]".formatted(uniform.min(), uniform.max())).withStyle(TextStyles.GRAY));

					for (String line : summarizeUniformDistribution(uniform.min(), uniform.max())) {
						Style color = line.contains("0x") ? TextStyles.RED : TextStyles.LIME;
						tooltip.add(ComponentUtil.space().append(line).withStyle(color));
					}
				}
				else if (countRange instanceof ItemCountRange.ConstantValue constant) {
					tooltip.add(ComponentUtil.literal("Constant Output Distribution").withStyle(TextStyles.MUTED_PURPLE));
					tooltip.add(ComponentUtil.literal("x%,d".formatted(constant.value())).withStyle(TextStyles.GRAY));
				}
				else if (countRange instanceof ItemCountRange.BinomialRange binomialRange) {
					tooltip.add(ComponentUtil.literal("Binomial Output Distribution").withStyle(TextStyles.MUTED_PURPLE));
					tooltip.add(ComponentUtil.literal("n=%d p=%s".formatted(binomialRange.n(), binomialRange.p())).withStyle(TextStyles.GRAY));
				}

				tooltip.add(ComponentUtil.EMPTY_LINE);
			});
		}
	}

	@Override
	public void draw(DecomposingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		Font font = Minecraft.getInstance().font;

		IRecipeSlotView slotView = recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT).get(0);
		ItemStack itemStack = slotView.getDisplayedItemStack().orElse(ItemStack.EMPTY);
		inputInventoryWrapper.setItem(0, itemStack);

		int ticks = recipe.getCraftingTimeTicks(inputInventoryWrapper);
		int seconds = ticks > 0 ? ticks / 20 : 0;
		MutableComponent timeString = ComponentUtil.translatable("gui.jei.category.smelting.time.seconds", seconds);
		guiGraphics.drawString(font, timeString, 16, 59 - font.lineHeight, ColorStyles.WHITE_ARGB);

		MutableComponent costString = ComponentUtil.literal("-" + recipe.getCraftingCostNutrients(inputInventoryWrapper));
		guiGraphics.drawString(font, costString, 16, 43 - font.lineHeight, ColorStyles.WHITE_ARGB);

		int gap = 10;
		int offset = 16 + gap;
		int y0 = 9 + 16 - Math.round(font.lineHeight / 2f) + 3;
		int x = 55 + 16 + 3;
		int y1 = y0 + offset; // 41

		List<VariableOutput> outputs = recipe.getOutputs();
		drawOutputAmount(font, guiGraphics, x, y0, outputs, 0);
		drawOutputAmount(font, guiGraphics, x, y1, outputs, 1);
		drawOutputAmount(font, guiGraphics, x + offset, y0, outputs, 2);
		drawOutputAmount(font, guiGraphics, x + offset, y1, outputs, 3);
		drawOutputAmount(font, guiGraphics, x + offset * 2, y0, outputs, 4);
		drawOutputAmount(font, guiGraphics, x + offset * 2, y1, outputs, 5);
	}

	private void drawOutputAmount(Font font, GuiGraphics guiGraphics, int x, int y, List<VariableOutput> outputs, int index) {
		assert index >= 0;
		assert index < DecomposingRecipe.MAX_OUTPUTS;

		if (index < outputs.size()) {
			VariableOutput output = outputs.get(index);
			if (output.getItemStack().isEmpty()) return;

			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(x, y, 0);
			guiGraphics.pose().scale(0.75f, 0.75f, 1f);
			guiGraphics.pose().translate(-x, -y, 90);

			ItemCountRange countRange = output.getCountRange();
			if (countRange instanceof ItemCountRange.UniformRange uniform) {
				MutableComponent component = ComponentUtil.literal("%d–%d".formatted(uniform.min(), uniform.max()));
				guiGraphics.drawString(font, component, x - font.width(component), y, ColorStyles.WHITE_ARGB);
			}
			else if (countRange instanceof ItemCountRange.ConstantValue constant) {
				MutableComponent component = ComponentUtil.literal("" + constant.value());
				guiGraphics.drawString(font, component, x - font.width(component), y, ColorStyles.WHITE_ARGB);
			}
			else if (countRange instanceof ItemCountRange.BinomialRange binomialRange) {
				MutableComponent component = ComponentUtil.literal("n=%d p=%s".formatted(binomialRange.n(), binomialRange.p()));
				guiGraphics.drawString(font, component, x - font.width(component), y, ColorStyles.WHITE_ARGB);
			}

			guiGraphics.pose().popPose();
		}
	}

	private float averageAmount(int min, int max) {
		if (max <= 0) return 0f;
		if (min >= 0) return (max + min) / 2f;

		float n = max - min + 1;
		float sumPositive = max * (max + 1f) / 2f;
		return sumPositive / n;
	}

	public static List<String> summarizeUniformDistribution(int min, int max) {
		List<String> lines = new ArrayList<>();

		if (max <= 0) {
			lines.add(String.format("0x → %d%% Chance", 100));
			return lines;
		}

		if (max == min) {
			lines.add(String.format("%,dx → %d%% Chance", max, 100));
			return lines;
		}

		int n = max - min + 1;
		float probabilityOfX = 1f / n;

		if (min <= 0) {
			float probabilityOfMinToZero = (0f - min + 1) / n;
			lines.add(String.format("0x → %.2f%% Chance", probabilityOfMinToZero * 100f));
		}

		if (max == 1) {
			lines.add(String.format("1x → %.2f%% Chance", probabilityOfX * 100f));
		}
		else {
			int lowerBound = Math.max(min, 1);
			float probabilityOfOneToMax = (float) (max - lowerBound + 1) / n;
			lines.add(String.format("%,d-%,dx → %.2f%% each (%.2f%% total)", lowerBound, max, probabilityOfX * 100f, probabilityOfOneToMax * 100f));
		}

		return lines;
	}

}
