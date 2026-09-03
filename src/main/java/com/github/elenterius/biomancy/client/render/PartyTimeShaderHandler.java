package com.github.elenterius.biomancy.client.render;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.client.ModRenderTypes;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID, value = Dist.CLIENT)
public final class PartyTimeShaderHandler {

	private static int ticks = 0;
	private static @Nullable ShaderInstance shader;
	private static @Nullable Uniform time;

	private PartyTimeShaderHandler() {}

	private static @Nullable Uniform getTimeUniform() {
		ShaderInstance currentShader = ModRenderTypes.getEntityCutoutPartyTimeShaderOrNull();
		if (currentShader == null) {
			shader = null;
			time = null;
			return null;
		}

		if (currentShader != shader) {
			shader = currentShader;
			time = currentShader.getUniform("Time");
		}

		return time;
	}

	@SubscribeEvent
	static void onClientTick(final ClientTickEvent.Post event) {
		ticks++;
	}

	@SubscribeEvent
	static void onRenderTick(final RenderFrameEvent.Pre event) {
		Uniform uniform = getTimeUniform();
		if (uniform == null) return;

		float renderTickTime = event.getPartialTick().getGameTimeDeltaPartialTick(true);
		float totalTicks = ticks + renderTickTime;
		float t = totalTicks * 0.05f; //convert to seconds, ticks/20.0 ~= 1 sec
		uniform.set(t);
	}

}
