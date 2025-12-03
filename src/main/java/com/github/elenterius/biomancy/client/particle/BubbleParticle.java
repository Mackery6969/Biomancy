package com.github.elenterius.biomancy.client.particle;

import com.github.elenterius.biomancy.init.ModFluids;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BubbleParticle extends TextureSheetParticle {

	private final SimpleParticleType popParticle;

	protected BubbleParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SimpleParticleType popParticle) {
		super(level, x, y, z);
		this.popParticle = popParticle;

		setSize(0.02f, 0.02f);
		quadSize *= random.nextFloat() * 0.6f + 0.2f;

		xd = xSpeed * 0.2d + (Math.random() * 2d - 1d) * 0.02d;
		yd = ySpeed * 0.2d + (Math.random() * 2d - 1d) * 0.02d;
		zd = zSpeed * 0.2d + (Math.random() * 2d - 1d) * 0.02d;
		lifetime = (int) (200d / (Math.random() * 0.8d + 0.2d));
		friction = 0.85f;
	}

	public void tick() {
		xo = x;
		yo = y;
		zo = z;

		if (lifetime-- <= 0) {
			remove();
			level.addParticle(popParticle, x, y, z, xd, yd, zd);
		}
		else {
			yd += 0.002D;
			move(xd, yd, zd);

			BlockPos pos = BlockPos.containing(x, y, z);
			FluidState fluidState = level.getFluidState(pos);

			boolean insideAcid = fluidState.is(ModFluids.ACID.get()) && y <= pos.getY() + fluidState.getHeight(level, pos);

			if (!insideAcid) {
				lifetime = Math.min(lifetime, 8);
				yd -= 0.002D;
			}

			xd *= friction;
			yd *= friction;
			zd *= friction;
		}
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public record AcidProvider(SpriteSet sprite, SimpleParticleType popParticle) implements ParticleProvider<SimpleParticleType> {
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			BubbleParticle particle = new BubbleParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, popParticle);
			particle.pickSprite(sprite);
			return particle;
		}
	}

	public record VolatileProvider(SpriteSet sprite, SimpleParticleType popParticle) implements ParticleProvider<SimpleParticleType> {
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			BubbleParticle particle = new BubbleParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, popParticle);
			particle.pickSprite(sprite);
			particle.quadSize *= particle.random.nextFloat() * 0.8f + 0.4f;
			return particle;
		}
	}

}
