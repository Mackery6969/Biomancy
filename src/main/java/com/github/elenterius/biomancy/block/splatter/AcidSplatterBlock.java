package com.github.elenterius.biomancy.block.splatter;

import com.github.elenterius.biomancy.init.AcidInteractions;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

public class AcidSplatterBlock extends SplatterBlock {

	public static final MapCodec<AcidSplatterBlock> CODEC = simpleCodec(AcidSplatterBlock::new);

	public AcidSplatterBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends AcidSplatterBlock> codec() {
		return CODEC;
	}

	@Override
	protected boolean reduceSplatter(BlockState state, Level level, BlockPos pos, RandomSource random) {
		return reduceSplatter(state, level, pos, random, true);
	}

	protected boolean reduceSplatter(BlockState state, Level level, BlockPos pos, RandomSource random, boolean affectNeighbor) {
		int age = AGE.getValue(state);

		if (age < AGE.getMax()) {
			level.setBlock(pos, AGE.setValue(state, age + 1), Block.UPDATE_CLIENTS);
			if (affectNeighbor) {
				affectNeighborBlock(state, level, pos, random);
			}

			return true;
		}

		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
		if (affectNeighbor) {
			affectNeighborBlock(state, level, pos, random);
		}

		return false;
	}

	protected void affectNeighborBlock(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(10) != 0) return;

		int visitedDirections = 0;
		for (int i = 0; i < DIRECTIONS.length; i++) {
			int directionIndex;
			do {
				directionIndex = random.nextInt(DIRECTIONS.length);
			} while ((visitedDirections & (1 << directionIndex)) != 0);
			visitedDirections |= 1 << directionIndex;

			Direction direction = DIRECTIONS[directionIndex];
			if (hasFace(state, direction)) {
				BlockPos neighborPos = pos.relative(direction);
				BlockState neighborState = level.getBlockState(neighborPos);
				Block neighborBlock = neighborState.getBlock();
				if (corrodeCopper(level, neighborPos, neighborBlock, neighborState) || erodeBlock(level, neighborPos, neighborBlock, neighborState)) {
					break;
				}
			}
		}
	}

	protected boolean corrodeCopper(Level level, BlockPos pos, Block block, BlockState blockState) {
		if (block instanceof WeatheringCopper weatheringCopper && WeatheringCopper.getNext(block).isPresent()) {
			weatheringCopper.getNext(blockState).ifPresent(state -> level.setBlockAndUpdate(pos, state));
			level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
			return true;
		}

		return false;
	}

	protected boolean erodeBlock(Level level, BlockPos pos, Block block, BlockState blockState) {
		if (!AcidInteractions.NORMAL_TO_ERODED_BLOCK_CONVERSION.containsKey(block)) return false;

		SoundType soundType = block.getSoundType(blockState, level, pos, null);
		level.setBlockAndUpdate(pos, AcidInteractions.NORMAL_TO_ERODED_BLOCK_CONVERSION.get(block));
		level.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, soundType.volume, soundType.pitch);
		level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
		return true;
	}

	@Override
	protected void entityInsideBoundingBox(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			AcidInteractions.handleEntityInsideAcid(livingEntity);
			if (livingEntity.tickCount % 20 == 0) {
				reduceSplatter(state, level, pos, level.random, false);
			}
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(5) != 0) return;

		Direction face = getRandomFaceExceptUp(state, random);
		if (face != null) {
			Vec3i normal = face.getNormal();

			double x = pos.getX() + 0.5d;
			double y = pos.getY() + 0.5d;
			double z = pos.getZ() + 0.5d;

			double u = (random.nextDouble() - random.nextDouble()) * ((normal.getX() * normal.getX() - 1d) * -1d);
			double v = (random.nextDouble() - random.nextDouble()) * ((normal.getY() * normal.getY() - 1d) * -1d);
			double w = (random.nextDouble() - random.nextDouble()) * ((normal.getZ() * normal.getZ() - 1d) * -1d);

			if (random.nextBoolean()) {
				level.addParticle(
						ParticleTypes.SMOKE,
						x + normal.getX() * 0.45d + u * 0.5d,
						y + normal.getY() * 0.45d + v * 0.5d,
						z + normal.getZ() * 0.45d + w * 0.5d,
						0d, 0d, 0d
				);
				if (random.nextFloat() < 0.4f) {
					level.playLocalSound(x, y, z, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
				}
			}
			else level.addParticle(
					ModParticleTypes.ACID_BUBBLE.get(),
					x + normal.getX() * 0.45d + u * 0.5d,
					y + normal.getY() * 0.45d + v * 0.5d,
					z + normal.getZ() * 0.45d + w * 0.5d,
					0d, 0.025d, 0d
			);
		}
	}

}
