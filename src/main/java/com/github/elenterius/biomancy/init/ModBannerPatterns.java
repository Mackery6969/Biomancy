package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModBannerPatterns {

	public static final DeferredRegister<BannerPattern> BANNERS = DeferredRegister.create(Registries.BANNER_PATTERN, BiomancyMod.MOD_ID);
	public static final DeferredHolder<BannerPattern, BannerPattern> MASCOT_BASE = register("mascot_base");
	public static final DeferredHolder<BannerPattern, BannerPattern> MASCOT_ACCENT = register("mascot_accent");
	public static final DeferredHolder<BannerPattern, BannerPattern> MASCOT_OUTLINE = register("mascot_outline");
	public static final TagKey<BannerPattern> TAG_MASCOT = createTagKey("mascot");

	private ModBannerPatterns() {}

	private static DeferredHolder<BannerPattern, BannerPattern> register(String name) {
		return BANNERS.register(name, () -> new BannerPattern(BiomancyMod.rlStr(name)));
	}

	private static TagKey<BannerPattern> createTagKey(String name) {
		return TagKey.create(Registries.BANNER_PATTERN, BiomancyMod.rl("pattern_item/" + name));
	}

	private static TagKey<BannerPattern> createTagKey(DeferredHolder<BannerPattern, BannerPattern> registryObject) {
		ResourceLocation registryName = registryObject.getId();
		String modId = registryName.getNamespace();
		String name = registryName.getPath();
		return TagKey.create(Registries.BANNER_PATTERN, new ResourceLocation(modId, "pattern_item/" + name));
	}

}
