package com.github.elenterius.biomancy.mixin.sable;

import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import dev.ryanhcode.sable.api.block.BlockSubLevelCollisionShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FleshVeinsBlock.class)
public abstract class FleshVeinsBlockMixin implements BlockSubLevelCollisionShape {

	@Override
	public VoxelShape getSubLevelCollisionShape(BlockGetter blockGetter, BlockState state) {
		return state.getShape(blockGetter, BlockPos.ZERO, CollisionContext.empty());
	}

}
