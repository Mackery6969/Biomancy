package com.github.elenterius.biomancy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/// We don't schedule andy fluid ticks on purpose to avoid water spreading everywhere
public class WaterGelBlock extends HalfTransparentBlock {

	private static final FluidState WATER_SOURCE = Fluids.WATER.getSource(false);

	protected static final VoxelShape SHAPE = Block.box(1, 1, 1, 15, 15, 15);

	public WaterGelBlock(Properties properties) {
		super(properties);
	}

	public static boolean canEntityWalkOnWaterGel(Entity entity) {
		return entity instanceof LivingEntity livingEntity && livingEntity.getItemBySlot(EquipmentSlot.FEET).canWalkOnPowderedSnow(livingEntity);
	}

	@Override
	public final FluidState getFluidState(BlockState state) {
		return getFluidState();
	}

	public FluidState getFluidState() {
		return WATER_SOURCE;
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		playerWillDestroy(level, pos, state, player);
		return level.setBlock(pos, Fluids.EMPTY.defaultFluidState().createLegacyBlock(), level.isClientSide ? Block.UPDATE_ALL_IMMEDIATE : Block.UPDATE_ALL);
	}

	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		entity.causeFallDamage(fallDistance, 0f, level.damageSources().fall());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext collision) {
			Entity entity = collision.getEntity();
			if (entity != null) {

				if (entity instanceof FallingBlockEntity) {
					return SHAPE;
				}

				if (entity.fallDistance > 0.25f) {
					return SHAPE;
				}

				double horizontalLengthSqr = entity.getDeltaMovement().horizontalDistanceSqr();

				if ((horizontalLengthSqr > 0.0057d || canEntityWalkOnWaterGel(entity)) && collision.isAbove(SHAPE, pos, false) && !collision.isDescending()) {
					return SHAPE;
				}
			}
		}

		return Shapes.empty();
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return Shapes.empty();
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type) {
		return true;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(15) == 1) {
			BlockPos posBelow = pos.below();
			BlockState stateBelow = level.getBlockState(posBelow);
			if (!(stateBelow.getBlock() instanceof WaterGelBlock) && (!stateBelow.canOcclude() || !stateBelow.isFaceSturdy(level, posBelow, Direction.UP))) {
				ParticleUtils.spawnParticleBelow(level, pos, random, ParticleTypes.DRIPPING_WATER);
			}
		}
	}

}
