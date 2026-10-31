package com.github.elenterius.biomancy.entity.mob.fleshblob;

import com.github.elenterius.biomancy.init.ModFeatureFlags;

class HiveAttackGoal extends FleshBlobAttackGoal {

	private final FleshBlob blob;

	public HiveAttackGoal(FleshBlob mob, double speed) {
		super(mob, speed);
		blob = mob;
	}

	@Override
	public boolean canUse() {
		return ModFeatureFlags.isHivemindEnabled(blob.level()) && super.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		return ModFeatureFlags.isHivemindEnabled(blob.level()) && super.canContinueToUse();
	}

}
