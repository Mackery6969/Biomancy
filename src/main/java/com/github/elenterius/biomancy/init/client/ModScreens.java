package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.client.gui.*;
import com.github.elenterius.biomancy.init.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;


public final class ModScreens {


	private ModScreens() {}

	static void registerMenuScreens(RegisterMenuScreensEvent event) {
		registerMenuScreen(event, ModMenuTypes.DECOMPOSER, DecomposerScreen::new);
		registerMenuScreen(event, ModMenuTypes.BIO_LAB, BioLabScreen::new);
		registerMenuScreen(event, ModMenuTypes.STORAGE_SAC, StorageSacScreen::new);
		registerMenuScreen(event, ModMenuTypes.FLESHKIN_CHEST, FleshkinChestScreen::new);
		registerMenuScreen(event, ModMenuTypes.DIGESTER, DigesterScreen::new);
		registerMenuScreen(event, ModMenuTypes.BIO_FORGE, BioForgeScreen::new);
	}

	private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerMenuScreen(RegisterMenuScreensEvent event, DeferredHolder<MenuType<?>, MenuType<M>> registryObject, MenuScreens.ScreenConstructor<M, U> factory) {
		event.register(registryObject.get(), factory);
	}

}
