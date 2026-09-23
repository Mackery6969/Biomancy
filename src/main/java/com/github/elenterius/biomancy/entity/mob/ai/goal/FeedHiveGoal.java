package com.github.elenterius.biomancy.entity.mob.ai.goal;

import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlock;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.init.ModFeatureFlags;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.LevelUtil;
import com.github.elenterius.biomancy.util.sounds.SoundUtil;
import com.github.elenterius.biomancy.world.hivemind.CarriedBiomass;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class FeedHiveGoal extends MoveToBlockGoal {

	public static final int SEARCH_RANGE = 32;

	private final PathfinderMob forager;
	private boolean canFeed;

	public FeedHiveGoal(PathfinderMob mob) {
		super(mob, 1f, SEARCH_RANGE, SEARCH_RANGE);
		forager = mob;
	}

	private boolean isGorged() {
		return forager.getData(ModCapabilities.CARRIED_BIOMASS).isGorged();
	}

	@Override
	public boolean canUse() {
		if (!ModFeatureFlags.isHivemindEnabled(forager.level())) return false;
		if (!isGorged()) return false;

		if (nextStartTick <= 0) canFeed = false;

		return super.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		return canFeed && isGorged() && super.canContinueToUse();
	}

	@Override
	public void tick() {
		super.tick();

		if (!isReachedTarget()) return;

		forager.getLookControl().setLookAt(blockPos.getX() + 0.5d, blockPos.getY() + 0.5d, blockPos.getZ() + 0.5d, 20f, forager.getMaxHeadXRot());

		if (canFeed && forager.level() instanceof ServerLevel serverLevel && serverLevel.getBlockEntity(blockPos) instanceof PrimordialCradleBlockEntity cradle) {
			CarriedBiomass carried = forager.getData(ModCapabilities.CARRIED_BIOMASS);
			if (carried.isGorged()) {
				cradle.feedFromForager(carried);
				forager.setData(ModCapabilities.CARRIED_BIOMASS, CarriedBiomass.NONE);

				SoundEvent soundEvent = cradle.isFull() ? ModSoundEvents.CRADLE_BECAME_FULL.get() : ModSoundEvents.CRADLE_EAT.get();
				SoundUtil.Server.playBlockSound(serverLevel, blockPos, soundEvent);
			}
		}

		canFeed = false;
		nextStartTick = 10;
	}

	@Override
	public double acceptedDistance() {
		return 1.5d;
	}

	@Override
	protected boolean isValidTarget(LevelReader level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof PrimordialCradleBlock && !canFeed && level.getBlockEntity(pos) instanceof PrimordialCradleBlockEntity) {
			canFeed = true;
			return true;
		}
		return false;
	}

	@Override
	protected boolean findNearestBlock() {
		if (!(forager.level() instanceof ServerLevel serverLevel)) return false;

		PrimordialCradleBlockEntity nearestBlockEntity = LevelUtil.findNearestBlockEntity(serverLevel, forager.blockPosition(), SEARCH_RANGE, PrimordialCradleBlockEntity.class);
		if (nearestBlockEntity == null) return false;

		BlockPos nearestPos = nearestBlockEntity.getBlockPos();
		if (isValidTarget(serverLevel, nearestPos)) {
			blockPos = nearestPos;
			return true;
		}

		return false;
	}

}
