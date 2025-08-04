package com.github.elenterius.geckolibextras;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.keyframe.event.KeyFrameEvent;
import software.bernie.geckolib.core.keyframe.event.data.KeyFrameData;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public final class GLibExtras {

	public static final String ID = "geckolib_extras";

	/// DataTicket for fetching the object that is hosting the current ItemStack.
	///
	/// Currently supported hosts:
	/// - [LivingEntity] that is supplied to [ItemRenderer#renderStatic]
	/// - [Player]/[LivingEntity] that renders the item through [ItemInHandRenderer#renderItem] ([ItemInHandLayer], [PlayerItemInHandLayer], [CrossedArmsItemLayer], [DolphinCarryingItemLayer], [FoxHeldItemLayer], [PandaHoldsItemLayer])
	/// - [LivingEntity] with [CustomHeadLayer] or [SnowGolemHeadLayer]
	/// - [ItemEntity]
	/// - [Entity] that is rendered via [ThrownItemRenderer] ([ThrowableItemProjectile])
	/// - [ItemFrame]
	/// - [Display.ItemDisplay]
	/// - [GeoEntity]/[GeoBlockEntity] with [BlockAndItemGeoLayer]
	///
	/// Normal BlockEntities are currently not supported but would be trivial to add with a mixin.
	///
	/// @implNote Uses object as type because Entity and BlockEntity don't have the same parent
	///
	public static final DataTicket<@Nullable Object> ITEM_HOST_TICKET = new DataTicket<>(ID + ":item_host_object", Object.class);

	private GLibExtras() {}

	/// Sets the host object for the current render pass.
	/// On post render the host will be automatically cleaned up (i.e. set to null).
	public static void setItemHostObject(ItemStack stack, Object host) {
		if (IClientItemExtensions.of(stack).getCustomRenderer() instanceof GeoItemRendererExtension extension) {
			extension.GLibExtras$setItemHostObject(host);
		}
	}

	public interface GeoItemRendererExtension {

		void GLibExtras$setItemHostObject(@Nullable Object host);

		@Nullable Object GLibExtras$getItemHostObject();

	}

	/// @return current ItemStack related to the KeyFrameEvent
	public static <T extends Item & GeoItem, E extends KeyFrameData> @Nullable ItemStack getCurrentItemStack(KeyFrameEvent<T, E> event) {
		if (IClientItemExtensions.of(event.getAnimatable()).getCustomRenderer() instanceof GeoItemRenderer<?> renderer) {
			return renderer.getCurrentItemStack();
		}
		return null;
	}

	/// @return current item host object related to the KeyFrameEvent
	public static <T extends Item & GeoItem, E extends KeyFrameData> @Nullable Object getCurrentItemHost(KeyFrameEvent<T, E> event) {
		if (IClientItemExtensions.of(event.getAnimatable()).getCustomRenderer() instanceof GeoItemRendererExtension extension) {
			return extension.GLibExtras$getItemHostObject();
		}
		return null;
	}

}
