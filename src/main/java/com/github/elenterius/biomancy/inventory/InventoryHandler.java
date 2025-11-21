package com.github.elenterius.biomancy.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;


public final class InventoryHandler<T extends SerializableItemHandler> implements SerializableItemHandler {

	private final T itemHandler;
	private LazyOptional<IItemHandler> optionalItemHandler;

	private final RecipeWrapper recipeWrapper;

	public InventoryHandler(T itemHandler) {
		this.itemHandler = itemHandler;
		recipeWrapper = new RecipeWrapper(itemHandler);
		optionalItemHandler = LazyOptional.of(() -> itemHandler);
	}

	public boolean isEmpty() {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			if (!itemHandler.getStackInSlot(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public CompoundTag serializeNBT() {
		return itemHandler.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		itemHandler.deserializeNBT(nbt);
	}

	public T get() {
		return itemHandler;
	}

	/**
	 * @return raw item handler without any attached behavior
	 */
	public IItemHandler getRaw() {
		return itemHandler instanceof BehavioralItemHandler handler ? handler.withoutBehavior() : itemHandler;
	}

	public LazyOptional<IItemHandler> getLazyOptional() {
		return optionalItemHandler;
	}

	public RecipeWrapper getRecipeWrapper() {
		return recipeWrapper;
	}

	public void invalidate() {
		optionalItemHandler.invalidate();
	}

	public void revive() {
		optionalItemHandler = LazyOptional.of(() -> itemHandler);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		itemHandler.setStackInSlot(slot, stack);
	}

	@Override
	public int getSlots() {
		return itemHandler.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return itemHandler.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return itemHandler.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return itemHandler.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return itemHandler.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return itemHandler.isItemValid(slot, stack);
	}

}
