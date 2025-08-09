package com.github.elenterius.biomancy.client.particle;

import com.github.elenterius.biomancy.init.ModParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;

public class GasExplosionParticleEmitter extends NoRenderParticle {

	private final double range;

	private int life;
	private final int lifeTime = 8;

	GasExplosionParticleEmitter(ClientLevel level, double x, double y, double z, double range) {
		super(level, x, y, z, 0, 0, 0);
		this.range = range;
	}

	public void tick() {
		for (int i = 0; i < range + 2; i++) {
			double px = x + (random.nextDouble() - random.nextDouble()) * range;
			double py = y + random.nextDouble() * range;
			double pz = z + (random.nextDouble() - random.nextDouble()) * range;
			level.addParticle(ModParticleTypes.TOXIN_GAS_EXPLOSION.get(), px, py, pz, (float) life / (float) lifeTime, 0d, 0d);
		}

		if (++life == lifeTime) {
			remove();
		}
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new GasExplosionParticleEmitter(level, x, y, z, xSpeed);
		}
	}

}