package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.serum.AbsorptionSerum;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import static com.github.elenterius.biomancy.gametest.GameTestTemplates.EMPTY_PLATFORM;
import static com.github.elenterius.biomancy.gametest.GameTestTemplates.PLATFORM_SIZE;
import static com.github.elenterius.biomancy.gametest.GameTestTemplates.GROUND_Y;
import static com.github.elenterius.biomancy.gametest.GameTestTemplates.SURFACE_Y;
import static com.github.elenterius.biomancy.gametest.GameTestTemplates.PLATFORM_CENTER;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SerumGameTests {

	private SerumGameTests() {}

	@GameTest(template = EMPTY_PLATFORM, timeoutTicks = 200)
	public static void absorptionSerumGrantsHearts(GameTestHelper helper) {
		prepareGround(helper);

		LivingEntity target = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, PLATFORM_CENTER);
		AbsorptionSerum serum = ModSerums.ABSORPTION_BOOST.get();

		serum.affectEntity(helper.getLevel(), new CompoundTag(), null, target);

		if (target.getAbsorptionAmount() <= 0f) {
			helper.fail("absorption serum granted no hearts (absorption=" + target.getAbsorptionAmount()
					+ ", max_absorption=" + target.getAttributeValue(Attributes.MAX_ABSORPTION)
					+ "); is the max_absorption attribute still at its default of 0?");
			return;
		}

		helper.succeed();
	}

	@GameTest(template = EMPTY_PLATFORM, timeoutTicks = 200)
	public static void absorptionSerumStacksUpToTheConfiguredMax(GameTestHelper helper) {
		prepareGround(helper);

		LivingEntity target = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, PLATFORM_CENTER);
		AbsorptionSerum serum = ModSerums.ABSORPTION_BOOST.get();

		float previous = 0f;
		for (int i = 0; i < 20; i++) {
			serum.affectEntity(helper.getLevel(), new CompoundTag(), null, target);

			float current = target.getAbsorptionAmount();
			if (current < previous) {
				helper.fail("absorption went down on injection " + i + ": " + previous + " -> " + current);
				return;
			}
			previous = current;
		}

		if (previous <= 0f) {
			helper.fail("absorption serum granted no hearts after repeated injections");
			return;
		}

		// it must settle at a ceiling rather than growing without bound
		serum.affectEntity(helper.getLevel(), new CompoundTag(), null, target);
		if (target.getAbsorptionAmount() > previous) {
			helper.fail("absorption kept growing past its ceiling: " + previous + " -> " + target.getAbsorptionAmount());
			return;
		}

		helper.succeed();
	}

	@GameTest(template = EMPTY_PLATFORM, timeoutTicks = 400)
	public static void absorptionSerumGrantsHeartsOnTheFirstInjection(GameTestHelper helper) {
		prepareGround(helper);

		LivingEntity target = helper.spawnWithNoFreeWill(EntityType.ZOMBIE,
				PLATFORM_CENTER);
		AbsorptionSerum serum = ModSerums.ABSORPTION_BOOST.get();

		float afterFirst = 0f;
		for (int injection = 1; injection <= 3; injection++) {
			serum.affectEntity(helper.getLevel(), new CompoundTag(), null, target);

			float immediately = target.getAbsorptionAmount();
			for (int t = 0; t < 5; t++) {
				target.tick();
			}
			float afterTicks = target.getAbsorptionAmount();

			BiomancyMod.LOGGER.info("ABSORPTION injection {}: immediately={} afterTicks={} maxAbsorptionAttr={}",
					injection, immediately, afterTicks, target.getAttributeValue(Attributes.MAX_ABSORPTION));

			if (injection == 1)
				afterFirst = afterTicks;
		}

		if (afterFirst <= 0f) {
			helper.fail("first injection left absorption at " + afterFirst + " (max_absorption="
					+ target.getAttributeValue(Attributes.MAX_ABSORPTION) + ")");
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
}
