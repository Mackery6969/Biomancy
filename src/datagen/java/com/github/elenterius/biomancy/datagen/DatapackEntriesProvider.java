package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBannerPatterns;
import com.github.elenterius.biomancy.init.ModPaintings;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatapackEntriesProvider extends DatapackBuiltinEntriesProvider {

	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.DAMAGE_TYPE, ModDamageTypesBootstrap::bootstrap)
			.add(Registries.ENCHANTMENT, ModEnchantmentsBootstrap::bootstrap)
			.add(Registries.BANNER_PATTERN, ModBannerPatterns::bootstrap)
			.add(Registries.PAINTING_VARIANT, ModPaintings::bootstrap);

	public DatapackEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, BUILDER, Set.of(BiomancyMod.MOD_ID));
	}

	@Override
	public String getName() {
		return "Biomancy's Datapack Entries";
	}

}
