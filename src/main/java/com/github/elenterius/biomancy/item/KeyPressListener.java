package com.github.elenterius.biomancy.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public interface KeyPressListener {

	/// @param slotIndex of EquipmentSlotType (Armor + Main & Off-Hand). This is not the inventory index of a player
	static void onReceiveKeybindingPacket(ServerLevel world, ServerPlayer player, int slotIndex, byte flag) {
		EquipmentSlot slotType = getEquipmentSlotTypeFrom(slotIndex);
		if (slotType != null) {
			ItemStack heldStack = player.getItemBySlot(slotType);
			if ((heldStack.getItem() instanceof KeyPressListener keyListener) && !(player.getCooldowns().isOnCooldown(heldStack.getItem()))) {
				keyListener.onServerReceiveKeyPress(heldStack, world, player, flag);
			}
		}
		else {
			ItemStack stackInSlot = player.getInventory().getItem(slotIndex);
			if (!stackInSlot.isEmpty() && (stackInSlot.getItem() instanceof KeyPressListener keyListener) && !(player.getCooldowns().isOnCooldown(stackInSlot.getItem()))) {
				keyListener.onServerReceiveKeyPress(stackInSlot, world, player, flag);
			}
		}
	}

	static @Nullable EquipmentSlot getEquipmentSlotTypeFrom(int slotIndex) {
		if (slotIndex < 0 || slotIndex > 5) return null;
		for (EquipmentSlot equipmentSlotType : EquipmentSlot.values()) {
			if (equipmentSlotType.getFilterFlag() == slotIndex) {
				return equipmentSlotType;
			}
		}
		return null;
	}

	/// If this method returns ActionResult Success, the result byte customFlags will be sent to the server
	KeyPressResult onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags);

	void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags);

	record KeyPressResult(byte flags, boolean success) {

		public static final byte NO_FLAGS = (byte) 0;

		public static KeyPressResult success(byte flags) {
			return new KeyPressResult(flags, true);
		}

		public static KeyPressResult fail() {
			return new KeyPressResult(NO_FLAGS, false);
		}

	}
}
