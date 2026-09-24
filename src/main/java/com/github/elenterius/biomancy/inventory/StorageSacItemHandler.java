package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.block.storagesac.StorageSacBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.items.ComponentItemHandler;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class StorageSacItemHandler extends ComponentItemHandler {

	private static final RegistryOps.RegistryInfoLookup ACTIVE_REGISTRIES = new RegistryOps.RegistryInfoLookup() {
		@Override
		public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> key) {
			HolderLookup.RegistryLookup<T> lookup = CommonHooks.resolveLookup(ResourceKey.createRegistryKey(key.location()));
			return Optional.ofNullable(lookup).map(RegistryOps.RegistryInfo::fromRegistryLookup);
		}
	};

	private final ItemStack sac;
	private @Nullable LegacyContents legacyContents;

	public StorageSacItemHandler(ItemStack sac) {
		super(sac, DataComponents.CONTAINER, StorageSacBlockEntity.SLOTS);
		this.sac = sac;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return !isReadOnly() && InventoryHandlers.EMPTY_ITEM_INVENTORY_PREDICATE.test(stack);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return isReadOnly() ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (!isReadOnly()) super.setStackInSlot(slot, stack);
	}

	@Override
	protected ItemContainerContents getContents() {
		LegacyContents legacy = getLegacyContents();
		return legacy != null ? legacy.contents() : super.getContents();
	}

	@Override
	protected void updateContents(ItemContainerContents contents, ItemStack stack, int slot) {
		boolean migratingLegacyContents = !sac.has(DataComponents.CONTAINER);
		super.updateContents(contents, stack, slot);
		if (migratingLegacyContents) {
			CustomData.update(DataComponents.BLOCK_ENTITY_DATA, sac, tag -> tag.remove(StorageSacBlockEntity.INVENTORY_KEY));
		}
	}

	private boolean isReadOnly() {
		LegacyContents legacy = getLegacyContents();
		return legacy != null && !legacy.complete();
	}

	private @Nullable LegacyContents getLegacyContents() {
		if (sac.has(DataComponents.CONTAINER)) return null;

		CustomData data = sac.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
		if (legacyContents == null || legacyContents.source() != data) {
			legacyContents = decodeLegacyContents(data);
		}
		return legacyContents;
	}

	private LegacyContents decodeLegacyContents(CustomData data) {
		if (!data.contains(StorageSacBlockEntity.INVENTORY_KEY)) return new LegacyContents(data, ItemContainerContents.EMPTY, true);

		RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, ACTIVE_REGISTRIES);
		NonNullList<ItemStack> items = NonNullList.withSize(getSlots(), ItemStack.EMPTY);
		boolean complete = true;

		ListTag entries = data.copyTag().getCompound(StorageSacBlockEntity.INVENTORY_KEY).getList("Items", Tag.TAG_COMPOUND);
		for (int index = 0; index < entries.size(); index++) {
			CompoundTag entry = entries.getCompound(index);
			int slot = entry.getInt("Slot");
			if (slot < 0 || slot >= items.size()) continue;

			Optional<ItemStack> stack = ItemStack.CODEC.parse(ops, entry).result();
			if (stack.isPresent()) items.set(slot, stack.get());
			else complete = false;
		}

		return new LegacyContents(data, ItemContainerContents.fromItems(items), complete);
	}

	private record LegacyContents(CustomData source, ItemContainerContents contents, boolean complete) {}

}
