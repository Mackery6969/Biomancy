package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.client.gui.BioForgeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPackListenerMixin {

	@Inject(method = "handleAddOrRemoveRecipes", at = @At(value = "TAIL"))
	private void onAddOrRemoveRecipes(ClientboundRecipePacket packet, CallbackInfo ci) {
		if (Minecraft.getInstance().screen instanceof BioForgeScreen screen) screen.onRecipeBookUpdated();
	}

}
