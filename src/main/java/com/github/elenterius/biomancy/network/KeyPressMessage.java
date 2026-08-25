package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.KeyPressListener;
import com.google.common.primitives.UnsignedBytes;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record KeyPressMessage(byte slotIndex, byte flag) implements CustomPacketPayload {

	public static final Type<KeyPressMessage> TYPE = new Type<>(BiomancyMod.rl("key_press"));

	public static final StreamCodec<ByteBuf, KeyPressMessage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BYTE, KeyPressMessage::slotIndex,
			ByteBufCodecs.BYTE, KeyPressMessage::flag,
			KeyPressMessage::new
	);

	public KeyPressMessage(int slotIndex, byte flag) {
		this(UnsignedBytes.checkedCast(slotIndex), flag);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(KeyPressMessage packet, IPayloadContext context) {
		if (context.player() instanceof ServerPlayer player) {
			ServerLevel level = player.serverLevel();
			KeyPressListener.onReceiveKeybindingPacket(level, player, UnsignedBytes.toInt(packet.slotIndex), packet.flag); //TODO: add version which is not tied to EquipmentSlotType
		}
	}

}
