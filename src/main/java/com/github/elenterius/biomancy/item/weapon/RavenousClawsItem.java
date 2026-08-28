package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.api.livingtool.LivingToolState;
import com.github.elenterius.biomancy.client.render.item.ravenousclaws.RavenousClawsRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.ItemCharge;
import com.github.elenterius.biomancy.item.MeleeDamageSourceProviderItem;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.CombatUtil;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.FormatUtil;
import com.github.elenterius.biomancy.util.MobUtil;
import com.github.elenterius.biomancy.util.sounds.SoundUtil;
import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.util.Lazy;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

public class RavenousClawsItem extends LivingClawsItem implements GeoItem, ItemCharge, MeleeDamageSourceProviderItem {
	protected static final ResourceLocation BASE_ATTACK_KNOCKBACK_ID = BiomancyMod.rl("ravenous_claws_attack_knockback");
	private final Lazy<ItemAttributeModifiers> brokenAttributes;
	private final Lazy<ItemAttributeModifiers> dormantAttributes;
	private final Lazy<ItemAttributeModifiers> awakenedAttributes;
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public RavenousClawsItem(Tier tier, float attackDamage, float attackSpeed, int maxNutrients, Properties properties) {
		super(tier, 0, 0, 0, maxNutrients, properties);

		float attackSpeedModifier = (float) (attackSpeed - Attributes.ATTACK_SPEED.value().getDefaultValue());
		brokenAttributes = Lazy.of(() -> createDefaultAttributeModifiers(0, 0, -0.5f));
		dormantAttributes = Lazy.of(() -> createDefaultAttributeModifiers(-1 + attackDamage, attackSpeedModifier, 0));
		awakenedAttributes = Lazy.of(() -> createDefaultAttributeModifiers(-1 + attackDamage + 2.5f, attackSpeedModifier, 0.5f));
	}

