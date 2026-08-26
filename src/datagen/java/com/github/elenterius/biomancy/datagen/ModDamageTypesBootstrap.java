package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.init.ModDamageTypes;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public final class ModDamageTypesBootstrap {

	private ModDamageTypesBootstrap() {
	}

	public static void bootstrap(BootstapContext<DamageType> ctx) {
		bootstrap(ctx, ModDamageTypes.IMPALER_PROJECTILE, DamageScaling.NEVER, 0.5f);
		bootstrap(ctx, ModDamageTypes.TOOTH_PROJECTILE);
		bootstrap(ctx, ModDamageTypes.PRIMORDIAL_SPIKES, DamageScaling.ALWAYS, 0);
		bootstrap(ctx, ModDamageTypes.CHEST_BITE, DamageScaling.ALWAYS, 0.25f);
		bootstrap(ctx, ModDamageTypes.CORROSIVE_ACID, 0.1f);
		bootstrap(ctx, ModDamageTypes.BLEED, 0.25f);
		bootstrap(ctx, ModDamageTypes.TOXIN);
		bootstrap(ctx, ModDamageTypes.SLASH, 0.25f);
		bootstrap(ctx, ModDamageTypes.FALL_ON_SPIKE);
		bootstrap(ctx, ModDamageTypes.IMPALED_BY_SPIKE);
	}

	private static void bootstrap(BootstapContext<DamageType> ctx, ResourceKey<DamageType> key) {
		ctx.register(key, new DamageType(key.location().toLanguageKey(), 0));
	}

	private static void bootstrap(BootstapContext<DamageType> ctx, ResourceKey<DamageType> key, float exhaustion) {
		ctx.register(key, new DamageType(key.location().toLanguageKey(), exhaustion));
	}

	private static void bootstrap(BootstapContext<DamageType> ctx, ResourceKey<DamageType> key, float exhaustion,
			DamageEffects effects) {
		ctx.register(key, new DamageType(key.location().toLanguageKey(), exhaustion, effects));
	}

	private static void bootstrap(BootstapContext<DamageType> ctx, ResourceKey<DamageType> key, DamageScaling scaling,
			float exhaustion) {
		ctx.register(key, new DamageType(key.location().toLanguageKey(), scaling, exhaustion));
	}

	private static void bootstrap(BootstapContext<DamageType> ctx, ResourceKey<DamageType> key, DamageScaling scaling,
			float exhaustion, DamageEffects effects) {
		ctx.register(key, new DamageType(key.location().toLanguageKey(), scaling, exhaustion, effects));
	}

}
