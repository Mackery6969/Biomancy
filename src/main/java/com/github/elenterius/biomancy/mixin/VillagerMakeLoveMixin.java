package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(VillagerMakeLove.class)
public abstract class VillagerMakeLoveMixin {

	@Inject(
			method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/behavior/BehaviorUtils;lockGazeAndWalkToEachOther(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;FI)V", shift = At.Shift.AFTER),
			cancellable = true
	)
	private void onCanBreed(ServerLevel level, Villager owner, long gameTime, CallbackInfo ci) {
		if (owner.hasEffect(ModMobEffects.LIBIDO) && gameTime % 40 == 0L) {
			Optional<Villager> partner = owner.getBrain().getMemory(MemoryModuleType.BREED_TARGET).map(Villager.class::cast);
			if (partner.isEmpty()) return;
			biomancy$breed(level, owner, partner.get());
			ci.cancel();
		}
	}

	@Invoker("breed")
	abstract Optional<Villager> biomancy$breed(ServerLevel level, Villager parent, Villager partner);

}
