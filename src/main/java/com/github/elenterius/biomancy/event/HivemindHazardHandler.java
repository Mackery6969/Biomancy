package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import com.github.elenterius.biomancy.init.ModFeatureFlags;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import com.github.elenterius.biomancy.world.hivemind.HivemindForaging;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import org.jspecify.annotations.Nullable;

import java.util.List;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID)
public final class HivemindHazardHandler {

	private HivemindHazardHandler() {}

	@SubscribeEvent
	public static void onExplosionDetonate(final ExplosionEvent.Detonate event) {
		Level level = event.getLevel();
		if (!(level instanceof ServerLevel serverLevel) || !ModFeatureFlags.isHivemindEnabled(serverLevel)) return;

		List<BlockPos> affectedBlocks = event.getAffectedBlocks();
		if (affectedBlocks.isEmpty()) return;

		LivingEntity culprit = event.getExplosion().getIndirectSourceEntity();

		for (BlockPos pos : affectedBlocks) {
			BlockState state = serverLevel.getBlockState(pos);
			if (isHiveFlesh(state)) {
				HivemindForaging.reportHazard(serverLevel, pos, culprit);
				return;
			}
		}
	}

	public static void onFleshHarmed(Level level, BlockPos pos, @Nullable LivingEntity culprit) {
		if (level instanceof ServerLevel serverLevel && ModFeatureFlags.isHivemindEnabled(serverLevel)) {
			HivemindForaging.reportHazard(serverLevel, pos, culprit);
		}
	}

	private static boolean isHiveFlesh(BlockState state) {
		return state.getBlock() instanceof FleshVeinsBlock || PrimordialEcosystem.FULL_FLESH_BLOCKS.contains(state.getBlock());
	}

}
