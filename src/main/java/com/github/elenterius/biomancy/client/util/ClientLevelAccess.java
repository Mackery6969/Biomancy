package com.github.elenterius.biomancy.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public final class ClientLevelAccess {

	private ClientLevelAccess() {}

	public static @Nullable Level getLevel() {
		return Minecraft.getInstance().level;
	}

}
