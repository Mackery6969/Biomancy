package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModParticleTypes {

	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, BiomancyMod.MOD_ID);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOODY_CLAWS_ATTACK = register("bloody_claws_attack", true);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FALLING_BLOOD = register("falling_blood", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LANDING_BLOOD = register("landing_blood", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CORROSIVE_SWIPE_ATTACK = register("corrosive_swipe", true);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DRIPPING_ACID = register("dripping_acid", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FALLING_ACID = register("falling_acid", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LANDING_ACID = register("landing_acid", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PINK_GLOW = register("pink_glow", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIGHT_GREEN_GLOW = register("light_green_glow", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HOSTILE = register("hostile", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BIOHAZARD = register("biohazard", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_BUBBLE = register("acid_bubble", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_BUBBLE_POP = register("acid_bubble_pop", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VOLATILE_BUBBLE = register("volatile_bubble", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VOLATILE_BUBBLE_POP = register("volatile_bubble_pop", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TOXIN_GAS = register("toxin_gas", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TOXIN_GAS_EXPLOSION = register("toxin_gas_explosion", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TOXIN_GAS_EXPLOSION_EMITTER = register("toxin_gas_explosion_emitter", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DECAY_EXPLOSION = register("decay_explosion", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DECAY_EXPLOSION_EMITTER = register("decay_explosion_emitter", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_EXPLOSION = register("acid_explosion", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_EXPLOSION_EMITTER = register("acid_explosion_emitter", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VOLATILE_EXPLOSION = register("volatile_explosion", false);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> VOLATILE_EXPLOSION_EMITTER = register("volatile_explosion_emitter", false);

	private ModParticleTypes() {}

	private static DeferredHolder<ParticleType<?>, SimpleParticleType> register(String name, boolean overrideLimiter) {
		return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(overrideLimiter));
	}

}
