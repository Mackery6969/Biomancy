package com.github.elenterius.biomancy.integration;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.integration.farmersdelight.FarmersDelightCompat;
import com.github.elenterius.biomancy.integration.overweightfarming.OverweightFarmingHelper;
import com.github.elenterius.biomancy.integration.overweightfarming.OverweightFarmingIntegration;
import com.github.elenterius.biomancy.integration.pehkui.PehkuiHelper;
import com.github.elenterius.biomancy.integration.pehkui.PehkuiIntegration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ModsCompatHandler {

	static final Marker LOG_MARKER = MarkerManager.getMarker(ModsCompatHandler.class.getSimpleName());

	static PehkuiHelper PEHKUI_HELPER = PehkuiHelper.EMPTY;
	static OverweightFarmingHelper OVERWEIGHT_FARMING_HELPER = OverweightFarmingHelper.createEmpty();

	private ModsCompatHandler() {}

	public static void onBiomancyInit(final IEventBus eventBus) {
		if (ModList.get().isLoaded("pehkui")) {
			BiomancyMod.LOGGER.info(LOG_MARKER, "Initializing Pehkui Integration...");
			PehkuiIntegration.init(helper -> PEHKUI_HELPER = helper);
		}

		if (ModList.get().isLoaded("overweight_farming")) {
			BiomancyMod.LOGGER.info(LOG_MARKER, "Initializing Overweight Farming integration...");
			OverweightFarmingIntegration.init(helper -> OVERWEIGHT_FARMING_HELPER = helper);
		}

		if (ModList.get().isLoaded("nerb")) {
			BiomancyMod.LOGGER.fatal(LOG_MARKER, "Detected incompatible mods: Biomancy is not compatible with 'Not Enough Recipe Book (NERB)'. Please remove/replace NERB with ORB.");
		}
	}

	public static void onBiomancyCommonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			if (ModList.get().isLoaded("farmersdelight")) {
				BiomancyMod.LOGGER.info(LOG_MARKER, "Setting up Farmer's Delight compat...");
				FarmersDelightCompat.onPostSetup();
			}

			if (ModList.get().isLoaded("overweight_farming")) {
				BiomancyMod.LOGGER.info(LOG_MARKER, "Setting up Overweight Farming compat...");
				OverweightFarmingIntegration.onPostSetup();
			}
		});
	}

	public static void onBiomancyClientSetup(final FMLClientSetupEvent event) {
		//		event.enqueueWork(() -> {
		//			if (ModList.get().isLoaded("jeresources")) {
		//				BiomancyMod.LOGGER.info(LOG_MARKER, "setup JER plugin...");
		//				BiomancyJerPlugin.onClientPostSetup();
		//			}
		//		});
	}

	public static PehkuiHelper getPehkuiHelper() {
		return PEHKUI_HELPER;
	}

	public static OverweightFarmingHelper getOverweightFarmingHelper() {
		return OVERWEIGHT_FARMING_HELPER;
	}

}
