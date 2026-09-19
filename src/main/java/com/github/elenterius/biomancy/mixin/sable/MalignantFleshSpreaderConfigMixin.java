package com.github.elenterius.biomancy.mixin.sable;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MultifaceSpreader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.github.elenterius.biomancy.block.veins.MalignantFleshSpreaderConfig")
public abstract class MalignantFleshSpreaderConfigMixin {

	@Inject(method = "canSpreadInto", at = @At("HEAD"), cancellable = true)
	private void biomancy$stopSpreadBeyondSubLevel(BlockGetter level, BlockPos pos, MultifaceSpreader.SpreadPos spreadPos, CallbackInfoReturnable<Boolean> cir) {
		if (!(level instanceof Level realLevel)) return;

		SubLevel subLevel = Sable.HELPER.getContaining(realLevel, pos);
		if (subLevel == null) return; // not on a structure, spread normally

		BlockPos target = spreadPos.pos();
		if (!subLevel.getPlot().getBoundingBox().contains(target.getX(), target.getY(), target.getZ())) {
			cir.setReturnValue(false);
		}
	}

}
