package com.github.elenterius.biomancy.gametest;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.projectile.ImpalerProjectile;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.armor.LivingArmorItem;
import com.github.elenterius.biomancy.item.armor.WarriorArmorItem;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.github.elenterius.biomancy.util.CombatUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.function.ToIntFunction;

import static com.github.elenterius.biomancy.gametest.GameTestTemplates.EMPTY_PLATFORM;
import static com.github.elenterius.biomancy.gametest.GameTestTemplates.PLATFORM_CENTER;

@GameTestHolder(BiomancyMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ItemRegressionGameTests {

	private ItemRegressionGameTests() {}

	@GameTest(template = EMPTY_PLATFORM)
	public static void dispenserInjectorAppliesStoredPotionEffects(GameTestHelper helper) {
		BlockPos pos = PLATFORM_CENTER;
		LivingEntity target = helper.spawnWithNoFreeWill(EntityType.COW, pos);
		ItemStack injector = ModItems.INJECTOR.get().getDefaultInstance();
		ItemStack vials = ModItems.POTION_SERUM.get().getInstanceFrom(Potions.SWIFTNESS,
				List.of(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1))).copyWithCount(2);
		InjectorItem.getItemHandler(injector).orElseThrow().setStack(vials);

		helper.assertTrue(InjectorItem.tryInjectLivingEntity(helper.getLevel(), helper.absolutePos(pos), injector),
				"dispenser injector rejected an unarmored cow");
		helper.assertLivingEntityHasMobEffect(target, MobEffects.MOVEMENT_SPEED, 0);
		helper.assertLivingEntityHasMobEffect(target, MobEffects.DAMAGE_RESISTANCE, 1);
		helper.assertValueEqual(InjectorItem.getItemHandler(injector).orElseThrow().getAmount(), 1,
				"remaining serum doses");
		helper.succeed();
	}

	@GameTest(template = EMPTY_PLATFORM)
	public static void impalerConsumesPiercingAcrossHitsAndReload(GameTestHelper helper) {
		LivingEntity target = helper.spawnWithNoFreeWill(EntityType.COW, PLATFORM_CENTER);
		TestImpaler projectile = new TestImpaler(helper.getLevel());
		projectile.setPierceLevel(2);
		projectile.hit(target);
		helper.assertValueEqual(projectile.getPierceLevel(), (byte) 1, "piercing after first hit");

		CompoundTag saved = new CompoundTag();
		projectile.addAdditionalSaveData(saved);
		TestImpaler restored = new TestImpaler(helper.getLevel());
		restored.readAdditionalSaveData(saved);
		helper.assertValueEqual(restored.getPierceLevel(), (byte) 1, "piercing after reload");
		restored.hit(target);
		helper.assertValueEqual(restored.getPierceLevel(), (byte) 0, "piercing after second hit");
		restored.hit(target);
		helper.assertTrue(restored.isRemoved(), "impaler survived after exhausting its piercing");
		helper.succeed();
	}

	@GameTest(template = EMPTY_PLATFORM)
	public static void warriorArmorRecognizesFourPiecePlayerSet(GameTestHelper helper) {
		Player player = equipWarriorArmor(helper);
		helper.assertTrue(CombatUtil.hasFullArmorSetEquipped(player, WarriorArmorItem.ARMOR_SET_PREDICATE),
				"four warrior armor pieces were not recognized as a full player set");
		player.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
		helper.assertFalse(CombatUtil.hasFullArmorSetEquipped(player, WarriorArmorItem.ARMOR_SET_PREDICATE),
				"warrior armor was considered complete without boots");
		helper.succeed();
	}

	@GameTest(template = EMPTY_PLATFORM)
	public static void armorNutrientCostSkipsDepletedPieces(GameTestHelper helper) {
		Player player = equipWarriorArmor(helper);
		setNutrients(player, slot -> slot == EquipmentSlot.FEET ? 0 : 100);

		consumeNutrients(player, 100);
		helper.assertValueEqual(totalNutrients(player), 200, "nutrients after paying roar cost with depleted boots");
		for (ItemStack stack : player.getArmorSlots()) {
			LivingArmorItem armor = (LivingArmorItem) stack.getItem();
			int nutrients = armor.getNutrients(stack);
			if (armor.getType().getSlot() == EquipmentSlot.FEET) {
				helper.assertValueEqual(nutrients, 0, "depleted boots nutrients");
			}
			else {
				helper.assertTrue(nutrients == 66 || nutrients == 67, "nutrient cost was not balanced across the three charged pieces");
			}
		}

		consumeNutrients(player, 200);
		helper.assertValueEqual(totalNutrients(player), 0, "nutrients after consuming the uneven remaining reserves");
		helper.succeed();
	}

	@GameTest(template = EMPTY_PLATFORM)
	public static void armorNutrientCostSmallerThanChargedPieceCountTerminates(GameTestHelper helper) {
		Player player = equipWarriorArmor(helper);
		setNutrients(player, slot -> slot == EquipmentSlot.FEET || slot == EquipmentSlot.LEGS ? 0 : 5);

		consumeNutrients(player, 1);
		helper.assertValueEqual(totalNutrients(player), 9, "nutrients after paying a cost of one across two charged pieces");
		helper.succeed();
	}

	private static void setNutrients(Player player, ToIntFunction<EquipmentSlot> nutrientsBySlot) {
		for (ItemStack stack : player.getArmorSlots()) {
			LivingArmorItem armor = (LivingArmorItem) stack.getItem();
			armor.setNutrients(stack, nutrientsBySlot.applyAsInt(armor.getType().getSlot()));
		}
	}

	private static void consumeNutrients(Player player, int amount) {
		LivingArmorItem.consumeNutrientsFromEquippedArmor(player, amount, WarriorArmorItem.ARMOR_SET_PREDICATE);
	}

	private static int totalNutrients(Player player) {
		return LivingArmorItem.getNutrientsFromEquippedArmor(player, WarriorArmorItem.ARMOR_SET_PREDICATE);
	}

	private static Player equipWarriorArmor(GameTestHelper helper) {
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		player.setItemSlot(EquipmentSlot.HEAD, ModItems.WARRIOR_ARMOR_HELMET.get().getDefaultInstance());
		player.setItemSlot(EquipmentSlot.CHEST, ModItems.WARRIOR_ARMOR_CHESTPLATE.get().getDefaultInstance());
		player.setItemSlot(EquipmentSlot.LEGS, ModItems.WARRIOR_ARMOR_LEGGINGS.get().getDefaultInstance());
		player.setItemSlot(EquipmentSlot.FEET, ModItems.WARRIOR_ARMOR_BOOTS.get().getDefaultInstance());
		return player;
	}

	private static final class TestImpaler extends ImpalerProjectile {
		private TestImpaler(Level level) {
			super(level, 0, 0, 0);
			setDamage(0);
		}

		private void hit(LivingEntity target) {
			onHitEntity(new EntityHitResult(target));
		}
	}

}
