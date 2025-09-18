package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.class)
public interface DisplayAccessor {

	@Invoker("setBillboardConstraints")
	void biomancy$setBillboardConstraints(Display.BillboardConstraints constraints);


}
