package com.github.elenterius.biomancy.network;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.util.ExplosionUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

//client bound message
public class CustomExplosionMessage implements CustomPacketPayload {

	public static final Type<CustomExplosionMessage> TYPE = new Type<>(BiomancyMod.rl("custom_explosion"));

	public static final StreamCodec<ByteBuf, CustomExplosionMessage> STREAM_CODEC = StreamCodec.of(CustomExplosionMessage::encode, CustomExplosionMessage::decode);

	private final ExplosionUtil.ExplosionType type;
	private final @Nullable Integer sourceId;

	private final double x;
	private final double y;
	private final double z;

	private final float radius;
	private final List<BlockPos> toBlow;

	private final float knockbackX;
	private final float knockbackY;
	private final float knockbackZ;

	public CustomExplosionMessage(ExplosionUtil.ExplosionType type, Explosion explosion, ServerPlayer serverPlayer) {
		this.type = type;

		Entity source = explosion.getDirectSourceEntity();
		sourceId = source != null ? source.getId() : null;

		Vec3 position = explosion.center();
		x = position.x;
		y = position.y;
		z = position.z;

		radius = explosion.radius();
		toBlow = explosion.getToBlow();

		Vec3 knockback = explosion.getHitPlayers().get(serverPlayer);

		if (knockback != null) {
			knockbackX = (float) knockback.x;
			knockbackY = (float) knockback.y;
			knockbackZ = (float) knockback.z;
		}
		else {
			knockbackX = 0f;
			knockbackY = 0f;
			knockbackZ = 0f;
		}
	}

	public CustomExplosionMessage(ExplosionUtil.ExplosionType type, @Nullable Integer sourceId, double x, double y, double z, float radius, List<BlockPos> toBlow, float knockbackX, float knockbackY, float knockbackZ) {
		this.type = type;
		this.sourceId = sourceId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.toBlow = toBlow;
		this.knockbackX = knockbackX;
		this.knockbackY = knockbackY;
		this.knockbackZ = knockbackZ;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(CustomExplosionMessage packet, IPayloadContext context) {
		context.enqueueWork(() -> ClientHandler.handle(packet));
	}

	public static void encode(final ByteBuf buffer, final CustomExplosionMessage message) {
		buffer.writeByte(message.type.id());

		if (message.sourceId != null) {
			buffer.writeBoolean(true);
			ByteBufCodecs.VAR_INT.encode(buffer, message.sourceId);
		}
		else {
			buffer.writeBoolean(false);
		}

		buffer.writeDouble(message.x);
		buffer.writeDouble(message.y);
		buffer.writeDouble(message.z);
		buffer.writeFloat(message.radius);

		int xi = Mth.floor(message.x);
		int yi = Mth.floor(message.y);
		int zi = Mth.floor(message.z);

		ByteBufCodecs.VAR_INT.encode(buffer, message.toBlow.size());
		for (BlockPos pos : message.toBlow) {
			buffer.writeByte(pos.getX() - xi);
			buffer.writeByte(pos.getY() - yi);
			buffer.writeByte(pos.getZ() - zi);
		}

		buffer.writeFloat(message.knockbackX);
		buffer.writeFloat(message.knockbackY);
		buffer.writeFloat(message.knockbackZ);
	}

	public static CustomExplosionMessage decode(final ByteBuf buffer) {
		ExplosionUtil.ExplosionType type = ExplosionUtil.ExplosionType.fromId(buffer.readByte());

		Integer sourceId = null;
		if (buffer.readBoolean()) {
			sourceId = ByteBufCodecs.VAR_INT.decode(buffer);
		}

		double x = buffer.readDouble();
		double y = buffer.readDouble();
		double z = buffer.readDouble();
		float power = buffer.readFloat();

		int xi = Mth.floor(x);
		int yi = Mth.floor(y);
		int zi = Mth.floor(z);

		int toBlowSize = ByteBufCodecs.VAR_INT.decode(buffer);
		List<BlockPos> toBlow = new ArrayList<>(toBlowSize);
		for (int i = 0; i < toBlowSize; i++) {
			toBlow.add(new BlockPos(
					buffer.readByte() + xi,
					buffer.readByte() + yi,
					buffer.readByte() + zi
			));
		}

		float knockbackX = buffer.readFloat();
		float knockbackY = buffer.readFloat();
		float knockbackZ = buffer.readFloat();

		return new CustomExplosionMessage(type, sourceId, x, y, z, power, toBlow, knockbackX, knockbackY, knockbackZ);
	}

	private static class ClientHandler {

		private static void handle(CustomExplosionMessage packet) {
			Minecraft minecraft = Minecraft.getInstance();
			ClientLevel level = minecraft.level;
			LocalPlayer player = minecraft.player;
			if (level == null || player == null) return;

			Entity source = null;
			if (packet.sourceId != null) {
				source = level.getEntity(packet.sourceId);
			}

			Explosion explosion = packet.type.clientFactory.create(level, source, packet.x, packet.y, packet.z, packet.radius, packet.toBlow);
			explosion.finalizeExplosion(true);
			player.setDeltaMovement(player.getDeltaMovement().add(packet.knockbackX, packet.knockbackY, packet.knockbackZ));
		}

	}

}
