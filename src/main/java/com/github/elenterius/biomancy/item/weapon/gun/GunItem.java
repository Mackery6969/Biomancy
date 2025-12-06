package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import com.github.elenterius.biomancy.init.ModProjectiles;
import com.github.elenterius.biomancy.item.KeyPressListener;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.FormatUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

public abstract class GunItem extends ProjectileWeaponItem implements Gun, KeyPressListener {

	public static final Set<Enchantment> VALID_ENCHANTMENTS = Set.of(Enchantments.PUNCH_ARROWS, Enchantments.POWER_ARROWS, Enchantments.QUICK_CHARGE);

	protected final GunProperties gunProperties;
	protected final ModProjectiles.ConfiguredProjectile<? extends BaseProjectile> configuredProjectile;

	protected GunItem(Properties properties, GunProperties gunProperties, ModProjectiles.ConfiguredProjectile<? extends BaseProjectile> configuredProjectile) {
		super(properties);
		this.gunProperties = gunProperties;
		this.configuredProjectile = configuredProjectile;
	}

	protected static int getBonusReloadReduction(ItemStack stack) {
		return 5 * stack.getEnchantmentLevel(Enchantments.QUICK_CHARGE);
	}

	protected static float getBonusProjectileDamageModifier(ItemStack stack) {
		return 0.6f * stack.getEnchantmentLevel(Enchantments.POWER_ARROWS);
	}

	protected static int getBonusShootDelayReduction(ItemStack stack) {
		//int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.QUICK_SHOT.get(), stack);
		int level = 0;
		return 2 * level;
	}

	protected static int getBonusProjectileKnockBackModifier(ItemStack stack) {
		return stack.getEnchantmentLevel(Enchantments.PUNCH_ARROWS);
	}

