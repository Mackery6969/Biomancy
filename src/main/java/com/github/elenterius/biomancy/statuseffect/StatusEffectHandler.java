package com.github.elenterius.biomancy.statuseffect;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.github.elenterius.biomancy.init.tags.ModMobEffectTags;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorItem;
import com.github.elenterius.biomancy.item.armor.LivingArmorItem;
import com.github.elenterius.biomancy.serum.FrenzySerum;
import com.github.elenterius.biomancy.util.OneShotTaskWorker;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID)
public final class StatusEffectHandler {

	private StatusEffectHandler() {}

	@SubscribeEvent
	public static void onEffectAdded(final MobEffectEvent.Added event) {
		if (event.getEntity().level().isClientSide) return;

		if (event.getEffectInstance().getEffect().is(ModMobEffects.FRENZY)) {
			if (event.getEntity() instanceof Mob mob) {
				FrenzySerum.injectAIBehavior(mob);
			}

			if (event.getEntity().hasEffect(ModMobEffects.WITHDRAWAL)) {
				OneShotTaskWorker.onNextTick(event.getEntity(), livingEntity -> livingEntity.removeEffect(ModMobEffects.WITHDRAWAL));
			}
		}
	}

	@SubscribeEvent
	public static void onEffectRemoval(final MobEffectEvent.Remove event) {
		if (event.getEntity().level().isClientSide) return;

		if (event.getEffect().is(ModMobEffects.ESSENCE_ANEMIA) && ModMobEffectTags.isNotRemovableWithCleansingSerum(ModMobEffects.ESSENCE_ANEMIA)) {
			event.setCanceled(true);
		}
	}

	/**
	 * We can't call this method from within a MobEffectEvent due to ConcurrentModification Exceptions
	 */
	public static void addWithdrawalAfterFrenzy(LivingEntity livingEntity, @Nullable MobEffectInstance removedEffectInstance) {
		if (removedEffectInstance == null) return;
		if (!removedEffectInstance.getEffect().is(ModMobEffects.FRENZY)) return;

		int amplifier = removedEffectInstance.getAmplifier();
		livingEntity.addEffect(new MobEffectInstance(ModMobEffects.WITHDRAWAL, FrenzySerum.DEFAULT_DURATION_TICKS / 2 + amplifier * 30 * 20, amplifier));
	}

	@SubscribeEvent
	public static void onFoodEaten(final LivingEntityUseItemEvent.Finish event) {
		if (event.getEntity().level().isClientSide) return;

		ItemStack stack = event.getItem();
		if (stack.is(ModItemTags.SUGARS)) {
			FoodProperties food = stack.getFoodProperties(event.getEntity());
			reduceWithdrawal(food != null ? food.nutrition() : 0, event.getEntity());
		}
	}

	public static void reduceWithdrawal(int nutrition, LivingEntity livingEntity) {
		MobEffectInstance withdrawalEffect = livingEntity.getEffect(ModMobEffects.WITHDRAWAL);
		if (withdrawalEffect != null && !withdrawalEffect.isInfiniteDuration()) {
			int duration = withdrawalEffect.getDuration() - ((nutrition * nutrition + 5) * 3 * 20); //decrease effect duration by at least 4 sec
			int amplifier = withdrawalEffect.getAmplifier();
			boolean ambient = withdrawalEffect.isAmbient();
			boolean visible = withdrawalEffect.isVisible();
			boolean showIcon = withdrawalEffect.showIcon();

			if (duration <= 0) {
				livingEntity.removeEffect(withdrawalEffect.getEffect());
			}
			else {
				overrideMobEffect(livingEntity, new MobEffectInstance(ModMobEffects.WITHDRAWAL, duration, amplifier, ambient, visible, showIcon));
			}
		}
	}

	public static void overrideMobEffect(LivingEntity livingEntity, MobEffectInstance newEffectInstance) {
		// we have to remove the old effect because the new effect has less duration and LivingEntity.addEffect() doesn't downgrade active effects
		// LivingEntity.addEffect() & EffectInstance.update() can only upgrade (duration/amplifier) effects
		livingEntity.removeEffect(newEffectInstance.getEffect());
		livingEntity.addEffect(newEffectInstance);
	}

	public static final BiConsumer<LivingArmorItem, ItemStack> CONSUME_ONE_NUTRIENT_PER_ARMOR_PIECE = (armor, itemStack) -> armor.decreaseNutrients(itemStack, 1);

	public static boolean canApplySplashEffectIfAllowed(Holder<MobEffect> effect, LivingEntity target, BiConsumer<LivingArmorItem, ItemStack> nutrientsConsumer) {

		if (ModMobEffectTags.forgeIsAcid(effect)) {
			return canApplyAcidEffect(target, nutrientsConsumer);
		}

		MobEffectCategory category = effect.value().getCategory();

		if (target.isInvertedHealAndHarm()) {
			if (effect.is(MobEffects.HEAL)) {
				category = MobEffectCategory.HARMFUL;
			}
			else if (effect.is(MobEffects.HARM)) {
				category = MobEffectCategory.BENEFICIAL;
			}
		}

		if (category == MobEffectCategory.HARMFUL) {
			int resistProbability = 0;
			int resistance = 15;

			for (ItemStack itemStack : target.getArmorSlots()) {
				if (itemStack.getItem() instanceof AcolyteArmorItem armor && armor.hasNutrients(itemStack)) {
					resistProbability += resistance;
					nutrientsConsumer.accept(armor, itemStack);
				}
			}

			if (resistProbability > 0) {
				return target.getRandom().nextInt(100) >= resistProbability;
			}
		}

		return true;
	}

	public static boolean canApplyAcidEffect(LivingEntity target, BiConsumer<LivingArmorItem, ItemStack> nutrientsConsumer) {
		int acidResistProbability = 0;
		for (ItemStack itemStack : target.getArmorSlots()) {
			if (itemStack.getItem() instanceof LivingArmorItem armor && armor.hasNutrients(itemStack)) {
				acidResistProbability += 25;
				nutrientsConsumer.accept(armor, itemStack);
			}
		}

		return acidResistProbability <= 0 || target.getRandom().nextInt(100) > acidResistProbability;
	}

	public static boolean hasAcidEffect(LivingEntity livingEntity) {
		for (Holder<MobEffect> effect : livingEntity.getActiveEffectsMap().keySet()) {
			if (ModMobEffectTags.forgeIsAcid(effect)) return true;
		}
		return false;
	}

	public static void applyCorrosiveEffect(LivingEntity livingEntity, int seconds) {
		if (livingEntity.hasEffect(ModMobEffects.CORROSIVE)) return;
		if (!canApplyAcidEffect(livingEntity, CONSUME_ONE_NUTRIENT_PER_ARMOR_PIECE)) return;

		MobEffectInstance acidEffect = new MobEffectInstance(ModMobEffects.CORROSIVE, seconds * 20, 0);

		if (!livingEntity.canBeAffected(acidEffect)) return;

		livingEntity.addEffect(acidEffect);
		livingEntity.addEffect(new MobEffectInstance(ModMobEffects.ARMOR_SHRED, (seconds + 3) * 20, 0));
	}

}
