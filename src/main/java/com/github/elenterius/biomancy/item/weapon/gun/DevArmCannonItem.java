package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.client.gui.DevCannonScreen;
import com.github.elenterius.biomancy.client.render.item.dev.DevArmCannonRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import com.github.elenterius.biomancy.init.ModProjectiles;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.IArmPoseProvider;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.item.KeyPressListener;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.function.FloatOperator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import com.github.elenterius.biomancy.init.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Deprecated
public class DevArmCannonItem extends Item implements GeoItem, IArmPoseProvider, ItemTooltipStyleProvider, KeyPressListener {

	public static final Set<ResourceKey<Enchantment>> VALID_ENCHANTMENTS = Set.of(Enchantments.PUNCH, Enchantments.POWER);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public DevArmCannonItem(Properties properties) {
		super(properties);
	}

	private static float getBonusDamage(ItemStack stack) {
		return 0.6f * ModEnchantments.getLevel(stack, Enchantments.POWER);
	}

	private static int getBonusKnockBack(ItemStack stack) {
		return ModEnchantments.getLevel(stack, Enchantments.PUNCH);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final DevArmCannonRenderer renderer = new DevArmCannonRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

	@Override
	public KeyPressResult onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		if (slot.getType() == EquipmentSlot.Type.HAND) {
			InteractionHand hand = slot == EquipmentSlot.MAINHAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
			tryToOpenClientScreen(hand);
		}
		return KeyPressResult.fail(); //don't send button press to server
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		if (flags < 0 || flags >= ModProjectiles.PRECONFIGURED_PROJECTILES.size()) {
			flags = 0;
		}
		byte index = flags;
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putByte("ProjectileIndex", index));
	}

	@OnlyIn(Dist.CLIENT)
	private void tryToOpenClientScreen(InteractionHand hand) {
		Screen currScreen = Minecraft.getInstance().screen;
		if (currScreen == null && Minecraft.getInstance().player != null) {
			Minecraft.getInstance().setScreen(new DevCannonScreen(hand));
			Minecraft.getInstance().player.playNotifySound(ModSoundEvents.UI_RADIAL_MENU_OPEN.get(), SoundSource.PLAYERS, 1f, 1f);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		if (!level.isClientSide) {
			ItemStack stack = player.getItemInHand(usedHand);

			byte index = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getByte("ProjectileIndex");
			if (index < 0 || index >= ModProjectiles.PRECONFIGURED_PROJECTILES.size()) {
				index = 0;
				CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putByte("ProjectileIndex", (byte) 0));
			}

			ModProjectiles.ConfiguredProjectile<? extends BaseProjectile> configuredProjectile = ModProjectiles.PRECONFIGURED_PROJECTILES.get(index);
			boolean success = configuredProjectile.shoot(level, player,
					FloatOperator.IDENTITY,
					d -> d + getBonusDamage(stack),
					k -> k + getBonusKnockBack(stack),
					FloatOperator.IDENTITY);

			if (success) {
				configuredProjectile.playShootSound(level, player);
			}
		}

		return InteractionResultHolder.consume(player.getItemInHand(usedHand));
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return enchantment.is(k -> VALID_ENCHANTMENTS.contains(k)) || super.supportsEnchantment(stack, enchantment);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public HumanoidModel.ArmPose getArmPose(Player player, InteractionHand usedHand, ItemStack stack) {
		return !player.swinging ? HumanoidModel.ArmPose.CROSSBOW_HOLD : HumanoidModel.ArmPose.ITEM;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		//do nothing
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));

		tooltip.add(ComponentUtil.EMPTY_LINE);
		tooltip.add(ComponentUtil.literal("The quick brown fox jumps over the lazy dog.").withStyle(TextStyles.PRIMORDIAL_RUNES_GRAY));

		tooltip.add(ComponentUtil.EMPTY_LINE);
		byte index = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getByte("ProjectileIndex");
		if (index < 0 || index >= ModProjectiles.PRECONFIGURED_PROJECTILES.size()) {
			index = 0;
		}
		tooltip.add(ComponentUtil.literal(ModProjectiles.PRECONFIGURED_PROJECTILES.get(index).name()));

		tooltip.add(ComponentUtil.EMPTY_LINE);

		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getActionText("open_inventory")).withStyle(TextStyles.PRIMORDIAL_RUNES_GRAY));
		// /tellraw @a {"text":"The quick brown fox jumps over the lazy dog. 1234567890!?","color":"#9e1316","font":"biomancy:caro_invitica"}
	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		byte index = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getByte("ProjectileIndex");
		if (index < 0 || index >= ModProjectiles.PRECONFIGURED_PROJECTILES.size()) {
			index = 0;
		}
		return ComponentUtil.mutable().append(displayName).append(" (" + ModProjectiles.PRECONFIGURED_PROJECTILES.get(index).name() + ")");
	}

}
