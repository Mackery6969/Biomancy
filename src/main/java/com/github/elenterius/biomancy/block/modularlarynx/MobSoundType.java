package com.github.elenterius.biomancy.block.modularlarynx;

import com.github.elenterius.biomancy.mixin.accessor.LivingEntityAccessor;
import com.github.elenterius.biomancy.mixin.accessor.MobEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jspecify.annotations.Nullable;

public enum MobSoundType implements StringRepresentable {
	AMBIENT("ambient"), HURT("hurt"), DEATH("death");

	private final String name;

	MobSoundType(String name) {
		this.name = name;
	}

	public static CompoundTag saveSounds(LivingEntity livingEntity) {
		CompoundTag tag = new CompoundTag();

		LivingEntityAccessor livingEntityAccessor = (LivingEntityAccessor) livingEntity;
		DEATH.putSound(tag, livingEntityAccessor.biomancy$getDeathSound());
		HURT.putSound(tag, livingEntityAccessor.biomancy$getHurtSound(livingEntity.level().damageSources().generic()));
		AMBIENT.putSound(tag, livingEntity instanceof Mob ? ((MobEntityAccessor) livingEntity).biomancy$getAmbientSound() : null);

		return tag;
	}

	public void putSound(CompoundTag tag, @Nullable SoundEvent soundEvent) {
		if (soundEvent != null) {
			tag.putString(name, soundEvent.getLocation().toString());
		}
		else tag.remove(name);
	}

	@Nullable
	public SoundEvent getSound(CompoundTag tag) {
		ResourceLocation soundId = ResourceLocation.tryParse(tag.getString(name));
		if (soundId != null) {
			return BuiltInRegistries.SOUND_EVENT.get(soundId);
		}
		return null;
	}

	public SoundEvent getSoundFallback() {
		return switch (this) {
			case AMBIENT -> SoundEvents.PLAYER_BREATH;
			case HURT -> SoundEvents.GENERIC_HURT;
			case DEATH -> SoundEvents.GENERIC_DEATH;
		};
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}
