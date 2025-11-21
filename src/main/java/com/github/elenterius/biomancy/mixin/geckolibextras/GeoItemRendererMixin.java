package com.github.elenterius.biomancy.mixin.geckolibextras;

import com.github.elenterius.geckolibextras.GLibExtras;
import com.llamalad7.mixinextras.sugar.Local;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.renderer.GeoItemRenderer;

@Mixin(value = GeoItemRenderer.class, remap = false)
public abstract class GeoItemRendererMixin implements GLibExtras.GeoItemRendererExtension {

	@Unique
	private @Nullable Object GLibExtras$ItemHostEntity;

	@Override
	public void GLibExtras$setItemHostObject(@Nullable Object host) {
		GLibExtras$ItemHostEntity = host;
	}

	@Inject(
			method = "actuallyRender(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/Item;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V",
			at = @At(value = "INVOKE", ordinal = 0, target = "Lsoftware/bernie/geckolib/core/animation/AnimationState;setData(Lsoftware/bernie/geckolib/core/object/DataTicket;Ljava/lang/Object;)V")
	)
	private void onActuallyRender(CallbackInfo ci, @Local(name = "animationState") AnimationState<?> animationState) {
		animationState.setData(GLibExtras.ITEM_HOST_TICKET, GLibExtras$ItemHostEntity);
	}

	@Inject(method = "doPostRenderCleanup", at = @At(value = "HEAD"))
	private void onPostRenderCleanup(CallbackInfo ci) {
		GLibExtras$ItemHostEntity = null;
	}

}
