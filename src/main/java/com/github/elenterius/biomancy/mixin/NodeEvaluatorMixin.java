package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.block.membrane.Membrane;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {FlyNodeEvaluator.class, SwimNodeEvaluator.class, WalkNodeEvaluator.class})
public abstract class NodeEvaluatorMixin {

	@Inject(method = "getPathTypeOfMob(Lnet/minecraft/world/level/pathfinder/PathfindingContext;IIILnet/minecraft/world/entity/Mob;)Lnet/minecraft/world/level/pathfinder/PathType;", at = @At(value = "HEAD"), cancellable = true)
	private void onGetPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob, @NonNull CallbackInfoReturnable<PathType> cir) {
		BlockPos pos = new BlockPos(x, y, z);
		BlockState state = context.getBlockState(pos);
		Block block = state.getBlock();

		if (block instanceof Membrane membrane) {
			cir.setReturnValue(membrane.shouldIgnoreEntityCollisionAt(state, context.level(), pos, mob) ? PathType.DOOR_OPEN : PathType.BLOCKED);
		}
	}

}
