package com.github.elenterius.biomancy.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public class ItemStackFilter implements Predicate<ItemStack>, INBTSerializable<CompoundTag> {

	public static final String FILTER_KEY = "Filter";
	public static final String STRICT_KEY = "Strict";

	public static final ItemStackFilter ALLOW_ANY = new ItemStackFilter(null, false);
	public static final ItemStackFilter ALLOW_NONE = new ItemStackFilter(ItemStack.EMPTY, false);

	@Nullable
	private ItemStack filter;
	private boolean isStrict;

	protected ItemStackFilter(@Nullable ItemStack filter, boolean isStrict) {
		this.filter = filter;
		this.isStrict = isStrict;
	}

	protected ItemStackFilter(HolderLookup.Provider registries, CompoundTag tag) {
		deserializeNBT(registries, tag);
	}

	public static ItemStackFilter of(HolderLookup.Provider registries, CompoundTag tag) {
		return new ItemStackFilter(registries, tag);
	}

	public static ItemStackFilter of(Item filter) {
		return of(filter.getDefaultInstance(), false);
	}

	public static ItemStackFilter of(@Nullable ItemStack filter) {
		return of(filter, true);
	}

	private static ItemStackFilter of(@Nullable ItemStack filter, boolean isStrict) {
		if (filter == null) return ALLOW_ANY;
		if (filter.isEmpty()) return ALLOW_NONE;

		filter = filter.copyWithCount(1);
		filter.remove(DataComponents.ENCHANTMENTS);
		filter.remove(DataComponents.ATTRIBUTE_MODIFIERS);

		return new ItemStackFilter(filter, isStrict);
	}

	@Override
	public boolean test(ItemStack stack) {
		if (stack.isEmpty()) return false;

		if (filter == null) return true;
		if (filter.isEmpty()) return false;

		if (isStrict) {
			return ItemStack.isSameItemSameComponents(filter, stack);
		}
		else {
			return filter.is(stack.getItem());
		}
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		if (filter != null) {
			tag.put(FILTER_KEY, filter.saveOptional(registries));
			tag.putBoolean(STRICT_KEY, isStrict);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider registries, CompoundTag tag) {
		filter = tag.contains(FILTER_KEY) ? ItemStack.parseOptional(registries, tag.getCompound(FILTER_KEY)) : null;
		isStrict = tag.getBoolean(STRICT_KEY);
	}

	public boolean allowsAny() {
		return filter == null;
	}

	public @Nullable ItemStack getItemStack() {
		return filter;
	}

}
