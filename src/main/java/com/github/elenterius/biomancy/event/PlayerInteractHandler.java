package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.serum.SerumContainer;
import com.github.elenterius.biomancy.block.vialholder.VialHolderBlock;
import com.github.elenterius.biomancy.item.ChrysalisBlockItem;
import com.github.elenterius.biomancy.item.extractor.ExtractorItem;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.github.elenterius.biomancy.util.MobUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID)
public final class PlayerInteractHandler {

	private PlayerInteractHandler() {}

	@SubscribeEvent
	public static void onPlayerInteractWithEntity(final PlayerInteractEvent.EntityInteract event) {
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();

		if (item instanceof ExtractorItem || item instanceof InjectorItem || item instanceof ChrysalisBlockItem) {
			Entity target = event.getTarget();

			if (target instanceof PartEntity<?> partEntity) {
				interactWithParent(event, stack, item, partEntity);
			}
			else if (target instanceof LivingEntity livingEntity) {
				bypassLivingInteraction(event, stack, item, livingEntity);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerInteractWithBlock(final PlayerInteractEvent.RightClickBlock event) {
		if (event.getItemStack().getItem() instanceof SerumContainer && event.getLevel().getBlockState(event.getPos()).getBlock() instanceof VialHolderBlock) {
			event.setUseBlock(Event.Result.ALLOW);
		}
	}

	private static void interactWithParent(PlayerInteractEvent.EntityInteract event, ItemStack stack, Item item, PartEntity<?> partEntity) {
		Entity parent = MobUtil.getParent(partEntity);
		if (parent instanceof LivingEntity livingEntity) {
			InteractionResult interactionResult = item.interactLivingEntity(stack, event.getEntity(), livingEntity, event.getHand());
			event.setCancellationResult(interactionResult);
			event.setCanceled(true);
		}
	}

	private static void bypassLivingInteraction(PlayerInteractEvent.EntityInteract event, ItemStack stack, Item item, LivingEntity livingEntity) {
		InteractionResult interactionResult = item.interactLivingEntity(stack, event.getEntity(), livingEntity, event.getHand());
		event.setCancellationResult(interactionResult);
		event.setCanceled(true);
	}

}
