package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.client.render.item.guidebook.GuideBookRenderer;
import com.github.elenterius.geckolibextras.GLibExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class GuideBookItem extends SimpleItem implements GeoItem {

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public GuideBookItem(Properties properties) {
		super(properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (level instanceof ServerLevel serverLevel) {
			GeoItem.getOrAssignId(stack, serverLevel); //hack to always ensure we have a unique id for the animation
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (level.isClientSide) {
			tryToOpenClientScreen(player);
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	@OnlyIn(Dist.CLIENT)
	private void tryToOpenClientScreen(Player player) {
		if (player instanceof LocalPlayer localPlayer) {
			Minecraft.getInstance().setScreen(new AdvancementsScreen(localPlayer.connection.getAdvancements())); //fallback
		}
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final GuideBookRenderer renderer = new GuideBookRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		AnimationController<GuideBookItem> controller = new AnimationController<>(this, "main", state -> {

			Object data = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE) == ItemDisplayContext.GUI ? Minecraft.getInstance().player : state.getData(GLibExtras.ITEM_HOST_TICKET);

			if (data instanceof LivingEntity livingEntity) {
				ItemStack stack = state.getData(DataTickets.ITEMSTACK);
				if (stack == livingEntity.getMainHandItem() || stack == livingEntity.getOffhandItem()) {
					return state.setAndContinue(Animations.OPEN_THEN_IDLE_ANIM);
				}

				if (state.getController().getCurrentAnimation() != null && !state.isCurrentAnimationStage("closed_idle")) {
					return state.setAndContinue(Animations.CLOSE_THEN_IDLE_ANIM);
				}
			}

			return state.setAndContinue(Animations.CLOSED_IDLE_ANIM);
		});

		controllers.add(controller);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	protected static final class Animations {
		public static final RawAnimation CLOSED_IDLE_ANIM = RawAnimation.begin().thenLoop("closed_idle");
		public static final RawAnimation OPEN_THEN_IDLE_ANIM = RawAnimation.begin().thenPlay("opening").thenLoop("open_idle");
		public static final RawAnimation CLOSE_THEN_IDLE_ANIM = RawAnimation.begin().thenPlay("closing").thenLoop("closed_idle");

		private Animations() {}
	}

}
