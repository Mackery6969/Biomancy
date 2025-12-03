package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.util.ExplosionUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

	@WrapOperation(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	private boolean onEntityHurt(Entity instance, DamageSource source, float amount, Operation<Boolean> original) {
		Boolean result = original.call(instance, source, amount);

		Explosion self = (Explosion) (Object) this;
		if (self instanceof ExplosionUtil.VolatileExplosion && amount > 0f && instance.isAlive() && instance instanceof LivingEntity living) {
			living.addEffect(new MobEffectInstance(ModMobEffects.VOLATILE.get(), 60 * 20));
		}

		return result;
	}

}