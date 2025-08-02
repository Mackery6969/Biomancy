package com.github.elenterius.biomancy.integration.jei;

import com.github.elenterius.biomancy.item.PotionSerumItem;
import com.github.elenterius.biomancy.serum.PotionSerum;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
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
		if (!stack.hasTag()) return IIngredientSubtypeInterpreter.NONE;

		if (stack.getItem() instanceof PotionSerumItem container) {
			CompoundTag tag = container.getSerumData(stack);
			Potion potion = PotionSerum.getPotion(tag);
			ResourceLocation key = BuiltInRegistries.POTION.getKey(potion);
			return key.toString();
		}

		return IIngredientSubtypeInterpreter.NONE;
	}

}
