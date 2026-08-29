package com.github.elenterius.biomancy.client.render.entity.projectile;

import com.github.elenterius.biomancy.entity.projectile.AcidSpitProjectile;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class AcidSpitProjectileRenderer extends EntityRenderer<AcidSpitProjectile> {

	private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/llama/spit.png");
	private final LlamaSpitModel<AcidSpitProjectile> model;

	public AcidSpitProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new LlamaSpitModel<>(context.bakeLayer(ModelLayers.LLAMA_SPIT));
	}

	@Override
	public void render(AcidSpitProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();

		poseStack.translate(0, 0.15f, 0);
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90f));
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));

		int rgb = ModMobEffects.CORROSIVE.get().getColor();
		int color = FastColor.ARGB32.opaque(rgb);
		model.renderToBuffer(poseStack, buffer.getBuffer(model.renderType(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, color);

		poseStack.popPose();

		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(AcidSpitProjectile entity) {
		return TEXTURE;
	}

}
