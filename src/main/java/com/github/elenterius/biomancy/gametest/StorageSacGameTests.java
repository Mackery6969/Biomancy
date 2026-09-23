package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.storagesac.StorageSacBlockEntity;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.inventory.FixedSizeItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;
import java.util.Objects;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class StorageSacGameTests {

	private static final BlockPos FIRST_POS = new BlockPos(2, 2, 2);
	private static final BlockPos SECOND_POS = new BlockPos(5, 2, 2);

	private StorageSacGameTests() {}

	@GameTest(template = "empty_platform")
	public static void itemContentsSurvivePlacementSaveAndSurvivalDrop(GameTestHelper helper) {
		ItemStack sac = new ItemStack(ModItems.STORAGE_SAC.get());
		sac.set(DataComponents.CUSTOM_NAME, Component.literal("Travel supplies"));
		ItemStack expected = namedEnchantedSword(helper);
		IItemHandler itemInventory = handler(sac);
		helper.assertTrue(itemInventory.insertItem(7, expected, false).isEmpty(), "Sac must accept its item contents");

		StorageSacBlockEntity placed = place(helper, FIRST_POS, sac);
		assertStack(helper, placed.getInventory().getStackInSlot(7), expected, "Placed sac lost its item contents or slot");
		StorageSacBlockEntity reloaded = new StorageSacBlockEntity(placed.getBlockPos(), placed.getBlockState());
		reloaded.loadWithComponents(placed.saveWithFullMetadata(helper.getLevel().registryAccess()), helper.getLevel().registryAccess());
		assertStack(helper, reloaded.getInventory().getStackInSlot(7), expected, "World save lost the contents");

		ItemStack dropped = survivalDrop(helper, placed);
		assertStack(helper, handler(dropped).getStackInSlot(7), expected, "Survival drop lost its contents");
		helper.assertTrue(dropped.getHoverName().equals(Component.literal("Travel supplies")), "Survival drop lost the custom name");
		helper.assertFalse(dropped.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).contains(StorageSacBlockEntity.INVENTORY_KEY), "Drop must not duplicate the inventory in legacy NBT");
		StorageSacBlockEntity replaced = place(helper, SECOND_POS, dropped);
		assertStack(helper, replaced.getInventory().getStackInSlot(7), expected, "Replacing the dropped sac lost its contents");
		helper.succeed();
	}

	@GameTest(template = "empty_platform")
	public static void legacyItemMigrationPreservesComponentsAndDoesNotMutateSimulation(GameTestHelper helper) {
		ItemStack expected = namedEnchantedSword(helper);
		ItemStack sac = legacySac(helper, 9, expected);
		ItemStack original = sac.copy();
		IItemHandler inventory = handler(sac);
		assertStack(helper, inventory.getStackInSlot(9), expected, "Legacy item capability cannot read its inventory");
		assertStack(helper, inventory.extractItem(9, 1, true), expected, "Legacy simulated extraction lost components");
		helper.assertTrue(ItemStack.matches(original, sac), "Reading or simulating a legacy item must not mutate it");

		ItemStack extracted = inventory.extractItem(9, 1, false);
		assertStack(helper, extracted, expected, "Legacy extraction lost components");
		helper.assertTrue(sac.has(DataComponents.CONTAINER), "Mutation must migrate legacy contents to the container component");
		helper.assertFalse(sac.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).contains(StorageSacBlockEntity.INVENTORY_KEY), "Migration must remove the old inventory to avoid restoring extracted items");
		StorageSacBlockEntity placed = place(helper, FIRST_POS, sac);
		helper.assertTrue(placed.isEmpty(), "Placing a migrated empty sac must not resurrect extracted contents");
		helper.succeed();
	}

	@GameTest(template = "empty_platform")
	public static void legacyItemCanBePlacedWithoutOpeningItsCapability(GameTestHelper helper) {
		ItemStack expected = new ItemStack(Items.DIAMOND, 11);
		StorageSacBlockEntity placed = place(helper, FIRST_POS, legacySac(helper, 12, expected));
		assertStack(helper, placed.getInventory().getStackInSlot(12), expected, "An absent container component must not clear legacy NBT contents");
		assertStack(helper, handler(survivalDrop(helper, placed)).getStackInSlot(12), expected, "Legacy placement must produce a usable modern drop");
		helper.succeed();
	}

	@GameTest(template = "empty_platform")
	public static void creativeBreakCopiesContentsWithoutDuplicatingLegacyData(GameTestHelper helper) {
		StorageSacBlockEntity placed = place(helper, FIRST_POS, new ItemStack(ModItems.STORAGE_SAC.get()));
		ItemStack expected = new ItemStack(Items.EMERALD, 19);
		placed.getInventory().setStackInSlot(4, expected);
		placed.setCustomName(Component.literal("Keepsakes"));
		Player player = helper.makeMockPlayer(GameType.CREATIVE);
		placed.getBlockState().getBlock().playerWillDestroy(helper.getLevel(), placed.getBlockPos(), placed.getBlockState(), player);
		List<ItemEntity> entities = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(placed.getBlockPos()));
		helper.assertTrue(entities.size() == 1, "Creative break must drop one filled sac");
		ItemStack dropped = entities.getFirst().getItem();
		assertStack(helper, handler(dropped).getStackInSlot(4), expected, "Creative drop lost contents");
		helper.assertTrue(dropped.getHoverName().equals(Component.literal("Keepsakes")), "Creative drop lost its custom name");
		helper.assertFalse(dropped.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).contains(StorageSacBlockEntity.INVENTORY_KEY), "Creative copy must not keep a second inventory");
		helper.succeed();
	}

	@GameTest(template = "empty_platform")
	public static void mixedOldAndNewItemContentsAreBothRecoveredOnPlacement(GameTestHelper helper) {
		ItemStack sac = legacySac(helper, 0, new ItemStack(Items.DIAMOND, 5));
		sac.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(new ItemStack(Items.EMERALD, 7))));
		StorageSacBlockEntity placed = place(helper, FIRST_POS, sac);
		assertStack(helper, placed.getInventory().getStackInSlot(0), new ItemStack(Items.DIAMOND, 5), "Mixed-format placement lost old contents");
		assertStack(helper, placed.getInventory().getStackInSlot(1), new ItemStack(Items.EMERALD, 7), "Mixed-format placement lost component contents");
		helper.succeed();
	}

	@GameTest(template = "empty_platform")
	public static void existingPlacedSacRecoversUnclaimedComponentsOnlyOnce(GameTestHelper helper) {
		StorageSacBlockEntity placed = place(helper, FIRST_POS, new ItemStack(ModItems.STORAGE_SAC.get()));
		placed.getInventory().setStackInSlot(0, new ItemStack(Items.DIAMOND, 5));
		placed.setComponents(DataComponentMap.builder().set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(new ItemStack(Items.EMERALD, 7)))).build());
		CompoundTag oldSave = placed.saveWithFullMetadata(helper.getLevel().registryAccess());
		placed.loadWithComponents(oldSave, helper.getLevel().registryAccess());
		placed.onLoad();
		assertStack(helper, placed.getInventory().getStackInSlot(0), new ItemStack(Items.DIAMOND, 5), "Recovery lost the existing world inventory");
		assertStack(helper, placed.getInventory().getStackInSlot(1), new ItemStack(Items.EMERALD, 7), "Recovery lost previously inaccessible component contents");
		helper.assertFalse(placed.components().has(DataComponents.CONTAINER), "Recovered generic components must be removed");
		placed.onLoad();
		assertStack(helper, placed.getInventory().getStackInSlot(1), new ItemStack(Items.EMERALD, 7), "Loading twice must not duplicate recovered contents");
		helper.succeed();
	}

	@GameTest(template = "empty_platform")
	public static void unopenedLootTableSurvivesSurvivalDropAndPlacement(GameTestHelper helper) {
		SeededContainerLoot expected = new SeededContainerLoot(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/simple_dungeon")), 12345L);
		StorageSacBlockEntity placed = place(helper, FIRST_POS, new ItemStack(ModItems.STORAGE_SAC.get()));
		placed.setLootTable(expected.lootTable().location(), expected.seed());
		ItemStack dropped = survivalDrop(helper, placed);
		helper.assertTrue(expected.equals(dropped.get(DataComponents.CONTAINER_LOOT)), "Survival drop lost an unopened loot table");
		StorageSacBlockEntity replaced = place(helper, SECOND_POS, dropped);
		helper.assertTrue(expected.equals(replaced.collectComponents().get(DataComponents.CONTAINER_LOOT)), "Placement lost the unopened loot table or seed");
		helper.succeed();
	}

	private static IItemHandler handler(ItemStack stack) {
		return Objects.requireNonNull(stack.getCapability(ModCapabilities.ITEM_HANDLER_ITEM));
	}

	private static ItemStack namedEnchantedSword(GameTestHelper helper) {
		ItemStack stack = new ItemStack(Items.DIAMOND_SWORD);
		stack.set(DataComponents.CUSTOM_NAME, Component.literal("Remember me"));
		stack.enchant(helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING), 3);
		return stack;
	}

	private static ItemStack legacySac(GameTestHelper helper, int slot, ItemStack contents) {
		FixedSizeItemStackHandler inventory = new FixedSizeItemStackHandler(StorageSacBlockEntity.SLOTS);
		inventory.setStackInSlot(slot, contents);
		CompoundTag data = new CompoundTag();
		data.putString("id", "biomancy:storage_sac");
		data.put(StorageSacBlockEntity.INVENTORY_KEY, inventory.serializeNBT(helper.getLevel().registryAccess()));
		ItemStack sac = new ItemStack(ModItems.STORAGE_SAC.get());
		sac.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(data));
		return sac;
	}

	private static StorageSacBlockEntity place(GameTestHelper helper, BlockPos relativePos, ItemStack sac) {
		helper.setBlock(relativePos.below(), Blocks.STONE);
		helper.setBlock(relativePos, Blocks.AIR);
		BlockPos pos = helper.absolutePos(relativePos);
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		player.setPos(Vec3.atCenterOf(pos).add(0, 0, 2));
		BlockHitResult hit = new BlockHitResult(Vec3.atBottomCenterOf(pos), Direction.UP, pos.below(), false);
		helper.assertTrue(((BlockItem) sac.getItem()).place(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, sac, hit)).consumesAction(), "Could not place the storage sac");
		return (StorageSacBlockEntity) Objects.requireNonNull(helper.getLevel().getBlockEntity(pos));
	}

	private static ItemStack survivalDrop(GameTestHelper helper, StorageSacBlockEntity sac) {
		List<ItemStack> drops = Block.getDrops(sac.getBlockState(), helper.getLevel(), sac.getBlockPos(), sac);
		helper.assertTrue(drops.size() == 1 && drops.getFirst().is(ModItems.STORAGE_SAC.get()), "Expected one storage sac drop");
		return drops.getFirst();
	}

	private static void assertStack(GameTestHelper helper, ItemStack actual, ItemStack expected, String message) {
		helper.assertTrue(ItemStack.matches(actual, expected), message);
	}
}
