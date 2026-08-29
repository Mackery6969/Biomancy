package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBannerPatterns;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBannerPatternTagsProvider extends TagsProvider<BannerPattern> {

	public ModBannerPatternTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.BANNER_PATTERN, lookupProvider, BiomancyMod.MOD_ID, existingFileHelper);
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ModBannerPatterns.TAG_MASCOT).add(
				ModBannerPatterns.MASCOT_BASE,
				ModBannerPatterns.MASCOT_ACCENT,
				ModBannerPatterns.MASCOT_OUTLINE
		);
	}

}
