package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModBlockProperties;
import com.github.elenterius.biomancy.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RandomTickGameTests {

	private static final String TEMPLATE = "empty_platform";
	private static final int PLATFORM_SIZE = 9;
	private static final int FLOOR_Y = 1;

	private static final int RANDOM_TICKS = 2000;

	private RandomTickGameTests() {}

	@GameTest(template = TEMPLATE, timeoutTicks = 600)
	public static void malignantFleshVeinsSpread(GameTestHelper helper) {
		BlockPos seed = new BlockPos(PLATFORM_SIZE / 2, FLOOR_Y, PLATFORM_SIZE / 2);

		BlockState veins = ModBlocks.MALIGNANT_FLESH_VEINS.get().defaultBlockState()
				.setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), true);
		veins = ModBlockProperties.CHARGE.setValue(veins, ModBlockProperties.CHARGE.getMax());
		helper.setBlock(seed, veins);

		int before = countBlocks(helper, ModBlocks.MALIGNANT_FLESH_VEINS.get());
		if (before != 1) {
			helper.fail("expected exactly one seeded veins block, found " + before);
			return;
		}

		forceRandomTicks(helper, seed, RANDOM_TICKS);

		int after = countBlocks(helper, ModBlocks.MALIGNANT_FLESH_VEINS.get());
		if (after <= before) {
			helper.fail("malignant flesh veins did not spread after " + RANDOM_TICKS
					+ " random ticks (still " + after + "); is randomTick wired to tick?");
			return;
		}

		helper.succeed();
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 600)
	public static void unchargedVeinsDoNotSpread(GameTestHelper helper) {
		BlockPos seed = new BlockPos(PLATFORM_SIZE / 2, FLOOR_Y, PLATFORM_SIZE / 2);

		BlockState veins = ModBlocks.MALIGNANT_FLESH_VEINS.get().defaultBlockState()
				.setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), true);
		veins = ModBlockProperties.CHARGE.setValue(veins, 0);
		helper.setBlock(seed, veins);

		forceRandomTicks(helper, seed, RANDOM_TICKS);

		int after = countBlocks(helper, ModBlocks.MALIGNANT_FLESH_VEINS.get());
		if (after != 1) {
			helper.fail("uncharged veins should not spread, found " + after + " blocks");
			return;
		}

		helper.succeed();
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 600)
	public static void acidSplatterDecays(GameTestHelper helper) {
		BlockPos pos = new BlockPos(PLATFORM_SIZE / 2, FLOOR_Y, PLATFORM_SIZE / 2);

		BlockState splatter = ModBlocks.ACID_SPLATTER.get().defaultBlockState()
				.setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), true);
		helper.setBlock(pos, splatter);
		helper.assertBlockPresent(ModBlocks.ACID_SPLATTER.get(), pos);

		forceRandomTicks(helper, pos, RANDOM_TICKS);

		if (helper.getBlockState(pos).is(ModBlocks.ACID_SPLATTER.get())) {
			helper.fail("acid splatter never decayed after " + RANDOM_TICKS
					+ " random ticks; is randomTick wired to tick?", pos);
			return;
		}

		helper.succeed();
	}

	private static void forceRandomTicks(GameTestHelper helper, BlockPos relativePos, int times) {
		ServerLevel level = helper.getLevel();
		BlockPos pos = helper.absolutePos(relativePos);

		for (int i = 0; i < times; i++) {
			BlockState state = level.getBlockState(pos);
			if (!state.isRandomlyTicking()) break;
			state.randomTick(level, pos, level.getRandom());
		}
	}

	private static int countBlocks(GameTestHelper helper, Block block) {
		int count = 0;

		for (int x = 0; x < PLATFORM_SIZE; x++) {
			for (int y = FLOOR_Y; y < FLOOR_Y + 3; y++) {
				for (int z = 0; z < PLATFORM_SIZE; z++) {
					if (helper.getBlockState(new BlockPos(x, y, z)).is(block)) count++;
				}
			}
		}

		return count;
	}
}
