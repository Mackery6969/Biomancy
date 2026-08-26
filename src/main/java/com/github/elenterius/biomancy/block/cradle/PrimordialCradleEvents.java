package com.github.elenterius.biomancy.block.cradle;

import com.github.elenterius.biomancy.api.tribute.SacrificeHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class PrimordialCradleEvents {

	public static boolean triggerCanSpawnMob(ServerLevel level, PrimordialCradleBlockEntity cradle, List<ServerPlayer> nearbyPlayers) {
		CanSpawnMob event = new CanSpawnMob(level, cradle, nearbyPlayers);
		NeoForge.EVENT_BUS.post(event);
		return !event.isCanceled();
	}

	public static Mob triggerOnSpawnMob(ServerLevel level, PrimordialCradleBlockEntity cradle, Mob mobToSpawn, List<ServerPlayer> nearbyPlayers) {
		OnSpawnMob event = new OnSpawnMob(level, cradle, mobToSpawn, nearbyPlayers);
		NeoForge.EVENT_BUS.post(event);
		return !event.isCanceled() ? event.getMobToSpawn() : mobToSpawn;
	}

	public static class CanSpawnMob extends Event implements ICancellableEvent {

		private final ServerLevel level;
		private final PrimordialCradleBlockEntity cradle;
		private final List<ServerPlayer> nearbyPlayers;

		public CanSpawnMob(ServerLevel level, PrimordialCradleBlockEntity cradle, List<ServerPlayer> nearbyPlayers) {
			this.level = level;
			this.cradle = cradle;
			this.nearbyPlayers = nearbyPlayers;
		}

		public ServerLevel getLevel() {
			return level;
		}

		public PrimordialCradleBlockEntity getCradle() {
			return cradle;
		}

		public SacrificeHandler getSacrificeHandler() {
			return cradle.sacrificeHandler;
		}

		public void cancelAndForceAttack() {
			setCanceled(true);
			cradle.sacrificeHandler.setHostile(100);
		}

		public List<ServerPlayer> getNearbyPlayers() {
			return nearbyPlayers;
		}

	}

	public static class OnSpawnMob extends Event implements ICancellableEvent {

		private final ServerLevel level;
		private final PrimordialCradleBlockEntity cradle;
		private final List<ServerPlayer> nearbyPlayers;

		private final Mob originalMobSpawn;
		private @Nullable Mob overrideMobSpawn;

		public OnSpawnMob(ServerLevel level, PrimordialCradleBlockEntity cradle, Mob mobToSpawn, List<ServerPlayer> nearbyPlayers) {
			this.level = level;
			this.cradle = cradle;
			this.nearbyPlayers = nearbyPlayers;
			this.originalMobSpawn = mobToSpawn;
		}

		public ServerLevel getLevel() {
			return level;
		}

		public PrimordialCradleBlockEntity getCradle() {
			return cradle;
		}

		public Mob getOriginalMob() {
			return originalMobSpawn;
		}

		@Nullable
		public Mob getMobOverride() {
			return overrideMobSpawn;
		}

		public void setMobOverride(@Nullable Mob mob) {
			overrideMobSpawn = mob;
		}

		public Mob getMobToSpawn() {
			return overrideMobSpawn != null ? overrideMobSpawn : originalMobSpawn;
		}

		public List<ServerPlayer> getNearbyPlayers() {
			return nearbyPlayers;
		}

	}

}
