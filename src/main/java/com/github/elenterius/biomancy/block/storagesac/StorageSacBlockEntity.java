package com.github.elenterius.biomancy.block.storagesac;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.base.SimpleContainerBlockEntity;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.inventory.InventoryHandler;
import com.github.elenterius.biomancy.inventory.InventoryHandlers;
import com.github.elenterius.biomancy.inventory.ItemHandlerUtil;
import com.github.elenterius.biomancy.menu.StorageSacMenu;
import com.github.elenterius.biomancy.util.ItemStackCounter;
import com.github.elenterius.biomancy.util.PlayerInteractionPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StorageSacBlockEntity extends SimpleContainerBlockEntity implements PlayerInteractionPredicate {

	public static final int SLOTS = 3 * 5;
	public static final String TOP5_BY_COUNT_KEY = "Top5ByCount";
	public static final String INVENTORY_KEY = "Inventory";
	private final InventoryHandler<?> inventory;
	protected final ItemStackCounter itemCounter = new ItemStackCounter();

	private List<ItemStackCounter.CountedItem> top5ItemsByCount = List.of();

	public static final String LOOT_TABLE_KEY = RandomizableContainerBlockEntity.LOOT_TABLE_TAG;
	public static final String LOOT_TABLE_SEED_TAG = RandomizableContainerBlockEntity.LOOT_TABLE_SEED_TAG;

	protected @Nullable ResourceLocation lootTableId;
	protected long lootTableSeed;

	public StorageSacBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.STORAGE_SAC.get(), pos, state);
		inventory = InventoryHandlers.denyItemWithFilledInventory(SLOTS, this::onInventoryChanged);
	}

	protected boolean tryLoadLootTable(CompoundTag tag) {
		if (!tag.contains(LOOT_TABLE_KEY, Tag.TAG_STRING)) return false;

		String id = tag.getString(LOOT_TABLE_KEY);
		lootTableId = id.isBlank() ? null : ResourceLocation.tryParse(id);
		lootTableSeed = tag.getLong(LOOT_TABLE_SEED_TAG);
		return true;
	}

	protected boolean trySaveLootTable(CompoundTag tag) {
		if (lootTableId == null) return false;

		tag.putString(LOOT_TABLE_KEY, lootTableId.toString());
		if (lootTableSeed != 0L) {
			tag.putLong(LOOT_TABLE_SEED_TAG, lootTableSeed);
		}
		return true;
	}

	public void unpackLootTable(@Nullable Player player) {
		if (lootTableId == null) return;
		if (!(level instanceof ServerLevel serverLevel)) return;

		LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(lootTableId);

		if (player instanceof ServerPlayer) {
			CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer) player, lootTableId);
		}

		lootTableId = null;
		LootParams.Builder builder = new LootParams.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition));
		if (player != null) {
			builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
		}

		lootTable.fill(inventory.getRecipeWrapper(), builder.create(LootContextParamSets.CHEST), lootTableSeed);
	}

	public void setLootTable(ResourceLocation lootTableId, long lootTableSeed) {
		this.lootTableId = lootTableId;
		this.lootTableSeed = lootTableSeed;
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		if (lootTableId != null && player.isSpectator()) return null;
		return StorageSacMenu.createServerMenu(containerId, playerInventory, this);
	}

	public InventoryHandler<?> getInventory() {
		unpackLootTable(null);
		return inventory;
	}

	@Override
	public Component getDefaultName() {
		return BiomancyMod.translatableFrom("container", "sac");
	}

	public List<ItemStackCounter.CountedItem> getItemsForRendering() {
		return top5ItemsByCount;
	}

	protected void countAllItems() {
		itemCounter.clear();
		itemCounter.accountStacks(inventory);
		top5ItemsByCount = itemCounter.getItemCountSorted(5, false);
	}

	public boolean isEmpty() {
		unpackLootTable(null);
		return inventory.isEmpty();
	}

	protected void onInventoryChanged() {
		if (level == null || level.isClientSide) return;

		countAllItems();
		setChanged();
		syncToClient();
	}

	protected void syncToClient() {
		if (level == null || level.isClientSide) return;

		BlockState state = getBlockState();
		level.sendBlockUpdated(getBlockPos(), state, state, Block.UPDATE_CLIENTS); //trigger sync to client using the data from getUpdateTag()
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		//serialize data for sync to client
		CompoundTag tag = new CompoundTag();
		tag.put(TOP5_BY_COUNT_KEY, serializeTop5());
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
		//handle received data on the client side from level chunk load
		super.handleUpdateTag(tag, registries);
	}

	@Override
	public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);

		if (trySaveLootTable(tag)) return;

		tag.put(INVENTORY_KEY, inventory.serializeNBT(registries));
		//tag.put(TOP5_BY_COUNT_KEY, serializeTop5()); //serialize for block destruction
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);

		if (tryLoadLootTable(tag)) return;

		if (tag.contains(INVENTORY_KEY)) {
			inventory.deserializeNBT(registries, tag.getCompound(INVENTORY_KEY));
			countAllItems();
		}

		if (tag.contains(TOP5_BY_COUNT_KEY)) {
			top5ItemsByCount = deserializeTop5(tag.getCompound(TOP5_BY_COUNT_KEY));
		}
	}

	public List<ItemStackCounter.CountedItem> deserializeTop5(CompoundTag store) {
		List<ItemStackCounter.CountedItem> items = new ArrayList<>();
		ListTag list = store.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag itemTag = list.getCompound(i);
			ItemStack stack = ItemStack.of(itemTag);
			if (!stack.isEmpty()) {
				int amount = itemTag.getInt("Amount");
				stack.setCount(Mth.clamp(amount, 1, 3));
				items.add(new ItemStackCounter.CountedItem(stack, amount));
			}
		}
		return items;
	}

	public CompoundTag serializeTop5() {
		ListTag list = new ListTag();

		for (ItemStackCounter.CountedItem countedItem : top5ItemsByCount) {
			CompoundTag tag = new CompoundTag();
			countedItem.stack().save(tag);
			tag.putInt("Amount", countedItem.amount());
			list.add(tag);
		}

		CompoundTag store = new CompoundTag();
		store.put("Items", list);

		return store;
	}

	@Override
	public void dropContainerContents(Level level, BlockPos pos) {
		unpackLootTable(null);
		ItemHandlerUtil.dropContents(level, pos, inventory);
	}

}
