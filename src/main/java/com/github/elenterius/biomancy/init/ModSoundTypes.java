package com.github.elenterius.biomancy.init;

import net.minecraft.world.level.block.SoundType;

public final class ModSoundTypes {

	public static final SoundType FLESH_BLOCK = new SoundType(1f, 1f,
			ModSoundEvents.FLESH_BLOCK_BREAK.get(), ModSoundEvents.FLESH_BLOCK_STEP.get(),
			ModSoundEvents.FLESH_BLOCK_PLACE.get(), ModSoundEvents.FLESH_BLOCK_HIT.get(), ModSoundEvents.FLESH_BLOCK_FALL.get());

	public static final SoundType BONY_FLESH_BLOCK = new SoundType(1f, 1f,
			ModSoundEvents.BONY_FLESH_BLOCK_BREAK.get(), ModSoundEvents.BONY_FLESH_BLOCK_STEP.get(),
			ModSoundEvents.BONY_FLESH_BLOCK_PLACE.get(), ModSoundEvents.BONY_FLESH_BLOCK_HIT.get(), ModSoundEvents.BONY_FLESH_BLOCK_FALL.get());

	public static final SoundType GEL_BLOCK = new SoundType(1f, 1f,
			ModSoundEvents.GEL_BLOCK_BREAK.get(), ModSoundEvents.GEL_BLOCK_STEP.get(),
			ModSoundEvents.GEL_BLOCK_PLACE.get(), ModSoundEvents.GEL_BLOCK_HIT.get(), ModSoundEvents.GEL_BLOCK_FALL.get());

	private ModSoundTypes() {}

}