	private static void playClawSwipeFX(LivingEntity attacker) {
		attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSoundEvents.CLAWS_ATTACK_STRONG.get(), attacker.getSoundSource(), 1f, 1f + attacker.getRandom().nextFloat() * 0.5f);
		if (attacker.level() instanceof ServerLevel serverLevel) {
			double xOffset = -Mth.sin(attacker.getYRot() * Mth.DEG_TO_RAD);
			double zOffset = Mth.cos(attacker.getYRot() * Mth.DEG_TO_RAD);
			serverLevel.sendParticles(ModParticleTypes.BLOODY_CLAWS_ATTACK.get(), attacker.getX() + xOffset, attacker.getY(0.52f), attacker.getZ() + zOffset, 0, xOffset, 0, zOffset, 0);
		}
	}

	private static void playBloodExplosionFX(LivingEntity target) {
		if (target.level() instanceof ServerLevel serverLevel) {
			float w = target.getBbWidth() * 0.45f;
			double x = serverLevel.getRandom().nextGaussian() * w;
			double y = serverLevel.getRandom().nextGaussian() * 0.2d;
			double z = serverLevel.getRandom().nextGaussian() * w;
			serverLevel.sendParticles(ModParticleTypes.FALLING_BLOOD.get(), target.getX(), target.getY(0.5f), target.getZ(), 20, x, y, z, 0.25);
		}
	}

	@Override
	protected ItemAttributeModifiers createDefaultAttributeModifiers(float attackDamageModifier, float attackSpeedModifier, float attackRangeModifier) {
		ItemAttributeModifiers modifiers = super.createDefaultAttributeModifiers(attackDamageModifier, attackSpeedModifier, attackRangeModifier);
		return modifiers.withModifierAdded(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(BASE_ATTACK_KNOCKBACK_ID, 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.MAINHAND);
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
		return switch (getLivingToolState(stack)) {
			case BROKEN -> brokenAttributes.get();
			case DORMANT -> dormantAttributes.get();
			case AWAKENED -> awakenedAttributes.get();
		};
	}

	@Override
	public boolean hasNutrients(ItemStack container) {
		return getNutrients(container) > getLivingToolActionCost(container, LivingToolState.AWAKENED, null);
	}

	@Override
	public KeyPressResult onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		if (!hasCharge(stack)) {
			player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_blood_charge"), true);
			player.playSound(ModSoundEvents.FLESHKIN_NO.get(), 1f, 1f + player.level().getRandom().nextFloat() * 0.4f);
			return KeyPressResult.fail();
		}

		return KeyPressResult.success(flags);
	}

	@Override
	public int getMaxCharge(ItemStack container) {
		return 50;
	}

	@Override
	public void onChargeChanged(ItemStack livingTool, int oldValue, int newValue) {
		if (newValue <= 0 && getLivingToolState(livingTool) == LivingToolState.AWAKENED) {
			setLivingToolState(livingTool, LivingToolState.DORMANT);
		}
	}

	@Override
	public void onNutrientsChanged(ItemStack livingTool, int oldValue, int newValue) {
		LivingToolState prevState = getLivingToolState(livingTool);
		LivingToolState state = prevState;

		if (newValue <= 0) {
			if (state != LivingToolState.BROKEN) setLivingToolState(livingTool, LivingToolState.BROKEN);
			return;
		}

		if (state == LivingToolState.BROKEN) {
			state = LivingToolState.DORMANT;
		}

		int maxCost = getLivingToolMaxActionCost(livingTool, state);
		if (newValue < maxCost && state == LivingToolState.DORMANT) state = LivingToolState.BROKEN;

		if (state != prevState) setLivingToolState(livingTool, state);
	}

	@Override
	public void updateLivingToolState(ItemStack livingTool, ServerLevel level, Player player) {
		GeoItem.getOrAssignId(livingTool, level);

		LivingToolState state = getLivingToolState(livingTool);
		boolean hasNutrients = hasNutrients(livingTool);

		if (!hasNutrients) {
			if (state != LivingToolState.BROKEN) {
				setLivingToolState(livingTool, LivingToolState.BROKEN);
				SoundUtil.Server.playItemSound(level, player, ModSoundEvents.FLESHKIN_BREAK.get());
			}
			return;
		}

		switch (state) {
			case BROKEN, AWAKENED -> {
				setLivingToolState(livingTool, LivingToolState.DORMANT);
				SoundUtil.Server.playItemSound(level, player, ModSoundEvents.FLESHKIN_BECOME_DORMANT.get());
			}
			case DORMANT -> {
				if (hasCharge(livingTool)) {
					setLivingToolState(livingTool, LivingToolState.AWAKENED);
					SoundUtil.Server.playItemSound(level, player, ModSoundEvents.FLESHKIN_BECOME_AWAKENED.get());
				}
			}
		}
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack livingTool, Slot slot, ClickAction action, Player player) {
		if (player.level() instanceof ServerLevel serverLevel) GeoItem.getOrAssignId(livingTool, serverLevel);
		return super.overrideStackedOnOther(livingTool, slot, action, player);
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack livingTool, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (player.level() instanceof ServerLevel serverLevel) GeoItem.getOrAssignId(livingTool, serverLevel);
		return super.overrideOtherStackedOnMe(livingTool, other, slot, action, player, access);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		if (itemAbility == ItemAbilities.SWORD_SWEEP) return false;
		return super.canPerformAction(stack, itemAbility);
	}

	@Override
	public @Nullable DamageSource getMeleeDamageSource(ItemStack stack, Entity target, LivingEntity attacker, float attackStrengthScale) {
		if (attackStrengthScale <= 0.9f) return null;

		return switch (getLivingToolState(stack)) {
			case BROKEN -> null;
			case DORMANT, AWAKENED -> {
				DamageSource damageSource = ModDamageSources.slash(attacker.level(), attacker);
				if (target.isInvulnerableTo(damageSource)) yield null; //use default melee damagesource as fallback
				yield damageSource;
			}
		};
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker.level().isClientSide) return true;

		LivingToolState livingToolState = getLivingToolState(stack);
		boolean isFullAttackStrength = !(attacker instanceof Player player) || player.getAttackStrengthScale(0.5f) >= 0.9f;
		boolean isNotCreativePlayer = !MobUtil.isCreativePlayer(attacker);

		switch (livingToolState) {
			case BROKEN -> { /* do nothing */ }
			case DORMANT -> {
				if (isNotCreativePlayer) {
					consumeNutrients(stack, 1);
				}

				if (isFullAttackStrength) {
					playClawSwipeFX(attacker);
					if (attacker.getRandom().nextInt(12) == 0) { //8.3%
						attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSoundEvents.CLAWS_ATTACK_BLEED_PROC.get(), attacker.getSoundSource(), 1f, 1f);

						CombatUtil.applyBleedEffect(target, 20);
						if (isNotCreativePlayer) {
							consumeNutrients(stack, 1);
						}
					}

					target.invulnerableTime = 0; //make victims vulnerable the next attack regardless of the damage amount
				}

				if (target.isDeadOrDying()) {
					addCharge(stack, 5);
				}
			}
			case AWAKENED -> {
				if (isNotCreativePlayer) {
					consumeCharge(stack, 1);
				}

				if (isFullAttackStrength) {
					playClawSwipeFX(attacker);
					if (attacker.getRandom().nextInt(5) == 0) { //20%
						attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSoundEvents.CLAWS_ATTACK_BLEED_PROC.get(), attacker.getSoundSource(), 1f, 1f);

						if (CombatUtil.getBleedEffectLevel(target) < 2) {
							playBloodExplosionFX(target);
							CombatUtil.hurtWithBleed(target, 0.1f * target.getMaxHealth());

							if (isNotCreativePlayer) {
								consumeCharge(stack, 4);
							}
						}

						CombatUtil.applyBleedEffect(target, 20);
						if (isNotCreativePlayer) {
							consumeCharge(stack, 1);
						}
					}

					target.invulnerableTime = 0; //make victims vulnerable the next attack regardless of the damage amount
				}
			}
		}

		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
		tooltip.add(ComponentUtil.EMPTY_LINE);

		appendLivingToolTooltip(stack, tooltip);

		if (stack.isEnchanted()) {
			tooltip.add(ComponentUtil.EMPTY_LINE);
		}
	}

	@Override
	public void appendLivingToolTooltip(ItemStack stack, List<Component> tooltip) {
		LivingToolState livingToolState = getLivingToolState(stack);

		switch (livingToolState) {
			case BROKEN -> {
				//do nothing
			}
			case DORMANT -> {
				tooltip.add(TextComponentUtil.getAbilityText("bleed_proc").append(" (8% chance)").withStyle(ChatFormatting.GRAY));
				tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getAbilityText("bleed_proc.desc")).withStyle(ChatFormatting.DARK_GRAY));
				tooltip.add(ComponentUtil.EMPTY_LINE);
			}
			case AWAKENED -> {
				tooltip.add(TextComponentUtil.getAbilityText("bleed_proc").append(" (20% chance)").withStyle(ChatFormatting.GRAY));
				tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getAbilityText("bleed_proc.desc")).withStyle(ChatFormatting.DARK_GRAY));
				tooltip.add(TextComponentUtil.getAbilityText("blood_explosion").append(" (20% chance)").withStyle(ChatFormatting.GRAY));
				tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getAbilityText("blood_explosion.desc")).withStyle(ChatFormatting.DARK_GRAY));
				tooltip.add(ComponentUtil.EMPTY_LINE);
			}
		}

		DecimalFormat df = FormatUtil.getIntegerFormatter();
		tooltip.add(TextComponentUtil.getTooltipText("nutrients_fuel").withStyle(ChatFormatting.GRAY));
		tooltip.add(ComponentUtil.literal(" %s/%s".formatted(df.format(getNutrients(stack)), df.format(getMaxNutrients(stack)))).withStyle(TextStyles.NUTRIENTS));
		tooltip.add(TextComponentUtil.getTooltipText("blood_charge").withStyle(ChatFormatting.GRAY));
		tooltip.add(ComponentUtil.literal(" %s/%s".formatted(df.format(getCharge(stack)), df.format(getMaxCharge(stack)))).withStyle(TextStyles.ERROR));

		switch (livingToolState) {
			case BROKEN -> {
				tooltip.add(ComponentUtil.EMPTY_LINE);
				tooltip.add(livingToolState.getTooltip());
			}
			case DORMANT -> {
				tooltip.add(ComponentUtil.EMPTY_LINE);
				tooltip.add(livingToolState.getTooltip().withStyle(TextStyles.ITALIC_GRAY));
				tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getActionText("enable_awakened_mode")));
			}
			case AWAKENED -> {
				tooltip.add(ComponentUtil.EMPTY_LINE);
				tooltip.add(livingToolState.getTooltip().withStyle(TextStyles.ITALIC_GRAY));
				tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getActionText("disable_awakened_mode")));
			}
		}
	}

	@Override
	public int getLivingToolActionCost(ItemStack livingTool, LivingToolState state, ItemAbility itemAbility) {
		return switch (state) {
			case AWAKENED, DORMANT -> 1;
			case BROKEN -> 0;
		};
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final RavenousClawsRenderer renderer = new RavenousClawsRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

	private PlayState handleAnimationState(AnimationState<RavenousClawsItem> animationState) {
		ItemStack stack = animationState.getData(DataTickets.ITEMSTACK);
		LivingToolState toolState = stack != null ? getLivingToolState(stack) : LivingToolState.BROKEN;

		AnimationController<RavenousClawsItem> controller = animationState.getController();
		switch (toolState) {
			case DORMANT -> Animations.setDormant(controller);
			case AWAKENED -> Animations.setAwakened(controller);
			case BROKEN -> Animations.setBroken(controller);
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, Animations.MAIN_CONTROLLER, 1, this::handleAnimationState));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	protected static class Animations {
		public static final String MAIN_CONTROLLER = "main";

		protected static final RawAnimation DORMANT = RawAnimation.begin().thenLoop("ravenous_claws.dormant");
		protected static final RawAnimation TO_SLEEP_TRANSITION = RawAnimation.begin().thenPlay("ravenous_claws.tosleep").thenLoop("ravenous_claws.dormant");
		protected static final RawAnimation BROKEN = RawAnimation.begin().thenLoop("ravenous_claws.broken");
		protected static final RawAnimation WAKEUP_TRANSITION = RawAnimation.begin().thenPlay("ravenous_claws.wakeup").thenLoop("ravenous_claws.awakened");
		protected static final RawAnimation AWAKENED = RawAnimation.begin().thenLoop("ravenous_claws.awakened");

		private Animations() {}

		protected static void setDormant(AnimationController<?> controller) {
			AnimationProcessor.QueuedAnimation queued = controller.getCurrentAnimation();
			if (queued == null) {
				controller.setAnimation(DORMANT);
				return;
			}

			if (!queued.animation().name().equals("ravenous_claws.dormant")) {
				controller.setAnimation(TO_SLEEP_TRANSITION);
				return;
			}

			controller.setAnimation(DORMANT);
		}

		protected static void setAwakened(AnimationController<?> controller) {
			AnimationProcessor.QueuedAnimation queued = controller.getCurrentAnimation();
			if (queued == null) {
				controller.setAnimation(AWAKENED);
				return;
			}

			if (!queued.animation().name().equals("ravenous_claws.awakened")) {
				controller.setAnimation(WAKEUP_TRANSITION);
				return;
			}

			controller.setAnimation(AWAKENED);
		}

		public static void setBroken(AnimationController<RavenousClawsItem> controller) {
			controller.setAnimation(BROKEN);
		}
	}

}
