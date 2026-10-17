package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID)
public final class ModFeatureFlags {

	public static final String PRIMORDIAL_HIVEMIND_PACK = "feature_packs/primordial_hivemind";

	private static @Nullable FeatureFlag primordialHivemind;

	private ModFeatureFlags() {}

	public static FeatureFlag primordialHivemind() {
		if (primordialHivemind == null) {
			primordialHivemind = FeatureFlags.REGISTRY.getFlag(BiomancyMod.rl("primordial_hivemind"));
		}
		return primordialHivemind;
	}

	public static boolean isHivemindEnabled(LevelReader level) {
		return level.enabledFeatures().contains(primordialHivemind());
	}

	public static boolean isHivemindEnabled(@Nullable Level level) {
		return level != null && isHivemindEnabled((LevelReader) level);
	}

	@SubscribeEvent
	public static void onAddPackFinders(final AddPackFindersEvent event) {
		event.addPackFinders(
				BiomancyMod.rl(PRIMORDIAL_HIVEMIND_PACK),
				PackType.SERVER_DATA,
				Component.translatable("dataPack.biomancy.primordial_hivemind.name"),
				PackSource.FEATURE,
				false,
				Pack.Position.TOP
		);
	}

}
