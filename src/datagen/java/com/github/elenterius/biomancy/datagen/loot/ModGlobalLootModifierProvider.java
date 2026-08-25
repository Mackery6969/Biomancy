package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModLoot;
import com.github.elenterius.biomancy.loot.CatMorningGiftLootModifier;
import com.github.elenterius.biomancy.loot.DespoilLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {

	public ModGlobalLootModifierProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider, BiomancyMod.MOD_ID);
	}

	@Override
	protected void start() {
		addLootModifier(ModLoot.DESPOIL_SERIALIZER, new DespoilLootModifier());
		addLootModifier(ModLoot.CAT_MORNING_GIFT_SERIALIZER, new CatMorningGiftLootModifier());
	}

	protected <T extends IGlobalLootModifier> void addLootModifier(DeferredHolder<?, ?> codecHolder, T lootModifier) {
		add(codecHolder.getId().getPath(), lootModifier);
	}

}
