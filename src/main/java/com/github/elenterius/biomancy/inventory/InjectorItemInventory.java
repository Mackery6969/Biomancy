package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.api.serum.SerumContainer;
import com.github.elenterius.biomancy.init.ModDataComponents;
import net.minecraft.world.item.ItemStack;

public class InjectorItemInventory {

	private final ItemStack cachedInventoryHost;
	private final LargeSingleItemStackHandler itemHandler;

	private InjectorItemInventory(short maxSlotSize, ItemStack inventoryHost) {
		itemHandler = new LargeSingleItemStackHandler(maxSlotSize) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() instanceof SerumContainer;
			}

			@Override
			protected void onContentsChanged() {
				serializeToHost();
			}
		};
		cachedInventoryHost = inventoryHost;
	}

	public static InjectorItemInventory create(short maxSlotSize, ItemStack inventoryHost) {
		InjectorItemInventory inventory = new InjectorItemInventory(maxSlotSize, inventoryHost);
		inventory.deserializeFromHost();
		return inventory;
	}

	private void serializeToHost() {
		ItemStack stack = itemHandler.getStack();
		int amount = itemHandler.getAmount();
		if (stack.isEmpty() || amount <= 0) {
			cachedInventoryHost.remove(ModDataComponents.INJECTOR_CONTENTS.get());
		}
		else {
			cachedInventoryHost.set(ModDataComponents.INJECTOR_CONTENTS.get(), new InjectorContents(stack.copyWithCount(1), amount));
		}
	}

	private void deserializeFromHost() {
		InjectorContents contents = cachedInventoryHost.getOrDefault(ModDataComponents.INJECTOR_CONTENTS.get(), InjectorContents.EMPTY);
		itemHandler.restoreState(contents.isEmpty() ? ItemStack.EMPTY : contents.stack(), contents.amount());
	}

	public boolean stillValid() {
		return !cachedInventoryHost.isEmpty();
	}

	public LargeSingleItemStackHandler getItemHandler() {
		deserializeFromHost(); //prime cheese
		return itemHandler;
	}

}
