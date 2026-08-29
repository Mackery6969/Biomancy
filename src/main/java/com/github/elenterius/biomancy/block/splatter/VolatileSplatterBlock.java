package com.github.elenterius.biomancy.block.splatter;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class VolatileSplatterBlock extends SplatterBlock {

	public static final MapCodec<VolatileSplatterBlock> CODEC = simpleCodec(VolatileSplatterBlock::new);

	public VolatileSplatterBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends VolatileSplatterBlock> codec() {
		return CODEC;
	}

	@Override
	protected void entityInsideBoundingBox(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (!level.isClientSide && entity instanceof LivingEntity livingEntity) {
			livingEntity.addEffect(new MobEffectInstance(ModMobEffects.VOLATILE, (60 + 30) * 20));
			if (livingEntity.tickCount % 20 == 0) {
				reduceSplatter(state, level, pos, level.random);
			}
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(5) != 0) return;

		List<Direction> availableFaces = new ArrayList<>();
		for (Direction direction : Direction.values()) {
			if (direction != Direction.UP && hasFace(state, direction)) {
				availableFaces.add(direction);
			}
		}

		if (!availableFaces.isEmpty()) {
			int index = availableFaces.size() == 1 ? 0 : random.nextIntBetweenInclusive(0, availableFaces.size() - 1);
			Direction face = availableFaces.get(index);
			Vec3i normal = face.getNormal();

			double x = pos.getX() + 0.5d;
			double y = pos.getY() + 0.5d;
			double z = pos.getZ() + 0.5d;

			double u = (random.nextDouble() - random.nextDouble()) * ((normal.getX() * normal.getX() - 1d) * -1d);
			double v = (random.nextDouble() - random.nextDouble()) * ((normal.getY() * normal.getY() - 1d) * -1d);
			double w = (random.nextDouble() - random.nextDouble()) * ((normal.getZ() * normal.getZ() - 1d) * -1d);

			level.addParticle(
					ModParticleTypes.VOLATILE_BUBBLE.get(),
					x + normal.getX() * 0.45d + u * 0.5d,
					y + normal.getY() * 0.45d + v * 0.5d,
					z + normal.getZ() * 0.45d + w * 0.5d,
					0d, 0.025d, 0d
			);
		}
	}

}
