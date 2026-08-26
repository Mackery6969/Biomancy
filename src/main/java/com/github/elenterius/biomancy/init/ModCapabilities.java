package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.storagesac.StorageSacBlockEntity;
import com.github.elenterius.biomancy.inventory.InjectorItemInventory;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID)
public final class ModCapabilities {

	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, BiomancyMod.MOD_ID);

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<FlagCapImpl>> NO_KNOCKBACK_FLAG = ATTACHMENT_TYPES.register("no_knockback", () -> AttachmentType.builder(FlagCapImpl::new).build());

	public static final BlockCapability<IItemHandler, Direction> ITEM_HANDLER = Capabilities.ItemHandler.BLOCK;
	public static final BlockCapability<IFluidHandler, Direction> FLUID_HANDLER = Capabilities.FluidHandler.BLOCK;
	public static final ItemCapability<IItemHandler, Void> ITEM_HANDLER_ITEM = Capabilities.ItemHandler.ITEM;

	private ModCapabilities() {}

	@SubscribeEvent
	public static void onRegisterCapabilities(final RegisterCapabilitiesEvent event) {
		ModBlockEntities.registerCapabilities(event);

		event.registerItem(ITEM_HANDLER_ITEM, (stack, ctx) ->
				new ComponentItemHandler(stack, DataComponents.CONTAINER, StorageSacBlockEntity.SLOTS),
				ModItems.STORAGE_SAC.get());

		event.registerItem(ITEM_HANDLER_ITEM, (stack, ctx) ->
				InjectorItemInventory.create(InjectorItem.MAX_SLOT_SIZE, stack).getItemHandler(),
				ModItems.INJECTOR.get());
	}

	public interface IFlagCap {
		boolean isEnabled();

		void set(boolean enabled);

		default void enable() {
			set(true);
		}

		default void disable() {
			set(false);
		}

		default void toggle() {
			set(!isEnabled());
		}
	}

	public static class FlagCapImpl implements IFlagCap {
		private boolean isEnabled = false;

		@Override
		public boolean isEnabled() {
			return isEnabled;
		}

		@Override
		public void set(boolean enabled) {
			isEnabled = enabled;
		}

	}

}
