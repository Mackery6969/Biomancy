package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.init.ModPlantTypes;
import com.github.elenterius.biomancy.util.IPlantable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;

public class FleshBlock extends Block {

	protected final ModPlantTypes supportedPlantType;

	public FleshBlock(Properties properties) {
		super(properties);
		supportedPlantType = ModPlantTypes.FLESH;
	}

	public FleshBlock(Properties properties, ModPlantTypes supportedPlantType) {
		super(properties);
		this.supportedPlantType = supportedPlantType;
	}

	@Override
	public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, BlockState plant) {
		boolean isSupported = plant.getBlock() instanceof IPlantable plantable && plantable.getPlantType() == supportedPlantType;
		return isSupported ? TriState.TRUE : TriState.FALSE;
	}

}
