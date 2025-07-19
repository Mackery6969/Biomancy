package com.github.elenterius.biomancy.item.armor;

import com.github.elenterius.biomancy.client.render.item.armor.WarriorArmorRenderer;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.item.ShowKnowledgeOverlay;
import com.github.elenterius.biomancy.mixin.accessor.ArmorItemAccessor;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class WarriorArmorItem extends LivingArmorGeoItem implements ShowKnowledgeOverlay, ItemTooltipStyleProvider {

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public WarriorArmorItem(ArmorMaterial material, Type type, int maxNutrients, Properties properties) {
		super(material, type, maxNutrients, properties);

		ArmorItemAccessor baseArmor = (ArmorItemAccessor) (ArmorItem) this;
		UUID uuid = ArmorItemAccessor.biomancy$ARMOR_MODIFIER_UUID_PER_TYPE().get(type);

		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.putAll(baseArmor.biomancy$getDefaultModifiers());
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "Armor attack damage", 0.05d, AttributeModifier.Operation.MULTIPLY_BASE));
		baseArmor.biomancy$setDefaultModifiers(builder.build());
	}

	@Override
	public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
		super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);

		if (type != Type.CHESTPLATE || player.getItemBySlot(EquipmentSlot.CHEST) != stack) return;
		if (level.isClientSide) return;
		if (player.tickCount % 20 != 0) return;

		for (ItemStack stackInArmorSlot : player.getArmorSlots()) {
			if (stackInArmorSlot.getItem() instanceof WarriorArmorItem armor) {
				if (!armor.hasNutrients(stackInArmorSlot)) return;
			}
			else return;
		}

		int effectDuration = 5 * 20;
		double radius = 8;
		double radiusSqr = radius * radius;
		Vec3 center = player.getBoundingBox().getCenter();

		Predicate<Entity> predicate = EntitySelector.LIVING_ENTITY_STILL_ALIVE.and(EntitySelector.NO_CREATIVE_OR_SPECTATOR);
		List<LivingEntity> livingEntities = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius), predicate);

		for (LivingEntity livingEntity : livingEntities) {
			if (livingEntity == player) continue;
			if (center.distanceToSqr(livingEntity.getX(), livingEntity.getY(0.5d), livingEntity.getZ()) >= radiusSqr) continue;

			livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, effectDuration));

			if (livingEntity instanceof Mob mob) {
				for (WrappedGoal wrappedGoal : mob.goalSelector.getAvailableGoals()) {
					if (wrappedGoal.getGoal() instanceof PanicGoal) {
						livingEntity.setLastHurtByMob(player);
					}
				}
			}
		}
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			private GeoArmorRenderer<?> renderer;

			@Override
			public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel<?> original) {
				if (renderer == null) {
					renderer = new WarriorArmorRenderer();
				}

				renderer.prepForRender(livingEntity, itemStack, slot, original);

				return renderer;
			}
		});
	}

	@Override
	public boolean canShowKnowledgeOverlay(ItemStack stack, Player player) {
		return AcolyteArmorUpgrades.hasUpgrade(stack, AcolyteArmorUpgrades.PRIMORDIAL_SIGHT) && hasNutrients(stack);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		//TODO: add idle animations?
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		AcolyteArmorUpgrades.appendHoverText(stack, tooltip);

		tooltip.add(ComponentUtil.emptyLine());
		tooltip.add(TextComponentUtil.getAbilityText("fleshkin_affinity").withStyle(ChatFormatting.GRAY));
		tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getAbilityText("fleshkin_affinity.desc")).withStyle(ChatFormatting.DARK_GRAY));

		tooltip.add(ComponentUtil.emptyLine());
		tooltip.add(TextComponentUtil.getAbilityText("imposing_aura").withStyle(ChatFormatting.GRAY));
		tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getAbilityText("imposing_aura.desc")).withStyle(ChatFormatting.DARK_GRAY));

		tooltip.add(ComponentUtil.emptyLine());

		//		CompoundTag compoundTag = stack.getOrCreateTag().getCompound("damage_resistance_tracker");
		//		AdaptiveDamageResistanceHandler.DamageTypeResistanceTracker.appendTooltipText(compoundTag, tooltip);

		appendLivingToolTooltip(stack, tooltip);

		if (stack.isEnchanted()) {
			tooltip.add(ComponentUtil.emptyLine());
		}
	}

}