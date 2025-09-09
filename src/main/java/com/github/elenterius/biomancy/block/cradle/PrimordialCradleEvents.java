package com.github.elenterius.biomancy.block.cradle;

import com.github.elenterius.biomancy.api.tribute.SacrificeHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

public final class PrimordialCradleEvents {

	public static boolean triggerCanSpawnMob(ServerLevel level, PrimordialCradleBlockEntity cradle) {
		CanSpawnMob event = new CanSpawnMob(level, cradle);
		return !MinecraftForge.EVENT_BUS.post(event);
	}

	public static @Nullable Mob triggerSpawnCustomMob(ServerLevel level, PrimordialCradleBlockEntity cradle) {
		SpawnCustomMob event = new SpawnCustomMob(level, cradle);
		boolean cancelled = MinecraftForge.EVENT_BUS.post(event);
		return !cancelled ? event.getCustomMob() : null;
	}

	@Cancelable
	public static class CanSpawnMob extends Event {

		private final ServerLevel level;
		private final PrimordialCradleBlockEntity cradle;

		public CanSpawnMob(ServerLevel level, PrimordialCradleBlockEntity cradle) {
			this.level = level;
			this.cradle = cradle;
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

	}

	@Cancelable
	public static class SpawnCustomMob extends Event {

		private final ServerLevel level;
		private final PrimordialCradleBlockEntity cradle;
		private @Nullable Mob customMob;

		public SpawnCustomMob(ServerLevel level, PrimordialCradleBlockEntity cradle) {
			this.level = level;
			this.cradle = cradle;
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

		@Nullable
		public Mob getCustomMob() {
			return customMob;
		}

		public void setCustomMob(Mob mob) {
			customMob = mob;
		}

	}

}
