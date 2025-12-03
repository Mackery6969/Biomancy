package com.github.elenterius.biomancy.block.splatter;

import com.github.elenterius.biomancy.util.EnhancedIntegerProperty;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public abstract class SplatterBlock extends MultifaceBlock {

	public static final EnhancedIntegerProperty AGE = EnhancedIntegerProperty.wrap(BlockStateProperties.AGE_3);

	protected final MultifaceSpreader spreader = new MultifaceSpreader(this);

	protected SplatterBlock(Properties properties) {
		super(properties.randomTicks().noOcclusion().noCollission().instabreak().replaceable().pushReaction(PushReaction.DESTROY));
		registerDefaultState(defaultBlockState().setValue(AGE.get(), AGE.getMin()));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(AGE.get());
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (random.nextInt(8) != 0) {
			level.scheduleTick(pos, this, Mth.nextInt(random, 40, 80));
			return;
		}

		if (level.isAreaLoaded(pos, 1)) {
			BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
			for (Direction direction : Direction.values()) {
				mutablePos.setWithOffset(pos, direction);
				BlockState neighborState = level.getBlockState(mutablePos);

				if (neighborState.is(this) && reduceSplatter(neighborState, level, mutablePos, random)) {
					level.scheduleTick(mutablePos, this, Mth.nextInt(random, 40, 80));
				}
			}
		}

		reduceSplatter(state, level, pos, random);
	}

	protected boolean reduceSplatter(BlockState state, Level level, BlockPos pos, RandomSource random) {
		int age = AGE.getValue(state);

		if (age < AGE.getMax()) {
			level.setBlock(pos, AGE.setValue(state, age + 1), Block.UPDATE_CLIENTS);
			return true;
		}

		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
		return false;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity.tickCount % 5 == 0 && isEntityInsideBoundingBox(level, pos, state, entity)) {
			entityInsideBoundingBox(level, pos, state, entity);
		}
	}

	protected boolean isEntityInsideBoundingBox(Level level, BlockPos pos, BlockState state, Entity entity) {
		VoxelShape blockShape = getShape(state, level, pos, CollisionContext.of(entity)).move(pos.getX(), pos.getY(), pos.getZ());
		VoxelShape entityShape = Shapes.create(entity.getBoundingBox());
		return Shapes.joinIsNotEmpty(blockShape, entityShape, BooleanOp.AND);
	}

	abstract void entityInsideBoundingBox(Level level, BlockPos pos, BlockState state, Entity entity);

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return !context.getItemInHand().is(asItem()) || super.canBeReplaced(state, context);
	}

	@Override
	public MultifaceSpreader getSpreader() {
		return spreader;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	public void placeSmallSplatter(ServerLevel level, BlockPos pos, Direction face, RandomSource random) {
		BlockState state = level.getBlockState(pos);

		if (state.is(this)) {
			if (AGE.getValue(state) > AGE.getMin()) {
				level.setBlock(pos, AGE.addValue(state, -1), Block.UPDATE_CLIENTS);
				level.playSound(null, pos, SoundEvents.SLIME_BLOCK_FALL, SoundSource.BLOCKS, 0.5f, 0.15f + random.nextFloat() * 0.5f);
			}
		}
		else if (state.canBeReplaced(new DirectionalPlaceContext(level, pos, face.getOpposite(), ItemStack.EMPTY, face))) {
			BlockState stateForPlacement = getStateForPlacement(state, level, pos, face.getOpposite());
			if (stateForPlacement != null) {
				level.setBlock(pos, AGE.setValue(stateForPlacement, random.nextFloat() <= 0.3f ? AGE.getMax() - 1 : AGE.getMax()), Block.UPDATE_CLIENTS);
				level.playSound(null, pos, SoundEvents.SLIME_BLOCK_FALL, SoundSource.BLOCKS, 0.5f, 0.15f + random.nextFloat() * 0.5f);
			}
		}
	}

	public void spreadSplatter(ServerLevel level, BlockPos pos, Direction face, RandomSource random) {
		BlockState state = level.getBlockState(pos);

		if (state.is(this)) {
			if (AGE.getValue(state) > AGE.getMin()) {
				level.setBlock(pos, AGE.setValue(state, AGE.getMin()), Block.UPDATE_CLIENTS);
			}

			spreadSplatterFromSource(level, pos, random);
		}
		else if (state.canBeReplaced(new DirectionalPlaceContext(level, pos, face.getOpposite(), ItemStack.EMPTY, face))) {
			BlockState stateForPlacement = getStateForPlacement(state, level, pos, face.getOpposite());
			if (stateForPlacement != null) {
				level.setBlock(pos, stateForPlacement, Block.UPDATE_CLIENTS);
				for (int i = 0; i < 4; i++) {
					getSpreader().spreadFromRandomFaceTowardRandomDirection(stateForPlacement, level, pos, random);
				}
				level.playSound(null, pos, SoundEvents.SLIME_BLOCK_FALL, SoundSource.BLOCKS, 0.7f, 0.15f + random.nextFloat() * 0.5f);
			}
		}

	}

	protected void spreadSplatterFromSource(ServerLevel level, BlockPos pos, RandomSource random) {
		BlockState state = level.getBlockState(pos);

		for (int i = 0; i < 4; i++) {
			if (random.nextFloat() < 0.6f) {
				getSpreader().spreadFromRandomFaceTowardRandomDirection(state, level, pos, random);
			}
		}
	}

	protected record Voxel(BlockPos pos, Direction[] spreadDirections, int cost, int depth, int[] directionCost) {
		public static Comparator<Voxel> INCREASING_COST_COMPARATOR = Comparator.comparingInt(Voxel::cost);
	}

	public void propagateSplatters(ServerLevel level, BlockPos startPos, int maxDepth, RandomSource random) {
		LongSet visited = new LongOpenHashSet();
		PriorityQueue<Voxel> queue = new PriorityQueue<>(Voxel.INCREASING_COST_COMPARATOR);

		queue.add(new Voxel(startPos, DIRECTIONS, 0, 0, new int[6]));

		while (!queue.isEmpty()) {
			Voxel voxel = queue.poll();

			for (Direction spreadDirection : voxel.spreadDirections) {
				spreadSplatter(level, voxel.pos, spreadDirection.getOpposite(), random);
			}

			int depth = voxel.depth + 1;
			if (depth >= maxDepth) continue;

			int[] directionCost = Arrays.copyOf(voxel.directionCost, voxel.directionCost.length);

			for (Direction direction : DIRECTIONS) {
				BlockPos neighborPos = voxel.pos.relative(direction);
				long key = neighborPos.asLong();

				if (visited.contains(key)) continue;
				visited.add(key);

				BlockState state = level.getBlockState(neighborPos);
				if (state.isAir() || (state.canBeReplaced() && state.getFluidState().isEmpty()) || state.getBlock() == this) {
					int cost = depth + voxel.directionCost[direction.get3DDataValue()];
					queue.add(new Voxel(neighborPos, new Direction[]{direction}, cost, depth, directionCost));
				}
				else {
					int dataValue = direction.get3DDataValue();
					directionCost[dataValue] += 1;
					directionCost[dataValue] *= 2;
				}
			}
		}
	}

}
