package com.github.elenterius.biomancy.world.hivemind;

import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.entity.mob.Fleshkin;
import com.github.elenterius.biomancy.entity.mob.fleshblob.FleshBlob;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.github.elenterius.biomancy.util.LevelUtil;
import com.github.elenterius.biomancy.util.MobUtil;
import com.github.elenterius.biomancy.world.mound.MoundShape;
import com.github.elenterius.spatialdb.SpatialDBManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public final class HivemindForaging {

	private static final int BIOMASS_PER_PREY = 25;
	private static final float MIN_PREY_SCALE = 0.1f;
	private static final float MAX_PREY_SCALE = 4f;
	private static final int HIVE_SEARCH_RANGE = 32;

	private HivemindForaging() {}

	public static void onPreyKilled(ServerLevel level, LivingEntity prey, DamageSource source) {
		if (!(source.getEntity() instanceof Mob killer)) return;
		if (!isFleshOfTheHive(killer)) return;

		CarriedBiomass carried = killer.getData(ModCapabilities.CARRIED_BIOMASS);
		if (carried.isFull()) return;

		float scale = Mth.clamp(MobUtil.getVolume(prey) / MobUtil.getVolume(EntityType.PLAYER), MIN_PREY_SCALE, MAX_PREY_SCALE);
		int biomass = Mth.ceil(scale * BIOMASS_PER_PREY);
		int lifeEnergy = Mth.ceil(prey.getMaxHealth() * scale);

		killer.setData(ModCapabilities.CARRIED_BIOMASS, carried.add(biomass, lifeEnergy));

		level.sendParticles(ModParticleTypes.PINK_GLOW.get(), killer.getX(), killer.getY(0.5d), killer.getZ(), 4, 0.25d, 0.25d, 0.25d, 0);
	}

	public static @Nullable PrimordialCradleBlockEntity findHive(Mob mob) {
		if (!(mob.level() instanceof ServerLevel serverLevel)) return null;
		return LevelUtil.findNearestBlockEntity(serverLevel, mob.blockPosition(), HIVE_SEARCH_RANGE, PrimordialCradleBlockEntity.class);
	}

	public static @Nullable PrimordialCradleBlockEntity findHive(ServerLevel level, BlockPos pos) {
		if (SpatialDBManager.getInstance(level).getClosestShape(level, pos, MoundShape.class::isInstance) instanceof MoundShape moundShape
				&& level.getBlockEntity(moundShape.getOrigin()) instanceof PrimordialCradleBlockEntity cradle) {
			return cradle;
		}
		return null;
	}

	public static void reportHazard(ServerLevel level, BlockPos hazardPos, @Nullable LivingEntity culprit) {
		PrimordialCradleBlockEntity hive = findHive(level, hazardPos);
		if (hive != null) hive.reportHazard(hazardPos, culprit);
	}

	public static boolean isHiveHungry(Mob mob) {
		PrimordialCradleBlockEntity hive = findHive(mob);
		return hive != null && !hive.isFull();
	}

	public static @Nullable LivingEntity findHiveThreat(Mob mob) {
		if (!(mob.level() instanceof ServerLevel serverLevel)) return null;

		PrimordialCradleBlockEntity hive = findHive(mob);
		if (hive == null) return null;

		UUID threatId = hive.getHivemind().getThreatId();
		if (threatId == null) return null;

		return serverLevel.getEntity(threatId) instanceof LivingEntity threat && threat.isAlive() ? threat : null;
	}

	private static boolean isFleshOfTheHive(Entity entity) {
		return entity instanceof FleshBlob || entity instanceof Fleshkin;
	}

}
