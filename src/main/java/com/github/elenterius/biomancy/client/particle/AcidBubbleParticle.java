package com.github.elenterius.biomancy.client.particle;

import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
class AcidBubbleParticle extends TextureSheetParticle {

	protected AcidBubbleParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(level, x, y, z);
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
			level.addParticle(ModParticleTypes.ACID_BUBBLE_POP.get(), x, y, z, xd, yd, zd);
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

}
