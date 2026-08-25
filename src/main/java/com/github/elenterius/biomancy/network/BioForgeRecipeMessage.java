package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.crafting.recipe.BioForgingRecipe;
import com.github.elenterius.biomancy.menu.BioForgeMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.network.handling.IPayloadContext;

//server bound message
public record BioForgeRecipeMessage(int containerId, ResourceLocation id) implements CustomPacketPayload {

	public static final Type<BioForgeRecipeMessage> TYPE = new Type<>(BiomancyMod.rl("bio_forge_recipe"));

	public static final StreamCodec<ByteBuf, BioForgeRecipeMessage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, BioForgeRecipeMessage::containerId,
			ResourceLocation.STREAM_CODEC, BioForgeRecipeMessage::id,
			BioForgeRecipeMessage::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(BioForgeRecipeMessage packet, IPayloadContext context) {
		if (context.player() instanceof ServerPlayer sender && !sender.isSpectator() && sender.containerMenu instanceof BioForgeMenu menu && menu.containerId == packet.containerId) {
			RecipeManager recipeManager = sender.level().getRecipeManager();
			BioForgingRecipe recipe = recipeManager.byKey(packet.id).map(holder -> holder.value() instanceof BioForgingRecipe r ? r : null).orElse(null);
			menu.setSelectedRecipe(recipe, sender);
		}
	}

}
