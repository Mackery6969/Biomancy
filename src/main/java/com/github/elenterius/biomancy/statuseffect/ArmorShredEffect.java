package com.github.elenterius.biomancy.statuseffect;

import net.minecraft.world.effect.MobEffectCategory;

public class ArmorShredEffect extends StatusEffect implements StackingStatusEffect {

	private final int maxStackSize;

	public ArmorShredEffect(MobEffectCategory category, int maxStackSize, int color) {
		super(category, color, false);
		this.maxStackSize = maxStackSize;
	}

	@Override
	public int getMaxEffectStack() {
		return maxStackSize;
	}

}
