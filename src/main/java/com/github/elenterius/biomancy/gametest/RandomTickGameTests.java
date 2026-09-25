package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.bloom.BloomBlock;
import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
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

import java.util.ArrayList;
import java.util.List;

import static com.github.elenterius.biomancy.gametest.GameTestTemplates.EMPTY_PLATFORM;
import static com.github.elenterius.biomancy.gametest.GameTestTemplates.PLATFORM_SIZE;
import static com.github.elenterius.biomancy.gametest.GameTestTemplates.GROUND_Y;
import static com.github.elenterius.biomancy.gametest.GameTestTemplates.SURFACE_Y;
import static com.github.elenterius.biomancy.gametest.GameTestTemplates.PLATFORM_CENTER;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RandomTickGameTests {

	private static final int RANDOM_TICKS = 2000;

	private RandomTickGameTests() {}

	@GameTest(template = EMPTY_PLATFORM, timeoutTicks = 600)
	public static void malignantFleshVeinsSpread(GameTestHelper helper) {
		prepareGround(helper);
		BlockPos seed = PLATFORM_CENTER;

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

	@GameTest(template = EMPTY_PLATFORM, timeoutTicks = 600)
	public static void unchargedVeinsDoNotSpread(GameTestHelper helper) {
		prepareGround(helper);
		BlockPos seed = PLATFORM_CENTER;

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

	@GameTest(template = EMPTY_PLATFORM, timeoutTicks = 600)
	public static void acidSplatterDecays(GameTestHelper helper) {
		prepareGround(helper);
		BlockPos pos = PLATFORM_CENTER;

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

	@GameTest(template = EMPTY_PLATFORM, timeoutTicks = 600)
	public static void cradleSeedsVeinsAroundItself(GameTestHelper helper) {
		prepareGround(helper);
		BlockPos cradlePos = PLATFORM_CENTER;

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

	@GameTest(template = EMPTY_PLATFORM, timeoutTicks = 600)
	public static void bloomAimCheckWorksWithoutAnEntity(GameTestHelper helper) {
		prepareGround(helper);
		BlockPos origin = PLATFORM_CENTER;

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

	@GameTest(template = EMPTY_PLATFORM, timeoutTicks = 600)
	public static void veinsEatDroppedMeatWithoutCrashing(GameTestHelper helper) {
		prepareGround(helper);
		BlockPos pos = PLATFORM_CENTER;

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

	@GameTest(template = EMPTY_PLATFORM, timeoutTicks = 600)
	public static void spreadingNeverLeavesFloatingVeins(GameTestHelper helper) {
		prepareGround(helper);
		ServerLevel level = helper.getLevel();
		Block veinsBlock = ModBlocks.MALIGNANT_FLESH_VEINS.get();

		BlockPos seed = PLATFORM_CENTER;
		BlockState seedState = veinsBlock.defaultBlockState()
				.setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), true);
		seedState = ModBlockProperties.CHARGE.setValue(seedState, ModBlockProperties.CHARGE.getMax());
		helper.setBlock(seed, seedState);

		// keep the colony charged so it spreads and converts as hard as it can
		for (int round = 0; round < 150; round++) {
			for (BlockPos rel : scanArea()) {
				BlockPos abs = helper.absolutePos(rel);
				BlockState state = level.getBlockState(abs);
				if (!state.is(veinsBlock))
					continue;

				level.setBlock(abs, ModBlockProperties.CHARGE.setValue(state, ModBlockProperties.CHARGE.getMax()),
						Block.UPDATE_CLIENTS);
				level.getBlockState(abs).randomTick(level, abs, level.getRandom());
			}
		}

		List<BlockPos> floating = new ArrayList<>();
		for (BlockPos rel : scanArea()) {
			BlockPos abs = helper.absolutePos(rel);
			BlockState state = level.getBlockState(abs);
			if (!state.is(veinsBlock))
				continue;

			boolean supported = false;
			for (Direction direction : Direction.values()) {
				if (!MultifaceBlock.hasFace(state, direction))
					continue;
				BlockPos neighbor = abs.relative(direction);
				if (FleshVeinsBlock.canVeinsAttachTo(level, direction, neighbor, level.getBlockState(neighbor))) {
					supported = true;
					break;
				}
			}

			if (!supported)
				floating.add(rel);
		}

		if (!floating.isEmpty()) {
			helper.fail("found " + floating.size() + " floating veins block(s) attached to nothing, e.g. "
					+ floating.get(0));
			return;
		}

		helper.succeed();
	}

	private static List<BlockPos> scanArea() {
		List<BlockPos> positions = new ArrayList<>();

		for (int x = 0; x < PLATFORM_SIZE; x++) {
			for (int y = SURFACE_Y; y < SURFACE_Y + 3; y++) {
				for (int z = 0; z < PLATFORM_SIZE; z++) {
					positions.add(new BlockPos(x, y, z));
				}
			}
		}

		return positions;
	}

	@GameTest(template = EMPTY_PLATFORM, timeoutTicks = 200)
	public static void removingSupportWithUpdateClientsDetachesVeins(GameTestHelper helper) {
		prepareGround(helper);
		ServerLevel level = helper.getLevel();
		Block veinsBlock = ModBlocks.MALIGNANT_FLESH_VEINS.get();

		BlockPos rel = PLATFORM_CENTER;
		BlockPos abs = helper.absolutePos(rel);
		BlockPos supportAbs = abs.below();

		BlockState veins = veinsBlock.defaultBlockState()
				.setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), true);
		level.setBlock(abs, veins, Block.UPDATE_ALL);

		if (!level.getBlockState(abs).is(veinsBlock)) {
			helper.fail("could not seed veins for the support test", rel);
			return;
		}

		// exactly what destroyBlockAndConvertIntoEnergy / chamber carving does
		level.setBlock(supportAbs, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);

		if (level.getBlockState(abs).is(veinsBlock)) {
			helper.fail("veins survived with no support: UPDATE_CLIENTS did not detach them", rel);
			return;
		}

		helper.succeed();
	}

	@GameTest(template = EMPTY_PLATFORM, timeoutTicks = 600)
	public static void spreadingOverVariedTerrainLeavesNoFloatingVeins(GameTestHelper helper) {
		prepareGround(helper);
		ServerLevel level = helper.getLevel();
		Block veinsBlock = ModBlocks.MALIGNANT_FLESH_VEINS.get();

		// logs get converted into flesh walls, which are not face-full
		for (int x = 0; x < PLATFORM_SIZE; x += 3) {
			for (int z = 0; z < PLATFORM_SIZE; z += 3) {
				helper.setBlock(new BlockPos(x, GROUND_Y, z), Blocks.OAK_LOG);
				helper.setBlock(new BlockPos(x, SURFACE_Y, z), Blocks.OAK_LOG);
			}
		}

		// a ceiling so veins spread onto downward-facing surfaces too
		for (int x = 0; x < PLATFORM_SIZE; x++) {
			for (int z = 0; z < PLATFORM_SIZE; z++) {
				helper.setBlock(new BlockPos(x, SURFACE_Y + 3, z), Blocks.STONE);
			}
		}

		BlockPos seed = new BlockPos(PLATFORM_SIZE / 2 + 1, SURFACE_Y, PLATFORM_SIZE / 2 + 1);
		BlockState seedState = veinsBlock.defaultBlockState()
				.setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), true);
		helper.setBlock(seed, ModBlockProperties.CHARGE.setValue(seedState, ModBlockProperties.CHARGE.getMax()));

		for (int round = 0; round < 200; round++) {
			for (BlockPos rel : scanArea()) {
				BlockPos abs = helper.absolutePos(rel);
				BlockState state = level.getBlockState(abs);
				if (!state.is(veinsBlock))
					continue;

				level.setBlock(abs, ModBlockProperties.CHARGE.setValue(state, ModBlockProperties.CHARGE.getMax()),
						Block.UPDATE_CLIENTS);
				level.getBlockState(abs).randomTick(level, abs, level.getRandom());
			}
		}

		List<BlockPos> floating = new ArrayList<>();
		for (BlockPos rel : scanArea()) {
			BlockPos abs = helper.absolutePos(rel);
			BlockState state = level.getBlockState(abs);
			if (!state.is(veinsBlock))
				continue;

			boolean supported = false;
			for (Direction direction : Direction.values()) {
				if (!MultifaceBlock.hasFace(state, direction))
					continue;
				BlockPos neighbor = abs.relative(direction);
				if (FleshVeinsBlock.canVeinsAttachTo(level, direction, neighbor, level.getBlockState(neighbor))) {
					supported = true;
					break;
				}
			}

			if (!supported) {
				floating.add(rel);
				BiomancyMod.LOGGER.error("FLOATING VEINS at {} state={} neighbors: {}", rel, state,
						describeNeighbors(level, abs));
			}
		}

		if (!floating.isEmpty()) {
			helper.fail("found " + floating.size() + " floating veins block(s), e.g. " + floating.get(0));
			return;
		}

		helper.succeed();
	}

	private static String describeNeighbors(ServerLevel level, BlockPos abs) {
		StringBuilder sb = new StringBuilder();

		for (Direction direction : Direction.values()) {
			sb.append(direction).append("=").append(level.getBlockState(abs.relative(direction)).getBlock())
					.append(" ");
		}

		return sb.toString();
	}

	/**
	 * Bulk block placement (/fill, schematics, contraptions) sets blocks with
	 * {@link Block#UPDATE_KNOWN_SHAPE}, which suppresses shape updates entirely.
	 * Veins whose
	 * support disappears that way are never told, so they stay behind floating.
	 */
	@GameTest(template = EMPTY_PLATFORM, timeoutTicks = 200)
	public static void veinsDoNotSurviveSupportRemovedWithoutShapeUpdate(GameTestHelper helper) {
		prepareGround(helper);
		ServerLevel level = helper.getLevel();
		Block veinsBlock = ModBlocks.MALIGNANT_FLESH_VEINS.get();

		BlockPos rel = PLATFORM_CENTER;
		BlockPos abs = helper.absolutePos(rel);

		BlockState veins = veinsBlock.defaultBlockState()
				.setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), true);
		veins = ModBlockProperties.CHARGE.setValue(veins, 0);
		level.setBlock(abs, veins, Block.UPDATE_ALL);

		// support yanked out with shape updates suppressed
		level.setBlock(abs.below(), Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);

		// something unrelated changes next door, which is the veins' next chance to
		// notice
		level.setBlock(abs.east(), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
		level.setBlock(abs.east(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

		if (level.getBlockState(abs).is(veinsBlock)) {
			helper.fail("veins are floating: support was removed without a shape update and they never re-checked",
					rel);
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
