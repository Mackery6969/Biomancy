package com.github.elenterius.biomancy.api.nutrients.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Experimental
public final class FluidNutrients {

	private static final Map<ResourceLocation, FluidToFuelConversion> FLUIDS = new HashMap<>();

	private FluidNutrients() {}

	@Nullable
	private static ResourceLocation getRegistryKey(Fluid fluid) {
		if (fluid instanceof FlowingFluid flowingFluid) {
			fluid = flowingFluid.getSource();
		}
		return BuiltInRegistries.FLUID.getKey(fluid);
	}

	public static void register(Fluid fluid, FluidToFuelConversion func) {
		ResourceLocation key = getRegistryKey(fluid);
		if (key == null) throw new IllegalArgumentException("Couldn't find a valid registry key for fluid " + fluid);
		FLUIDS.put(key, func);
	}

	public static void register(DeferredHolder<Fluid, ? extends Fluid> fluidHolder, FluidToFuelConversion func) {
		ResourceLocation key = fluidHolder.getId();
		FLUIDS.put(key, func);
	}

	public static void register(ResourceLocation fluidRegistryKey, FluidToFuelConversion func) {
		FLUIDS.put(fluidRegistryKey, func);
	}

	public static boolean isValid(FluidStack fluidStack) {
		ResourceLocation key = getRegistryKey(fluidStack.getFluid());
		return key != null && FLUIDS.containsKey(key);
	}

	public static @Nullable FluidToFuelConversion getConversion(FluidStack fluidStack) {
		ResourceLocation key = getRegistryKey(fluidStack.getFluid());
		if (key == null) return null;
		return FLUIDS.get(key);
	}

}
