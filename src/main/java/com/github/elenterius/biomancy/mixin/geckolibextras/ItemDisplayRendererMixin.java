package com.github.elenterius.biomancy.mixin.geckolibextras;

import com.github.elenterius.geckolibextras.GLibExtras;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisplayRenderer.ItemDisplayRenderer.class)
public abstract class ItemDisplayRendererMixin {

	@Inject(
			method = "renderInner(Lnet/minecraft/world/entity/Display$ItemDisplay;Lnet/minecraft/world/entity/Display$ItemDisplay$ItemRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;I)V")
	)
	private void onRenderItem(Display.ItemDisplay itemDisplay, Display.ItemDisplay.ItemRenderState renderState, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float interpolationProgress, CallbackInfo ci) {
		GLibExtras.setItemHostObject(renderState.itemStack(), itemDisplay);
	}

}
