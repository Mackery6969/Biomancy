package com.github.elenterius.biomancy.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class ItemHandlerContainer implements Container {

	protected final IItemHandlerModifiable itemHandler;

	public ItemHandlerContainer(IItemHandlerModifiable itemHandler) {
		this.itemHandler = itemHandler;
	}

	@Override
	public int getContainerSize() {
		return itemHandler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			if (!itemHandler.getStackInSlot(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return itemHandler.getStackInSlot(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		return itemHandler.extractItem(slot, amount, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = itemHandler.getStackInSlot(slot);
		itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		itemHandler.setStackInSlot(slot, stack);
	}

	@Override
	public void setChanged() {}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			itemHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

}
