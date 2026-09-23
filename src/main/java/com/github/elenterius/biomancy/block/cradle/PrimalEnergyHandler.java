package com.github.elenterius.biomancy.block.cradle;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

public interface PrimalEnergyHandler {

	int getPrimalEnergy();

	/**
	 * @param amount
	 * @return the amount that was successfully filled
	 */
	int fillPrimalEnergy(int amount);

	/**
	 * @param amount
	 * @return the amount that was successfully drained
	 */
	int drainPrimalEnergy(int amount);

	/**
	 * @param amount
	 * @param requesterPos position of the block requesting the energy
	 * @return the amount that was successfully drained
	 */
	default int drainPrimalEnergy(int amount, BlockPos requesterPos) {
		return drainPrimalEnergy(amount);
	}

	/**
	 * reports flesh being damaged by fire, lava or explosions at the given position
	 */
	default void reportHazard(BlockPos hazardPos, @Nullable LivingEntity culprit) {}

	/**
	 * @return true if the hive is thickening its flesh around a hazard at the given position
	 */
	default boolean isReinforcing(BlockPos pos) {
		return false;
	}

	/**
	 * @return true if the handler has starved and stopped sustaining the flesh it grew
	 */
	default boolean isStarving() {
		return false;
	}

}
