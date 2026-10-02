package com.github.elenterius.biomancy.world.hivemind;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.init.ModFeatureFlags;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;

public final class HiveBond {

	public static final ResourceLocation DAMAGE_MODIFIER_ID = BiomancyMod.rl("hivemind_bond_damage");
	public static final ResourceLocation SPEED_MODIFIER_ID = BiomancyMod.rl("hivemind_bond_speed");
	public static final ResourceLocation ARMOR_MODIFIER_ID = BiomancyMod.rl("hivemind_bond_armor");

	private static final int TICK_INTERVAL = 40;
	private static final int STUCK_THRESHOLD = 60;
	private static final int ENERGY_PER_FLESH = 20;
	private static final int HEAL_COST = 12;

	private static final double DAMAGE_BONUS = 0.20d;
	private static final double SPEED_BONUS = 0.15d;
	private static final double ARMOR_BONUS = 2d;

	private HiveBond() {}

	public static void serverTick(Mob blob, ServerLevel level) {
		if (blob.tickCount % TICK_INTERVAL != 0) return;

		if (!ModFeatureFlags.isHivemindEnabled(level)) {
			setBonded(blob, false);
			return;
		}

		PrimordialCradleBlockEntity hive = HivemindForaging.findHive(blob);
		setBonded(blob, hive != null && !hive.isStarving());

		CarriedBiomass carried = blob.getData(ModCapabilities.CARRIED_BIOMASS);

		if (carried.isGorged() && hive == null) {
			blob.setData(ModCapabilities.CARRIED_BIOMASS, carried.absorbTribute());
			return;
		}

		if (carried.ownEnergy() >= HEAL_COST && blob.getHealth() < blob.getMaxHealth()) {
			blob.heal(1f);
			blob.setData(ModCapabilities.CARRIED_BIOMASS, carried.spendOwnEnergy(HEAL_COST));
		}
	}

	/**
	 * the flesh the hive grows can wall its own blobs in, so a blob that cannot move eats its way back out
	 */
	public static boolean tryEatThroughFlesh(Mob blob, ServerLevel level) {
		if (!ModFeatureFlags.isHivemindEnabled(level)) return false;

		BlockPos origin = blob.blockPosition();

		for (BlockPos pos : new BlockPos[]{origin, origin.above()}) {
			for (Direction direction : Direction.values()) {
				BlockPos target = pos.relative(direction);
				BlockState state = level.getBlockState(target);

				if (!isEdibleFlesh(state)) continue;

				level.destroyBlock(target, false);

				CarriedBiomass carried = blob.getData(ModCapabilities.CARRIED_BIOMASS);
				blob.setData(ModCapabilities.CARRIED_BIOMASS, carried.addOwnEnergy(ENERGY_PER_FLESH));

				level.sendParticles(ModParticleTypes.PINK_GLOW.get(), target.getX() + 0.5d, target.getY() + 0.5d, target.getZ() + 0.5d, 3, 0.25d, 0.25d, 0.25d, 0);
				return true;
			}
		}

		return false;
	}

	public static int stuckThreshold() {
		return STUCK_THRESHOLD;
	}

	private static boolean isEdibleFlesh(BlockState state) {
		return state.getBlock() instanceof FleshVeinsBlock || PrimordialEcosystem.FULL_FLESH_BLOCKS.contains(state.getBlock());
	}

	private static void setBonded(LivingEntity blob, boolean bonded) {
		applyModifier(blob, Attributes.ATTACK_DAMAGE, DAMAGE_MODIFIER_ID, DAMAGE_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, bonded);
		applyModifier(blob, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_ID, SPEED_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, bonded);
		applyModifier(blob, Attributes.ARMOR, ARMOR_MODIFIER_ID, ARMOR_BONUS, AttributeModifier.Operation.ADD_VALUE, bonded);
	}

	private static void applyModifier(LivingEntity blob, Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation, boolean bonded) {
		AttributeInstance instance = blob.getAttribute(attribute);
		if (instance == null) return;

		if (bonded) {
			if (instance.getModifier(id) == null) {
				instance.addTransientModifier(new AttributeModifier(id, amount, operation));
			}
		}
		else {
			instance.removeModifier(id);
		}
	}

}
