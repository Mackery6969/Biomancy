package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.bloom.BloomBlock;
import com.github.elenterius.biomancy.init.ModBlockProperties;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RandomTickGameTests {

	private static final String TEMPLATE = "empty_platform";
	private static final int PLATFORM_SIZE = 9;
	private static final int GROUND_Y = 1;
	private static final int SURFACE_Y = GROUND_Y + 1;

	private static final int RANDOM_TICKS = 2000;

	private RandomTickGameTests() {}

	@GameTest(template = TEMPLATE, timeoutTicks = 600)
	public static void malignantFleshVeinsSpread(GameTestHelper helper) {
		prepareGround(helper);
		BlockPos seed = new BlockPos(PLATFORM_SIZE / 2, SURFACE_Y, PLATFORM_SIZE / 2);

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
		prepareGround(helper);
		BlockPos seed = new BlockPos(PLATFORM_SIZE / 2, SURFACE_Y, PLATFORM_SIZE / 2);

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
		prepareGround(helper);
		BlockPos pos = new BlockPos(PLATFORM_SIZE / 2, SURFACE_Y, PLATFORM_SIZE / 2);

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

	@GameTest(template = TEMPLATE, timeoutTicks = 600)
	public static void cradleSeedsVeinsAroundItself(GameTestHelper helper) {
		prepareGround(helper);
		BlockPos cradlePos = new BlockPos(PLATFORM_SIZE / 2, SURFACE_Y, PLATFORM_SIZE / 2);

		helper.setBlock(cradlePos, ModBlocks.PRIMORDIAL_CRADLE.get());
		helper.setBlock(cradlePos.below(), ModBlocks.MALIGNANT_FLESH.get());

		for (int i = 0; i < 40; i++) {
			PrimordialEcosystem.spreadMalignantVeinsFromSource(
					helper.getLevel(), helper.absolutePos(cradlePos), PrimordialEcosystem.MAX_CHARGE_SUPPLIER);
		}

		int veins = countBlocks(helper, ModBlocks.MALIGNANT_FLESH_VEINS.get());
		if (veins == 0) {
			helper.fail("the cradle seeded no veins; veins are a multiface block and need a "
					+ "neighbour with a full face, which the cradle's shape never provides", cradlePos);
			return;
		}

		helper.succeed();
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 600)
	public static void bloomAimCheckWorksWithoutAnEntity(GameTestHelper helper) {
		prepareGround(helper);
		BlockPos origin = new BlockPos(PLATFORM_SIZE / 2, SURFACE_Y, PLATFORM_SIZE / 2);

		BloomBlock bloom = ModBlocks.PRIMAL_BLOOM.get();
		for (Direction direction : Direction.values()) {
			try {
				bloom.hasUnobstructedAim(helper.getLevel(), helper.absolutePos(origin), direction);
			} catch (Exception e) {
				helper.fail("aim check threw for " + direction + ": " + e, origin);
				return;
			}
		}

		helper.succeed();
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 600)
	public static void veinsEatDroppedMeatWithoutCrashing(GameTestHelper helper) {
		prepareGround(helper);
		BlockPos pos = new BlockPos(PLATFORM_SIZE / 2, SURFACE_Y, PLATFORM_SIZE / 2);

		BlockState veins = ModBlocks.MALIGNANT_FLESH_VEINS.get().defaultBlockState()
				.setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), true);
		veins = ModBlockProperties.CHARGE.setValue(veins, 0);
		helper.setBlock(pos, veins);

		ItemEntity item = helper.spawnItem(Items.BEEF, pos);

		try {
			helper.getBlockState(pos).entityInside(helper.getLevel(), helper.absolutePos(pos), item);
		} catch (Exception e) {
			helper.fail("veins eating a dropped item threw: " + e, pos);
			return;
		}

		if (ModBlockProperties.CHARGE.getValue(helper.getBlockState(pos)) <= 0) {
			helper.fail("veins ate the item but gained no charge", pos);
			return;
		}

		helper.succeed();
	}

	private static void prepareGround(GameTestHelper helper) {
		for (int x = 0; x < PLATFORM_SIZE; x++) {
			for (int z = 0; z < PLATFORM_SIZE; z++) {
				helper.setBlock(new BlockPos(x, GROUND_Y, z), Blocks.STONE);
				for (int y = SURFACE_Y; y < SURFACE_Y + 3; y++) {
					helper.setBlock(new BlockPos(x, y, z), Blocks.AIR);
				}
			}
		}
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
			for (int y = SURFACE_Y; y < SURFACE_Y + 3; y++) {
				for (int z = 0; z < PLATFORM_SIZE; z++) {
					if (helper.getBlockState(new BlockPos(x, y, z)).is(block)) count++;
				}
			}
		}

		return count;
	}
}
