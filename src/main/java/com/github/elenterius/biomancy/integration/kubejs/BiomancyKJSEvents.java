package com.github.elenterius.biomancy.integration.kubejs;

import com.github.elenterius.biomancy.api.tribute.SacrificeHandler;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleEvents;
import dev.latvian.mods.kubejs.event.*;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

final class BiomancyKJSEvents {

	static final EventGroup GROUP = EventGroup.of("BiomancyEvents");
	static final EventHandler CAN_SPAWN_MOB = GROUP.server("canCradleSpawnMob", () -> CanCradleSpawnMobEventKJS.class);
	static final EventHandler ON_SPAWN_CUSTOM_MOB = GROUP.server("onCradleSpawnCustomMob", () -> OnCradleSpawnCustomMobEventKJS.class);

	static void canCradleSpawnMob(PrimordialCradleEvents.CanSpawnMob forgeEvent) {
		if (!CAN_SPAWN_MOB.hasListeners()) return;

		EventResult eventResult = CAN_SPAWN_MOB.post(new CanCradleSpawnMobEventKJS(forgeEvent));
		if (eventResult.interruptFalse()) {
			forgeEvent.setCanceled(true);
		}
	}

	static void onCradleSpawnCustomMob(PrimordialCradleEvents.SpawnCustomMob forgeEvent) {
		if (!ON_SPAWN_CUSTOM_MOB.hasListeners()) return;

		EventResult eventResult = ON_SPAWN_CUSTOM_MOB.post(new OnCradleSpawnCustomMobEventKJS(forgeEvent));
		if (eventResult.interruptFalse()) {
			forgeEvent.setCanceled(true);
		}
	}

	@Info("""
			Allows you to determine if any mob should be spawned or not.
			Canceling this event will lead to the cradle either doing nothing or attacking anyone nearby.""")
	static class CanCradleSpawnMobEventKJS extends EventJS {
		private final PrimordialCradleEvents.CanSpawnMob forgeEvent;

		public CanCradleSpawnMobEventKJS(PrimordialCradleEvents.CanSpawnMob forgeEvent) {
			this.forgeEvent = forgeEvent;
		}

		public ServerLevel getLevel() {
			return forgeEvent.getLevel();
		}

		public PrimordialCradleBlockEntity getCradle() {
			return forgeEvent.getCradle();
		}

		@Info("""
				Provides info about values like success, hostility, anomaly, etc.
				
				You may edit these values.
				But if you want to prevent the spawn of any mob you must cancel this event instead.""")
		public SacrificeHandler getSacrificeHandler() {
			return forgeEvent.getSacrificeHandler();
		}

		@Info("Cancel the event and force the cradle to attack")
		public void cancelAndForceAttack() throws EventExit {
			forgeEvent.getSacrificeHandler().setHostile(100);
			cancel();
		}

	}

	@Info("""
			Allows you to provide your own mob that should be spawned instead of an flesh blob.
			Placement and rotation of the mob is handled by the cradle.
			
			The event is cancelable.""")
	static class OnCradleSpawnCustomMobEventKJS extends EventJS {
		private final PrimordialCradleEvents.SpawnCustomMob forgeEvent;

		public OnCradleSpawnCustomMobEventKJS(PrimordialCradleEvents.SpawnCustomMob forgeEvent) {
			this.forgeEvent = forgeEvent;
		}

		public ServerLevel getLevel() {
			return forgeEvent.getLevel();
		}

		public PrimordialCradleBlockEntity getCradle() {
			return forgeEvent.getCradle();
		}

		@Info("Provides info about values like success, hostility, anomaly, etc.")
		public SacrificeHandler getSacrificeHandler() {
			return forgeEvent.getSacrificeHandler();
		}

		@Nullable
		public Mob getCustomMob() {
			return forgeEvent.getCustomMob();
		}

		@Info(
				value = "Set the mob that should be spawned instead of an flesh blob. Positioning and rotation of the mob is handled by the cradle.\n\nWARNING! Do not add the mob to the level yourself!",
				params = {@Param("mob")}
		)
		public void setCustomMob(Mob mob) {
			forgeEvent.setCustomMob(mob);
		}

	}

}
