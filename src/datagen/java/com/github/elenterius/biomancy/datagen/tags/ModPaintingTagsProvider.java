package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModPaintings;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModPaintingTagsProvider extends TagsProvider<PaintingVariant> {

	public ModPaintingTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.PAINTING_VARIANT, lookupProvider, BiomancyMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider pProvider) {
		tag(PaintingVariantTags.PLACEABLE)
				.add(ModPaintings.JERRY_PROVIDES);
	}

}
