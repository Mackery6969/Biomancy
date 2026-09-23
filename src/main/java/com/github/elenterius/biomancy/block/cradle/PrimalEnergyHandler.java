package com.github.elenterius.biomancy.block.cradle;

import net.minecraft.core.BlockPos;

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
	 * @return true if the handler has starved and stopped sustaining the flesh it grew
	 */
	default boolean isStarving() {
		return false;
	}

}
