package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.data.models.model.TextureSlot;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;



@Mixin(TextureSlot.class)
public interface TextureSlotAccessor {

	@Invoker("create")
	static @NonNull TextureSlot biomancy$create(String id) {
		//noinspection DataFlowIssue
		return null;
	}

}
