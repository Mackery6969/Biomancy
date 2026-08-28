package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.init.ModDataComponents;
import com.github.elenterius.biomancy.init.ModProjectiles;
import com.github.elenterius.biomancy.item.weapon.BladeProperties;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Locale;

public abstract class GunbladeItem extends GunItem {

	protected final ItemAttributeModifiers defaultBladeModifiers;
	protected final ItemAttributeModifiers defaultGunModifiers;

	protected GunbladeItem(Properties itemProperties, BladeProperties bladeProperties, GunProperties gunProperties, ModProjectiles.ConfiguredProjectile<?> projectile) {
		super(itemProperties, gunProperties, projectile);

		defaultBladeModifiers = createDefaultBladeModifiers(bladeProperties);
		defaultGunModifiers = createDefaultGunModifiers(bladeProperties);
	}

	public static GunbladeMode getMode(ItemStack stack) {
		return GunbladeMode.from(stack);
	}

	@Override
	public KeyPressResult onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		return KeyPressResult.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		GunState gunState = getGunState(stack);

		if (gunState == GunState.RELOADING) {
			cancelReload(stack, level, player);
		}

		GunbladeMode.set(stack, GunbladeMode.from(stack) == GunbladeMode.RANGED ? GunbladeMode.MELEE : GunbladeMode.RANGED);
		onChangeGunbladeMode(level, player, stack);
	}

	public void onChangeGunbladeMode(ServerLevel level, LivingEntity shooter, ItemStack stack) {}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (GunbladeMode.from(stack) == GunbladeMode.RANGED) {
			return useInRangedMode(level, player, usedHand, stack);
		}
		else {
			return useInMeleeMode(level, player, usedHand, stack);
		}
	}

	public InteractionResultHolder<ItemStack> useInRangedMode(Level level, Player player, InteractionHand usedHand, ItemStack stack) {
		return super.use(level, player, usedHand);
	}

	public InteractionResultHolder<ItemStack> useInMeleeMode(Level level, Player player, InteractionHand usedHand, ItemStack stack) {
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingUseDuration) {
		if (level.isClientSide) return;
		if (!(level instanceof ServerLevel serverLevel)) return;
		if (getGunState(stack) != GunState.SHOOTING_OR_CHARGING) return;

		if (GunbladeMode.from(stack) != GunbladeMode.RANGED) {
			shooter.releaseUsingItem();
			stopShooting(stack, serverLevel, shooter);
		}
		else {
			super.onUseTick(level, shooter, stack, remainingUseDuration);
		}
	}

	protected ItemAttributeModifiers createDefaultBladeModifiers(BladeProperties bladeProperties) {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, bladeProperties.attackDamageModifier(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, bladeProperties.attackSpeedModifier(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.build();
	}

	protected ItemAttributeModifiers createDefaultGunModifiers(BladeProperties bladeProperties) {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, Math.max(bladeProperties.attackDamageModifier() - 2d, 0.5d), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, Math.max(bladeProperties.attackSpeedModifier() - 0.2d, -3.8d), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.build();
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
		return GunbladeMode.from(stack).isBlade() ? defaultBladeModifiers : defaultGunModifiers;
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return GunbladeMode.from(stack).isBlade() && itemAbility != ItemAbilities.SWORD_SWEEP && ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(itemAbility);
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
		return !player.isCreative();
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (GunbladeMode.from(stack).isGun()) return 0.5f;

		if (state.is(Blocks.COBWEB)) {
			return 15f;
		}

		return state.is(BlockTags.SWORD_EFFICIENT) ? 1.5f : 1f;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
		return true;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		if (state.getDestroySpeed(level, pos) != 0f) {
			stack.hurtAndBreak(2, miningEntity, EquipmentSlot.MAINHAND);
		}

		return true;
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return state.is(Blocks.COBWEB);
	}

	public enum GunbladeMode implements StringRepresentable {
		MELEE, RANGED;

		public static final Codec<GunbladeMode> CODEC = StringRepresentable.fromEnum(GunbladeMode::values);

		public static GunbladeMode from(ItemStack stack) {
			return stack.getOrDefault(ModDataComponents.GUNBLADE_MODE.get(), MELEE);
		}

		public static void set(ItemStack stack, GunbladeMode mode) {
			if (mode == MELEE) stack.remove(ModDataComponents.GUNBLADE_MODE.get());
			else stack.set(ModDataComponents.GUNBLADE_MODE.get(), mode);
		}

		public boolean isBlade() {
			return this == MELEE;
		}

		public boolean isGun() {
			return this == RANGED;
		}

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
