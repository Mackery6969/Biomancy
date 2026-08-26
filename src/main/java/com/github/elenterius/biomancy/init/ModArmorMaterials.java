package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public final class ModArmorMaterials {

	public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, BiomancyMod.MOD_ID);

	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ACOLYTE = register("acolyte", defense(2, 6, 5, 2), 0, ModSoundEvents.ARMOR_EQUIP_BIO_ALCHEMIST, 0.25f, 0.1f, () -> Ingredient.EMPTY);
	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> WARRIOR = register("warrior", defense(3, 8, 6, 3), 0, ModSoundEvents.ARMOR_EQUIP_WARRIOR, 0.5f, 0, () -> Ingredient.EMPTY);

	private ModArmorMaterials() {}

	private static EnumMap<ArmorItem.Type, Integer> defense(int helmet, int chestplate, int leggings, int boots) {
		EnumMap<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
		map.put(ArmorItem.Type.HELMET, helmet);
		map.put(ArmorItem.Type.CHESTPLATE, chestplate);
		map.put(ArmorItem.Type.LEGGINGS, leggings);
		map.put(ArmorItem.Type.BOOTS, boots);
		map.put(ArmorItem.Type.BODY, chestplate);
		return map;
	}

	private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> defense, int enchantmentValue, DeferredHolder<SoundEvent, SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
		List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(BiomancyMod.rl(name)));
		return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(defense, enchantmentValue, Holder.direct(equipSound.get()), repairIngredient, layers, toughness, knockbackResistance));
	}

}
