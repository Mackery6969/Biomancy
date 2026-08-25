package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.fluid.AcidFluid;
import com.github.elenterius.biomancy.fluid.TintedFluidType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class ModFluids {

	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, BiomancyMod.MOD_ID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, BiomancyMod.MOD_ID);

	public static final DeferredHolder<FluidType, FluidType> ACID_TYPE = registerType("acid", properties -> properties.density(1024).viscosity(1024));
	public static final Supplier<BaseFlowingFluid.Properties> ACID_FLUID_PROPERTIES = () -> new BaseFlowingFluid
			.Properties(ACID_TYPE, ModFluids.ACID, ModFluids.FLOWING_ACID)
			.slopeFindDistance(2)
			.levelDecreasePerBlock(2)
			.block(ModBlocks.ACID_FLUID_BLOCK)
			.bucket(ModItems.ACID_BUCKET);
	public static final DeferredHolder<Fluid, BaseFlowingFluid> ACID = register("acid", () -> new AcidFluid.Source(ACID_FLUID_PROPERTIES.get()));
	public static final DeferredHolder<Fluid, BaseFlowingFluid> FLOWING_ACID = register("flowing_acid", () -> new AcidFluid.Flowing(ACID_FLUID_PROPERTIES.get()));

	private ModFluids() {}

	static void registerInteractions() {
		FluidInteractionRegistry.addInteraction(ACID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
				NeoForgeMod.WATER_TYPE.value(),
				fluidState -> fluidState.isSource() ? Blocks.CALCITE.defaultBlockState() : Blocks.DIORITE.defaultBlockState()
		));
		FluidInteractionRegistry.addInteraction(ACID_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
				NeoForgeMod.LAVA_TYPE.value(),
				fluidState -> fluidState.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.DIORITE.defaultBlockState()
		));
	}

	private static <T extends Fluid> DeferredHolder<Fluid, T> register(String name, Supplier<T> factory) {
		return FLUIDS.register(name, factory);
	}

	private static DeferredHolder<FluidType, TintedFluidType> registerTintedType(String name, int colorARGB, UnaryOperator<FluidType.Properties> operator) {
		return FLUID_TYPES.register(name, () -> new TintedFluidType(operator.apply(createFluidTypeProperties()), colorARGB));
	}

	private static DeferredHolder<FluidType, FluidType> registerType(String name, UnaryOperator<FluidType.Properties> operator) {
		return FLUID_TYPES.register(name, () -> new FluidType(operator.apply(createFluidTypeProperties())) {

			private final ResourceLocation stillTexture = BiomancyMod.rl("block/%s_still".formatted(name));
			private final ResourceLocation flowingTexture = BiomancyMod.rl("block/%s_flowing".formatted(name));
			private final ResourceLocation blockOverlayTexture = BiomancyMod.rl("block/%s_overlay".formatted(name));
			private final ResourceLocation screenOverlayTexture = BiomancyMod.rl("textures/block/%s_overlay.png".formatted(name));

			@Override
			public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
				consumer.accept(new IClientFluidTypeExtensions() {
					@Override
					public ResourceLocation getStillTexture() {
						return stillTexture;
					}

					@Override
					public ResourceLocation getFlowingTexture() {
						return flowingTexture;
					}

					@Override
					public ResourceLocation getOverlayTexture() {
						return blockOverlayTexture;
					}

					@Override
					public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
						return screenOverlayTexture;
					}
				});
			}
		});
	}

	private static FluidType.Properties createFluidTypeProperties() {
		return FluidType.Properties.create()
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
	}

}
