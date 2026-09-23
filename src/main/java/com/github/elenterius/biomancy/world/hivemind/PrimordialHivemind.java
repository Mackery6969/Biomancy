package com.github.elenterius.biomancy.world.hivemind;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PrimordialHivemind {

	public enum HazardResponse {
		NONE, AVOID, REINFORCE
	}

	public static final int TICK_INTERVAL = 20;

	private static final String DORMANT_KEY = "Dormant";
	private static final String GROWTH_TARGET_KEY = "GrowthTarget";
	private static final String TARGET_AGE_KEY = "TargetAge";
	private static final String THREAT_KEY = "Threat";
	private static final String THREAT_AGE_KEY = "ThreatAge";
	private static final String AWAKENED_KEY = "Awakened";
	private static final String HAZARD_KEY = "Hazard";
	private static final String HAZARD_AGE_KEY = "HazardAge";
	private static final String REINFORCING_KEY = "Reinforcing";

	private static final int IDLE_UPKEEP = 2;
	private static final int HUNTING_UPKEEP = 6;
	private static final int WAKE_THRESHOLD = 256;
	private static final int TARGET_SEARCH_RANGE = 64;
	private static final int TARGET_REFRESH_CYCLES = 30;
	private static final int THREAT_MEMORY_CYCLES = 30;
	private static final int HAZARD_MEMORY_CYCLES = 60;
	private static final int REINFORCE_THRESHOLD = 1024;

	private boolean dormant;
	private boolean awakened;
	private @Nullable BlockPos growthTarget;
	private int targetAge;
	private @Nullable UUID threatId;
	private int threatAge;
	private @Nullable BlockPos hazardPos;
	private int hazardAge;
	private boolean reinforcing;

	public boolean isDormant() {
		return dormant;
	}

	/**
	 * a hive that has never fed is inert rather than starving, so worldgen structures keep the flesh they spawned with
	 */
	public boolean isStarving() {
		return awakened && dormant;
	}

	public @Nullable BlockPos getGrowthTarget() {
		return growthTarget;
	}

	/**
	 * @return true when the requester lies in the half-space the hivemind is currently growing toward
	 */
	public boolean isWithinGrowthCone(BlockPos origin, BlockPos requesterPos) {
		if (getHazardResponse() == HazardResponse.AVOID && hazardPos != null && !isSameSide(origin, hazardPos, requesterPos)) {
			return false;
		}

		if (growthTarget == null) return true;

		double toTargetX = growthTarget.getX() - origin.getX();
		double toTargetZ = growthTarget.getZ() - origin.getZ();
		double toRequesterX = requesterPos.getX() - origin.getX();
		double toRequesterZ = requesterPos.getZ() - origin.getZ();

		return toTargetX * toRequesterX + toTargetZ * toRequesterZ >= 0;
	}

	private static boolean isSameSide(BlockPos origin, BlockPos a, BlockPos b) {
		double toAX = a.getX() - origin.getX();
		double toAZ = a.getZ() - origin.getZ();
		double toBX = b.getX() - origin.getX();
		double toBZ = b.getZ() - origin.getZ();

		return toAX * toBX + toAZ * toBZ <= 0;
	}

	/**
	 * a well fed hive grows into the hazard to smother it, a weak one recoils and grows elsewhere
	 */
	public void onHarmed(BlockPos pos, int availablePrimalEnergy) {
		hazardPos = pos;
		hazardAge = HAZARD_MEMORY_CYCLES;
		reinforcing = !dormant && availablePrimalEnergy >= REINFORCE_THRESHOLD;

		if (reinforcing) {
			growthTarget = pos;
			targetAge = TARGET_REFRESH_CYCLES;
		}
	}

	public HazardResponse getHazardResponse() {
		if (hazardAge <= 0 || hazardPos == null) return HazardResponse.NONE;
		return reinforcing ? HazardResponse.REINFORCE : HazardResponse.AVOID;
	}

	public @Nullable BlockPos getHazardPos() {
		return hazardAge > 0 ? hazardPos : null;
	}

	public void setThreat(LivingEntity entity) {
		threatId = entity.getUUID();
		threatAge = THREAT_MEMORY_CYCLES;
	}

	public @Nullable UUID getThreatId() {
		return threatAge > 0 ? threatId : null;
	}

	public void onFed(int primalEnergy) {
		if (primalEnergy <= 0) return;

		awakened = true;

		if (dormant && primalEnergy >= WAKE_THRESHOLD) {
			dormant = false;
			targetAge = TARGET_REFRESH_CYCLES;
		}
	}

	/**
	 * @return the amount of primal energy the hivemind wants to burn this cycle
	 */
	public int serverTick(ServerLevel level, BlockPos origin, int availablePrimalEnergy) {
		if (targetAge > 0) targetAge--;
		if (threatAge > 0) threatAge--;
		if (hazardAge > 0) hazardAge--;

		if (dormant) {
			growthTarget = null;
			return 0;
		}

		if (targetAge <= 0) {
			growthTarget = findPrey(level, origin);
			targetAge = TARGET_REFRESH_CYCLES;
		}

		int upkeep = growthTarget != null ? HUNTING_UPKEEP : IDLE_UPKEEP;

		if (availablePrimalEnergy < upkeep) {
			dormant = true;
			growthTarget = null;
			return availablePrimalEnergy;
		}

		return upkeep;
	}

	private @Nullable BlockPos findPrey(ServerLevel level, BlockPos origin) {
		Vec3 center = Vec3.atCenterOf(origin);
		AABB aabb = AABB.ofSize(center, TARGET_SEARCH_RANGE * 2, TARGET_SEARCH_RANGE * 2d, TARGET_SEARCH_RANGE * 2);

		List<Player> players = level.getEntitiesOfClass(Player.class, aabb, EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(Entity::isAlive));
		LivingEntity nearest = null;
		double nearestDistSqr = Double.MAX_VALUE;

		for (Player player : players) {
			double distSqr = player.distanceToSqr(center);
			if (distSqr < nearestDistSqr) {
				nearest = player;
				nearestDistSqr = distSqr;
			}
		}

		return nearest != null ? nearest.blockPosition() : null;
	}

	public void writeTo(CompoundTag tag) {
		tag.putBoolean(DORMANT_KEY, dormant);
		tag.putBoolean(AWAKENED_KEY, awakened);
		tag.putInt(TARGET_AGE_KEY, targetAge);
		tag.putInt(THREAT_AGE_KEY, threatAge);
		tag.putInt(HAZARD_AGE_KEY, hazardAge);
		tag.putBoolean(REINFORCING_KEY, reinforcing);
		if (hazardPos != null) {
			tag.putLong(HAZARD_KEY, hazardPos.asLong());
		}
		if (threatId != null) {
			tag.putUUID(THREAT_KEY, threatId);
		}
		if (growthTarget != null) {
			tag.putLong(GROWTH_TARGET_KEY, growthTarget.asLong());
		}
	}

	public void readFrom(CompoundTag tag) {
		dormant = tag.getBoolean(DORMANT_KEY);
		awakened = tag.getBoolean(AWAKENED_KEY);
		targetAge = tag.getInt(TARGET_AGE_KEY);
		threatAge = tag.getInt(THREAT_AGE_KEY);
		hazardAge = tag.getInt(HAZARD_AGE_KEY);
		reinforcing = tag.getBoolean(REINFORCING_KEY);
		hazardPos = tag.contains(HAZARD_KEY) ? BlockPos.of(tag.getLong(HAZARD_KEY)) : null;
		threatId = tag.hasUUID(THREAT_KEY) ? tag.getUUID(THREAT_KEY) : null;
		growthTarget = tag.contains(GROWTH_TARGET_KEY) ? BlockPos.of(tag.getLong(GROWTH_TARGET_KEY)) : null;
	}

}
