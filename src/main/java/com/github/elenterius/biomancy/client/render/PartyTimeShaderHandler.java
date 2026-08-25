package com.github.elenterius.biomancy.client.render;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.client.ModRenderTypes;
import com.mojang.blaze3d.shaders.Uniform;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID, value = Dist.CLIENT)
public final class PartyTimeShaderHandler {

	private static int ticks = 0;
	private static Uniform time;

	private PartyTimeShaderHandler() {}

	private static Uniform getTimeUniform() {
		if (time == null) {
			time = ModRenderTypes.getEntityCutoutPartyTimeShader().getUniform("Time");
		}
		return time;
	}

	@SubscribeEvent
	static void onClientTick(final ClientTickEvent.Post event) {
		ticks++;
	}

	@SubscribeEvent
	static void onRenderTick(final RenderFrameEvent.Pre event) {
		float renderTickTime = event.getPartialTick().getGameTimeDeltaPartialTick(true);
		float totalTicks = ticks + renderTickTime;
		float t = totalTicks * 0.05f; //convert to seconds, ticks/20.0 ~= 1 sec
		getTimeUniform().set(t);
	}

}
