package com.github.elenterius.biomancy.gametest;

import net.minecraft.core.BlockPos;

final class GameTestTemplates {

	static final String EMPTY_PLATFORM = "empty_platform";
	static final int PLATFORM_SIZE = 9;
	static final int GROUND_Y = 1;
	static final int SURFACE_Y = GROUND_Y + 1;
	static final BlockPos PLATFORM_CENTER = new BlockPos(PLATFORM_SIZE / 2, SURFACE_Y, PLATFORM_SIZE / 2);

	private GameTestTemplates() {}

}
