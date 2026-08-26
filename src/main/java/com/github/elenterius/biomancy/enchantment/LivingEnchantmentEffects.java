package com.github.elenterius.biomancy.enchantment;

import com.github.elenterius.biomancy.api.livingtool.LivingTool;
import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.api.nutrients.NutrientsContainerItem;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.util.EnchantmentUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public final class LivingEnchantmentEffects {

	private LivingEnchantmentEffects() {}

	public static void repairSelfFeedingItems(Player player) {
		Holder<Enchantment> enchantment = ModEnchantments.getHolder(ModEnchantments.SELF_FEEDING, player.level());
		List<Map.Entry<EquipmentSlot, ItemStack>> enchantedItems = EnchantmentUtil.getItemsWithEnchantment(enchantment, player, LivingTool.NEED_NUTRIENTS_PREDICATE);
		if (enchantedItems.isEmpty()) return;

		Map.Entry<EquipmentSlot, ItemStack> slotItem = enchantedItems.get(player.getRandom().nextInt(enchantedItems.size()));
		ItemStack stack = slotItem.getValue();
		NutrientsContainerItem nutrientsContainer = (NutrientsContainerItem) stack.getItem();
		int neededRepairValue = nutrientsContainer.getMaxNutrients(stack) - nutrientsContainer.getNutrients(stack);

		ItemStack repairItemStack = getBestRepairItem(player, neededRepairValue);
		if (repairItemStack.isEmpty()) return;

		nutrientsContainer.increaseNutrients(stack, Nutrients.getRepairValue(repairItemStack));

		if (!player.getAbilities().instabuild) {
			if (repairItemStack.hasCraftingRemainingItem()) {
				ItemStack craftingRemainder = repairItemStack.getCraftingRemainingItem();
				repairItemStack.shrink(1);
				if (!craftingRemainder.isEmpty() && !player.addItem(craftingRemainder)) {
					player.drop(craftingRemainder, false);
				}
			}
			else repairItemStack.shrink(1);
		}
	}

	private static ItemStack getBestRepairItem(Player player, int neededRepairValue) {
		NonNullList<ItemStack> items = player.getInventory().items;

		ItemStack repairItemStack = ItemStack.EMPTY;
		int minError = Integer.MAX_VALUE;

		//loop through hot-bar slots
		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = items.get(i);

			int repairValue = Nutrients.getRepairValue(itemStack);
			if (repairValue <= 0) continue;

			int error;
			Item item = itemStack.getItem();
			if (item == ModItems.NUTRIENT_PASTE.get() || item == ModItems.NUTRIENT_BAR.get()) {
				error = repairValue > neededRepairValue ? (repairValue - neededRepairValue) / 2 : -repairValue * 2;
			}
			else {
				error = repairValue > neededRepairValue ? (repairValue - neededRepairValue) * 2 : -repairValue / 2;
			}

			if (error < minError) {
				minError = error;
				repairItemStack = itemStack;
			}
		}

		return repairItemStack;
	}

	public static void repairParasiticMetabolismItems(Player player) {
		if (player.getHealth() <= 10f) return;

		FoodData foodData = player.getFoodData();
		if (foodData.getFoodLevel() <= 2) return;

		Holder<Enchantment> enchantment = ModEnchantments.getHolder(ModEnchantments.PARASITIC_METABOLISM, player.level());
		List<Map.Entry<EquipmentSlot, ItemStack>> enchantedItems = EnchantmentUtil.getItemsWithEnchantment(enchantment, player, LivingTool.NEED_NUTRIENTS_PREDICATE);

		if (!enchantedItems.isEmpty()) {
			Map.Entry<EquipmentSlot, ItemStack> slotItem = enchantedItems.get(player.getRandom().nextInt(enchantedItems.size()));
			ItemStack stack = slotItem.getValue();
			NutrientsContainerItem item = (NutrientsContainerItem) stack.getItem();

			int bonusRepairValue = 2;
			item.increaseNutrients(stack, Nutrients.getRepairValue(ModItems.NUTRIENT_PASTE.get().getDefaultInstance()) + bonusRepairValue);

			if (!player.getAbilities().invulnerable) {
				foodData.setFoodLevel(foodData.getFoodLevel() - 1);
			}
		}
	}

}
