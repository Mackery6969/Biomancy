package com.github.elenterius.biomancy.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface KnowledgeReader {

	boolean canShowKnowledgeOverlay(ItemStack stack, Player player);

	boolean canTranslatePrimordialRunes(ItemStack stack, Player player);

	static boolean canShowKnowledgeOverlay(Player player, EquipmentSlot slot) {
		ItemStack stack = player.getItemBySlot(slot);
		return stack.getItem() instanceof KnowledgeReader reader && reader.canShowKnowledgeOverlay(stack, player);
	}

	static boolean canTranslatePrimordialRunes(Player player, EquipmentSlot slot) {
		ItemStack stack = player.getItemBySlot(slot);
		return stack.getItem() instanceof KnowledgeReader reader && reader.canTranslatePrimordialRunes(stack, player);
	}

}
