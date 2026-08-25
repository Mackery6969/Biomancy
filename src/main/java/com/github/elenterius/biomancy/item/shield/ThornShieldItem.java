package com.github.elenterius.biomancy.item.shield;

import com.github.elenterius.biomancy.client.render.item.shield.ThornShieldRenderer;
import com.github.elenterius.biomancy.item.ShieldBlockingListener;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.geckolibextras.GLibExtras;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class ThornShieldItem extends LivingShieldItem implements Equipable, ShieldBlockingListener, GeoItem {

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public ThornShieldItem(int maxNutrients, Properties properties) {
		super(maxNutrients, properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (level instanceof ServerLevel serverLevel) {
			GeoItem.getOrAssignId(stack, serverLevel); //hack to always ensure we have a unique id for the animation
		}
	}

	@Override
	public void onShieldBlocking(ItemStack shield, LivingEntity user, LivingEntity attacker) {
		attacker.hurt(user.damageSources().thorns(user), 1.5f + user.getRandom().nextInt(4));
	}

	@Override
	public boolean canDisableShield(ItemStack attackerItem, ItemStack shield, LivingEntity user, LivingEntity attacker) {
		return super.canDisableShield(attackerItem, shield, user, attacker);
	}

	@Override
	public void appendLivingToolTooltip(ItemStack stack, List<Component> tooltip) {
		tooltip.add(TextComponentUtil.getAbilityText("thorny_hide").withStyle(TextStyles.GRAY));
		tooltip.add(ComponentUtil.space().append(TextComponentUtil.getAbilityText("thorny_hide.desc")).withStyle(TextStyles.DARK_GRAY));

		super.appendLivingToolTooltip(stack, tooltip);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final ThornShieldRenderer renderer = new ThornShieldRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		AnimationController<ThornShieldItem> controller = new AnimationController<>(this, "main", state -> {

			if (state.getData(GLibExtras.ITEM_HOST_TICKET) instanceof LivingEntity livingEntity) {
				if (livingEntity.isBlocking() && livingEntity.getUseItem() == state.getData(DataTickets.ITEMSTACK)) {
					return state.setAndContinue(Animations.TRANSITION_TO_EXTENDED);
				}
			}

			if (state.getController().getCurrentAnimation() != null && !state.isCurrentAnimationStage("retracted")) {
				return state.setAndContinue(Animations.TRANSITION_TO_RETRACTED);
			}

			return state.setAndContinue(Animations.RETRACTED);
		});

		controllers.add(controller);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	protected static final class Animations {
		public static final RawAnimation RETRACTED = RawAnimation.begin().thenPlay("retracted");
		public static final RawAnimation TRANSITION_TO_RETRACTED = RawAnimation.begin().thenPlay("retract").thenPlay("retracted");
		public static final RawAnimation TRANSITION_TO_EXTENDED = RawAnimation.begin().thenPlay("extend").thenPlay("extended");

		private Animations() {}

	}

}
