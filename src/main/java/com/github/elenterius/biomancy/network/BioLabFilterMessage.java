package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.menu.BioLabMenu;
import com.github.elenterius.biomancy.util.ItemStackFilter;
import com.github.elenterius.biomancy.util.ItemStackFilterList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//client bound message
public record BioLabFilterMessage(int containerId, List<@Nullable ItemStack> filters) implements CustomPacketPayload {

	public static final Type<BioLabFilterMessage> TYPE = new Type<>(BiomancyMod.rl("bio_lab_filter"));

	private static final StreamCodec<RegistryFriendlyByteBuf, @Nullable ItemStack> NULLABLE_ITEM_STACK_STREAM_CODEC = StreamCodec.of(
			(buffer, stack) -> {
				buffer.writeBoolean(stack != null);
				if (stack != null) ItemStack.STREAM_CODEC.encode(buffer, stack);
			},
			buffer -> !buffer.readBoolean() ? null : ItemStack.STREAM_CODEC.decode(buffer)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, BioLabFilterMessage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, BioLabFilterMessage::containerId,
			NULLABLE_ITEM_STACK_STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)), BioLabFilterMessage::filters,
			BioLabFilterMessage::new
	);

	public BioLabFilterMessage(int containerId, ItemStackFilterList filters) {
		this(containerId, filters.stream().map(ItemStackFilter::getItemStack).collect(Collectors.toCollection(ArrayList::new)));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(BioLabFilterMessage packet, IPayloadContext context) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && player.containerMenu instanceof BioLabMenu menu && menu.containerId == packet.containerId) {
			menu.setFilters(packet.filters);
		}
	}

}
