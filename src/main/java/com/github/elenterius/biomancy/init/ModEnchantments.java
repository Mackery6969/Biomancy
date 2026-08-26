package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.LevelReader;

import java.util.List;

public final class ModEnchantments {

	public static final ResourceKey<Enchantment> DESPOIL = key("despoil");
	public static final ResourceKey<Enchantment> ANESTHETIC = key("anesthetic");
	public static final ResourceKey<Enchantment> SURGICAL_PRECISION = key("surgical_precision");
	public static final ResourceKey<Enchantment> PARASITIC_METABOLISM = key("parasitic_metabolism");
	public static final ResourceKey<Enchantment> SELF_FEEDING = key("self_feeding");

	public static final List<ResourceKey<Enchantment>> ALL = List.of(DESPOIL, ANESTHETIC, SURGICAL_PRECISION, PARASITIC_METABOLISM, SELF_FEEDING);

	private ModEnchantments() {}

	private static ResourceKey<Enchantment> key(String name) {
		return ResourceKey.create(Registries.ENCHANTMENT, BiomancyMod.rl(name));
	}

	public static Holder<Enchantment> getHolder(ResourceKey<Enchantment> key, LevelReader level) {
		return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key);
	}

	public static Holder<Enchantment> getHolder(ResourceKey<Enchantment> key, HolderLookup.Provider registries) {
		return registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
	}

	public static int getLevel(ItemStack stack, ResourceKey<Enchantment> key) {
		for (Holder<Enchantment> holder : stack.getTagEnchantments().keySet()) {
			if (holder.is(key)) return stack.getEnchantmentLevel(holder);
		}
		return 0;
	}

}
