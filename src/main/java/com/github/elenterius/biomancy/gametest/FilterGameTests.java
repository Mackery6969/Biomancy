package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.inventory.BehavioralItemHandler;
import com.github.elenterius.biomancy.inventory.InventoryHandler;
import com.github.elenterius.biomancy.inventory.InventoryHandlers;
import com.github.elenterius.biomancy.util.ItemStackFilter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import static com.github.elenterius.biomancy.gametest.GameTestTemplates.EMPTY_PLATFORM;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FilterGameTests {

	private FilterGameTests() {}

	@GameTest(template = EMPTY_PLATFORM)
	public static void lockedInputAcceptsItsOriginalIngredientAfterReload(GameTestHelper helper) {
		InventoryHandler<BehavioralItemHandler.LockableItemStackFilterInput> inventory =
				InventoryHandlers.lockableFilterInput(2, () -> {});
		inventory.setStackInSlot(0, new ItemStack(Items.BEEF, 4));
		inventory.get().setLocked(true);
		inventory.extractItem(0, 4, false);

		CompoundTag saved = inventory.serializeNBT(helper.getLevel().registryAccess());
		InventoryHandler<BehavioralItemHandler.LockableItemStackFilterInput> reloaded =
				InventoryHandlers.lockableFilterInput(2, () -> {});
		reloaded.deserializeNBT(helper.getLevel().registryAccess(), saved);

		ItemStack input = new ItemStack(Items.BEEF, 3);
		ItemStack before = input.copy();
		helper.assertTrue(reloaded.isItemValid(0, input), "locked input rejected its original ingredient");
		helper.assertTrue(reloaded.insertItem(0, input, false).isEmpty(), "locked input could not be refilled after reload");
		helper.assertTrue(ItemStack.matches(before, input), "checking a filter mutated the incoming item's components");
		helper.assertFalse(reloaded.isItemValid(0, new ItemStack(Items.PORKCHOP)), "locked input accepted the wrong item");
		helper.assertFalse(reloaded.isItemValid(1, new ItemStack(Items.BEEF)), "locked empty input accepted an item");
		helper.succeed();
	}

	@GameTest(template = EMPTY_PLATFORM)
	public static void strictFiltersStillDistinguishPotionContents(GameTestHelper helper) {
		ItemStack healing = PotionContents.createItemStack(Items.POTION, Potions.HEALING);
		ItemStack harming = PotionContents.createItemStack(Items.POTION, Potions.HARMING);
		ItemStackFilter filter = ItemStackFilter.of(healing);

		helper.assertTrue(filter.test(healing), "filter rejected the original potion");
		helper.assertFalse(filter.test(harming), "filter ignored differing potion components");
		ItemStack withoutIgnoredComponents = healing.copy();
		withoutIgnoredComponents.remove(DataComponents.ENCHANTMENTS);
		withoutIgnoredComponents.remove(DataComponents.ATTRIBUTE_MODIFIERS);
		helper.assertTrue(filter.test(withoutIgnoredComponents), "ignored components changed the filter result");
		helper.succeed();
	}

}
