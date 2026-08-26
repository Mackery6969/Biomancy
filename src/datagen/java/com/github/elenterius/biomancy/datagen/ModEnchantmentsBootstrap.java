package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantmentsBootstrap {

	private ModEnchantmentsBootstrap() {
	}

	public static void bootstrap(BootstapContext<Enchantment> ctx) {
		HolderGetter<Item> items = ctx.lookup(Registries.ITEM);

		register(ctx, ModEnchantments.DESPOIL, Enchantment.enchantment(
				Enchantment.definition(items.getOrThrow(ModItemTags.ENCHANTABLE_WEAPON), 2, 3,
						Enchantment.dynamicCost(15, 9), Enchantment.dynamicCost(65, 9), 4,
						EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND)));

		register(ctx, ModEnchantments.ANESTHETIC, Enchantment.enchantment(
				Enchantment.definition(items.getOrThrow(ModItemTags.ENCHANTABLE_SYRINGE), 2, 1,
						Enchantment.dynamicCost(15, 9), Enchantment.dynamicCost(65, 9), 4,
						EquipmentSlotGroup.MAINHAND)));

		register(ctx, ModEnchantments.SURGICAL_PRECISION, Enchantment.enchantment(
				Enchantment.definition(items.getOrThrow(ModItemTags.ENCHANTABLE_SURGERY), 2, 3,
						Enchantment.dynamicCost(15, 9), Enchantment.dynamicCost(65, 9), 4,
						EquipmentSlotGroup.MAINHAND)));

		register(ctx, ModEnchantments.PARASITIC_METABOLISM, Enchantment.enchantment(
				Enchantment.definition(items.getOrThrow(ModItemTags.ENCHANTABLE_LIVING), 2, 1,
						Enchantment.dynamicCost(15, 9), Enchantment.dynamicCost(65, 9), 4,
						EquipmentSlotGroup.ANY)));

		register(ctx, ModEnchantments.SELF_FEEDING, Enchantment.enchantment(
				Enchantment.definition(items.getOrThrow(ModItemTags.ENCHANTABLE_LIVING), 2, 1,
						Enchantment.dynamicCost(15, 9), Enchantment.dynamicCost(65, 9), 4,
						EquipmentSlotGroup.ANY)));
	}

	private static void register(BootstapContext<Enchantment> ctx, ResourceKey<Enchantment> key,
			Enchantment.Builder builder) {
		ctx.register(key, builder.build(key.location()));
	}

}
