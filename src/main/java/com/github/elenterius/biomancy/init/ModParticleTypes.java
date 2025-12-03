package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticleTypes {

	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BiomancyMod.MOD_ID);
	public static final RegistryObject<SimpleParticleType> BLOODY_CLAWS_ATTACK = register("bloody_claws_attack", true);
	public static final RegistryObject<SimpleParticleType> FALLING_BLOOD = register("falling_blood", false);
	public static final RegistryObject<SimpleParticleType> LANDING_BLOOD = register("landing_blood", false);
	public static final RegistryObject<SimpleParticleType> CORROSIVE_SWIPE_ATTACK = register("corrosive_swipe", true);
	public static final RegistryObject<SimpleParticleType> DRIPPING_ACID = register("dripping_acid", false);
	public static final RegistryObject<SimpleParticleType> FALLING_ACID = register("falling_acid", false);
	public static final RegistryObject<SimpleParticleType> LANDING_ACID = register("landing_acid", false);
	public static final RegistryObject<SimpleParticleType> PINK_GLOW = register("pink_glow", false);
	public static final RegistryObject<SimpleParticleType> LIGHT_GREEN_GLOW = register("light_green_glow", false);
	public static final RegistryObject<SimpleParticleType> HOSTILE = register("hostile", false);
	public static final RegistryObject<SimpleParticleType> BIOHAZARD = register("biohazard", false);
	public static final RegistryObject<SimpleParticleType> ACID_BUBBLE = register("acid_bubble", false);
	public static final RegistryObject<SimpleParticleType> ACID_BUBBLE_POP = register("acid_bubble_pop", false);
	public static final RegistryObject<SimpleParticleType> VOLATILE_BUBBLE = register("volatile_bubble", false);
	public static final RegistryObject<SimpleParticleType> VOLATILE_BUBBLE_POP = register("volatile_bubble_pop", false);
	public static final RegistryObject<SimpleParticleType> TOXIN_GAS = register("toxin_gas", false);
	public static final RegistryObject<SimpleParticleType> TOXIN_GAS_EXPLOSION = register("toxin_gas_explosion", false);
	public static final RegistryObject<SimpleParticleType> TOXIN_GAS_EXPLOSION_EMITTER = register("toxin_gas_explosion_emitter", false);
	public static final RegistryObject<SimpleParticleType> DECAY_EXPLOSION = register("decay_explosion", false);
	public static final RegistryObject<SimpleParticleType> DECAY_EXPLOSION_EMITTER = register("decay_explosion_emitter", false);
	public static final RegistryObject<SimpleParticleType> ACID_EXPLOSION = register("acid_explosion", false);
	public static final RegistryObject<SimpleParticleType> ACID_EXPLOSION_EMITTER = register("acid_explosion_emitter", false);
	public static final RegistryObject<SimpleParticleType> VOLATILE_EXPLOSION = register("volatile_explosion", false);
	public static final RegistryObject<SimpleParticleType> VOLATILE_EXPLOSION_EMITTER = register("volatile_explosion_emitter", false);

	private ModParticleTypes() {}

	private static RegistryObject<SimpleParticleType> register(String name, boolean overrideLimiter) {
		return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(overrideLimiter));
	}

}