	@Override
	public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon) {
		boolean success = configuredProjectile.shoot(level, shooter,
				baseVelocity -> modifyProjectileVelocity(baseVelocity, projectileWeapon),
				baseDamage -> modifyProjectileDamage(baseDamage, projectileWeapon),
				baseKnockBack -> modifyProjectileKnockBack(baseKnockBack, projectileWeapon),
				baseInaccuracy -> modifyProjectileInaccuracy(baseInaccuracy, projectileWeapon));

		if (success) {
			configuredProjectile.playShootSound(level, shooter);
			projectileWeapon.hurtAndBreak(getDurabilityCost(projectileWeapon), shooter, entity -> entity.broadcastBreakEvent(usedHand));
			consumeAmmo(shooter, projectileWeapon, getAmmoCost(projectileWeapon));
		}
	}

	@Override
	public KeyPressResult onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		GunState state = getGunState(stack);
		if (state == GunState.NONE && !canReload(stack, player)) {
			playSFX(level, player, SoundEvents.DISPENSER_FAIL);
			return KeyPressResult.fail(); //don't send button press to server
		}

		if (state == GunState.SHOOTING_OR_CHARGING) {
			return KeyPressResult.fail(); //don't send button press to server
		}

		return KeyPressResult.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		GunState state = getGunState(stack);

		if (state == GunState.NONE) {
			startReload(stack, level, player);
			return;
		}

		if (state == GunState.RELOADING) {
			cancelReload(stack, level, player);
		}
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return ONE_HOUR_IN_TICKS;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		GunState state = getGunState(stack);

		if (getAmmo(stack) >= getAmmoCost(stack)) {
			if (state == GunState.RELOADING && level instanceof ServerLevel serverLevel) {
				cancelReload(stack, serverLevel, player);
			}
			setGunState(stack, GunState.SHOOTING_OR_CHARGING);
			player.startUsingItem(usedHand);
			return InteractionResultHolder.consume(stack);
		}

		player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_ammo"), true);
		return InteractionResultHolder.fail(stack);
	}

	@Override
	public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingUseDuration) {
		if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
			if (getGunState(stack) != GunState.SHOOTING_OR_CHARGING) return;

			if (getAmmo(stack) < getAmmoCost(stack)) {
				if (shooter instanceof Player player) {
					player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_ammo"), true);
				}
				shooter.releaseUsingItem();
				stopShooting(stack, serverLevel, shooter);
				return;
			}

			int elapsedTime = getUseDuration(stack) - remainingUseDuration;
			int delayBetweenShots = getDelayBetweenShots(stack);

			//prevent right click spam attack by user
			if (elapsedTime == 0 && serverLevel.getGameTime() - getShootTimestamp(stack) < delayBetweenShots) {
				// play sound to indicate that the gun is "jammed"
				// playSFX(serverLevel, shooter, SoundEvents.DISPENSER_FAIL); //we don't play this sounds because it's misleading
				return;
			}

			boolean canShoot = switch (gunProperties.shootBehavior()) {
				case INSTANT -> elapsedTime % delayBetweenShots == 0;
				case ON_FULL_CHARGE -> elapsedTime % delayBetweenShots == 0 && elapsedTime > 0;
				case ON_RELEASE_INSTANT, ON_RELEASE_WITH_FULL_CHARGE -> false;
			};

			if (canShoot) {
				shoot(serverLevel, shooter, shooter.getUsedItemHand(), stack);
				stack.getOrCreateTag().putLong(SHOOT_TIMESTAMP_KEY, serverLevel.getGameTime());
			}
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity shooter, int remainingUseDuration) {

		if (level instanceof ServerLevel serverLevel && gunProperties.shootBehavior().isOnRelease()) {
			int elapsedTime = getUseDuration(stack) - remainingUseDuration;
			int delayBetweenShots = getDelayBetweenShots(stack);

			switch (gunProperties.shootBehavior()) {
				case ON_RELEASE_INSTANT -> {
					shoot(serverLevel, shooter, shooter.getUsedItemHand(), stack);
					stack.getOrCreateTag().putLong(SHOOT_TIMESTAMP_KEY, serverLevel.getGameTime());
				}
				case ON_RELEASE_WITH_FULL_CHARGE -> {
					if (elapsedTime >= delayBetweenShots && serverLevel.getGameTime() - getShootTimestamp(stack) < delayBetweenShots) {
						shoot(serverLevel, shooter, shooter.getUsedItemHand(), stack);
						stack.getOrCreateTag().putLong(SHOOT_TIMESTAMP_KEY, serverLevel.getGameTime());
					}
				}
				default -> {}
			}
		}

		setGunState(stack, GunState.NONE);

		if (shooter instanceof Player player) {
			player.awardStat(Stats.ITEM_USED.get(this));
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (!level.isClientSide && level instanceof ServerLevel serverLevel && entity instanceof LivingEntity livingEntity) {
			if (getGunState(stack) != GunState.RELOADING) return;

			if (isSelected && canReload(stack, livingEntity)) {
				long elapsedTime = serverLevel.getGameTime() - getReloadStartTime(stack);
				if (elapsedTime < 0) return;

				onReloadTick(stack, serverLevel, livingEntity, elapsedTime);

				if (elapsedTime >= getReloadDurationTicks(stack)) {
					finishReload(stack, serverLevel, livingEntity);
				}
				return;
			}

			stopReload(stack, serverLevel, livingEntity);
		}
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return VALID_ENCHANTMENTS.contains(enchantment) || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public float getAccuracy(ItemStack stack) {
		return gunProperties.accuracy();
	}

	@Override
	public int getDelayBetweenShots(ItemStack stack) {
		return gunProperties.delayBetweenShots() - getBonusShootDelayReduction(stack);
	}

	@Override
	public int getReloadDurationTicks(ItemStack stack) {
		return gunProperties.reloadDurationTicks() - getBonusReloadReduction(stack);
	}

	@Override
	public float modifyProjectileDamage(float baseDamage, ItemStack stack) {
		return baseDamage + gunProperties.projectileDamageModifier() + getBonusProjectileDamageModifier(stack);
	}

	@Override
	public int modifyProjectileKnockBack(int baseKnockBack, ItemStack stack) {
		return baseKnockBack + getBonusProjectileKnockBackModifier(stack);
	}

	@Override
	public GunProperties.ShootBehavior getShootBehavior() {
		return gunProperties.shootBehavior();
	}

	@Override
	public int getMaxAmmo(ItemStack stack) {
		//		int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MAX_AMMO.get(), stack);
		int level = 0;
		return Mth.floor(gunProperties.maxAmmo() + gunProperties.maxAmmo() * 0.5f * level);
	}

	@Override
	public void stopShooting(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		if (gunProperties.isAutoReload()) startReload(stack, level, shooter);
	}

	@Override
	public ItemStack findAmmoInInv(ItemStack stack, LivingEntity shooter) {
		ItemStack ammo = shooter.getProjectile(stack); //vanilla mobs only look for held ammo (i.e. in off-hand)
		if (ammo.getItem() == Items.ARROW) { //if mobs/creative players can't find any ammo they will return arrows
			if (shooter instanceof Player player && player.getAbilities().instabuild) {
				ammo = ammo.copy();
				ammo.setCount(getReloadCost(stack));
				return ammo;
			}

			if (getSupportedHeldProjectiles().test(ammo) || getAllSupportedProjectiles().test(ammo)) return ammo;
			return ItemStack.EMPTY;
		}
		return ammo;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		appendGunStats(stack, tooltip);
		tooltip.add(ComponentUtil.EMPTY_LINE);
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getActionText("reload")).withStyle(TextStyles.DARK_GRAY));
	}

	public void appendGunStats(ItemStack stack, List<Component> tooltip) {
		DecimalFormat df = FormatUtil.getDoubleFormatter();

		float velocity = modifyProjectileVelocity(configuredProjectile.velocity(), stack);
		float bonusVelocity = velocity - configuredProjectile.velocity();
		tooltip.add(TextComponentUtil.getTooltipText("projectile_speed").append(String.format(": %s m/s ", df.format(velocity * 20))).append(formatBonusValue(df, bonusVelocity * 20)).withStyle(ChatFormatting.GRAY));

		float damage = modifyProjectileDamage(configuredProjectile.damage(), stack);
		float bonusDamage = damage - configuredProjectile.damage();
		tooltip.add(TextComponentUtil.getTooltipText("projectile_damage").append(String.format(": %s ", df.format(damage))).append(formatBonusValue(df, bonusDamage)).withStyle(ChatFormatting.GRAY));

		int knockBack = modifyProjectileKnockBack(configuredProjectile.knockback(), stack);
		if (knockBack != 0) {
			int bonusValue = knockBack - configuredProjectile.knockback();
			tooltip.add(TextComponentUtil.getTooltipText("projectile_knock_back").append(String.format(": %s ", df.format(knockBack))).append(formatBonusValue(df, bonusValue)).withStyle(ChatFormatting.GRAY));
		}

		float inaccuracy = modifyProjectileInaccuracy(configuredProjectile.inaccuracy(), stack);
		float accuracy = -MAX_INACCURACY * inaccuracy + MAX_INACCURACY;
		float bonusAccuracy = -1f * (inaccuracy - configuredProjectile.inaccuracy());
		tooltip.add(TextComponentUtil.getTooltipText("accuracy").append(String.format(": %s ", df.format(accuracy))).append(formatBonusValue(df, bonusAccuracy)).withStyle(ChatFormatting.GRAY));

		float fireRate = getFireRate(stack);
		float bonusFireRate = fireRate - (ONE_SECOND_IN_TICKS / (float) gunProperties.delayBetweenShots());
		tooltip.add(TextComponentUtil.getTooltipText("fire_rate").append(String.format(": %s rps ", df.format(fireRate))).append(formatBonusValue(df, bonusFireRate)).withStyle(ChatFormatting.GRAY));

		float reloadDurationSeconds = getReloadDurationTicks(stack) / (float) ONE_SECOND_IN_TICKS;
		float bonusReloadReduction = reloadDurationSeconds - (gunProperties.reloadDurationTicks() / (float) ONE_SECOND_IN_TICKS);
		tooltip.add(TextComponentUtil.getTooltipText("reload_time").append(String.format(": %ss ", df.format(reloadDurationSeconds))).append(formatBonusValue(df, bonusReloadReduction, true)).withStyle(ChatFormatting.GRAY));

		tooltip.add(ComponentUtil.EMPTY_LINE);
		tooltip.add(TextComponentUtil.getTooltipText("ammo").append(String.format(": %d/%d ", getAmmo(stack), getMaxAmmo(stack))).withStyle(ChatFormatting.GRAY));
	}

	private Component formatBonusValue(DecimalFormat df, float value) {
		return formatBonusValue(df, value, false);
	}

	private Component formatBonusValue(DecimalFormat df, float value, boolean inverted) {
		if (value == 0f) return ComponentUtil.EMPTY;

		boolean isBeneficial = (inverted && value < 0f) || (!inverted && value > 0f);
		Style style = isBeneficial ? TextStyles.LIME : TextStyles.ERROR;

		String formattedDecimal = (value > 0f ? "+" : "") + df.format(value);
		MutableComponent component = ComponentUtil.literal(formattedDecimal).withStyle(style);

		return ComponentUtil.mutable().append("(").append(component).append(")").withStyle(TextStyles.DARK_GRAY);
	}

}
