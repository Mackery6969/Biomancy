package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.serum.FrenzySerum;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EffectGameTests {

	private EffectGameTests() {}

	@GameTest(template = "empty_platform")
	public static void reloadedFrenzyCausesWithdrawalWhenItExpires(GameTestHelper helper) {
		LivingEntity target = addReloadedFrenzy(helper, 1);
		target.tick();
		assertWithdrawal(helper, target);
		helper.succeed();
	}

	@GameTest(template = "empty_platform")
	public static void reloadedFrenzyCausesWithdrawalWhenCured(GameTestHelper helper) {
		LivingEntity target = addReloadedFrenzy(helper, 200);
		helper.assertTrue(target.removeEffectsCuredBy(EffectCures.MILK), "milk did not remove the reloaded Frenzy effect");
		assertWithdrawal(helper, target);
		helper.succeed();
	}

	private static LivingEntity addReloadedFrenzy(GameTestHelper helper, int duration) {
		LivingEntity target = helper.spawnWithNoFreeWill(EntityType.COW, new BlockPos(4, 2, 4));
		MobEffectInstance original = new MobEffectInstance(ModMobEffects.FRENZY, duration, 1);
		MobEffectInstance reloaded = MobEffectInstance.load((CompoundTag) original.save());
		helper.assertTrue(reloaded != null, "Frenzy effect failed to reload from saved data");
		helper.assertTrue(reloaded.getEffect() != ModMobEffects.FRENZY, "test must exercise a deserialized registry holder");
		helper.assertTrue(target.addEffect(reloaded), "target rejected the reloaded Frenzy effect");
		return target;
	}

	private static void assertWithdrawal(GameTestHelper helper, LivingEntity target) {
		helper.assertTrue(!target.hasEffect(ModMobEffects.FRENZY), "Frenzy remained active");
		MobEffectInstance withdrawal = target.getEffect(ModMobEffects.WITHDRAWAL);
		helper.assertTrue(withdrawal != null, "removing reloaded Frenzy did not apply Withdrawal");
		helper.assertTrue(withdrawal.getAmplifier() == 1, "Withdrawal lost the Frenzy amplifier");
		helper.assertTrue(withdrawal.getDuration() == FrenzySerum.DEFAULT_DURATION_TICKS / 2 + 30 * 20,
				"Withdrawal has an unexpected duration");
	}

}
