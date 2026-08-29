package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor {

	@Accessor("amplifier")
	void biomancy$setAmplifier(int amplifier);

	@Accessor("duration")
	void biomancy$setDuration(int ticks);

}
