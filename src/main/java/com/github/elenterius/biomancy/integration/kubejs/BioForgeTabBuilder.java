package com.github.elenterius.biomancy.integration.kubejs;

import com.github.elenterius.biomancy.menu.BioForgeTab;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class BioForgeTabBuilder extends BuilderBase<BioForgeTab> {

	public transient int sortPriority = 0;
	public transient Item iconItem = Items.AIR;

	protected BioForgeTabBuilder(ResourceLocation id) {
		super(id);
	}

	@Override
	public RegistryInfo<BioForgeTab> getRegistryType() {
		return BiomancyKubeJSPlugin.BIO_FORGE_TAB_REGISTRY;
	}

	public BioForgeTabBuilder sortPriority(int priority) {
		this.sortPriority = priority;
		return this;
	}

	public BioForgeTabBuilder iconItem(Item item) {
		this.iconItem = item;
		return this;
	}

	@Override
	public BioForgeTab createObject() {
		return new BioForgeTab(sortPriority, iconItem);
	}

}
