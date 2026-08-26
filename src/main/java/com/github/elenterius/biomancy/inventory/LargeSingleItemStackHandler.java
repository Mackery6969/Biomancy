package com.github.elenterius.biomancy.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LargeSingleItemStackHandler extends SingleItemStackHandler {

	public static final String ITEM_AMOUNT_TAG = "ItemAmount";

	private final short maxItemAmount;
	private short itemAmount;

	public LargeSingleItemStackHandler() {
		this(Short.MAX_VALUE);
	}

	public LargeSingleItemStackHandler(short maxItemAmount) {
		this.maxItemAmount = maxItemAmount;
	}

	@Override
	public int getSlotLimit(int slot) {
		return maxItemAmount;
	}

	@Override
	public int getMaxAmount() {
		return maxItemAmount;
	}

	@Override
	public int getAmount() {
		return itemAmount;
	}

	@Override
	public void setAmount(short amount) {
		if (!cachedStack.isEmpty()) {
			int value = Mth.clamp(amount, 0, getMaxAmount());
			itemAmount = (short) value;
			cachedStack.setCount(value);
			onContentsChanged();
		}
	}

	@Override
	public void setStack(ItemStack stack) {
		cachedStack = stack;
		itemAmount = (short) cachedStack.getCount();
		onContentsChanged();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stackIn, boolean simulate) {
		ItemStack remainder = internalInsertItem(slot, stackIn, simulate);
		if (!simulate) {
			itemAmount = (short) cachedStack.getCount();
			onContentsChanged();
		}
		return remainder;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack remainder = internalExtractItem(slot, amount, simulate);
		if (!simulate) {
			itemAmount = (short) cachedStack.getCount();
			onContentsChanged();
		}
		return remainder;
	}

	@Override
	public void serializeItemAmount(CompoundTag tag) {
		tag.putShort(ITEM_AMOUNT_TAG, itemAmount);
	}

	@Override
	public int deserializeItemAmount(CompoundTag tag) {
		itemAmount = tag.getShort(ITEM_AMOUNT_TAG);
		return itemAmount;
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		if (!cachedStack.isEmpty()) {
			serializeItemAmount(tag);
			if (itemAmount > Item.ABSOLUTE_MAX_STACK_SIZE) {
				cachedStack.setCount(Item.ABSOLUTE_MAX_STACK_SIZE); //prevent codec count-range overflow
				tag.put(ITEM_TAG, cachedStack.save(registries));
				cachedStack.setCount(itemAmount); //restore item count
			}
			else {
				tag.put(ITEM_TAG, cachedStack.save(registries));
			}
		}
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider registries, CompoundTag tag) {
		cachedStack = tag.contains(ITEM_TAG) ? ItemStack.parseOptional(registries, tag.getCompound(ITEM_TAG)) : ItemStack.EMPTY;
		if (!cachedStack.isEmpty()) {
			cachedStack.setCount(deserializeItemAmount(tag)); //restore item amount
		}
	}

	void restoreState(ItemStack stack, int amount) {
		cachedStack = stack;
		if (!cachedStack.isEmpty()) {
			cachedStack.setCount(amount);
		}
		itemAmount = (short) amount;
	}

}
