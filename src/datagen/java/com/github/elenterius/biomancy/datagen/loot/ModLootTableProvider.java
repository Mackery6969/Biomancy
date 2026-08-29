package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.init.ModLoot;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModLootTableProvider extends LootTableProvider {

	public static final Marker LOG_MARKER = MarkerManager.getMarker("LootTableProvider");

	public ModLootTableProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
		super(packOutput, requiredTables(), subProviders(), registries);
	}

	private static Set<ResourceKey<LootTable>> requiredTables() {
		return ModLoot.Entity.all().stream().map(rl -> ResourceKey.create(Registries.LOOT_TABLE, rl)).collect(Collectors.toSet());
	}

	private static List<SubProviderEntry> subProviders() {
		return List.of(
				new SubProviderEntry(ModEntityLoot::new, LootContextParamSets.ENTITY),
				new SubProviderEntry(ModDespoilLoot::new, LootContextParamSets.ENTITY),
				new SubProviderEntry(ModBlockLoot::new, LootContextParamSets.BLOCK)
		);
	}

}
