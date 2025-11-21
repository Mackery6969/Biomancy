package com.github.elenterius.biomancy.integration.kubejs;

import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.serum.BasicSerum;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class SerumBuilder extends BuilderBase<Serum> {

	public transient int argbColor = id.hashCode() & 0xffffff;

	public transient CanAffectTargetCallback canAffectTarget = DefaultCallbacks.CAN_AFFECT_ENTITY;
	public transient AffectTargetCallback affectTarget = DefaultCallbacks.AFFECT_ENTITY;
	public transient CanAffectUserCallback canAffectUser = DefaultCallbacks.CAN_AFFECT_USER;
	public transient AffectUserCallback affectUser = DefaultCallbacks.AFFECT_USER;

	protected SerumBuilder(ResourceLocation id) {
		super(id);
	}

	@Override
	public RegistryInfo<Serum> getRegistryType() {
		return BiomancyKubeJSPlugin.SERUM_REGISTRY;
	}

	public SerumBuilder color(int argbColor) {
		this.argbColor = argbColor;
		return this;
	}

	@Info(
			value = "Determines if the target LivingEntity can be injected with the serum.",
			params = {@Param(name = "canAffectTarget")}
	)
	public SerumBuilder canAffectEntity(CanAffectTargetCallback canAffectTarget) {
		this.canAffectTarget = canAffectTarget;
		return this;
	}

	@Info(
			value = "Inject the target LivingEntity with the serum.",
			params = {@Param(name = "affectTarget")}
	)
	public SerumBuilder affectEntity(AffectTargetCallback affectTarget) {
		this.affectTarget = affectTarget;
		return this;
	}

	@Info(
			value = "Determines if the player can inject themself with the serum.",
			params = {@Param(name = "canAffectUser")}
	)
	public SerumBuilder canAffectPlayerSelf(CanAffectUserCallback canAffectUser) {
		this.canAffectUser = canAffectUser;
		return this;
	}

	@Info(
			value = "Inject the player self with the serum.",
			params = {@Param(name = "affectUser")}
	)
	public SerumBuilder affectPlayerSelf(AffectUserCallback affectUser) {
		this.affectUser = affectUser;
		return this;
	}

	@Override
	public Serum createObject() {
		return new BasicSerum(argbColor) {
			@Override
			public boolean canAffectEntity(CompoundTag serumData, @Nullable LivingEntity source, LivingEntity target) {
				return canAffectTarget.canAffect(target.level(), serumData, source, target);
			}

			@Override
			public void affectEntity(ServerLevel serverLevel, CompoundTag serumData, @Nullable LivingEntity source, LivingEntity target) {
				affectTarget.affect(serverLevel, serumData, source, target);
			}

			@Override
			public boolean canAffectPlayerSelf(CompoundTag serumData, Player targetSelf) {
				return canAffectUser.canAffect(targetSelf.level(), serumData, targetSelf);
			}

			@Override
			public void affectPlayerSelf(ServerLevel serverLevel, CompoundTag serumData, ServerPlayer targetSelf) {
				affectUser.affect(serverLevel, serumData, targetSelf);
			}
		};
	}

	@FunctionalInterface
	public interface CanAffectTargetCallback {

		@Info(
				value = "Check if the target LivingEntity can be injected.\nCalled on client and server side.",
				params = {
						@Param(name = "level", value = "Client sided or server sided level"),
						@Param(name = "serumData", value = "CompoundTag stored under the tag key `biomancy:serum_data` on the Serum ItemStack"),
						@Param(name = "source", value = "Nullable source of the injection.\nWhen the source is not null it is the LivingEntity with a injector.\nWhen the source is null it is a dispenser or something similar."),
						@Param(name = "target", value = "The LivingEntity to be injected.")
				}
		)
		boolean canAffect(Level level, CompoundTag serumData, @Nullable LivingEntity source, LivingEntity target);

	}

	@FunctionalInterface
	public interface AffectTargetCallback {

		@Info(
				value = "Called when the target LivingEntity was successfully injected.\nServer side only.",
				params = {
						@Param(name = "serverLevel"),
						@Param(name = "serumData", value = "CompoundTag stored under the tag key `biomancy:serum_data` on the Serum ItemStack"),
						@Param(name = "source", value = "Nullable source of the injection.\nWhen the source is not null it is the LivingEntity with a injector.\nWhen the source is null it is a dispenser or something similar."),
						@Param(name = "target", value = "The LivingEntity that was injected.")
				}
		)
		void affect(ServerLevel level, CompoundTag serumData, @Nullable LivingEntity source, LivingEntity target);

	}

	@FunctionalInterface
	public interface CanAffectUserCallback {

		@Info(
				value = "Check if the Player can inject themself.\nCalled on client and server side.",
				params = {
						@Param(name = "level"),
						@Param(name = "serumData", value = "CompoundTag stored under the tag key `biomancy:serum_data` on the Serum ItemStack"),
						@Param(name = "user", value = "The Player that wants to inject themself.")
				}
		)
		boolean canAffect(Level level, CompoundTag serumData, Player user);

	}

	@FunctionalInterface
	public interface AffectUserCallback {

		@Info(
				value = "Called when the Player successfully injected themself.\nServer side only.",
				params = {
						@Param(name = "serverLevel"),
						@Param(name = "serumData", value = "CompoundTag stored under the tag key `biomancy:serum_data` on the Serum ItemStack"),
						@Param(name = "user", value = "The Player that is injecting themself.")
				}
		)
		void affect(ServerLevel serverLevel, CompoundTag serumData, Player user);

	}

	private interface DefaultCallbacks {
		CanAffectTargetCallback CAN_AFFECT_ENTITY = (level, serumData, source, target) -> true;
		AffectTargetCallback AFFECT_ENTITY = (serverLevel, serumData, source, target) -> {};
		CanAffectUserCallback CAN_AFFECT_USER = (level, serumData, targetSelf) -> true;
		AffectUserCallback AFFECT_USER = (serverLevel, serumData, targetSelf) -> {};
	}

}
