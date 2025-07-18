package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.entity.misc.LivingEntityData;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class JumpPadBlock extends MultifaceBlock {

	protected static final Map<Direction, VoxelShape> SHAPE_BY_FACE = Util.make(new EnumMap<>(Direction.class), map -> {
		map.put(Direction.UP, createFaceShape(Direction.UP));
		map.put(Direction.DOWN, createFaceShape(Direction.DOWN));
		map.put(Direction.NORTH, createFaceShape(Direction.NORTH));
		map.put(Direction.SOUTH, createFaceShape(Direction.SOUTH));
		map.put(Direction.EAST, createFaceShape(Direction.EAST));
		map.put(Direction.WEST, createFaceShape(Direction.WEST));
	});

	protected final ImmutableMap<BlockState, VoxelShape> shapesCache;
	private final MultifaceSpreader spreader = new MultifaceSpreader(this);

	public JumpPadBlock(Properties properties) {
		super(properties.noCollission().forceSolidOn());
		shapesCache = getShapeForEachState(JumpPadBlock::calculateMultifaceShape);
	}

	/**
	 * @param face this is not the direction but the face that is located at that position
	 */
	private static VoxelShape createFaceShape(Direction face) {
		return VoxelShapeUtil.createXZRotatedTowards(face, 2, 14, 2, 14, 16, 14);
	}

	private static VoxelShape calculateMultifaceShape(BlockState state) {
		VoxelShape voxelshape = Shapes.empty();

		for (Direction face : DIRECTIONS) {
			if (hasFace(state, face)) {
				voxelshape = Shapes.or(voxelshape, SHAPE_BY_FACE.get(face));
			}
		}

		return voxelshape.isEmpty() ? Shapes.block() : voxelshape;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (level.isClientSide) return;

		List<Direction> availableFaces = new ArrayList<>();
		List<AABB> collisionAABBs = new ArrayList<>();
		for (Direction face : Direction.values()) {
			if (hasFace(state, face)) {
				availableFaces.add(face);
				collisionAABBs.add(SHAPE_BY_FACE.get(face).bounds().move(pos));
			}
		}

		CollisionResult collisionResult = sweptAABB(entity, collisionAABBs);

		boolean skipCollision = switch (collisionResult) {
			case PREVIOUS_TICK, NEXT_TICK, NONE -> true;
			case TUNNELED_PREVIOUS_TICK, CURRENT_TICK, TUNNELS_NEXT_TICK -> false;
		};

		if (collisionResult != CollisionResult.NONE) {
			entity.fallDistance = 0;

			// to be able to properly launch entities from the ground we need to disable friction
			// we do this as well for unlaunched entities to allow players to exploit frictionless ground movement for other stuff
			if (entity instanceof LivingEntityData.TransientDataProvider provider) {
				// NOTE: we do not call the native method because we need to temporarily disable friction for the current and next tick,
				//       our LivingEventHandler will re-enable friction after the next tick
				if (!entity.isSteppingCarefully()) provider.biomancy$getData().setDiscardFriction(true);
			}
		}

		if (skipCollision) return;
		if (entity.isSteppingCarefully() || entity.isSuppressingBounce()) return;

		Vector3d mutableImpulse = new Vector3d();
		for (Direction face : availableFaces) {
			Vec3i normal = face.getOpposite().getNormal();
			mutableImpulse.add(normal.getX() * 2d, normal.getY(), normal.getZ() * 2d); // make horizontal movement stronger
		}

		final Vec3 velocity = entity.getDeltaMovement();
		//		Vec3 direction = velocity.normalize();
		//		double directionSimilarity = mutableImpulse.normalize().dot(direction.x, direction.y, direction.z);
		//		double recoveredVelocity = velocity.length() * Mth.clamp(directionSimilarity * -1d, 0, 1); //only recover velocity from frontal collisions
		//		double negateVelocity = Mth.clamp(directionSimilarity + 1d, 0, 1);

		//		mutableImpulse.mul(Math.max(2d, recoveredVelocity) * (entity instanceof LivingEntity ? 1d : 0.8d));
		double length = (entity instanceof LivingEntity ? 1d : 0.8d);
		mutableImpulse.normalize(length + velocity.length() * 0.8d);

		if (mutableImpulse.length() > 0d) {
			//execute jump only once per tick -> players tend to call the entityInside method more than once per tick
			if (debounce(entity, pos)) return;

			entity.setDeltaMovement(mutableImpulse.x, mutableImpulse.y, mutableImpulse.z);
			entity.hasImpulse = true; //force sync movement to client
			entity.hurtMarked = true; //force sync for players

			level.playSound(null, pos, ModSoundEvents.FLESH_BLOB_JUMP.get(), SoundSource.BLOCKS, 0.5f, 1.2f);
		}
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return context.getItemInHand().is(asItem()) && super.canBeReplaced(state, context);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		//noinspection DataFlowIssue
		return shapesCache.get(state);
	}

	@Override
	public MultifaceSpreader getSpreader() {
		return spreader;
	}

	private static CollisionResult sweptAABB(Entity entity, List<AABB> collisionAABBs) {
		AABB entityAABB = entity.getBoundingBox();
		Vec3 currentPos = entityAABB.getCenter();
		Vec3 offset = currentPos.subtract(entity.position());
		Vec3 prevPos = new Vec3(entity.xOld + offset.x, entity.yOld + offset.y, entity.zOld + offset.z);

		//		double gravity = entity instanceof LivingEntity && !entity.isNoGravity() ? MobUtil.getGravity(entity) : 0d;
		//		Vec3 nextPos = currentPos.add(entity.getDeltaMovement()).add(0d, -gravity, 0d); //predicted position
		Vec3 nextPos = currentPos.add(entity.getDeltaMovement()); //predicted position

		double epsilon = 1.0e-7d;
		double halfX = entityAABB.getXsize() / 2d + epsilon;
		double halfY = entityAABB.getYsize() / 2d + epsilon;
		double halfZ = entityAABB.getZsize() / 2d + epsilon;

		for (AABB collisionAABB : collisionAABBs) {
			AABB inflatedAABB = collisionAABB.inflate(halfX, halfY, halfZ); // minkowski sum

			if (inflatedAABB.contains(currentPos)) {
				//if (inflatedAABB.contains(nextPos)) return CollisionResult.NEXT_TICK;
				return CollisionResult.CURRENT_TICK;
			}

			if (inflatedAABB.intersects(prevPos, currentPos)) {
				if (inflatedAABB.contains(prevPos)) return CollisionResult.PREVIOUS_TICK;
				return CollisionResult.TUNNELED_PREVIOUS_TICK;
			}

			if (inflatedAABB.intersects(currentPos, nextPos)) {
				if (inflatedAABB.contains(nextPos)) return CollisionResult.NEXT_TICK;

				inflatedAABB.clip(currentPos, nextPos).ifPresent(clipPos -> {
					//force entity closer to prevent jumps in the air above the pad
					entity.setPos(clipPos.x - offset.x, clipPos.y - offset.y, clipPos.z - offset.z);
				});

				return CollisionResult.TUNNELS_NEXT_TICK;
			}
		}

		return CollisionResult.NONE;
	}

	private static boolean debounce(Entity entity, BlockPos blockPos) {
		CompoundTag tag = entity.getPersistentData().getCompound("biomancy:jump_pad");
		if (entity.tickCount != tag.getInt("tick")) {
			tag.putInt("tick", entity.tickCount);
			tag.remove("visitedPositions");
		}

		long visitingPos = blockPos.asLong();
		ListTag visitedPositions = tag.getList("visitedPositions", Tag.TAG_LONG);
		for (Tag visitedPos : visitedPositions) {
			if (visitedPos instanceof LongTag longTag) {
				if (longTag.getAsLong() == visitingPos) {
					return true;
				}
			}
		}
		visitedPositions.add(LongTag.valueOf(visitingPos));
		tag.put("visitedPositions", visitedPositions);
		entity.getPersistentData().put("biomancy:jump_pad", tag);

		return false;
	}

	private enum CollisionResult {
		PREVIOUS_TICK,
		TUNNELED_PREVIOUS_TICK,
		CURRENT_TICK,
		NEXT_TICK,
		TUNNELS_NEXT_TICK,
		NONE
	}

}
