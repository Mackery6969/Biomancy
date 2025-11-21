package com.github.elenterius.biomancy.entity.mob;

import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

public interface JukeboxDancer {

	boolean isDancing();

	void setDancing(boolean flag);

	@Nullable BlockPos getJukeboxPos();

}
