package com.github.elenterius.biomancy.mixin.geckolibextras;

import com.github.elenterius.geckolibextras.GLibExtras;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

@Mixin(value = BlockAndItemGeoLayer.class, remap = false)
public abstract class BlockAndItemGeoLayerMixin {

	@Inject(
			method = "renderStackForBone(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/cache/object/GeoBone;Lnet/minecraft/world/item/ItemStack;Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;Lnet/minecraft/client/renderer/MultiBufferSource;FII)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;I)V"),
			remap = true
	)
	private void onRenderStaticItem(PoseStack poseStack, GeoBone bone, ItemStack itemStack, GeoAnimatable animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay, CallbackInfo ci) {
		if (animatable instanceof Entity || animatable instanceof BlockEntity) {
			GLibExtras.setItemHostObject(itemStack, animatable);
		}
	}

}
