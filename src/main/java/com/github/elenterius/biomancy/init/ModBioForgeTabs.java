package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModBioForgeTabs {

	public static final ResourceLocation REGISTRY_KEY = BiomancyMod.rl("bio_forge_tab");
	public static final DeferredRegister<BioForgeTab> BIO_FORGE_TABS = DeferredRegister.create(REGISTRY_KEY, BiomancyMod.MOD_ID);
	public static final Registry<BioForgeTab> REGISTRY = BIO_FORGE_TABS.makeRegistry(builder -> {});

	public static final DeferredHolder<BioForgeTab, BioForgeTab> SEARCH = register("search", 99, () -> Items.COMPASS);
	public static final DeferredHolder<BioForgeTab, BioForgeTab> BUILDING_BLOCKS = register("blocks", 10, ModItems.FLESH_BLOCK);
	public static final DeferredHolder<BioForgeTab, BioForgeTab> MACHINES = register("machines", 9, ModItems.DECOMPOSER);
	public static final DeferredHolder<BioForgeTab, BioForgeTab> TOOLS = register("tools", 8, ModItems.RAVENOUS_CLAWS);
	public static final DeferredHolder<BioForgeTab, BioForgeTab> COMPONENTS = register("components", 7, ModItems.CREATOR_MIX);
	public static final DeferredHolder<BioForgeTab, BioForgeTab> MISC = register("misc", -99, ModItems.FLESH_IRIS_DOOR);

	private ModBioForgeTabs() {}

	private static DeferredHolder<BioForgeTab, BioForgeTab> register(String name, Supplier<? extends Item> itemSupplier) {
		return BIO_FORGE_TABS.register(name, () -> new BioForgeTab(itemSupplier.get()));
	}

	private static DeferredHolder<BioForgeTab, BioForgeTab> register(String name, int sortPriority, Supplier<? extends Item> itemSupplier) {
		return BIO_FORGE_TABS.register(name, () -> new BioForgeTab(sortPriority, itemSupplier.get()));
	}

}
