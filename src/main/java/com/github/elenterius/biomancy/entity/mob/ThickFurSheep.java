package com.github.elenterius.biomancy.entity.mob;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.common.ForgeMod;
import org.jspecify.annotations.Nullable;

public class ThickFurSheep extends Sheep {

	private static final EntityDataAccessor<Byte> WOOL_SIZE = SynchedEntityData.defineId(ThickFurSheep.class, EntityDataSerializers.BYTE);

	public static final byte MAX_WOOL_SIZE = 10;
	public static final String WOOL_SIZE_TAG = "wool_size";

	public ThickFurSheep(EntityType<? extends Sheep> entityType, Level level) {
		super(entityType, level);
		fixupDimensions();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(WOOL_SIZE, (byte) 1);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putByte(WOOL_SIZE_TAG, (byte) getWoolSize());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		setWoolSize(compound.getByte(WOOL_SIZE_TAG));
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		float widthFactor = 1f + ((float) getWoolSize() / MAX_WOOL_SIZE) * 0.5f;
		return super.getDimensions(pose).scale(widthFactor, 1f);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData data, @Nullable CompoundTag compound) {
		SpawnGroupData spawnData = super.finalizeSpawn(level, difficulty, spawnType, data, compound);
		setWoolSize(!isSheared() ? (byte) 1 : (byte) 0);
		return spawnData;
	}

	public int getWoolSize() {
		return entityData.get(WOOL_SIZE);
	}

	protected void setWoolSize(byte size) {
		size = (byte) Mth.clamp(size, 0, MAX_WOOL_SIZE);
		entityData.set(WOOL_SIZE, size);

		refreshDimensions();

		double pct = (double) size / MAX_WOOL_SIZE;
		getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.23d - 0.16d * pct);
		getAttribute(ForgeMod.ENTITY_GRAVITY.get()).setBaseValue(0.08d + 0.16d * pct);
		getAttribute(Attributes.ARMOR).setBaseValue(Mth.clamp(16d * pct - 1.6d, 0d, 16d));
		getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(Mth.clamp(3d * pct - 1d, 0d, 16d));
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (WOOL_SIZE.equals(key)) {
			refreshDimensions();
			//yRotO = yHeadRot;
			//yBodyRot = yHeadRot;
		}
		super.onSyncedDataUpdated(key);
	}

	@Override
	public void ate() {
		if (isBaby()) {
			if (isSheared()) setSheared(false);
			else ageUp(60);
		}
		else {
			setSheared(false);
		}
	}

	@Override
	public void setSheared(boolean sheared) {
		if (sheared) {
			int woolSize = getWoolSize() - 1;
			setWoolSize((byte) woolSize);
			if (woolSize <= 0) {
				super.setSheared(true);
			}
		}
		else {
			int woolSize = getWoolSize();
			if (woolSize < MAX_WOOL_SIZE) setWoolSize((byte) (woolSize + 1));
			super.setSheared(false);
		}
	}

	@Nullable
	@Override
	public Sheep getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		float p = (hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f) + (otherParent.hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f);
		if (p > 0 && random.nextFloat() < p) {
			return ModEntityTypes.FLESH_SHEEP.get().create(level);
		}

		return ModEntityTypes.THICK_FUR_SHEEP.get().create(level);
	}

}
