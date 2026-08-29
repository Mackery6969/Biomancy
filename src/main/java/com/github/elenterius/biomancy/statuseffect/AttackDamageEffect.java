package com.github.elenterius.biomancy.statuseffect;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AttackDamageEffect extends StatusEffect {

	public AttackDamageEffect(MobEffectCategory category, int color) {
		this(category, color, true);
	}

	public AttackDamageEffect(MobEffectCategory category, int color, boolean isCurable) {
		super(category, color, isCurable);
	}

	public AttackDamageEffect addAttackDamageModifier(String uuid, double damageMultiplier, double amount, AttributeModifier.Operation operation) {
		addAttributeModifier(Attributes.ATTACK_DAMAGE, BiomancyMod.rl(uuid), operation, amplifier -> (amplifier + 1) * damageMultiplier);
		return this;
	}

}
