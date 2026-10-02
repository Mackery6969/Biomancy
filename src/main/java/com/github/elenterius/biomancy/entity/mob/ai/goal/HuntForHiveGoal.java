package com.github.elenterius.biomancy.entity.mob.ai.goal;

import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.init.ModFeatureFlags;
import com.github.elenterius.biomancy.world.hivemind.HivemindForaging;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import java.util.function.Predicate;

public class HuntForHiveGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

	private static final int RECHECK_INTERVAL = 10;

	public HuntForHiveGoal(Mob mob, Class<T> targetType, boolean mustSee, Predicate<LivingEntity> targetPredicate) {
		super(mob, targetType, RECHECK_INTERVAL, mustSee, false, targetPredicate);
	}

	@Override
	public boolean canUse() {
		if (!ModFeatureFlags.isHivemindEnabled(mob.level())) return false;
		if (mob.getData(ModCapabilities.CARRIED_BIOMASS).isFull()) return false;

		return super.canUse() && HivemindForaging.isHiveHungry(mob);
	}

	@Override
	public boolean canContinueToUse() {
		return ModFeatureFlags.isHivemindEnabled(mob.level()) && super.canContinueToUse();
	}

}
