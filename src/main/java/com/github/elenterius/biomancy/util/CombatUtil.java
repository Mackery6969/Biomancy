package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorItem;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public final class CombatUtil {
	private CombatUtil() {}

	public static boolean canPierceThroughArmor(ItemStack weapon, LivingEntity target, @Nullable LivingEntity attacker) {
		float pierceProbability = 0;
		for (ItemStack itemStack : target.getArmorSlots()) {
			if (itemStack.getItem() instanceof AcolyteArmorItem) {
				pierceProbability += 0.25f;
			}
		}

		if (attacker != null) {
			for (ItemStack itemStack : attacker.getArmorSlots()) {
				if (itemStack.getItem() instanceof AcolyteArmorItem) {
					pierceProbability += 0.25f;
				}
			}
		}

		int pierceLevel = weapon.getEnchantmentLevel(Enchantments.PIERCING);
		float pct = CombatRules.getDamageAfterAbsorb(20f, target.getArmorValue(), (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS)) / 20f;
		return target.getRandom().nextFloat() < pct + 0.075f * pierceLevel + pierceProbability;
	}

	public static boolean hasFulLArmorSetEquipped(Player player, Predicate<ItemStack> predicate) {
		Set<ArmorItem.Type> equippedTypes = EnumSet.noneOf(ArmorItem.Type.class);

		for (ItemStack stackInArmorSlot : player.getArmorSlots()) {
			if (stackInArmorSlot.getItem() instanceof ArmorItem armor && predicate.test(stackInArmorSlot)) {
				equippedTypes.add(armor.getType());
			}
			else return false;
		}

		return equippedTypes.size() == ArmorItem.Type.values().length;
	}

	public static void performWaterAOE(Level level, Entity attacker, double maxDistance) {
		AABB aabb = attacker.getBoundingBox().inflate(maxDistance, maxDistance / 2d, maxDistance);
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb, LivingEntity::isSensitiveToWater);
		if (!entities.isEmpty()) {
			double maxDistSq = maxDistance * maxDistance;
			for (LivingEntity victim : entities) {
				if (attacker.distanceToSqr(victim) < maxDistSq) {
					victim.hurt(level.damageSources().indirectMagic(victim, attacker), 1f);
				}
			}
		}
	}

	public static void hurtWithAcid(LivingEntity livingEntity, float damage) {
		livingEntity.hurt(ModDamageSources.acid(livingEntity.level(), null), damage);
		livingEntity.invulnerableTime = 0; //leave open for next damage
	}

	public static void hurtWithBleed(LivingEntity livingEntity, float damage) {
		livingEntity.hurt(ModDamageSources.bleed(livingEntity.level(), null), damage);
		livingEntity.invulnerableTime = 0; //leave open for next damage
	}

	public static void applyBleedEffect(LivingEntity livingEntity, int seconds) {
		livingEntity.addEffect(new MobEffectInstance(ModMobEffects.BLEED.get(), seconds * 20, 0, false, false, true));
	}

	public static int getBleedEffectLevel(LivingEntity target) {
		MobEffectInstance effectInstance = target.getEffect(ModMobEffects.BLEED.get());
		if (effectInstance == null) {
			return 0;
		}
		return effectInstance.getAmplifier() + 1;
	}

}
