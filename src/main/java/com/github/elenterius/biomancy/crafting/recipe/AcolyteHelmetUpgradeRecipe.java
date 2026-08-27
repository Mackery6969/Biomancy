package com.github.elenterius.biomancy.crafting.recipe;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorUpgrades;
import com.github.elenterius.biomancy.item.armor.LivingArmorItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AcolyteHelmetUpgradeRecipe extends CustomRecipe {

	public AcolyteHelmetUpgradeRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput inventory, Level level) {
		boolean hasHelmet = false;
		boolean hasPrimordialCore = false;

		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getItem(i);

			if (stack.isEmpty()) continue;
			Item item = stack.getItem();

			if (item instanceof LivingArmorItem armor && armor.getType() == ArmorItem.Type.HELMET) {
				if (hasHelmet) return false;
				if (AcolyteArmorUpgrades.hasUpgrade(stack, AcolyteArmorUpgrades.PRIMORDIAL_SIGHT)) return false;
				hasHelmet = true;
			}
			else if (item == ModItems.PRIMORDIAL_CORE.get()) {
				if (hasPrimordialCore) return false;
				hasPrimordialCore = true;
			}
			else return false;
		}

		return hasHelmet && hasPrimordialCore;
	}

	@Override
	public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registries) {
		ItemStack helmet = ItemStack.EMPTY;
		boolean hasPrimordialCore = false;

		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getItem(i);

			if (stack.isEmpty()) continue;
			Item item = stack.getItem();

			if (item instanceof LivingArmorItem armor && armor.getType() == ArmorItem.Type.HELMET) {
				if (!helmet.isEmpty()) return ItemStack.EMPTY;
				if (AcolyteArmorUpgrades.hasUpgrade(stack, AcolyteArmorUpgrades.PRIMORDIAL_SIGHT)) return ItemStack.EMPTY;
				helmet = stack;
			}
			else if (item == ModItems.PRIMORDIAL_CORE.get()) {
				if (hasPrimordialCore) return ItemStack.EMPTY;
				hasPrimordialCore = true;
			}
			else return ItemStack.EMPTY;
		}

		return hasPrimordialCore && !helmet.isEmpty() ? AcolyteArmorUpgrades.addUpgrade(helmet.copy(), AcolyteArmorUpgrades.PRIMORDIAL_SIGHT) : ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.ACOLYTE_HELMET_UPGRADE_SERIALIZER.get();
	}

}
