package com.github.elenterius.biomancy.client.render.item.injector;

import com.github.elenterius.biomancy.client.render.item.GeoItemWithArmRenderer;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

public class InjectorRenderer extends GeoItemWithArmRenderer<InjectorItem> {

	private int serumColor = -1;

	public InjectorRenderer() {
		super(new InjectorModel());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
	}

	@Override
	public void preRender(PoseStack poseStack, InjectorItem item, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
		serumColor = item.getSerumColor(getCurrentItemStack());
		super.preRender(poseStack, item, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
	}

	@Override
	public void renderRecursively(PoseStack poseStack, InjectorItem item, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
		if (bone.getName().equals("_serum_core")) {
			renderSerumBone(poseStack, item, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
		}
		else {
			super.renderRecursively(poseStack, item, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
		}
	}

	private void renderSerumBone(PoseStack poseStack, InjectorItem item, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
		if (serumColor == -1) return; //don't render
		int alpha = FastColor.ARGB32.alpha(colour);
		int r = FastColor.ARGB32.red(serumColor);
		int g = FastColor.ARGB32.green(serumColor);
		int b = FastColor.ARGB32.blue(serumColor);
		int serumColourWithAlpha = FastColor.ARGB32.color(alpha, r, g, b);
		super.renderRecursively(poseStack, item, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, serumColourWithAlpha);
	}

}
