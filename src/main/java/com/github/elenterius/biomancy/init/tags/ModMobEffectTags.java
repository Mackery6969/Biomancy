package com.github.elenterius.biomancy.init.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public final class ModMobEffectTags {

	public static final TagKey<MobEffect> NOT_REMOVABLE_WITH_CLEANSING_SERUM = tag("not_removable_with_cleansing_serum");

	public static final TagKey<MobEffect> CRADLE_LIFE_ENERGY_SOURCE = tag("cradle/life_energy_sources");
	public static final TagKey<MobEffect> CRADLE_DISEASE_SOURCE = tag("cradle/disease_sources");
	public static final TagKey<MobEffect> CRADLE_SUCCESS_SOURCE = tag("cradle/success_sources");
	public static final TagKey<MobEffect> CRADLE_HOSTILITY_SOURCE = tag("cradle/hostility_sources");
	public static final TagKey<MobEffect> CRADLE_ANOMALY_SOURCE = tag("cradle/anomaly_sources");

	public static final TagKey<MobEffect> C_IS_ACID = conventionalTag("is_acid");

	private ModMobEffectTags() {}

	public static boolean isNotRemovableWithCleansingSerum(Holder<MobEffect> mobEffect) {
		return mobEffect.is(NOT_REMOVABLE_WITH_CLEANSING_SERUM);
	}

	public static boolean isCradleLifeEnergySource(Holder<MobEffect> mobEffect) {
		return mobEffect.is(CRADLE_LIFE_ENERGY_SOURCE);
	}

	public static boolean isCradleDiseaseSource(Holder<MobEffect> mobEffect) {
		return mobEffect.is(CRADLE_DISEASE_SOURCE);
	}

	public static boolean isCradleSuccessSource(Holder<MobEffect> mobEffect) {
		return mobEffect.is(CRADLE_SUCCESS_SOURCE);
	}

	public static boolean isCradleHostilitySource(Holder<MobEffect> mobEffect) {
		return mobEffect.is(CRADLE_HOSTILITY_SOURCE);
	}

	public static boolean isCradleAnomalySource(Holder<MobEffect> mobEffect) {
		return mobEffect.is(CRADLE_ANOMALY_SOURCE);
	}

	public static boolean isAcid(Holder<MobEffect> mobEffect) {
		return mobEffect.is(C_IS_ACID);
	}

	private static TagKey<MobEffect> tag(String name) {
		return createTag(BiomancyMod.rl(name));
	}

	private static TagKey<MobEffect> conventionalTag(String name) {
		return createTag(ResourceLocation.fromNamespaceAndPath("c", name));
	}

	private static TagKey<MobEffect> createTag(ResourceLocation key) {
		return TagKey.create(Registries.MOB_EFFECT, key);
	}

}
