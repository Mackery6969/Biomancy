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

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SerumGameTests {

	private static final String TEMPLATE = "empty_platform";
	private static final int PLATFORM_SIZE = 9;
	private static final int GROUND_Y = 1;
	private static final int SURFACE_Y = GROUND_Y + 1;

	private SerumGameTests() {}

	@GameTest(template = TEMPLATE, timeoutTicks = 200)
	public static void absorptionSerumGrantsHearts(GameTestHelper helper) {
		prepareGround(helper);

		LivingEntity target = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(PLATFORM_SIZE / 2, SURFACE_Y, PLATFORM_SIZE / 2));
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

	@GameTest(template = TEMPLATE, timeoutTicks = 200)
	public static void absorptionSerumStacksUpToTheConfiguredMax(GameTestHelper helper) {
		prepareGround(helper);

		LivingEntity target = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(PLATFORM_SIZE / 2, SURFACE_Y, PLATFORM_SIZE / 2));
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
