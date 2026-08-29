package com.github.elenterius.biomancy.datagen.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.tags.ModMobEffectTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModMobEffectTagsProvider extends IntrinsicHolderTagsProvider<MobEffect> {

	public ModMobEffectTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.MOB_EFFECT, lookupProvider, mobEffect -> BuiltInRegistries.MOB_EFFECT.getResourceKey(mobEffect).orElseThrow(), BiomancyMod.MOD_ID, existingFileHelper);
	}

	private static TagKey<MobEffect> forgeTag(String path) {
		return TagKey.create(Registries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath("forge", path));
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}

	@Override
	protected void addTags(HolderLookup.Provider pProvider) {
		addBiomancyTags();

		tag(forgeTag("is_poison")).add(ModMobEffects.TOXIN.get());

		tag(ModMobEffectTags.FORGE_IS_ACID).add(
				ModMobEffects.CORROSIVE.get()
		);
	}

	private void addBiomancyTags() {
		tag(ModMobEffectTags.NOT_REMOVABLE_WITH_CLEANSING_SERUM).add(
				ModMobEffects.ESSENCE_ANEMIA.get(),
				ModMobEffects.WITHDRAWAL.get()
		);

		tag(ModMobEffectTags.CRADLE_LIFE_ENERGY_SOURCE).add(
				MobEffects.HEAL.value(),
				MobEffects.REGENERATION.value(),
				MobEffects.HEALTH_BOOST.value(),
				MobEffects.ABSORPTION.value()
		);

		tag(ModMobEffectTags.CRADLE_DISEASE_SOURCE).add(
				MobEffects.WEAKNESS.value(),
				MobEffects.WITHER.value(),
				MobEffects.POISON.value(),
				ModMobEffects.BLEED.get()
		);

		tag(ModMobEffectTags.CRADLE_SUCCESS_SOURCE).add(
				MobEffects.LUCK.value(),
				MobEffects.SATURATION.value(),
				ModMobEffects.LIBIDO.get()
		);

		tag(ModMobEffectTags.CRADLE_HOSTILITY_SOURCE).add(
				MobEffects.HUNGER.value(),
				MobEffects.CONFUSION.value(),
				MobEffects.BLINDNESS.value(),
				MobEffects.HARM.value(),
				MobEffects.WITHER.value(),
				MobEffects.POISON.value(),
				ModMobEffects.BLEED.get()
		);

		tag(ModMobEffectTags.CRADLE_ANOMALY_SOURCE).add(
				MobEffects.BAD_OMEN.value(),
				MobEffects.DARKNESS.value(),
				ModMobEffects.CORROSIVE.get(),
				ModMobEffects.PRIMORDIAL_INFESTATION.get()
		);
	}

}
