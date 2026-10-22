package com.github.elenterius.biomancy.entity.mob.ai.goal;

import com.github.elenterius.biomancy.init.ModFeatureFlags;
import com.github.elenterius.biomancy.world.hivemind.HivemindForaging;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public class DefendHiveGoal extends TargetGoal {

	private static final TargetingConditions THREAT_CONDITIONS = TargetingConditions.forCombat().ignoreLineOfSight();

	private static final int RECHECK_INTERVAL = 20;

	private @Nullable LivingEntity threat;
	private int cooldown;

	public DefendHiveGoal(Mob mob) {
		super(mob, false, false);
		setFlags(EnumSet.of(Flag.TARGET));
	}

	@Override
	public boolean canUse() {
		if (!ModFeatureFlags.isHivemindEnabled(mob.level())) return false;

		if (cooldown > 0) {
			cooldown--;
			return false;
		}
		cooldown = RECHECK_INTERVAL;

		threat = HivemindForaging.findHiveThreat(mob);
		return threat != null && threat != mob && canAttack(threat, THREAT_CONDITIONS);
	}

	@Override
	public void start() {
		mob.setTarget(threat);
		super.start();
	}

	@Override
	public boolean canContinueToUse() {
		return ModFeatureFlags.isHivemindEnabled(mob.level()) && super.canContinueToUse();
	}

}
