package com.github.elenterius.biomancy.mixin.accessor;

import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PotionBrewing.class)
public interface PotionBrewingAccessor {

	@SuppressWarnings("rawtypes")
	@Accessor("potionMixes")
	List biomancy$potionMixes();

}
