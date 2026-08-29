package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.item.PotionSerumItem;
import com.github.elenterius.biomancy.serum.PotionSerum;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;

public class PotionSerumSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {

	public static final PotionSerumSubtypeInterpreter INSTANCE = new PotionSerumSubtypeInterpreter();

	private PotionSerumSubtypeInterpreter() {}

	@Override
	public String apply(ItemStack stack, UidContext context) {
		if (!stack.has(DataComponents.CUSTOM_DATA)) return IIngredientSubtypeInterpreter.NONE;

		if (stack.getItem() instanceof PotionSerumItem container) {
			CompoundTag tag = container.getSerumData(stack);
			Holder<Potion> potion = PotionSerum.getPotion(tag);
			ResourceLocation key = BuiltInRegistries.POTION.getKey(potion.value());
			return key.toString();
		}

		return IIngredientSubtypeInterpreter.NONE;
	}

}
