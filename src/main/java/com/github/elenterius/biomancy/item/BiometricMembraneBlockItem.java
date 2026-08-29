package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.block.membrane.BiometricMembraneBlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

public class BiometricMembraneBlockItem extends SimpleBlockItem {

	public BiometricMembraneBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		CompoundTag compoundTag = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
		if (!compoundTag.contains(BiometricMembraneBlockEntity.MEMBRANE_KEY)) {
			return getDescriptionId();
		}

		CompoundTag tag = compoundTag.getCompound(BiometricMembraneBlockEntity.MEMBRANE_KEY);

		String inverted = tag.getBoolean(BiometricMembraneBlockEntity.IS_INVERTED_KEY) ? ".inverted" : "";
		String unique = tag.hasUUID(BiometricMembraneBlockEntity.ENTITY_UUID_KEY) ? ".unique" : "";

		return getDescriptionId() + inverted + unique;
	}

}
