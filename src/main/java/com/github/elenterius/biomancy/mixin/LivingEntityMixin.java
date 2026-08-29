package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.entity.misc.LivingEntityData;
import com.github.elenterius.biomancy.event.LivingEventHandler;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.tags.ModItemTags;
import com.github.elenterius.biomancy.item.ShieldBlockingListener;
import com.github.elenterius.biomancy.serum.FrenzySerum;
import com.github.elenterius.biomancy.statuseffect.StatusEffectHandler;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.EffectCure;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityData.TransientDataProvider {

	@Shadow
	protected ItemStack useItem;

	private LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow
	public abstract boolean hasEffect(Holder<MobEffect> effect);

	@Shadow
	public abstract AttributeMap getAttributes();

	@Shadow
	@Final
	private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;

	@Shadow
	public abstract Collection<MobEffectInstance> getActiveEffects();

	@Unique
	DataHolder biomancy$transientData = new DataHolder();

	@Override
	public DataHolder biomancy$getData() {
		return biomancy$transientData;
	}

	@Inject(method = "checkAutoSpinAttack", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;isEmpty()Z"), cancellable = true)
	private void onAutoSpinHorizontalCollision(AABB aabbBeforeSpin, AABB aabbAfterSpin, CallbackInfo ci, @Local List<Entity> list) {
		if (list.isEmpty() && horizontalCollision && LivingEventHandler.onAutoSpinHorizontalCollision(biomancy$self())) {
			ci.cancel();
		}
	}

	@Inject(method = "shouldDiscardFriction", at = @At("HEAD"), cancellable = true)
	private void onShouldDiscardFriction(CallbackInfoReturnable<Boolean> cir) {
		if (biomancy$transientData.shouldDiscardFriction()) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "getAttributeValue(Lnet/minecraft/core/Holder;)D", at = @At("HEAD"), cancellable = true)
	protected void onGetAttributeValue(Holder<Attribute> attribute, CallbackInfoReturnable<Double> cir) {
		if (attribute == Attributes.ATTACK_DAMAGE && !getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) && hasEffect(ModMobEffects.FRENZY)) {
			cir.setReturnValue(FrenzySerum.ATTACK_DAMAGE_FALLBACK);
		}
	}

	@Inject(method = "isSensitiveToWater", at = @At(value = "HEAD"), cancellable = true)
	private void onIsSensitiveToWater(CallbackInfoReturnable<Boolean> cir) {
		if (hasEffect(ModMobEffects.CORROSIVE)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "blockUsingShield", at = @At(value = "HEAD"))
	private void onBlockUsingShield(LivingEntity attacker, CallbackInfo ci) {
		if (useItem.getItem() instanceof ShieldBlockingListener listener) {
			listener.onShieldBlocking(useItem, (LivingEntity) (Object) this, attacker);
		}
	}

	@Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "HEAD"), cancellable = true)
	private void onAddEffect(MobEffectInstance effectInstance, @Nullable Entity source, CallbackInfoReturnable<Boolean> cir) {
		if (source instanceof AreaEffectCloud || source instanceof ThrownPotion || source instanceof Arrow) {
			// Note: ThrownPotion or AbstractArrow will only be matched if they have no owner (owner == null)
			if (!StatusEffectHandler.canApplySplashEffectIfAllowed(effectInstance.getEffect(), biomancy$self(), StatusEffectHandler.CONSUME_ONE_NUTRIENT_PER_ARMOR_PIECE)) {
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "TAIL"))
	private void onAddEatEffect(Level level, ItemStack food, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> cir) {
		if (!level.isClientSide && biomancy$getRawMeatNutrition(food) > 2 && getRandom().nextFloat() < 0.2f) {
			biomancy$self().addEffect(new MobEffectInstance(ModMobEffects.PRIMORDIAL_INFESTATION, 20 * 8, 0));
		}
	}

	@ModifyArg(
			method = "removeEffectsCuredBy",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V"),
			remap = false
	)
	private MobEffectInstance onCurePotionEffects(MobEffectInstance effectInstance, @Share("removedFrenzy") LocalRef<MobEffectInstance> removedFrenzyRef) {
		if (effectInstance.getEffect() == ModMobEffects.FRENZY) {
			removedFrenzyRef.set(effectInstance);
		}
		return effectInstance;
	}

	@Inject(method = "removeEffectsCuredBy", at = @At(value = "TAIL"), remap = false)
	private void onPostCurePotionEffects(EffectCure cure, CallbackInfoReturnable<Boolean> cir, @Share("removedFrenzy") LocalRef<MobEffectInstance> removedFrenzyRef) {
		if (level().isClientSide) return;

		MobEffectInstance removedFrenzyEffect = removedFrenzyRef.get();
		if (removedFrenzyEffect == null) return;

		StatusEffectHandler.addWithdrawalAfterFrenzy(biomancy$self(), removedFrenzyEffect);
	}

	@ModifyArg(
			method = "tickEffects",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V")
	)
	private MobEffectInstance onTickEffects(MobEffectInstance effectInstance, @Share("expiredFrenzy") LocalRef<MobEffectInstance> expiredFrenzyRef) {
		if (effectInstance.getEffect() == ModMobEffects.FRENZY) {
			expiredFrenzyRef.set(effectInstance);
		}
		return effectInstance;
	}

	@Inject(method = "tickEffects", at = @At(value = "TAIL"))
	private void onPostTickEffects(CallbackInfo ci, @Share("expiredFrenzy") LocalRef<MobEffectInstance> expiredFrenzyRef) {
		if (level().isClientSide) return;

		MobEffectInstance removedFrenzyEffect = expiredFrenzyRef.get();
		if (removedFrenzyEffect == null) return;

		StatusEffectHandler.addWithdrawalAfterFrenzy(biomancy$self(), removedFrenzyEffect);
	}

	@Unique
	private static int biomancy$getRawMeatNutrition(ItemStack itemStack) {
		FoodProperties food = itemStack.getFoodProperties(null);
		return food != null && itemStack.is(ModItemTags.FRESH_RAW_MEATS) ? food.nutrition() : 0;
	}

	@Unique
	private LivingEntity biomancy$self() {
		return (LivingEntity) (Object) this;
	}

}
