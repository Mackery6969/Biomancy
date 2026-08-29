package com.github.elenterius.biomancy.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;

public class BloomberryItem extends SimpleItem {

	public BloomberryItem(Properties properties) {
		super(properties);
	}

	private static void applyPotion(LivingEntity livingEntity, Potion potion) {
		for (MobEffectInstance effectInstance : potion.getEffects()) {
			if (effectInstance.getEffect().value().isInstantenous()) {
				effectInstance.getEffect().value().applyInstantenousEffect(livingEntity, livingEntity, livingEntity, effectInstance.getAmplifier(), 1);
			}
			else {
				livingEntity.addEffect(new MobEffectInstance(effectInstance));
			}
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		ItemStack result = livingEntity.eat(level, stack);

		if (!level.isClientSide) {
			BuiltInRegistries.POTION.stream().skip(level.random.nextInt(BuiltInRegistries.POTION.size())).findFirst().ifPresent(potion -> applyPotion(livingEntity, potion));
		}

		return result;
	}
}
