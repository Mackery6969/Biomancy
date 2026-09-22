package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.inventory.LargeSingleItemStackHandler;
import com.github.elenterius.biomancy.inventory.SingleItemStackHandler;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class InventoryGameTests {

	private InventoryGameTests() {}

	@GameTest(template = "empty_platform")
	public static void emptyBufferAcceptsAndReturnsWholeTransfer(GameTestHelper helper) {
		SingleItemStackHandler handler = new SingleItemStackHandler();
		ItemStack transfer = new ItemStack(Items.COBBLESTONE, 16);
		helper.assertTrue(handler.insertItem(transfer, true).isEmpty(), "Simulation must accept all sixteen items");
		helper.assertTrue(handler.isEmpty(), "Simulation must not change inventory");
		helper.assertTrue(handler.insertItem(transfer, false).isEmpty(), "Empty buffer must accept all sixteen items");
		ItemStack extracted = handler.extractItem(16, false);
		helper.assertTrue(extracted.getCount() == 16 && handler.isEmpty(), "Extraction must return the whole transfer");
		helper.assertTrue(handler.insertItem(extracted, false).isEmpty(), "Failed output must be able to return the whole transfer");
		helper.assertTrue(handler.getAmount() == 16, "Returning a transfer must preserve every item");
		helper.succeed();
	}

	@GameTest(template = "empty_platform")
	public static void normalBufferRespectsIncomingStackLimit(GameTestHelper helper) {
		SingleItemStackHandler handler = new SingleItemStackHandler();
		ItemStack remainder = handler.insertItem(new ItemStack(Items.SNOWBALL, 32), false);
		helper.assertTrue(handler.getAmount() == 16 && remainder.getCount() == 16, "Snowballs must keep their sixteen-item limit");
		handler.extractItem(16, false);
		remainder = handler.insertItem(new ItemStack(Items.WOODEN_SWORD, 3), false);
		helper.assertTrue(handler.getAmount() == 1 && remainder.getCount() == 2, "Unstackable items must keep their one-item limit");
		helper.succeed();
	}

	@GameTest(template = "empty_platform")
	public static void largeBufferRetainsItsExtendedCapacity(GameTestHelper helper) {
		LargeSingleItemStackHandler handler = new LargeSingleItemStackHandler((short) 200);
		helper.assertTrue(handler.insertItem(new ItemStack(Items.COBBLESTONE, 128), false).isEmpty(), "Large buffer must accept more than a normal stack");
		ItemStack remainder = handler.insertItem(new ItemStack(Items.COBBLESTONE, 100), false);
		helper.assertTrue(handler.getAmount() == 200 && remainder.getCount() == 28, "Large buffer must respect its configured capacity");
		helper.succeed();
	}
}
