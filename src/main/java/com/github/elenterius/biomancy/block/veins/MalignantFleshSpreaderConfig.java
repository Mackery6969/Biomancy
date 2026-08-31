package com.github.elenterius.biomancy.block.veins;

import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlock;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.util.LevelUtil;
import com.github.elenterius.biomancy.util.random.CellularNoise;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.Set;

class MalignantFleshSpreaderConfig extends MultifaceSpreader.DefaultSpreaderConfig {

	protected static final Set<Block> VALID_SOURCES = Set.of(ModBlocks.MALIGNANT_FLESH_SLAB.get(), ModBlocks.MALIGNANT_FLESH_STAIRS.get(), ModBlocks.MALIGNANT_FLESH.get());
	private static final Direction[] DIRECTIONS = Direction.values();

	public MalignantFleshSpreaderConfig(MultifaceBlock block) {
		super(block);
	}

	@Override
	public boolean isOtherBlockValidAsSource(BlockState state) {
		Block block = state.getBlock();
		return block instanceof PrimordialCradleBlock || VALID_SOURCES.contains(block);
	}

	@Override
	protected boolean stateCanBeReplaced(BlockGetter level, BlockPos posA, BlockPos posB, Direction direction, BlockState state) {
		BlockState blockstate = level.getBlockState(posB.relative(direction));
		if (!blockstate.is(Blocks.MOVING_PISTON)) {
			FluidState fluidState = state.getFluidState();
			if (!fluidState.isEmpty() && !fluidState.is(Fluids.WATER)) return false;

			if (state.is(BlockTags.FIRE)) return false;

			return state.canBeReplaced() || super.stateCanBeReplaced(level, posA, posB, direction, state);
		}

		return super.stateCanBeReplaced(level, posA, posB, direction, state);
	}

	@Override
	public boolean canSpreadInto(BlockGetter level, BlockPos pos, MultifaceSpreader.SpreadPos spreadPos) {
		BlockState state = level.getBlockState(spreadPos.pos());
		if (PrimordialEcosystem.MALIGNANT_UPGRADE_TARGETS.contains(state.getBlock())) {
			if (level instanceof ServerLevel serverLevel) {
				CellularNoise cellularNoise = PrimordialEcosystem.getCellularNoise(serverLevel);
				float borderThreshold = cellularNoise.borderThreshold() - 0.005f;
				float n = cellularNoise.getValueAtCenter(pos);
				return n >= borderThreshold && !LevelUtil.isBlockNearby(serverLevel, spreadPos.pos(), 4, blockState -> blockState.is(ModBlocks.PRIMAL_BLOOM.get()));
			}
			return true;
		}
		return stateCanBeReplaced(level, pos, spreadPos.pos(), spreadPos.face(), state) && block.isValidStateForPlacement(level, state, spreadPos.pos(), spreadPos.face());
	}

	@Override
	public boolean placeBlock(LevelAccessor level, MultifaceSpreader.SpreadPos spreadPos, BlockState state, boolean markForPostprocessing) {
		if (PrimordialEcosystem.MALIGNANT_UPGRADE_TARGETS.contains(state.getBlock())) {
			if (level.getRandom().nextFloat() < 0.25f) {
				return level.setBlock(spreadPos.pos(), ModBlocks.MALIGNANT_FLESH.get().defaultBlockState(), Block.UPDATE_CLIENTS);
			}
			return false; //prevent upgrade target from being replaced by veins
		}

		int neighbors = 0;
		BlockPos sourcePos = spreadPos.pos();
		BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos belowNeighborPos = new BlockPos.MutableBlockPos();
		for (Direction direction : DIRECTIONS) {
			int stepX = direction.getStepX();
			int stepY = direction.getStepY();
			int stepZ = direction.getStepZ();
			for (int i = 1; i <= 2; i++) {
				neighborPos.setWithOffset(sourcePos, stepX * i, stepY * i, stepZ * i);
				BlockState neighborState = level.getBlockState(neighborPos);
				neighbors += neighborState.is(block) ? 1 : 0;

				belowNeighborPos.setWithOffset(neighborPos, Direction.DOWN);
				Block belowNeighborBlock = level.getBlockState(belowNeighborPos).getBlock();
				boolean reduceNeighbors = PrimordialEcosystem.SOLID_FLESH_BLOCKS.contains(belowNeighborBlock);
				if (reduceNeighbors) {
					neighbors--;
				}
			}
		}
		if (neighbors >= 4) return false;

		BlockState blockstate = getStateForPlacement(state, level, sourcePos, spreadPos.face());
		if (blockstate == null) return false;

		if (markForPostprocessing) {
			level.getChunk(sourcePos).markPosForPostprocessing(sourcePos);
		}

		return level.setBlock(sourcePos, blockstate, Block.UPDATE_CLIENTS);
	}
}
