package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class ForgeGuiMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@ModifyExpressionValue(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasEffect(Lnet/minecraft/core/Holder;)Z"))
	private boolean canRenderFoodHunger(boolean hasHungerEffect) {
		//noinspection DataFlowIssue
		if (!hasHungerEffect && minecraft.player.hasEffect(ModMobEffects.WITHDRAWAL)) {
			return true;
		}
		return hasHungerEffect;
	}

}
