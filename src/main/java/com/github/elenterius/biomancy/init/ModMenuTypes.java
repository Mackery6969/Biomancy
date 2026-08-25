package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.menu.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModMenuTypes {

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, BiomancyMod.MOD_ID);
	public static final DeferredHolder<MenuType<?>, MenuType<DecomposerMenu>> DECOMPOSER = MENUS.register("decomposer", () -> IForgeMenuType.create(DecomposerMenu::createClientMenu));
	public static final DeferredHolder<MenuType<?>, MenuType<BioLabMenu>> BIO_LAB = MENUS.register("bio_lab", () -> IForgeMenuType.create(BioLabMenu::createClientMenu));
	public static final DeferredHolder<MenuType<?>, MenuType<StorageSacMenu>> STORAGE_SAC = MENUS.register("storage_sac", () -> IForgeMenuType.create(StorageSacMenu::createClientMenu));
	public static final DeferredHolder<MenuType<?>, MenuType<FleshkinChestMenu>> FLESHKIN_CHEST = MENUS.register("flesh_chest", () -> IForgeMenuType.create(FleshkinChestMenu::createClientMenu));
	public static final DeferredHolder<MenuType<?>, MenuType<DigesterMenu>> DIGESTER = MENUS.register("digester", () -> IForgeMenuType.create(DigesterMenu::createClientMenu));
	public static final DeferredHolder<MenuType<?>, MenuType<BioForgeMenu>> BIO_FORGE = MENUS.register("bio_forge", () -> IForgeMenuType.create(BioForgeMenu::createClientMenu));
	private ModMenuTypes() {}

}
