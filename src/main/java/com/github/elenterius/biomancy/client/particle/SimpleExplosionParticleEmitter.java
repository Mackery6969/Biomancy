package com.github.elenterius.biomancy.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;

public class SimpleExplosionParticleEmitter extends NoRenderParticle {

	private final double range;

	private int life;
	private final int lifeTime = 8;
	private final SimpleParticleType explosionParticle;

	SimpleExplosionParticleEmitter(ClientLevel level, double x, double y, double z, double range, SimpleParticleType explosionParticle) {
		super(level, x, y, z, 0, 0, 0);
		this.range = range;
		this.explosionParticle = explosionParticle;
	}

	public void tick() {
		for (int i = 0; i < range + 2; i++) {
			double px = x + (random.nextDouble() - random.nextDouble()) * range;
			double py = y + random.nextDouble() * range;
			double pz = z + (random.nextDouble() - random.nextDouble()) * range;
			level.addParticle(explosionParticle, px, py, pz, (float) life / (float) lifeTime, 0d, 0d);
		}

		if (++life == lifeTime) {
			remove();
		}
	}

	public record Provider(SimpleParticleType explosionParticle) implements ParticleProvider<SimpleParticleType> {
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new SimpleExplosionParticleEmitter(level, x, y, z, xSpeed, explosionParticle);
		}
	}

}