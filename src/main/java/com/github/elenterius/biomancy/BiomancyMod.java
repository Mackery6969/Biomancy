package com.github.elenterius.biomancy;

import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.EventCalendar;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.Random;

@Mod(BiomancyMod.MOD_ID)
public final class BiomancyMod {

	public static final String MOD_ID = "biomancy";
	public static final Logger LOGGER = LogManager.getLogger("Biomancy");
	public static final Random GLOBAL_RANDOM = new Random();

	public static final EventCalendar EVENT_CALENDAR = new EventCalendar();

	public static final String DEV_MODE_PROPERTY_KEY = "biomancy.dev_mode";

	public BiomancyMod(IEventBus modEventBus, ModContainer modContainer) {
		GeckoLib.initialize();

		ModBannerPatterns.BANNERS.register(modEventBus);

		ModDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);

		ModBlocks.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
		ModCreativeModeTabs.CREATIVE_TABS.register(modEventBus);

		if (isDevModeEnabled()) {
			ModItems.DEV_ITEMS.register(modEventBus);
			ModCreativeModeTabs.CREATIVE_DEV_TABS.register(modEventBus);
		}

		ModFluids.FLUID_TYPES.register(modEventBus);
		ModFluids.FLUIDS.register(modEventBus);

		ModEntityTypes.ENTITIES.register(modEventBus);
		ModAttributes.ATTRIBUTES.register(modEventBus);
		ModEnchantments.ENCHANTMENTS.register(modEventBus);
		ModMobEffects.EFFECTS.register(modEventBus);
		ModSerums.SERUMS.register(modEventBus);
		ModPotions.POTIONS.register(modEventBus);
		ModPaintings.PAINTINGS.register(modEventBus);

		ModMenuTypes.MENUS.register(modEventBus);

		ModRecipes.RECIPE_TYPES.register(modEventBus);
		ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);
		ModBioForgeTabs.BIO_FORGE_TABS.register(modEventBus);

		ModLoot.GLOBAL_MODIFIERS.register(modEventBus);

		ModSoundEvents.SOUND_EVENTS.register(modEventBus);
		ModParticleTypes.PARTICLE_TYPES.register(modEventBus);

		ModCapabilities.ATTACHMENT_TYPES.register(modEventBus);

		BiomancyConfig.register(modContainer);
		ModsCompatHandler.onBiomancyInit(modEventBus);
	}

	public static boolean isDevModeEnabled() {
		return Boolean.getBoolean(DEV_MODE_PROPERTY_KEY);
	}

	public static ResourceLocation rl(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	public static String rlStr(String path) {
		return MOD_ID + ":" + path;
	}

	public static String translationKey(String prefix, String suffix) {
		return prefix + "." + MOD_ID + "." + suffix;
	}

	public static MutableComponent translatableFrom(String prefix, String suffix) {
		return ComponentUtil.translatable(translationKey(prefix, suffix));
	}

}
