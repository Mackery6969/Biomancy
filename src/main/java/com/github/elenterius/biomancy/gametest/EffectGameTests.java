package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.serum.FrenzySerum;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

import static com.github.elenterius.biomancy.gametest.GameTestTemplates.EMPTY_PLATFORM;
import static com.github.elenterius.biomancy.gametest.GameTestTemplates.PLATFORM_CENTER;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EffectGameTests {

	private EffectGameTests() {}

	@GameTest(template = EMPTY_PLATFORM)
	public static void reloadedFrenzyCausesWithdrawalWhenItExpires(GameTestHelper helper) {
		LivingEntity target = addReloadedFrenzy(helper, 1);
		target.tick();
		assertWithdrawal(helper, target);
		helper.succeed();
	}

	@GameTest(template = EMPTY_PLATFORM)
	public static void reloadedFrenzyCausesWithdrawalWhenCured(GameTestHelper helper) {
		LivingEntity target = addReloadedFrenzy(helper, 200);
		helper.assertTrue(target.removeEffectsCuredBy(EffectCures.MILK), "milk did not remove the reloaded Frenzy effect");
		assertWithdrawal(helper, target);
		helper.succeed();
	}

	@GameTest(template = EMPTY_PLATFORM)
	public static void primordialEaterBlobsRejectReloadedInfestation(GameTestHelper helper) {
		MobEffectInstance infestation = reload(helper, new MobEffectInstance(ModMobEffects.PRIMORDIAL_INFESTATION, 200));
		List<EntityType<? extends Mob>> blobTypes = List.of(ModEntityTypes.PRIMORDIAL_FLESH_BLOB.get(), ModEntityTypes.PRIMORDIAL_HUNGRY_FLESH_BLOB.get());
		for (EntityType<? extends Mob> blobType : blobTypes) {
			Mob blob = helper.spawnWithNoFreeWill(blobType, PLATFORM_CENTER);
			helper.assertFalse(blob.canBeAffected(infestation), EntityType.getKey(blobType) + " accepted Primordial Infestation");
		}
		helper.succeed();
	}

	private static LivingEntity addReloadedFrenzy(GameTestHelper helper, int duration) {
		LivingEntity target = helper.spawnWithNoFreeWill(EntityType.COW, PLATFORM_CENTER);
		MobEffectInstance frenzy = reload(helper, new MobEffectInstance(ModMobEffects.FRENZY, duration, 1));
		helper.assertTrue(target.addEffect(frenzy), "target rejected the reloaded Frenzy effect");
		return target;
	}

	private static MobEffectInstance reload(GameTestHelper helper, MobEffectInstance original) {
		MobEffectInstance reloaded = MobEffectInstance.load((CompoundTag) original.save());
		helper.assertTrue(reloaded != null, "effect failed to reload from saved data");
		helper.assertTrue(reloaded.getEffect() != original.getEffect(), "test must exercise a deserialized registry holder");
		return reloaded;
	}

	private static void assertWithdrawal(GameTestHelper helper, LivingEntity target) {
		helper.assertFalse(target.hasEffect(ModMobEffects.FRENZY), "Frenzy remained active");
		MobEffectInstance withdrawal = target.getEffect(ModMobEffects.WITHDRAWAL);
		helper.assertTrue(withdrawal != null, "removing reloaded Frenzy did not apply Withdrawal");
		helper.assertValueEqual(withdrawal.getAmplifier(), 1, "Withdrawal amplifier");
		helper.assertValueEqual(withdrawal.getDuration(), FrenzySerum.DEFAULT_DURATION_TICKS / 2 + 30 * 20, "Withdrawal duration");
	}

}
