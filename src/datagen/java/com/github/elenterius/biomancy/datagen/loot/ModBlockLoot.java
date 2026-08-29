package com.github.elenterius.biomancy.datagen.loot;

import com.github.elenterius.biomancy.block.base.DirectionalSlabBlock;
import com.github.elenterius.biomancy.block.base.SimpleMultiFaceBlock;
import com.github.elenterius.biomancy.block.chrysalis.Chrysalis;
import com.github.elenterius.biomancy.block.fleshspike.FleshSpikeBlock;
import com.github.elenterius.biomancy.block.membrane.BiometricMembraneBlockEntity;
import com.github.elenterius.biomancy.block.property.DirectionalSlabType;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.loot.CopyBlockEntityDataFunction;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.logging.log4j.Marker;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.github.elenterius.biomancy.BiomancyMod.LOGGER;

public class ModBlockLoot extends BlockLootSubProvider {

	protected static final Marker LOG_MARKER = ModLootTableProvider.LOG_MARKER;

	private static final Set<Item> EXPLOSION_RESISTANT = Set.of();

	public ModBlockLoot(HolderLookup.Provider registries) {
		super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags(), registries);
	}

	protected LootItemCondition.Builder hasShearsOrSilkTouch() {
		return HAS_SHEARS.or(hasSilkTouch());
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		List<Block> blocks = ModBlocks.BLOCKS.getEntries().stream().map(holder -> (Block) holder.get()).toList();
		LOGGER.info(LOG_MARKER, "generating loot tables for {} blocks...", blocks.size());
		return blocks;
	}

	protected LootTable.Builder createShearsOrSilkTouchOnlyDrop(ItemLike item) {
		return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(hasShearsOrSilkTouch()).add(LootItem.lootTableItem(item)));
	}

	protected LootTable.Builder createNameableBioMachineTable(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyBlockEntityDataFunction.copyData("Fuel"))
				)));
	}

	protected LootTable.Builder createPrimordialCradleTable(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyBlockEntityDataFunction.copyData("PrimalEnergy", "ProcGenValues", "SacrificeHandler"))
				)));
	}

	protected LootTable.Builder dropWithInventory(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyBlockEntityDataFunction.copyData("Inventory"))
				)));
	}

	protected LootTable.Builder dropChrysalisWithEntity(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyBlockEntityDataFunction.copyData(Chrysalis.ENTITY_KEY))
				)));
	}

	protected LootTable.Builder dropMembraneSettings(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyBlockEntityDataFunction.copyData(BiometricMembraneBlockEntity.MEMBRANE_KEY))
				)));
	}

	protected LootTable.Builder dropOwnableInventory(Block block) {
		return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(block)
						.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
						.apply(CopyBlockEntityDataFunction.copyData("Inventory", "OwnerUUID", "UserList"))
				)));
	}

	protected LootTable.Builder dropWithOwnableData(Block container) {
		return LootTable.lootTable().withPool(applyExplosionCondition(container, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(container)
						.apply(CopyBlockEntityDataFunction.copyData("OwnerUUID", "UserList"))
				)));
	}

	protected LootTable.Builder createFleshDoorTable(DoorBlock block) {
		return createSinglePropConditionTable(block, DoorBlock.HALF, DoubleBlockHalf.LOWER);
	}

	protected LootTable.Builder createDirectionalSlabTable(Block slab) {
		return LootTable.lootTable().withPool(
				LootPool.lootPool().setRolls(ConstantValue.exactly(1))
						.add(applyExplosionDecay(slab, LootItem.lootTableItem(slab)
								.apply(
										SetItemCountFunction.setCount(ConstantValue.exactly(2)).when(
												LootItemBlockStatePropertyCondition
														.hasBlockStateProperties(slab)
														.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DirectionalSlabBlock.TYPE, DirectionalSlabType.FULL))
										))
						)));
	}

	protected LootTable.Builder createFleshSpikeTable(FleshSpikeBlock block) {
		return LootTable.lootTable().withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.add(applyExplosionDecay(block, LootItem.lootTableItem(block).apply(
						IntStream.range(FleshSpikeBlock.SPIKES.getMin() + 1, FleshSpikeBlock.SPIKES.getMax() + 1).boxed().toList(),
						spikes -> SetItemCountFunction.setCount(ConstantValue.exactly(spikes))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FleshSpikeBlock.SPIKES.get(), spikes)))
				))));
	}

	protected LootTable.Builder drop(Item item) {
		return LootTable.lootTable().withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.add(applyExplosionDecay(item, LootItem.lootTableItem(item))));
	}

	protected LootTable.Builder createMultifaceBlockDrops(SimpleMultiFaceBlock block) {
		return LootTable.lootTable().withPool(LootPool.lootPool()
				.add(applyExplosionDecay(block, LootItem.lootTableItem(block)
						.apply(
								Direction.values(),
								face -> SetItemCountFunction.setCount(ConstantValue.exactly(1), true)
										.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
												.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SimpleMultiFaceBlock.getFaceProperty(face), true)))
						)
						.apply(SetItemCountFunction.setCount(ConstantValue.exactly(-1), true)))));
	}

	protected LootTable.Builder createMultifaceBlockDrops(MultifaceBlock block) {
		return LootTable.lootTable().withPool(LootPool.lootPool()
				.add(applyExplosionDecay(block, LootItem.lootTableItem(block)
						.apply(
								Direction.values(),
								face -> SetItemCountFunction.setCount(ConstantValue.exactly(1), true)
										.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
												.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MultifaceBlock.getFaceProperty(face), true)))
						)
						.apply(SetItemCountFunction.setCount(ConstantValue.exactly(-1), true)))));
	}

	@Override
	protected void generate() {
		LOGGER.info(LOG_MARKER, "registering block loot...");

		add(ModBlocks.PRIMORDIAL_CRADLE.get(), this::createPrimordialCradleTable);
		dropSelf(ModBlocks.TONGUE.get());
		dropSelf(ModBlocks.MAW_HOPPER.get());
		add(ModBlocks.STORAGE_SAC.get(), this::dropWithInventory);
		add(ModBlocks.CHRYSALIS.get(), this::dropChrysalisWithEntity);

		add(ModBlocks.BIO_FORGE.get(), this::createNameableBioMachineTable);
		add(ModBlocks.BIO_LAB.get(), this::createNameableBioMachineTable);
		add(ModBlocks.DIGESTER.get(), this::createNameableBioMachineTable);
		add(ModBlocks.DECOMPOSER.get(), this::createNameableBioMachineTable);

		add(ModBlocks.FLESHKIN_CHEST.get(), this::createNameableBlockEntityTable);
		add(ModBlocks.FLESHKIN_PRESSURE_PLATE.get(), this::dropWithOwnableData);

		dropSelf(ModBlocks.FLESH.get());
		add(ModBlocks.FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.FLESH_STAIRS.get());
		dropSelf(ModBlocks.FLESH_WALL.get());

		dropSelf(ModBlocks.PACKED_FLESH.get());
		add(ModBlocks.PACKED_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.PACKED_FLESH_STAIRS.get());
		dropSelf(ModBlocks.PACKED_FLESH_WALL.get());

		dropSelf(ModBlocks.FIBROUS_FLESH.get());
		add(ModBlocks.FIBROUS_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.FIBROUS_FLESH_STAIRS.get());
		dropSelf(ModBlocks.FIBROUS_FLESH_WALL.get());

		dropSelf(ModBlocks.FLESH_PILLAR.get());
		dropSelf(ModBlocks.CHISELED_FLESH.get());
		dropSelf(ModBlocks.ORNATE_FLESH.get());
		add(ModBlocks.ORNATE_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.TUBULAR_FLESH_BLOCK.get());

		dropSelf(ModBlocks.PRIMAL_FLESH.get());
		add(ModBlocks.PRIMAL_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.PRIMAL_FLESH_STAIRS.get());
		dropSelf(ModBlocks.PRIMAL_FLESH_WALL.get());

		dropSelf(ModBlocks.SMOOTH_PRIMAL_FLESH.get());
		add(ModBlocks.SMOOTH_PRIMAL_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.SMOOTH_PRIMAL_FLESH_STAIRS.get());
		dropSelf(ModBlocks.SMOOTH_PRIMAL_FLESH_WALL.get());

		dropSelf(ModBlocks.FIBROUS_PRIMAL_FLESH.get());
		add(ModBlocks.FIBROUS_PRIMAL_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.FIBROUS_PRIMAL_FLESH_STAIRS.get());
		dropSelf(ModBlocks.FIBROUS_PRIMAL_FLESH_WALL.get());

		dropSelf(ModBlocks.POROUS_PRIMAL_FLESH.get());
		add(ModBlocks.POROUS_PRIMAL_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.POROUS_PRIMAL_FLESH_STAIRS.get());
		dropSelf(ModBlocks.POROUS_PRIMAL_FLESH_WALL.get());

		dropSelf(ModBlocks.MALIGNANT_FLESH.get());
		add(ModBlocks.MALIGNANT_FLESH_SLAB.get(), this::createDirectionalSlabTable);
		dropSelf(ModBlocks.MALIGNANT_FLESH_STAIRS.get());
		dropSelf(ModBlocks.MALIGNANT_FLESH_WALL.get());
		add(ModBlocks.MALIGNANT_FLESH_VEINS.get(), block -> createMultifaceBlockDrops(block, hasShearsOrSilkTouch()));
		add(ModBlocks.PRIMAL_BLOOM.get(), this::createShearsOrSilkTouchOnlyDrop);
		dropSelf(ModBlocks.PRIMAL_ORIFICE.get());
		dropSelf(ModBlocks.PRIMAL_BONE.get());

		dropSelf(ModBlocks.IMPERMEABLE_MEMBRANE.get());
		dropSelf(ModBlocks.IMPERMEABLE_MEMBRANE_PANE.get());
		dropSelf(ModBlocks.BABY_PERMEABLE_MEMBRANE.get());
		dropSelf(ModBlocks.BABY_PERMEABLE_MEMBRANE_PANE.get());
		dropSelf(ModBlocks.ADULT_PERMEABLE_MEMBRANE.get());
		dropSelf(ModBlocks.ADULT_PERMEABLE_MEMBRANE_PANE.get());
		dropSelf(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE.get());
		dropSelf(ModBlocks.PRIMAL_PERMEABLE_MEMBRANE_PANE.get());
		dropSelf(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE.get());
		dropSelf(ModBlocks.UNDEAD_PERMEABLE_MEMBRANE_PANE.get());
		add(ModBlocks.BIOMETRIC_MEMBRANE.get(), this::dropMembraneSettings);
		dropSelf(ModBlocks.ONEWAY_MEMBRANE.get());

		dropSelf(ModBlocks.MODULAR_LARYNX.get());
		//dropSelf(ModBlocks.NEURAL_INTERCEPTOR.get());

		dropSelf(ModBlocks.FLESH_IRIS_DOOR.get());
		dropSelf(ModBlocks.FLESH_FENCE.get());
		dropSelf(ModBlocks.FLESH_FENCE_GATE.get());
		dropSelf(ModBlocks.FLESH_LADDER.get());
		dropSelf(ModBlocks.YELLOW_BIO_LANTERN.get());
		dropSelf(ModBlocks.PRIMORDIAL_BIO_LANTERN.get());
		dropSelf(ModBlocks.BLOOMLIGHT.get());
		dropSelf(ModBlocks.BLUE_BIO_LANTERN.get());
		dropSelf(ModBlocks.TENDON_CHAIN.get());
		dropSelf(ModBlocks.VIAL_HOLDER.get());
		addCustom(ModBlocks.JUMP_PAD.get(), this::createMultifaceBlockDrops);

		addCustom(ModBlocks.FLESH_DOOR.get(), this::createFleshDoorTable);
		addCustom(ModBlocks.FULL_FLESH_DOOR.get(), this::createFleshDoorTable);

		addCustom(ModBlocks.FLESH_SPIKE.get(), this::createFleshSpikeTable);

		add(ModBlocks.ACID_CAULDRON.get(), drop(Items.CAULDRON));
		add(ModBlocks.ACID_SPLATTER.get(), noDrop());
		add(ModBlocks.VOLATILE_SPLATTER.get(), noDrop());
		dropSelf(ModBlocks.WATER_GEL_BLOCK.get());
	}

	protected <T extends Block> void addCustom(T block, Function<T, LootTable.Builder> function) {
		add(block, function.apply(block));
	}

}
