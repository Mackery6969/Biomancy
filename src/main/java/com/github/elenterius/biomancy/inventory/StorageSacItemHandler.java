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

import java.util.Optional;

public class StorageSacItemHandler extends ComponentItemHandler {

	private final ItemStack sac;

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
		return InventoryHandlers.EMPTY_ITEM_INVENTORY_PREDICATE.test(stack);
	}

	@Override
	protected ItemContainerContents getContents() {
		if (sac.has(DataComponents.CONTAINER)) return super.getContents();

		CustomData data = sac.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
		if (!data.contains(StorageSacBlockEntity.INVENTORY_KEY)) return ItemContainerContents.EMPTY;

		// Capabilities have no level context. Resolve registry-backed item components on the active side.
		RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, new RegistryOps.RegistryInfoLookup() {
			@Override
			public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> key) {
				HolderLookup.RegistryLookup<T> lookup = CommonHooks.resolveLookup(ResourceKey.createRegistryKey(key.location()));
				return Optional.ofNullable(lookup).map(RegistryOps.RegistryInfo::fromRegistryLookup);
			}
		});
		NonNullList<ItemStack> items = NonNullList.withSize(getSlots(), ItemStack.EMPTY);
		ListTag entries = data.copyTag().getCompound(StorageSacBlockEntity.INVENTORY_KEY).getList("Items", Tag.TAG_COMPOUND);
		for (int index = 0; index < entries.size(); index++) {
			CompoundTag entry = entries.getCompound(index);
			int slot = entry.getInt("Slot");
			if (slot >= 0 && slot < items.size()) {
				// Do not replace legacy data with a partially decoded inventory on a failed migration.
				items.set(slot, ItemStack.CODEC.parse(ops, entry).getOrThrow());
			}
		}
		return ItemContainerContents.fromItems(items);
	}

	@Override
	protected void updateContents(ItemContainerContents contents, ItemStack stack, int slot) {
		boolean migratingLegacyContents = !sac.has(DataComponents.CONTAINER);
		super.updateContents(contents, stack, slot);
		if (migratingLegacyContents) {
			CustomData.update(DataComponents.BLOCK_ENTITY_DATA, sac, tag -> tag.remove(StorageSacBlockEntity.INVENTORY_KEY));
		}
	}
}
