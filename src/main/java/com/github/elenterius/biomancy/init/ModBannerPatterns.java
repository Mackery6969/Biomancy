package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.List;

public final class ModBannerPatterns {

	public static final ResourceKey<BannerPattern> MASCOT_BASE = key("mascot_base");
	public static final ResourceKey<BannerPattern> MASCOT_ACCENT = key("mascot_accent");
	public static final ResourceKey<BannerPattern> MASCOT_OUTLINE = key("mascot_outline");

	public static final List<ResourceKey<BannerPattern>> ALL = List.of(MASCOT_BASE, MASCOT_ACCENT, MASCOT_OUTLINE);

	public static final TagKey<BannerPattern> TAG_MASCOT = createTagKey("mascot");

	private ModBannerPatterns() {}

	private static ResourceKey<BannerPattern> key(String name) {
		return ResourceKey.create(Registries.BANNER_PATTERN, BiomancyMod.rl(name));
	}

	private static TagKey<BannerPattern> createTagKey(String name) {
		return TagKey.create(Registries.BANNER_PATTERN, BiomancyMod.rl("pattern_item/" + name));
	}

	public static void bootstrap(BootstrapContext<BannerPattern> ctx) {
		for (ResourceKey<BannerPattern> key : ALL) {
			String name = key.location().getPath();
			ctx.register(key, new BannerPattern(BiomancyMod.rl(name), "block." + BiomancyMod.MOD_ID + ".banner." + name));
		}
	}

}
