package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.inventory.LargeSingleItemStackHandler;
import com.github.elenterius.biomancy.inventory.SingleItemStackHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import static com.github.elenterius.biomancy.gametest.GameTestTemplates.EMPTY_PLATFORM;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class InventoryGameTests {

	private InventoryGameTests() {}

	@GameTest(template = EMPTY_PLATFORM)
	public static void emptyBufferAcceptsAndReturnsWholeTransfer(GameTestHelper helper) {
		SingleItemStackHandler handler = new SingleItemStackHandler();
		ItemStack transfer = new ItemStack(Items.COBBLESTONE, 16);
		helper.assertTrue(handler.insertItem(transfer, true).isEmpty(), "Simulation must accept all sixteen items");
		helper.assertTrue(handler.isEmpty(), "Simulation must not change inventory");
		helper.assertTrue(handler.insertItem(transfer, false).isEmpty(), "Empty buffer must accept all sixteen items");
		ItemStack extracted = handler.extractItem(16, false);
		helper.assertValueEqual(extracted.getCount(), 16, "extracted amount");
		helper.assertTrue(handler.isEmpty(), "Extraction must return the whole transfer");
		helper.assertTrue(handler.insertItem(extracted, false).isEmpty(), "Failed output must be able to return the whole transfer");
		helper.assertValueEqual(handler.getAmount(), 16, "amount after returning the transfer");
		helper.succeed();
	}

	@GameTest(template = EMPTY_PLATFORM)
	public static void normalBufferRespectsIncomingStackLimit(GameTestHelper helper) {
		SingleItemStackHandler handler = new SingleItemStackHandler();
		ItemStack remainder = handler.insertItem(new ItemStack(Items.SNOWBALL, 32), false);
		helper.assertValueEqual(handler.getAmount(), 16, "stored snowballs");
		helper.assertValueEqual(remainder.getCount(), 16, "rejected snowballs");
		handler.extractItem(16, false);
		remainder = handler.insertItem(new ItemStack(Items.WOODEN_SWORD, 3), false);
		helper.assertValueEqual(handler.getAmount(), 1, "stored swords");
		helper.assertValueEqual(remainder.getCount(), 2, "rejected swords");
		helper.succeed();
	}

	@GameTest(template = EMPTY_PLATFORM)
	public static void bufferSavesStacksLargerThanSixtyFour(GameTestHelper helper) {
		HolderLookup.Provider registries = helper.getLevel().registryAccess();
		ItemStack stack = new ItemStack(Items.COBBLESTONE, Item.ABSOLUTE_MAX_STACK_SIZE);
		stack.set(DataComponents.MAX_STACK_SIZE, Item.ABSOLUTE_MAX_STACK_SIZE);
		SingleItemStackHandler handler = new SingleItemStackHandler();
		helper.assertTrue(handler.insertItem(stack, false).isEmpty(), "Empty buffer must accept a full 99-item stack");

		SingleItemStackHandler reloaded = new SingleItemStackHandler();
		reloaded.deserializeNBT(registries, handler.serializeNBT(registries));
		helper.assertValueEqual(reloaded.getAmount(), Item.ABSOLUTE_MAX_STACK_SIZE, "amount after reload");
		helper.succeed();
	}

	@GameTest(template = EMPTY_PLATFORM)
	public static void largeBufferRetainsItsExtendedCapacity(GameTestHelper helper) {
		LargeSingleItemStackHandler handler = new LargeSingleItemStackHandler((short) 200);
		helper.assertTrue(handler.insertItem(new ItemStack(Items.COBBLESTONE, 128), false).isEmpty(), "Large buffer must accept more than a normal stack");
		ItemStack remainder = handler.insertItem(new ItemStack(Items.COBBLESTONE, 100), false);
		helper.assertValueEqual(handler.getAmount(), 200, "stored amount");
		helper.assertValueEqual(remainder.getCount(), 28, "rejected amount");
		helper.succeed();
	}

}
