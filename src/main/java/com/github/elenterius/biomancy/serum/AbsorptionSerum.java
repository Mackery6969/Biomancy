package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.BiomancyConfig;
import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class AbsorptionSerum extends BasicSerum {

	private static final ResourceLocation MAX_ABSORPTION_MODIFIER_ID = BiomancyMod.rl("absorption_serum");

	public AbsorptionSerum(int color) {
		super(color);
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		addAbsorption(target);
	}

	@Override
	public void affectPlayerSelf(ServerLevel level, CompoundTag tag, ServerPlayer targetSelf) {
		addAbsorption(targetSelf);
	}

	private void addAbsorption(LivingEntity target) {
		float maxAbsorption = getMaxHearts() * 2f;

		// Since 1.21 setAbsorptionAmount is clamped to the max_absorption attribute, and that
		// attribute defaults to 0, so the ceiling has to be raised or nothing sticks at all.
		AttributeInstance maxAbsorptionAttribute = target.getAttribute(Attributes.MAX_ABSORPTION);
		if (maxAbsorptionAttribute == null) return;

		AttributeModifier modifier = maxAbsorptionAttribute.getModifier(MAX_ABSORPTION_MODIFIER_ID);
		if (modifier == null || modifier.amount() != maxAbsorption) {
			maxAbsorptionAttribute.removeModifier(MAX_ABSORPTION_MODIFIER_ID);
			// permanent: a transient one is lost on relog and the hearts get clamped away again
			maxAbsorptionAttribute.addPermanentModifier(new AttributeModifier(MAX_ABSORPTION_MODIFIER_ID, maxAbsorption, AttributeModifier.Operation.ADD_VALUE));
		}

		float absorptionAmount = target.getAbsorptionAmount();
		if (absorptionAmount < maxAbsorption) {
			target.setAbsorptionAmount(Math.min(maxAbsorption, absorptionAmount + getHearts() * 2f));
		}
	}

	@Override
	public void appendTooltip(CompoundTag tag, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(ComponentUtil.translatable(getDescriptionTranslationKey(), getHearts(), getMaxHearts()).withStyle(TextStyles.LORE));
	}

	protected float getHearts() {
		return BiomancyConfig.SERVER.absorptionHearts.get().floatValue();
	}

	protected float getMaxHearts() {
		return BiomancyConfig.SERVER.absorptionMaxHearts.get().floatValue();
	}

}
