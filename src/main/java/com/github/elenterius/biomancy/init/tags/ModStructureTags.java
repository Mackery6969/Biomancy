package com.github.elenterius.biomancy.init.tags;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class ModStructureTags {

	public static final TagKey<Structure> SMALL_WORM = tag("small_worm");
	public static final TagKey<Structure> GIANT_WORM = tag("giant_worm");
	public static final TagKey<Structure> VAULT = tag("vault");
	public static final TagKey<Structure> LAB = tag("vault");

	private ModStructureTags() {}

	private static TagKey<Structure> tag(String name) {
		return TagKey.create(Registries.STRUCTURE, BiomancyMod.rl(name));
	}

}
