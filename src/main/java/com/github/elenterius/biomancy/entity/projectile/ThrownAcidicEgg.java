package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.statuseffect.StatusEffectHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownAcidicEgg extends ThrowableItemProjectile {

	public ThrownAcidicEgg(EntityType<? extends ThrownAcidicEgg> entityType, Level level) {
		super(entityType, level);
	}

	public ThrownAcidicEgg(Level level, LivingEntity thrower) {
		super(ModEntityTypes.ACIDIC_EGG_PROJECTILE.get(), thrower, level);
	}

	public ThrownAcidicEgg(Level level, double x, double y, double z) {
		super(ModEntityTypes.ACIDIC_EGG_PROJECTILE.get(), x, y, z, level);
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 3) {
			double multiplier = 0.08d;
			for (int i = 0; i < 8; i++) {
				level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, getItem()), getX(), getY(), getZ(), (random.nextFloat() - 0.5D) * multiplier, (random.nextFloat() - 0.5D) * multiplier, (random.nextFloat() - 0.5D) * multiplier);
			}
		}
	}

	@Override
	protected void onHit(HitResult hitResult) {
		super.onHit(hitResult);
		if (!level().isClientSide) {
			level().broadcastEntityEvent(this, (byte) 3);
			discard();
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult hitResult) {
		Entity entity = hitResult.getEntity();
		entity.hurt(damageSources().thrown(this, getOwner()), 0f);

		if (!level().isClientSide && entity instanceof LivingEntity livingEntity && random.nextFloat() < 0.4) {
			StatusEffectHandler.applyCorrosiveEffect(livingEntity, 2);
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult hitResult) {
		super.onHitBlock(hitResult);
		if (level() instanceof ServerLevel serverLevel && random.nextFloat() < 0.4) {
			ModBlocks.ACID_SPLATTER.get().propagateSplatters(serverLevel, getImpactPos(hitResult), 0, random);
		}
	}

	protected BlockPos getImpactPos(HitResult hitResult) {
		if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult blockHitResult) {
			return blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
		}
		return blockPosition();
	}

	@Override
	protected Item getDefaultItem() {
		return ModItems.ACIDIC_EGG.get();
	}

}
