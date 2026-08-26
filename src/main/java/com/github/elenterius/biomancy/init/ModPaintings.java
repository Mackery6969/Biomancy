package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModPaintings {

	public static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(Registries.PAINTING_VARIANT, BiomancyMod.MOD_ID);

	public static final DeferredHolder<PaintingVariant, PaintingVariant> JERRY_PROVIDES = PAINTINGS.register("jerry_provides", () -> new PaintingVariant(16, 16, BiomancyMod.rl("jerry_provides")));

	private ModPaintings() {}

}
