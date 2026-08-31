package com.github.elenterius.biomancy.block.base;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public abstract class SimpleMultiFaceBlock extends Block {

	private static final float THICKNESS = 1;
	protected static final VoxelShape UP_AABB = Block.box(0, 16 - THICKNESS, 0, 16, 16, 16);
	protected static final VoxelShape DOWN_AABB = Block.box(0, 0, 0, 16, THICKNESS, 16);
	protected static final VoxelShape WEST_AABB = Block.box(0, 0, 0, THICKNESS, 16, 16);
	protected static final VoxelShape EAST_AABB = Block.box(16 - THICKNESS, 0, 0, 16, 16, 16);
	protected static final VoxelShape NORTH_AABB = Block.box(0, 0, 0, 16, 16, THICKNESS);
	protected static final VoxelShape SOUTH_AABB = Block.box(0, 0, 16 - THICKNESS, 16, 16, 16);

	protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
	protected static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), map -> {
		map.put(Direction.NORTH, NORTH_AABB);
		map.put(Direction.EAST, EAST_AABB);
		map.put(Direction.SOUTH, SOUTH_AABB);
		map.put(Direction.WEST, WEST_AABB);
		map.put(Direction.UP, UP_AABB);
		map.put(Direction.DOWN, DOWN_AABB);
	});
	protected static final Direction[] DIRECTIONS = Direction.values();

	protected final ImmutableMap<BlockState, VoxelShape> shapesCache;
	protected final boolean canRotate;
	protected final boolean canMirrorX;
	protected final boolean canMirrorZ;

	public SimpleMultiFaceBlock(Properties properties) {
		super(properties);

		registerDefaultState(getDefaultMultifaceState(stateDefinition));

		canRotate = Direction.Plane.HORIZONTAL.stream().allMatch(this::isFaceSupported);
		canMirrorX = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.X).filter(this::isFaceSupported).count() % 2L == 0L;
		canMirrorZ = Direction.Plane.HORIZONTAL.stream().filter(Direction.Axis.Z).filter(this::isFaceSupported).count() % 2L == 0L;

		shapesCache = getShapeForEachState(this::calculateMultifaceShape);
	}

	protected boolean isFaceSupported(Direction direction) {
		return true;
	}

	protected VoxelShape calculateMultifaceShape(BlockState state) {
		VoxelShape voxelshape = Shapes.empty();

		for (Direction face : DIRECTIONS) {
			if (hasFace(state, face)) {
				voxelshape = Shapes.or(voxelshape, SHAPE_BY_DIRECTION.get(face));
			}
		}

		return voxelshape.isEmpty() ? Shapes.block() : voxelshape;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		Arrays.stream(DIRECTIONS)
				.filter(this::isFaceSupported)
				.map(SimpleMultiFaceBlock::getFaceProperty)
				.forEach(builder::add);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (!hasAnyFace(state)) {
			return Blocks.AIR.defaultBlockState();
		}

		if (hasFace(state, direction) && !canAttachTo(level, direction, neighborPos, neighborState)) {
			return removeFace(state, direction, level, pos);
		}

		return state;
	}

	protected BlockState removeFace(BlockState state, Direction face, LevelAccessor level, BlockPos pos) {
		BlockState blockState = state.setValue(getFaceProperty(face), Boolean.FALSE);
		return hasAnyFace(blockState) ? blockState : Blocks.AIR.defaultBlockState();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		//noinspection DataFlowIssue
		return shapesCache.getOrDefault(state, Shapes.block());
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		boolean flag = false;

		for (Direction direction : DIRECTIONS) {
			if (hasFace(state, direction)) {
				BlockPos blockPos = pos.relative(direction);
				if (!canAttachTo(level, direction, blockPos, level.getBlockState(blockPos))) {
					return false;
				}
				flag = true;
			}
		}

		return flag;
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return hasAnyVacantFace(state);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos clickedPos = context.getClickedPos();
		BlockState blockState = level.getBlockState(clickedPos);
		return Arrays.stream(context.getNearestLookingDirections())
				.map(direction -> getStateForPlacement(blockState, level, clickedPos, direction))
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
	}

	public boolean isValidStateForPlacement(BlockGetter level, BlockState state, BlockPos pos, Direction direction) {
		if (isFaceSupported(direction) && (!state.is(this) || !hasFace(state, direction))) {
			BlockPos neighborPos = pos.relative(direction);
			BlockState neighborState = level.getBlockState(neighborPos);
			return canAttachTo(level, direction, neighborPos, neighborState);
		}
		return false;
	}

	public @Nullable BlockState getStateForPlacement(BlockState currentState, BlockGetter level, BlockPos pos, Direction lookingDirection) {
		if (!isValidStateForPlacement(level, currentState, pos, lookingDirection)) {
			return null;
		}

		BlockState blockstate;
		if (currentState.is(this)) {
			blockstate = currentState;
		}
		else if (isWaterloggable(stateDefinition) && currentState.getFluidState().isSourceOfType(Fluids.WATER)) {
			blockstate = defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE);
		}
		else {
			blockstate = defaultBlockState();
		}

		return blockstate.setValue(getFaceProperty(lookingDirection), Boolean.TRUE);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return !canRotate ? state : mapDirections(state, rotation::rotate);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		if (mirror == Mirror.FRONT_BACK && !canMirrorX) {
			return state;
		}
		else {
			return mirror == Mirror.LEFT_RIGHT && !canMirrorZ ? state : mapDirections(state, mirror::mirror);
		}
	}

	private BlockState mapDirections(BlockState state, Function<Direction, Direction> directionalFunction) {
		BlockState blockstate = state;

		for (Direction direction : DIRECTIONS) {
			if (isFaceSupported(direction)) {
				blockstate = blockstate.setValue(getFaceProperty(directionalFunction.apply(direction)), state.getValue(getFaceProperty(direction)));
			}
		}

		return blockstate;
	}

	public static boolean hasFace(BlockState state, Direction direction) {
		BooleanProperty property = getFaceProperty(direction);
		return state.hasProperty(property) && state.getValue(property);
	}

	public static boolean canAttachTo(BlockGetter level, Direction direction, BlockPos pos, BlockState state) {
		return Block.isFaceFull(state.getBlockSupportShape(level, pos), direction.getOpposite()) || Block.isFaceFull(state.getCollisionShape(level, pos), direction.getOpposite());
	}

	private static boolean isWaterloggable(StateDefinition<Block, BlockState> stateDefinition) {
		return stateDefinition.getProperties().contains(BlockStateProperties.WATERLOGGED);
	}

	public static BooleanProperty getFaceProperty(Direction direction) {
		return PROPERTY_BY_DIRECTION.get(direction);
	}

	private static BlockState getDefaultMultifaceState(StateDefinition<Block, BlockState> stateDefinition) {
		BlockState state = stateDefinition.any();

		for (BooleanProperty property : PROPERTY_BY_DIRECTION.values()) {
			if (state.hasProperty(property)) {
				state = state.setValue(property, Boolean.FALSE);
			}
		}

		return state;
	}

	protected static boolean hasAnyFace(BlockState state) {
		for (Direction direction : DIRECTIONS) {
			if (hasFace(state, direction)) return true;
		}
		return false;
	}

	private static boolean hasAnyVacantFace(BlockState state) {
		for (Direction direction : DIRECTIONS) {
			if (!hasFace(state, direction)) return true;
		}
		return false;
	}

	public static EnumSet<Direction> getAvailableFaces(BlockState state) {
		if (!(state.getBlock() instanceof SimpleMultiFaceBlock)) {
			return EnumSet.noneOf(Direction.class);
		}

		EnumSet<Direction> set = EnumSet.noneOf(Direction.class);
		for (Direction direction : DIRECTIONS) {
			if (hasFace(state, direction)) {
				set.add(direction);
			}
		}
		return set;
	}

}
