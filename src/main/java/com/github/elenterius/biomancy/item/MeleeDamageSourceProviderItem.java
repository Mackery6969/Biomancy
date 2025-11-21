package com.github.elenterius.biomancy.item;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface MeleeDamageSourceProviderItem {
	@Nullable DamageSource getMeleeDamageSource(ItemStack stack, Entity target, LivingEntity attacker, float attackStrengthScale);
}
