package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.entity.projectile.*;
import com.github.elenterius.biomancy.item.weapon.gun.Gun;
import com.github.elenterius.biomancy.util.function.FloatOperator;
import com.github.elenterius.biomancy.util.function.IntOperator;
import com.github.elenterius.biomancy.util.sounds.SoundUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ModProjectiles {

	public static final List<ConfiguredProjectile<? extends BaseProjectile>> PRECONFIGURED_PROJECTILES = new ArrayList<>();
	public static final ConfiguredProjectile<ToothProjectile> TOOTH = create("Sharp Tooth", 1.75f, 5f, 0, 0.92f, ToothProjectile::new);
	public static final ConfiguredProjectile<ImpalerProjectile> IMPALER_PROJECTILE = create("Impaler Projectile", 3f, 20f, 0, 0.99f, ModSoundEvents.IMPALER_SHOOT.get(), ImpalerProjectile::new);
	public static final ConfiguredProjectile<AcidBlobProjectile> ACID_BLOB = create("Acid Blob", 1.5f, 2, 0, 0.9f, ModSoundEvents.ACID_BLOB_SHOOT.get(), (level, x, y, z) -> new AcidBlobProjectile(level, x, y, z, false));
	public static final ConfiguredProjectile<AcidBlobProjectile> FALLING_ACID_BLOB = create("Falling Acid Blob", 0.1f, 2, 0, 0.9f, ModSoundEvents.ACID_BLOB_FALL.get(), AcidBlobProjectile::new);
	public static final ConfiguredProjectile<AcidSpitProjectile> GASTRIC_SPIT = create("Gastric Acid Spit", 1.5f, 1, 0, 0.25f, ModSoundEvents.ACID_SPIT.get(), AcidSpitProjectile::new);
	public static final ConfiguredProjectile<BloomberryProjectile> BLOOMBERRY = create("Bloomberry", 1.25f, 2, 0, 0.9f, ModSoundEvents.BLOOMBERRY_SHOOT.get(), BloomberryProjectile::new);

	private static float convertToInaccuracy(float accuracy) {
		return -Gun.MAX_INACCURACY * accuracy + Gun.MAX_INACCURACY;
	}

	private static <T extends BaseProjectile> ConfiguredProjectile<T> create(String name, float velocity, float damage, int knockback, float accuracy, ProjectileFactory<T> factory) {
		if (accuracy < 0f || accuracy > 1f) throw new IllegalArgumentException("accuracy of " + accuracy + "is not within valid bounds of 0..1");
		ConfiguredProjectile<T> configuredProjectile = new ConfiguredProjectile<>(name, velocity, damage, knockback, convertToInaccuracy(accuracy), SoundEvents.CROSSBOW_SHOOT, factory);
		PRECONFIGURED_PROJECTILES.add(configuredProjectile);
		return configuredProjectile;
	}

	private static <T extends BaseProjectile> ConfiguredProjectile<T> create(String name, float velocity, float damage, int knockback, float accuracy, SoundEvent shootSound, ProjectileFactory<T> factory) {
		if (accuracy < 0f || accuracy > 1f) throw new IllegalArgumentException("accuracy of " + accuracy + "is not within valid bounds of 0..1");
		ConfiguredProjectile<T> configuredProjectile = new ConfiguredProjectile<>(name, velocity, damage, knockback, convertToInaccuracy(accuracy), shootSound, factory);
		PRECONFIGURED_PROJECTILES.add(configuredProjectile);
		return configuredProjectile;
	}

	public static <T extends BaseProjectile> boolean shootProjectile(Level level, LivingEntity shooter, float velocity, float damage, int knockback, float inaccuracy, ProjectileFactory<T> factory, Consumer<T> modify) {
		T projectile = factory.create(level, shooter.getX(), shooter.getEyeY() - 0.1f, shooter.getZ());
		projectile.setOwner(shooter);

		projectile.setDamage(damage);
		if (knockback > 0) {
			projectile.setKnockback((byte) knockback);
		}

		modify.accept(projectile);

		Vec3 direction = shooter.getLookAngle();
		projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, inaccuracy);

		return level.addFreshEntity(projectile);
	}

	public static <T extends BaseProjectile> boolean shootProjectile(Level level, @Nullable LivingEntity shooter, Vec3 origin, Vec3 target, float velocity, float damage, int knockback, float inaccuracy, ProjectileFactory<T> factory, Consumer<T> modify) {
		T projectile = factory.create(level, origin.x, origin.y, origin.z);
		projectile.setOwner(shooter);

		projectile.setDamage(damage);
		if (knockback > 0) {
			projectile.setKnockback((byte) knockback);
		}

		modify.accept(projectile);

		Vec3 direction = target.subtract(origin).normalize();
		projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, inaccuracy);

		return level.addFreshEntity(projectile);
	}

	public interface ProjectileFactory<T extends BaseProjectile> {
		T create(Level level, double x, double v, double z);
	}

	public record ConfiguredProjectile<T extends BaseProjectile>(String name, float velocity, float damage, int knockback, float inaccuracy, SoundEvent shootSound, ProjectileFactory<T> factory) {

		public boolean shoot(Level level, Vec3 origin, Vec3 target) {
			return shootProjectile(level, null, origin, target, velocity, damage, knockback, inaccuracy, factory, projectile -> {});
		}

		public boolean shoot(Level level, Vec3 origin, Vec3 target, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator inaccuracyModifier) {
			return shootProjectile(level, null, origin, target, velocityModifier.apply(velocity), damageModifier.apply(damage), knockbackModifier.apply(knockback), inaccuracyModifier.apply(inaccuracy), factory, projectile -> {});
		}

		public boolean shoot(Level level, LivingEntity shooter, Vec3 origin, Vec3 target) {
			return shootProjectile(level, shooter, origin, target, velocity, damage, knockback, inaccuracy, factory, projectile -> {});
		}

		public boolean shoot(Level level, LivingEntity shooter) {
			return shootProjectile(level, shooter, velocity, damage, knockback, inaccuracy, factory, projectile -> {});
		}

		public boolean shoot(Level level, LivingEntity shooter, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator inaccuracyModifier) {
			return shootProjectile(level, shooter, velocityModifier.apply(velocity), damageModifier.apply(damage), knockbackModifier.apply(knockback), inaccuracyModifier.apply(inaccuracy), factory, projectile -> {});
		}

		public boolean shoot(Level level, LivingEntity shooter, FloatOperator velocityModifier, FloatOperator damageModifier, IntOperator knockbackModifier, FloatOperator inaccuracyModifier, Consumer<T> modify) {
			return shootProjectile(level, shooter, velocityModifier.apply(velocity), damageModifier.apply(damage), knockbackModifier.apply(knockback), inaccuracyModifier.apply(inaccuracy), factory, modify);
		}

		public void playShootSound(Level level, Vec3 origin, SoundSource soundSource) {
			playShootSound(level, origin, soundSource, 0.8f, 0.4f);
		}

		public void playShootSound(Level level, Vec3 origin, SoundSource soundSource, float volume, float pitch) {
			level.playSound(null, origin.x, origin.y, origin.z, shootSound, soundSource, volume, pitch);
		}

		public void playShootSound(Level level, LivingEntity shooter) {
			playShootSound(level, shooter, 0.8f, 0.4f);
		}

		public void playShootSound(Level level, LivingEntity shooter, float volume, float pitch) {
			playShootSound(level, shooter, SoundUtil.soundSourceFor(shooter), volume, pitch);
		}

		public void playShootSound(Level level, LivingEntity shooter, SoundSource soundSource, float volume, float pitch) {
			level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), shootSound, soundSource, volume, pitch);
		}

		public float accuracy() {
			return -Gun.MAX_INACCURACY * inaccuracy + Gun.MAX_INACCURACY;
		}

	}

}
