package com.github.elenterius.biomancy.integration.kubejs;

import com.github.elenterius.biomancy.api.tribute.SacrificeHandler;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleEvents;
import dev.latvian.mods.kubejs.event.*;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.Context;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import org.jspecify.annotations.Nullable;

import java.util.List;

final class BiomancyKJSEvents {

	static final EventGroup GROUP = EventGroup.of("BiomancyEvents");
	static final EventHandler CAN_SPAWN_MOB = GROUP.server("canCradleSpawnMob", () -> CanCradleSpawnMobEventKJS.class);
	static final EventHandler ON_SPAWN_MOB = GROUP.server("onCradleSpawnMob", () -> OnCradleSpawnMobEventKJS.class);

	static void canCradleSpawnMob(PrimordialCradleEvents.CanSpawnMob forgeEvent) {
		if (!CAN_SPAWN_MOB.hasListeners()) return;

		EventResult eventResult = CAN_SPAWN_MOB.post(new CanCradleSpawnMobEventKJS(forgeEvent));
		if (eventResult.interruptFalse()) {
			forgeEvent.setCanceled(true);
		}
	}

	static void onCradleSpawnMob(PrimordialCradleEvents.OnSpawnMob forgeEvent) {
		if (!ON_SPAWN_MOB.hasListeners()) return;

		EventResult eventResult = ON_SPAWN_MOB.post(new OnCradleSpawnMobEventKJS(forgeEvent));
		if (eventResult.interruptFalse()) {
			forgeEvent.setCanceled(true);
		}
	}

	@Info("""
			Allows you to determine if any mob should be spawned or not.
			Canceling this event will lead to the cradle either doing nothing or attacking anyone nearby.""")
	static class CanCradleSpawnMobEventKJS implements KubeEvent {
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

		@Info("All players that were present in a 8 block radius")
		public List<ServerPlayer> getNearbyPlayers() {
			return forgeEvent.getNearbyPlayers();
		}

		@Info("""
				Provides info about values like success, hostility, anomaly, etc.
				
				You may modify these values if you know what you are doing.
				
				If you want to prevent the spawn of any mob regardless of the success chance you must cancel this event instead.""")
		public SacrificeHandler getInternalValues() {
			return forgeEvent.getSacrificeHandler();
		}

		@Info("Get the probability that a mob will spawn")
		public float getSuccessChance() {
			return forgeEvent.getSacrificeHandler().getSuccessChance();
		}

		@Info(
				value = "Set the probability that a mob will spawn",
				params = @Param(name = "probability", value = "floating point number")
		)
		public void setSuccessChance(float probability) {
			forgeEvent.getSacrificeHandler().setSuccess(Math.round(probability * 100));
		}

		@Info("Get the probability that a hostile flesh blob will spawn")
		public float getHostileChance() {
			return forgeEvent.getSacrificeHandler().getHostileChance();
		}

		@Info(
				value = "Set the probability that a hostile flesh blob will spawn",
				params = @Param(name = "probability", value = "floating point number")
		)
		public void setHostileChance(float probability) {
			forgeEvent.getSacrificeHandler().setHostile(Math.round(probability * 100));
		}

		@Info("Get the probability that a primordial flesh blob will spawn")
		public float getAnomalyChance() {
			return forgeEvent.getSacrificeHandler().getAnomalyChance();
		}

		@Info(
				value = "Set the probability that a primordial flesh blob will spawn",
				params = @Param(name = "probability", value = "floating point number")
		)
		public void setAnomalyChance(float probability) {
			forgeEvent.getSacrificeHandler().setAnomaly(Math.round(probability * 100));
		}

		@Info("Get the probability of how many tumors a flesh blob will have")
		public float getDiseaseChance() {
			return forgeEvent.getSacrificeHandler().getTumorFactor();
		}

		@Info(
				value = "Set the probability of how many tumors a flesh blob will have",
				params = @Param(name = "probability", value = "floating point number")
		)
		public void setDiseaseChance(float probability) {
			forgeEvent.getSacrificeHandler().setDisease(Math.round(probability * 100));
		}

		@Info("Cancel the event and force the cradle to attack")
		public void cancelAndForceAttack(Context cx) throws EventExit {
			forgeEvent.getSacrificeHandler().setHostile(100);
			cancel(cx);
		}

	}

	@Info("""
			Allows you to override with [#setMobOverride] what mob the cradle spawns.
			If you don't set the override the cradle will spawn a Flesh Blob according to its own logic.
			
			Placement and rotation of the mob is handled by the cradle.
			
			Canceling this event won't stop the cradle from spawning a Flesh Blob. Use the CanCradleSpawnMob event for that.""")
	static class OnCradleSpawnMobEventKJS implements KubeEvent {

		private final PrimordialCradleEvents.OnSpawnMob forgeEvent;

		public OnCradleSpawnMobEventKJS(PrimordialCradleEvents.OnSpawnMob forgeEvent) {
			this.forgeEvent = forgeEvent;
		}

		public ServerLevel getLevel() {
			return forgeEvent.getLevel();
		}

		public PrimordialCradleBlockEntity getCradle() {
			return forgeEvent.getCradle();
		}

		@Info("All players that were present in a 8 block radius")
		public List<ServerPlayer> getNearbyPlayers() {
			return forgeEvent.getNearbyPlayers();
		}

		@Info("Get the original mob the cradle wanted to spawn")
		public Mob getOriginalMob() {
			return forgeEvent.getOriginalMob();
		}

		@Info("Get the mob spawn override if there is any. Nullable.")
		public @Nullable Mob getMobOverride() {
			return forgeEvent.getMobOverride();
		}

		@Info(
				value = """
						Set the mob that should be spawned instead of an flesh blob. Positioning and rotation of the mob is handled by the cradle.
						
						WARNING! Do not add the mob to the level yourself!""",
				params = {
						@Param(name = "mob", value = "Override what mob to spawn. Setting this value to null only clears the override and won't stop the cradle form spawning flesh blobs.")
				}
		)
		public void setMobOverride(@Nullable Mob mob) {
			forgeEvent.setMobOverride(mob);
		}

		@Info("Get the probability that a mob will spawn")
		public float getSuccessChance() {
			return forgeEvent.getCradle().getSuccessChance();
		}

		@Info("Get the probability that a hostile flesh blob will spawn")
		public float getHostileChance() {
			return forgeEvent.getCradle().getHostileChance();
		}

		@Info("Get the probability that a primordial flesh blob will spawn")
		public float getAnomalyChance() {
			return forgeEvent.getCradle().getAnomalyChance();
		}

		@Info("Get the probability of how many tumors a flesh blob will have")
		public float getDiseaseChance() {
			return forgeEvent.getCradle().getDiseaseChance();
		}

	}

}
