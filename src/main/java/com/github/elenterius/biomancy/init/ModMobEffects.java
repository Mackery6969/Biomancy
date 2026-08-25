package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.statuseffect.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModMobEffects {

	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, BiomancyMod.MOD_ID);

	public static final DeferredHolder<MobEffect, CorrosiveEffect> CORROSIVE = EFFECTS.register("corrosive", () -> new CorrosiveEffect(MobEffectCategory.HARMFUL, 0x39FF14));
	public static final DeferredHolder<MobEffect, ArmorShredEffect> ARMOR_SHRED = EFFECTS.register("armor_shred", () -> new ArmorShredEffect(MobEffectCategory.HARMFUL, 20, 0x909090)
			.addModifier(Attributes.ARMOR, "a15ed03e-c5db-4cf8-a0f5-4eb4657bb731", -1f, AttributeModifier.Operation.ADDITION));
	public static final DeferredHolder<MobEffect, BleedEffect> BLEED = EFFECTS.register("bleed", () -> new BleedEffect(MobEffectCategory.HARMFUL, 0x8a0303, 2));
	public static final DeferredHolder<MobEffect, ToxinEffect> TOXIN = EFFECTS.register("toxin", () -> new ToxinEffect(MobEffectCategory.HARMFUL, 0x87a363));
	public static final DeferredHolder<MobEffect, VolatileEffect> VOLATILE = EFFECTS.register("volatile", () -> new VolatileEffect(MobEffectCategory.HARMFUL, 0xff681f));

	public static final DeferredHolder<MobEffect, EssenceAnemiaEffect> ESSENCE_ANEMIA = EFFECTS.register("essence_anemia", () -> new EssenceAnemiaEffect(MobEffectCategory.HARMFUL, 0xfefefe)
			.addModifier(Attributes.MAX_HEALTH, "a6ca3300-17d9-41c7-b29d-af93fa367b23", -0.2f, AttributeModifier.Operation.MULTIPLY_BASE)
	);
	public static final DeferredHolder<MobEffect, DespoilEffect> DESPOIL = EFFECTS.register("despoil", () -> new DespoilEffect(MobEffectCategory.BENEFICIAL, 0xdd77ff));
	public static final DeferredHolder<MobEffect, LibidoEffect> LIBIDO = EFFECTS.register("libido", () -> new LibidoEffect(MobEffectCategory.NEUTRAL, 0xe06a78));

	public static final DeferredHolder<MobEffect, AttackDamageEffect> FRENZY = EFFECTS.register("frenzy", () -> new FrenzyEffect(MobEffectCategory.BENEFICIAL, 0xd1001c)
			.addAttackDamageModifier("1f1fb00f-d6bc-4b42-8533-422054cea63d", 6f, 0, AttributeModifier.Operation.ADDITION) // Strength ~II
			.addModifier(Attributes.MOVEMENT_SPEED, "14e2a39c-abb5-43a4-9449-522eec57ff2e", 0.225f, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addModifier(Attributes.ATTACK_SPEED, "08a20d5b-60ce-4769-9e67-71cab0abe989", 0.175f, AttributeModifier.Operation.MULTIPLY_TOTAL));

	public static final DeferredHolder<MobEffect, WithdrawalEffect> WITHDRAWAL = EFFECTS.register("withdrawal", () -> new WithdrawalEffect(0x5c4b88)
			.addAttackDamageModifier("8dadcbe5-9098-4545-b07c-3e9120c84232", -3, 0, AttributeModifier.Operation.ADDITION)
			.addModifier(Attributes.MOVEMENT_SPEED, "0f1be88c-cbb2-455c-8559-0b420caa980d", -0.1125f, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addModifier(Attributes.ATTACK_SPEED, "ab116bd1-196b-4bf8-a136-6c24e7c0e80d", -0.0625f, AttributeModifier.Operation.MULTIPLY_TOTAL));

	public static final DeferredHolder<MobEffect, StatusEffect> PRIMORDIAL_INFESTATION = EFFECTS.register("primordial_infestation", () -> new StatusEffect(MobEffectCategory.HARMFUL, 0xbe3ee1, false));

	private ModMobEffects() {}

}
