package com.github.elenterius.biomancy.entity.mob.ai.goal;

import com.github.elenterius.biomancy.init.ModFeatureFlags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

public class DefendSelfForHiveGoal extends HurtByTargetGoal {

	private final PathfinderMob defender;

	public DefendSelfForHiveGoal(PathfinderMob mob, Class<?>... toIgnore) {
		super(mob, toIgnore);
		defender = mob;
	}

	@Override
	public boolean canUse() {
		return ModFeatureFlags.isHivemindEnabled(defender.level()) && super.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		return ModFeatureFlags.isHivemindEnabled(defender.level()) && super.canContinueToUse();
	}

}
