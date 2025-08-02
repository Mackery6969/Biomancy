package com.github.elenterius.biomancy.api.serum;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface SerumContainer extends ItemLike {

	default Serum getSerum(ItemStack stack) {
		return Serum.EMPTY;
	}

	default CompoundTag getSerumData(ItemStack stack) {
		return Serum.getDataTag(stack);
	}

	default int getSerumColor(ItemStack stack) {
		return getSerum(stack).getColor(getSerumData(stack));
	}

}
