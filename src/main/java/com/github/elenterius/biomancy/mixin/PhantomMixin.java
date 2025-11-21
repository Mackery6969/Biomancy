package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.serum.InsomniaCureSerum;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class PhantomMixin {

	@Inject(
			method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;)Z",
			at = @At(value = "HEAD"), cancellable = true
	)
	private void onCanAttack(LivingEntity target, TargetingConditions condition, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity attacker = (LivingEntity) (Object) this;
		if (attacker instanceof Phantom) {
			CompoundTag data = target.getPersistentData();
			if (data.contains(InsomniaCureSerum.DATA_KEY)) {
				long elapsedTime = attacker.level().getGameTime() - data.getLong(InsomniaCureSerum.DATA_KEY);
				if (elapsedTime < InsomniaCureSerum.PROTECTION_TICKS) {
					if (elapsedTime < InsomniaCureSerum.PROTECTION_TICKS / 2 && attacker.getRandom().nextFloat() < 0.25f) {
						attacker.hurt(attacker.level().damageSources().magic(), 1f);
					}
					cir.setReturnValue(false);
				}
				else {
					data.remove(InsomniaCureSerum.DATA_KEY);
				}
			}
		}
	}

}
