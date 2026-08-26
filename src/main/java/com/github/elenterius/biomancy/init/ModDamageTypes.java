package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.LevelReader;

public final class ModDamageTypes {

	public static final ResourceKey<DamageType> TOOTH_PROJECTILE = key("tooth_projectile");
	public static final ResourceKey<DamageType> IMPALER_PROJECTILE = key("impaler_projectile");
	public static final ResourceKey<DamageType> PRIMORDIAL_SPIKES = key("primordial_spikes");
	public static final ResourceKey<DamageType> CHEST_BITE = key("chest_bite");
	public static final ResourceKey<DamageType> CORROSIVE_ACID = key("corrosive_acid");
	public static final ResourceKey<DamageType> BLEED = key("bleed");
	public static final ResourceKey<DamageType> TOXIN = key("toxin");
	public static final ResourceKey<DamageType> SLASH = key("slash");
	public static final ResourceKey<DamageType> FALL_ON_SPIKE = key("spike_fall_on");
	public static final ResourceKey<DamageType> IMPALED_BY_SPIKE = key("spike_impale");

	private ModDamageTypes() {}

	private static ResourceKey<DamageType> key(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, BiomancyMod.rl(name));
	}

	public static Holder.Reference<DamageType> getHolder(ResourceKey<DamageType> key, LevelReader level) {
		return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
	}

}
