package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Locale;
import java.util.function.Supplier;

public final class ModPotions {

	public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, BiomancyMod.MOD_ID);

	public static final DeferredHolder<Potion, Potion> PRIMORDIAL_INFESTATION = register("primordial_infestation", () -> new MobEffectInstance[]{
			new MobEffectInstance(ModMobEffects.PRIMORDIAL_INFESTATION, 20 * 90, 0),
	});

	public static final DeferredHolder<Potion, Potion> LONG_PRIMORDIAL_INFESTATION = register("primordial_infestation", PotionVariant.LONG, () -> new MobEffectInstance[]{
			new MobEffectInstance(ModMobEffects.PRIMORDIAL_INFESTATION, 20 * 240, 0),
	});

	private ModPotions() {}

	private static DeferredHolder<Potion, Potion> register(String name, Supplier<MobEffectInstance[]> effects) {
		String translationKey = BiomancyMod.MOD_ID + "." + name;
		return POTIONS.register(name, () -> new Potion(translationKey, effects.get()));
	}

	private static DeferredHolder<Potion, Potion> register(String name, PotionVariant variant, Supplier<MobEffectInstance[]> effects) {
		String key = variant.name().toLowerCase(Locale.ENGLISH) + "_" + name;
		String translationKey = BiomancyMod.MOD_ID + "." + name;
		return POTIONS.register(key, () -> new Potion(translationKey, effects.get()));
	}

	private enum PotionVariant {
		LONG, STRONG
	}

}
