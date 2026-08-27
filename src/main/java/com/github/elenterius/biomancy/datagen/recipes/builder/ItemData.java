package com.github.elenterius.biomancy.datagen.recipes.builder;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class ItemData {

	private final ResourceLocation registryName;
	private final int count;
	private final @Nullable CompoundTag tag;

	public ItemData(ItemStack stack) {
		this(stack, stack.getCount());
	}

	public ItemData(ItemLike item) {
		this(item, 1);
	}

	public ItemData(ItemStack stack, int count) {
		this(stack.getItem(), stack.get(DataComponents.CUSTOM_DATA) != null ? stack.get(DataComponents.CUSTOM_DATA).copyTag() : null, count);
	}

	public ItemData(ItemLike item, int count) {
		this(item, null, count);
	}

	public ItemData(ItemLike item, @Nullable CompoundTag tag, int count) {
		this.registryName = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item.asItem()));
		this.tag = tag;
		this.count = count;
	}

	public static ItemData from(String namespace, String path) {
		return new ItemData(ResourceLocation.fromNamespaceAndPath(namespace, path));
	}

	public ItemData(ResourceLocation registryName) {
		this(registryName, null, 1);
	}

	public ItemData(ResourceLocation registryName, @Nullable CompoundTag tag, int count) {
		this.registryName = registryName;
		this.tag = tag;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public ResourceLocation getRegistryName() {
		return registryName;
	}

	public ItemStack toItemStack() {
		ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(registryName), count);
		if (tag != null && !tag.isEmpty()) {
			stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		}
		return stack;
	}

	public String getItemPath() {
		return registryName.getPath();
	}

}
