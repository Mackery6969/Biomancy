package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.base.BlockEntityDelegator;
import com.github.elenterius.biomancy.block.bioforge.BioForgeBlockEntity;
import com.github.elenterius.biomancy.block.biolab.BioLabBlockEntity;
import com.github.elenterius.biomancy.block.chrysalis.ChrysalisBlockEntity;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.block.decomposer.DecomposerBlockEntity;
import com.github.elenterius.biomancy.block.digester.DigesterBlockEntity;
import com.github.elenterius.biomancy.block.fleshkinchest.FleshkinChestBlockEntity;
import com.github.elenterius.biomancy.block.mawhopper.MawHopperBlockEntity;
import com.github.elenterius.biomancy.block.membrane.BiometricMembraneBlockEntity;
import com.github.elenterius.biomancy.block.modularlarynx.ModularLarynxBlockEntity;
import com.github.elenterius.biomancy.block.ownable.OwnableBlockEntity;
import com.github.elenterius.biomancy.block.storagesac.StorageSacBlockEntity;
import com.github.elenterius.biomancy.block.tongue.TongueBlockEntity;
import com.github.elenterius.biomancy.block.vialholder.VialHolderBlockEntity;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Arrays;

public final class ModBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BiomancyMod.MOD_ID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PrimordialCradleBlockEntity>> PRIMORDIAL_CRADLE = register(ModBlocks.PRIMORDIAL_CRADLE, PrimordialCradleBlockEntity::new);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DecomposerBlockEntity>> DECOMPOSER = register(ModBlocks.DECOMPOSER, DecomposerBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BioForgeBlockEntity>> BIO_FORGE = register(ModBlocks.BIO_FORGE, BioForgeBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BioLabBlockEntity>> BIO_LAB = register(ModBlocks.BIO_LAB, BioLabBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DigesterBlockEntity>> DIGESTER = register(ModBlocks.DIGESTER, DigesterBlockEntity::new);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TongueBlockEntity>> TONGUE = register(ModBlocks.TONGUE, TongueBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MawHopperBlockEntity>> MAW_HOPPER = register(ModBlocks.MAW_HOPPER, MawHopperBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StorageSacBlockEntity>> STORAGE_SAC = register(ModBlocks.STORAGE_SAC, StorageSacBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FleshkinChestBlockEntity>> FLESHKIN_CHEST = register(ModBlocks.FLESHKIN_CHEST, FleshkinChestBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VialHolderBlockEntity>> VIAL_HOLDER = register(ModBlocks.VIAL_HOLDER, VialHolderBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModularLarynxBlockEntity>> MODULAR_LARYNX = register(ModBlocks.MODULAR_LARYNX, ModularLarynxBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChrysalisBlockEntity>> CHRYSALIS = register(ModBlocks.CHRYSALIS, ChrysalisBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BiometricMembraneBlockEntity>> BIOMETRIC_MEMBRANE = register(ModBlocks.BIOMETRIC_MEMBRANE, BiometricMembraneBlockEntity::new);

	//# Special
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OwnableBlockEntity>> OWNABLE_BE = BLOCK_ENTITIES.register("ownable_block_entity", () -> BlockEntityType.Builder.of(OwnableBlockEntity::new, /*ModBlocks.FLESHKIN_DOOR.get(), ModBlocks.FLESHKIN_TRAPDOOR.get(),*/ ModBlocks.FLESHKIN_PRESSURE_PLATE.get()).build(noDataFixer()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityDelegator>> BE_DELEGATOR = BLOCK_ENTITIES.register("block_entity_delegator", () -> BlockEntityType.Builder.of(BlockEntityDelegator::new, Blocks.AIR /*ModBlocks.FLESHKIN_DOOR.get()*/).build(noDataFixer()));

	private ModBlockEntities() {}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(ModCapabilities.FLUID_HANDLER, PRIMORDIAL_CRADLE.get(), (be, side) -> be.getFluidConsumer());

		event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, DECOMPOSER.get(), (be, side) -> {
			if (side == null || side == Direction.DOWN) return be.getOutputInventory();
			if (side == Direction.UP) return be.getInputInventory();
			return be.getFuelInventory();
		});
		event.registerBlockEntity(ModCapabilities.FLUID_HANDLER, DECOMPOSER.get(), (be, side) -> be.getFluidConsumer());

		event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, BIO_FORGE.get(), (be, side) -> {
			if (side != null && side.getAxis().isHorizontal()) return be.getFuelInventory();
			return null;
		});
		event.registerBlockEntity(ModCapabilities.FLUID_HANDLER, BIO_FORGE.get(), (be, side) -> be.getFluidConsumer());

		event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, BIO_LAB.get(), (be, side) -> {
			if (side == null || side == Direction.DOWN) return be.getOutputInventory();
			if (side == Direction.UP) return be.getInputInventory();
			return be.getCombinedInventory();
		});
		event.registerBlockEntity(ModCapabilities.FLUID_HANDLER, BIO_LAB.get(), (be, side) -> be.getFluidConsumer());

		event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, DIGESTER.get(), (be, side) -> {
			if (side == null || side == Direction.DOWN) return be.getOutputInventory();
			if (side == Direction.UP) return be.getInputInventory();
			return be.getFuelInventory();
		});
		event.registerBlockEntity(ModCapabilities.FLUID_HANDLER, DIGESTER.get(), (be, side) -> be.getFluidConsumer());

		event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, MAW_HOPPER.get(), (be, side) -> be.getInventoryHandler());
		event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, STORAGE_SAC.get(), (be, side) -> be.getInventory());
		event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, MODULAR_LARYNX.get(), (be, side) -> be.getInventoryHandler());

		event.registerBlockEntity(ModCapabilities.ITEM_HANDLER, BE_DELEGATOR.get(), (be, side) -> {
			BlockEntity delegate = be.getDelegate();
			Level level = delegate != null ? delegate.getLevel() : null;
			return level != null ? level.getCapability(ModCapabilities.ITEM_HANDLER, delegate.getBlockPos(), side) : null;
		});
		event.registerBlockEntity(ModCapabilities.FLUID_HANDLER, BE_DELEGATOR.get(), (be, side) -> {
			BlockEntity delegate = be.getDelegate();
			Level level = delegate != null ? delegate.getLevel() : null;
			return level != null ? level.getCapability(ModCapabilities.FLUID_HANDLER, delegate.getBlockPos(), side) : null;
		});
	}

	private static <T extends BlockEntity, B extends Block> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(DeferredHolder<Block, B> blockHolder, BlockEntityType.BlockEntitySupplier<T> factory) {
		return BLOCK_ENTITIES.register(blockHolder.getId().getPath(), () -> BlockEntityType.Builder.of(factory, blockHolder.get()).build(noDataFixer()));
	}

	@SafeVarargs
	private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, DeferredHolder<Block, ? extends Block>... blockHolders) {
		return BLOCK_ENTITIES.register(name, () -> {
			Block[] blocks = Arrays.stream(blockHolders).map(DeferredHolder::get).toList().toArray(new Block[]{});
			return BlockEntityType.Builder.of(factory, blocks).build(noDataFixer());
		});
	}

	private static Type<?> noDataFixer() {
		//noinspection ConstantConditions
		return null;
	}

}
