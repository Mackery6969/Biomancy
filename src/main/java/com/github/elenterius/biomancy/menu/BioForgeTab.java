package com.github.elenterius.biomancy.menu;

import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class BioForgeTab {
	public static final String JSON_KEY = "bio_forge_tab";
	private final int sortPriority;
	private final Item iconItem;

	public BioForgeTab(int sortPriority, Item iconItem) {
		this.sortPriority = sortPriority;
		this.iconItem = iconItem;
	}

	public BioForgeTab(Item itemSupplier) {
		this(0, itemSupplier);
	}

	@Nullable
	public static BioForgeTab fromJson(JsonObject json) {
		String categoryId = GsonHelper.getAsString(json, JSON_KEY);
		return ModBioForgeTabs.REGISTRY.get(ResourceLocation.parse(categoryId));
	}

	public static String getTabId(JsonObject json) {
		return GsonHelper.getAsString(json, JSON_KEY);
	}

	public static BioForgeTab fromNetwork(FriendlyByteBuf buffer) {
		BioForgeTab value = ModBioForgeTabs.REGISTRY.get(buffer.readResourceLocation());
		return value != null ? value : ModBioForgeTabs.MISC.get();
	}

	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(ModBioForgeTabs.REGISTRY.getKey(this));
	}

	public void toJson(JsonObject json) {
		json.addProperty(JSON_KEY, ModBioForgeTabs.REGISTRY.getKey(this).toString());
	}

	public ItemStack getIcon() {
		return new ItemStack(iconItem);
	}

	public String enumId() {
		return enumIdFrom(ModBioForgeTabs.REGISTRY.getKey(this));
	}

	public static String enumIdFrom(ResourceLocation key) {
		return JSON_KEY + "_" + key.getNamespace() + "_" + key.getPath();
	}

	public String translationKey() {
		return ModBioForgeTabs.REGISTRY.getKey(this).toLanguageKey(JSON_KEY);
	}

	public int sortPriority() {
		return sortPriority;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (BioForgeTab) obj;
		return ModBioForgeTabs.REGISTRY.getKey(this).equals(ModBioForgeTabs.REGISTRY.getKey(that)) && this.sortPriority == that.sortPriority && Objects.equals(this.iconItem, that.iconItem);
	}

	/*@Override
	public int hashCode() {
		return 31 * ModBioForgeTabs.REGISTRY.getKey(this).getNamespace().hashCode() + ModBioForgeTabs.REGISTRY.getKey(this).getPath().hashCode();
	}*/

	@Override
	public String toString() {
		return "BioForgeTab[" + "id=" + ModBioForgeTabs.REGISTRY.getKey(this) + ", " + "sortPriority=" + sortPriority + ", " + "iconSupplier=" + iconItem + ']';
	}

}
