package com.github.elenterius.biomancy.api.serum;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public interface SerumInjector extends ItemLike {

	Serum getSerum(ItemStack stack);

	CompoundTag getSerumData(ItemStack stack);

	int getSerumColor(ItemStack stack);

	default boolean canInteractWithPlayerSelf(ItemStack stack, Player player) {
		return getSerum(stack).canAffectPlayerSelf(getSerumData(stack), player);
	}

	default boolean canInteractWithLivingTarget(ItemStack stack, @Nullable Player player, LivingEntity target) {
		return getSerum(stack).canAffectEntity(getSerumData(stack), player, target);
	}

}
