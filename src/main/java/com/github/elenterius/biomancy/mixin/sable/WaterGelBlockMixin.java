package com.github.elenterius.biomancy.mixin.sable;

import com.github.elenterius.biomancy.block.WaterGelBlock;
import dev.ryanhcode.sable.api.block.BlockSubLevelCollisionShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WaterGelBlock.class)
public abstract class WaterGelBlockMixin implements BlockSubLevelCollisionShape {

	private static final VoxelShape SABLE_COLLISION_SHAPE = Block.box(1, 1, 1, 15, 15, 15);

	@Override
	public VoxelShape getSubLevelCollisionShape(BlockGetter blockGetter, BlockState state) {
		return SABLE_COLLISION_SHAPE;
	}

}
