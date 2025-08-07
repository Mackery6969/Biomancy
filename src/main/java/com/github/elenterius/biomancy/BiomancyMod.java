package com.github.elenterius.biomancy;

import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.util.EventCalendar;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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

	public BiomancyMod() {
		GeckoLib.initialize();

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext modLoadingContext = ModLoadingContext.get();

		ModBannerPatterns.BANNERS.register(modEventBus);

		ModBlocks.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModCreativeModeTabs.CREATIVE_TABS.register(modEventBus);
		ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

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

		BiomancyConfig.register(modLoadingContext);
		ModsCompatHandler.onBiomancyInit(modEventBus);
	}

	public static ResourceLocation createRL(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static String createRLString(String path) {
		return MOD_ID + ":" + path;
	}

}
