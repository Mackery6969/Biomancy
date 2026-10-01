package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.biolab.BioLabBlockEntity;
import com.github.elenterius.biomancy.inventory.BehavioralItemHandler;
import com.github.elenterius.biomancy.inventory.InventoryHandler;
import com.github.elenterius.biomancy.inventory.InventoryHandlers;
import com.github.elenterius.biomancy.network.BioLabFilterMessage;
import io.netty.buffer.Unpooled;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.network.connection.ConnectionType;

import static com.github.elenterius.biomancy.gametest.GameTestTemplates.EMPTY_PLATFORM;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkGameTests {

	private NetworkGameTests() {}

	@GameTest(template = EMPTY_PLATFORM)
	public static void bioLabFiltersPreserveLockedEmptyAndUnlockedSlots(GameTestHelper helper) {
		InventoryHandler<BehavioralItemHandler.LockableItemStackFilterInput> inventory =
				InventoryHandlers.lockableFilterInput(BioLabBlockEntity.INPUT_SLOTS, () -> {});
		ItemStack ingredient = new ItemStack(Items.BEEF, 3);
		ingredient.set(DataComponents.CUSTOM_NAME, Component.literal("Filter ingredient"));
		inventory.setStackInSlot(0, ingredient);
		inventory.get().setLocked(true);

		BioLabFilterMessage locked = roundTrip(helper, new BioLabFilterMessage(17, inventory.get().getFilters()));
		helper.assertValueEqual(locked.containerId(), 17, "filter packet container id");
		helper.assertValueEqual(locked.filters().size(), BioLabBlockEntity.INPUT_SLOTS, "filter packet slot count");
		helper.assertTrue(ItemStack.matches(locked.filters().getFirst(), inventory.get().getFilterItemStack(0)),
				"filter packet lost the populated slot's item or components");
		for (int i = 1; i < locked.filters().size(); i++) {
			ItemStack filter = locked.filters().get(i);
			helper.assertTrue(filter != null && filter.isEmpty(), "locked empty slot must reject all items after synchronization");
		}

		inventory.get().setLocked(false);
		BioLabFilterMessage unlocked = roundTrip(helper, new BioLabFilterMessage(17, inventory.get().getFilters()));
		helper.assertTrue(unlocked.filters().stream().allMatch(filter -> filter == null),
				"unlocked filters must remain distinct from locked empty slots");
		helper.succeed();
	}

	private static BioLabFilterMessage roundTrip(GameTestHelper helper, BioLabFilterMessage message) {
		RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess(), ConnectionType.NEOFORGE);
		try {
			BioLabFilterMessage.STREAM_CODEC.encode(buffer, message);
			BioLabFilterMessage decoded = BioLabFilterMessage.STREAM_CODEC.decode(buffer);
			helper.assertFalse(buffer.isReadable(), "filter packet left unread bytes");
			return decoded;
		}
		finally {
			buffer.release();
		}
	}

}
