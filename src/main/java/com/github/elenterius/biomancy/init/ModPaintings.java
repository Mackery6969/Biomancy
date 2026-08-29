package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

public final class ModPaintings {

	public static final ResourceKey<PaintingVariant> JERRY_PROVIDES = key("jerry_provides");

	private ModPaintings() {}

	private static ResourceKey<PaintingVariant> key(String name) {
		return ResourceKey.create(Registries.PAINTING_VARIANT, BiomancyMod.rl(name));
	}

	public static void bootstrap(BootstrapContext<PaintingVariant> ctx) {
		ctx.register(JERRY_PROVIDES, new PaintingVariant(16, 16, BiomancyMod.rl("jerry_provides")));
	}

}
