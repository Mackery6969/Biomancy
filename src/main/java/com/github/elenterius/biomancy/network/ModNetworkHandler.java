package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.recipe.BioForgingRecipe;
import com.github.elenterius.biomancy.util.ExplosionUtil;
import com.github.elenterius.biomancy.util.ItemStackFilterList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Explosion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID)
public final class ModNetworkHandler {

	private static final String PROTOCOL_VERSION = "1";

	private ModNetworkHandler() {}

	public static void sendKeyBindPressToServer(InteractionHand hand, byte flag) {
		EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
		sendKeyBindPressToServer(slot, flag);
	}

	public static void sendKeyBindPressToServer(EquipmentSlot slot, byte flag) {
		PacketDistributor.sendToServer(new KeyPressMessage(slot.getFilterFlag(), flag));
	}

	public static void sendKeyBindPressToServer(int slotIndex, byte flag) {
		PacketDistributor.sendToServer(new KeyPressMessage(slotIndex, flag));
	}

	public static void sendBioForgeRecipeToServer(int containerId, RecipeHolder<BioForgingRecipe> recipeHolder) {
		PacketDistributor.sendToServer(new BioForgeRecipeMessage(containerId, recipeHolder.id()));
	}

	public static void sendBioLabFilterToClient(ServerPlayer player, int containerId, ItemStackFilterList filters) {
		PacketDistributor.sendToPlayer(player, new BioLabFilterMessage(containerId, filters));
	}

	public static void sendCustomExplosionToClients(ServerLevel level, ExplosionUtil.ExplosionType explosionType, Explosion explosion) {
		if (!explosion.interactsWithBlocks()) {
			explosion.clearToBlow();
		}

		double radius = Math.min(explosion.radius() + 64d, 64d + 32d);
		double radiusSqr = radius * radius;

		for (ServerPlayer player : level.players()) {
			if (player.distanceToSqr(explosion.center()) < radiusSqr) {
				PacketDistributor.sendToPlayer(player, new CustomExplosionMessage(explosionType, explosion, player));
			}
		}
	}

	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
		registrar.playToServer(KeyPressMessage.TYPE, KeyPressMessage.STREAM_CODEC, KeyPressMessage::handle);
		registrar.playToServer(BioForgeRecipeMessage.TYPE, BioForgeRecipeMessage.STREAM_CODEC, BioForgeRecipeMessage::handle);
		registrar.playToClient(BioLabFilterMessage.TYPE, BioLabFilterMessage.STREAM_CODEC, BioLabFilterMessage::handle);
		registrar.playToClient(CustomExplosionMessage.TYPE, CustomExplosionMessage.STREAM_CODEC, CustomExplosionMessage::handle);
	}

}
