package com.github.elenterius.biomancy.item.weapon.gun;

import net.minecraft.core.Holder;
import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.livingtool.SimpleLivingTool;
import com.github.elenterius.biomancy.client.render.item.caustic_gunblade.CausticGunbladeRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.item.CriticalHitListener;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.item.MeleeDamageSourceProviderItem;
import com.github.elenterius.biomancy.item.weapon.BladeProperties;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.animation.TriggerableAnimation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.ItemAbility;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CausticGunbladeItem extends GunbladeItem implements SimpleLivingTool, CriticalHitListener, MeleeDamageSourceProviderItem, ItemTooltipStyleProvider, GeoItem {

	protected final ItemAttributeModifiers disabledBladeModifiers;
	protected final ItemAttributeModifiers disabledGunModifiers;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	private final int maxNutrients;

	String LAST_USE_TIMESTAMP_KEY = "last_use_timestamp";

	public CausticGunbladeItem(int maxNutrients, Properties itemProperties) {
		super(itemProperties,
				BladeProperties.builder().attackDamage(6).attackSpeed(1.2f).build(),
				GunProperties.builder()
						.fireRate(0.5f)
						.maxAmmo(10).reloadDuration(10 * 20).autoReload()
						.build(),
				ModProjectiles.ACID_BLOB);

		this.maxNutrients = maxNutrients;

		disabledBladeModifiers = keepOnly(defaultBladeModifiers, Attributes.ATTACK_SPEED);
		disabledGunModifiers = keepOnly(defaultGunModifiers, Attributes.ATTACK_SPEED);

		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	private static ItemAttributeModifiers keepOnly(ItemAttributeModifiers source, Holder<Attribute> attribute) {
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		for (ItemAttributeModifiers.Entry entry : source.modifiers()) {
			if (entry.attribute().equals(attribute)) {
				builder.add(entry.attribute(), entry.modifier(), entry.slot());
			}
		}
		return builder.build();
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
		boolean isMeleeMode = GunbladeMode.from(stack).isBlade();

		if (hasNutrients(stack)) {
			return isMeleeMode ? defaultBladeModifiers : defaultGunModifiers;
		}
		return isMeleeMode ? disabledBladeModifiers : disabledGunModifiers;
	}

	private static void playSwipeFX(LivingEntity attacker) {
		attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSoundEvents.CLAWS_ATTACK_STRONG.get(), attacker.getSoundSource(), 1f, 1f + attacker.getRandom().nextFloat() * 0.5f);
		if (attacker.level() instanceof ServerLevel serverLevel) {
			double xOffset = -Mth.sin(attacker.getYRot() * Mth.DEG_TO_RAD);
			double zOffset = Mth.cos(attacker.getYRot() * Mth.DEG_TO_RAD);
			serverLevel.sendParticles(ModParticleTypes.CORROSIVE_SWIPE_ATTACK.get(), attacker.getX() + xOffset, attacker.getY(0.52f), attacker.getZ() + zOffset, 0, xOffset, 0, zOffset, 0);
		}
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return itemStack -> false;
	}

	@Override
	public int getDefaultProjectileRange() {
		return 16;
	}

	protected long getLastUseTimestamp(ItemStack stack) {
		return getTag(stack).getLong(LAST_USE_TIMESTAMP_KEY);
	}

	protected void setLastUseTimestamp(ItemStack stack, long timestamp) {
		updateTag(stack, tag -> tag.putLong(LAST_USE_TIMESTAMP_KEY, timestamp));
	}

	@Override
	public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon) {
		broadcastAnimation(level, shooter, projectileWeapon, Animations.SHOOT);

		boolean success = configuredProjectile.shoot(level, shooter,
				baseVelocity -> modifyProjectileVelocity(baseVelocity, projectileWeapon),
				baseDamage -> modifyProjectileDamage(baseDamage, projectileWeapon),
				baseKnockBack -> modifyProjectileKnockBack(baseKnockBack, projectileWeapon),
				baseInaccuracy -> modifyProjectileInaccuracy(baseInaccuracy, projectileWeapon));

		if (success) {
			configuredProjectile.playShootSound(level, shooter);
			consumeAmmo(shooter, projectileWeapon, getAmmoCost(projectileWeapon));
			consumeNutrients(projectileWeapon, getDurabilityCost(projectileWeapon));
		}

		setLastUseTimestamp(projectileWeapon, level.getGameTime());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!hasNutrients(stack)) {
			if (level.isClientSide()) {
				player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_nutrients"), true);
				playSound(player, ModSoundEvents.FLESHKIN_NO.get());
			}
			return InteractionResultHolder.fail(stack);
		}

		return super.use(level, player, hand);
	}

	@Override
	public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingUseDuration) {
		if (level.isClientSide) return;
		if (!(level instanceof ServerLevel serverLevel)) return;
		if (getGunState(stack) != GunState.SHOOTING_OR_CHARGING) return;

		if (!hasNutrients(stack)) {
			shooter.releaseUsingItem();
			stopShooting(stack, serverLevel, shooter);
		}
		else {
			super.onUseTick(level, shooter, stack, remainingUseDuration);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> useInMeleeMode(Level level, Player player, InteractionHand usedHand, ItemStack stack) {
		if (level instanceof ServerLevel serverLevel) {
			if (getAmmo(stack) > 1 && !Abilities.ACID_COAT.isActive(stack)) {
				consumeAmmo(player, stack, 1);
				Abilities.ACID_COAT.setActive(serverLevel, stack, player);
				broadcastAnimation(serverLevel, player, stack, Animations.COAT_BLADES);
				setLastUseTimestamp(stack, serverLevel.getGameTime());
			}
		}

		return InteractionResultHolder.fail(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (level.isClientSide) return;
		if (!(level instanceof ServerLevel serverLevel)) return;
		if (!(entity instanceof LivingEntity shooter)) return;

		if (isSelected) {
			Abilities.ACID_COAT.tick(serverLevel, stack, shooter);

			if (getGunState(stack) == GunState.NONE && !Abilities.ACID_COAT.isActive(stack) && canReload(stack, shooter)) {
				startReload(stack, serverLevel, shooter);
				return;
			}
		}

		super.inventoryTick(stack, level, entity, slotId, isSelected);
	}

	@Override
	public boolean canReload(ItemStack stack, LivingEntity shooter) {
		long elapsedTime = shooter.level().getGameTime() - getLastUseTimestamp(stack);
		return elapsedTime > 5 * 20 && getAmmo(stack) < getMaxAmmo(stack) && getNutrients(stack) >= getReloadCost(stack);
	}

	@Override
	public int getReloadCost(ItemStack stack) {
		return 5;
	}

	@Override
	public ItemStack findAmmoInInv(ItemStack stack, LivingEntity shooter) {
		return new ItemStack(Items.ARROW, 64);
	}

	@Override
	public @Nullable DamageSource getMeleeDamageSource(ItemStack stack, Entity target, LivingEntity attacker, float attackStrengthScale) {
		if (GunbladeMode.from(stack) != GunbladeMode.MELEE) return null;
		if (!Abilities.ACID_COAT.isActive(stack)) return null;

		DamageSource damageSource = ModDamageSources.acid(attacker.level(), attacker);
		if (target.isInvulnerableTo(damageSource)) return null; //use default melee damagesource as fallback
		return damageSource;
	}

	@Override
	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {
		if (attacker.level().isClientSide) return;
		if (GunbladeMode.from(stack) != GunbladeMode.MELEE) return;

		if (Abilities.ACID_COAT.isActive(stack)) {
			target.addEffect(new MobEffectInstance(ModMobEffects.CORROSIVE, 3 * 20, 1));
			target.addEffect(new MobEffectInstance(ModMobEffects.ARMOR_SHRED, 4 * 20, 1));
		}
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker.level().isClientSide) return true;

		setLastUseTimestamp(stack, attacker.level().getGameTime());

		consumeNutrients(stack, 2);

		if (GunbladeMode.from(stack) != GunbladeMode.MELEE) return true;

		if (Abilities.ACID_COAT.isActive(stack)) {
			boolean isFullAttackStrength = !(attacker instanceof Player player) || player.getAttackStrengthScale(0.5f) >= 0.9f;
			if (isFullAttackStrength) {
				playSwipeFX(attacker);
				target.addEffect(new MobEffectInstance(ModMobEffects.CORROSIVE, 3 * 20, 0));
				target.addEffect(new MobEffectInstance(ModMobEffects.ARMOR_SHRED, 4 * 20, 0));
			}

			Abilities.ACID_COAT.use(attacker.level(), stack, attacker);
		}

		return true;
	}

	@Override
	public void onChangeGunbladeMode(ServerLevel level, LivingEntity shooter, ItemStack stack) {
		Abilities.ACID_COAT.cancel(level, stack, shooter);
		setLastUseTimestamp(stack, level.getGameTime());

		SoundEvent soundEvent = GunbladeMode.from(stack) == GunbladeMode.MELEE ? ModSoundEvents.FLESHKIN_BECOME_DORMANT.get() : ModSoundEvents.FLESHKIN_BECOME_AWAKENED.get();
		playSFX(level, shooter, soundEvent);
	}

	@Override
	public void onReloadTick(ItemStack stack, ServerLevel level, LivingEntity shooter, long elapsedTime) {
		//if (elapsedTime % 20L == 0L) playSFX(level, shooter, SoundEvents.GENERIC_EAT);
	}

	@Override
	public void onReloadStarted(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.GENERIC_EAT);
	}

	@Override
	public void onReloadCanceled(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.TROPICAL_FISH_FLOP);
	}

	@Override
	public void onReloadStopped(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.TROPICAL_FISH_FLOP);
	}

	@Override
	public void onReloadFinished(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		consumeNutrients(stack, getReloadCost(stack));
		playSFX(level, shooter, SoundEvents.PLAYER_BURP);
	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		return !Abilities.ACID_COAT.isActive(stack) ? displayName : ComponentUtil.mutable().append(displayName).append(" (").append(ComponentUtil.translatable(Abilities.ACID_COAT.getTranslationKey())).append(")");
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
		tooltip.add(ComponentUtil.EMPTY_LINE);

		if (GunbladeMode.from(stack) == GunbladeMode.MELEE) {
			Abilities.ACID_COAT.appendAbilityDescription(stack, tooltip);
		}
		else {
			appendGunStats(stack, tooltip);
		}

		tooltip.add(ComponentUtil.EMPTY_LINE);
		appendLivingToolTooltip(stack, tooltip);

		tooltip.add(ComponentUtil.EMPTY_LINE);
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getActionText("switch_mode")).withStyle(TextStyles.DARK_GRAY));

		if (stack.isEnchanted()) {
			tooltip.add(ComponentUtil.EMPTY_LINE);
		}
	}

	@Override
	public int getMaxNutrients(ItemStack stack) {
		return maxNutrients;
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if (handleOverrideStackedOnOther(stack, slot, action, player)) {
			playSound(player, ModSoundEvents.FLESHKIN_EAT.get());
			return true;
		}
		return false;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (handleOverrideOtherStackedOnMe(stack, other, slot, action, player, access)) {
			playSound(player, ModSoundEvents.FLESHKIN_EAT.get());
			return true;
		}
		return false;
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return super.canPerformAction(stack, itemAbility) && hasNutrients(stack);
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return isValidEnchantment(stack, enchantment) && super.supportsEnchantment(stack, enchantment);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getNutrients(stack) < getMaxNutrients(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round(getNutrientsPct(stack) * 13f);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return ColorStyles.NUTRIENTS_FUEL_BAR;
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return false;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		//do nothing
	}

	@Override
	public int getDamage(ItemStack stack) {
		return 0;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return 0;
	}

	protected void playSound(Player player, SoundEvent soundEvent) {
		player.playSound(soundEvent, 0.8f, 0.8f + player.level().getRandom().nextFloat() * 0.4f);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final CausticGunbladeRenderer renderer = new CausticGunbladeRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}

			@Override
			public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
				if (GunbladeMode.from(itemStack) == GunbladeMode.RANGED) {
					return HumanoidModel.ArmPose.CROSSBOW_HOLD;
				}
				return null;
			}
		});
	}

	protected void broadcastAnimation(ServerLevel level, Entity relatedEntity, ItemStack stack, TriggerableAnimation animation) {
		long id = GeoItem.getOrAssignId(stack, level);
		triggerAnim(relatedEntity, id, animation.controller(), animation.name());
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		Animations.registerControllers(this, controllers);
	}

	public interface Ability {
		String name();

		void setActive(ServerLevel level, ItemStack stack, LivingEntity itemOwner);

		boolean isActive(ItemStack stack);

		void tick(Level level, ItemStack stack, LivingEntity itemOwner);

		void use(Level level, ItemStack stack, LivingEntity itemOwner);

		void cancel(ServerLevel level, ItemStack stack, LivingEntity itemOwner);

		default String getTranslationKey() {
			return BiomancyMod.translationKey("ability", name());
		}

		default void appendAbilityDescription(ItemStack stack, List<Component> components) {
			String translationKey = getTranslationKey();
			components.add(ComponentUtil.translatable(translationKey).withStyle(TextStyles.GRAY));
			components.addAll(ClientTextUtil.splitLinesByNewLine(ComponentUtil.translatable(translationKey + ".desc").withStyle(TextStyles.DARK_GRAY)));
		}
	}

	protected static final class Abilities {

		private static CompoundTag getTag(ItemStack stack) {
			return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		}

		private static void updateTag(ItemStack stack, Consumer<CompoundTag> updater) {
			CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
		}

		public static final Ability ACID_COAT = new Ability() {
			static final String NAME = "acid_coat";
			static final String KEY = BiomancyMod.rlStr(NAME);
			static final String REMAINING_USES = "uses";

			@Override
			public String name() {
				return NAME;
			}

			@Override
			public boolean isActive(ItemStack stack) {
				return getTag(stack).contains(KEY);
			}

			@Override
			public void setActive(ServerLevel level, ItemStack stack, LivingEntity itemOwner) {
				updateTag(stack, tag -> {
					CompoundTag abilityTag = new CompoundTag();
					abilityTag.putByte(REMAINING_USES, (byte) 2);
					tag.put(KEY, abilityTag);
				});
			}

			@Override
			public void tick(Level level, ItemStack stack, LivingEntity itemOwner) {
				//do nothing
			}

			@Override
			public void use(Level level, ItemStack stack, LivingEntity itemOwner) {
				if (!getTag(stack).contains(KEY)) return;

				updateTag(stack, tag -> {
					CompoundTag abilityTag = tag.getCompound(KEY);
					int uses = abilityTag.getByte(REMAINING_USES) - 1;

					if (uses > 0) {
						abilityTag.putByte(REMAINING_USES, (byte) uses);
					}
					else {
						tag.remove(KEY);
					}
				});
			}

			@Override
			public void cancel(ServerLevel level, ItemStack stack, LivingEntity itemOwner) {
				updateTag(stack, tag -> tag.remove(KEY));
			}
		};
	}

	protected static final class Animations {
		static final String MAIN_CONTROLLER = "main";
		static final String ACID_COAT_CONTROLLER = "acid_blades";
		static final String AMMO_CONTROLLER = "ammo";

		static final RawAnimation IDLE_RANGED = RawAnimation.begin().thenPlay("idle_ranged");
		static final RawAnimation IDLE_MELEE = RawAnimation.begin().thenPlay("idle_melee");
		static final RawAnimation RANGED_TO_MELEE = RawAnimation.begin().thenPlay("ranged_to_melee").thenPlay("idle_melee");
		static final RawAnimation MELEE_TO_RANGED = RawAnimation.begin().thenPlay("melee_to_ranged").thenPlay("idle_ranged");
		static final RawAnimation COATED_BLADES = RawAnimation.begin().thenPlay("coated_blades");
		static final RawAnimation UNCOATED_BLADES = RawAnimation.begin().thenPlay("uncoated_blades");
		static final RawAnimation FULL_AMMO = RawAnimation.begin().thenPlay("full_ammo");
		static final RawAnimation HALF_AMMO = RawAnimation.begin().thenPlay("half_ammo");
		static final RawAnimation NO_AMMO = RawAnimation.begin().thenPlay("no_ammo");

		private static final List<TriggerableAnimation> TRIGGERABLE_ANIMATIONS = new ArrayList<>();
		static final TriggerableAnimation SHOOT = register(MAIN_CONTROLLER, "shoot", RawAnimation.begin().thenPlay("shoot"));
		static final TriggerableAnimation COAT_BLADES = register(MAIN_CONTROLLER, "coat_blades", RawAnimation.begin().thenPlay("coat_blades"));

		private Animations() {}

		static <T extends CausticGunbladeItem> PlayState handleMain(AnimationState<T> state) {

			if (state.getController().isPlayingTriggeredAnimation()) return PlayState.CONTINUE;

			ItemStack itemStack = state.getData(DataTickets.ITEMSTACK);
			GunbladeMode gunbladeMode = GunbladeMode.from(itemStack);

			if (gunbladeMode == GunbladeMode.MELEE) {
				return state.setAndContinue(Animations.RANGED_TO_MELEE);
			}
			else {
				return state.setAndContinue(Animations.MELEE_TO_RANGED);
			}
		}

		static <T extends CausticGunbladeItem> PlayState handleAcidCoat(AnimationState<T> state) {
			ItemStack itemStack = state.getData(DataTickets.ITEMSTACK);
			boolean hasCoatedBlades = Abilities.ACID_COAT.isActive(itemStack);
			return state.setAndContinue(hasCoatedBlades ? Animations.COATED_BLADES : Animations.UNCOATED_BLADES);
		}

		static <T extends CausticGunbladeItem> PlayState handleAmmo(AnimationState<T> state) {
			ItemStack itemStack = state.getData(DataTickets.ITEMSTACK);
			CausticGunbladeItem item = (CausticGunbladeItem) itemStack.getItem();

			int ammo = item.getAmmo(itemStack);
			int maxAmmo = item.getMaxAmmo(itemStack);

			if (ammo <= 0) {
				return state.setAndContinue(Animations.NO_AMMO);
			}

			return state.setAndContinue(ammo < maxAmmo ? Animations.HALF_AMMO : Animations.FULL_AMMO);
		}

		static void registerControllers(CausticGunbladeItem animatable, AnimatableManager.ControllerRegistrar controllers) {
			AnimationController<CausticGunbladeItem> mainController = new AnimationController<>(animatable, MAIN_CONTROLLER, 0, Animations::handleMain);
			Animations.registerTriggerableAnimations(mainController);
			controllers.add(mainController);

			controllers.add(new AnimationController<>(animatable, ACID_COAT_CONTROLLER, 0, Animations::handleAcidCoat));
			controllers.add(new AnimationController<>(animatable, AMMO_CONTROLLER, 0, Animations::handleAmmo));
		}

		private static TriggerableAnimation register(String controller, String name, RawAnimation rawAnimation) {
			TriggerableAnimation animation = new TriggerableAnimation(controller, name, rawAnimation);
			TRIGGERABLE_ANIMATIONS.add(animation);
			return animation;
		}

		private static void registerTriggerableAnimations(AnimationController<?> controller) {
			for (TriggerableAnimation animation : TRIGGERABLE_ANIMATIONS) {
				if (animation.controller().equals(controller.getName())) {
					controller.triggerableAnim(animation.name(), animation.rawAnimation());
				}
			}
		}
	}

}
