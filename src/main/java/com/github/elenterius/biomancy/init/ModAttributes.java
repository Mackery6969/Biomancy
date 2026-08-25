package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ModAttributes {
	
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, BiomancyMod.MOD_ID);

	private ModAttributes() {}

}
