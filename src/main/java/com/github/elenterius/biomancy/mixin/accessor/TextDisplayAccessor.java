package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.TextDisplay.class)
public interface TextDisplayAccessor {

	@Invoker("setText")
	void biomancy$setText(Component text);

	@Invoker("setFlags")
	void biomancy$setStyleFlags(byte flags);

	@Invoker("getFlags")
	byte biomancy$getStyleFlags();

}
