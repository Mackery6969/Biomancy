package com.github.elenterius.biomancy.api.livingtool;

import com.github.elenterius.biomancy.api.nutrients.Nutrients;
import com.github.elenterius.biomancy.api.nutrients.NutrientsContainerItem;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.FormatUtil;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.ApiStatus;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

@ApiStatus.Experimental
public interface LivingTool extends NutrientsContainerItem {

	Set<ResourceKey<Enchantment>> INVALID_ENCHANTMENTS = Set.of(Enchantments.FLAME, Enchantments.FIRE_ASPECT, Enchantments.FIRE_PROTECTION, Enchantments.UNBREAKING);

	default boolean isValidEnchantment(ItemStack livingTool, Holder<Enchantment> enchantment) {
		return !enchantment.is(k -> INVALID_ENCHANTMENTS.contains(k));
	}

	@Override
	default boolean isValidNutrientsResource(ItemStack container, ItemStack resource) {
		return Nutrients.isValidRepairMaterial(resource);
	}

	@Override
	default int getNutrientsResourceValue(ItemStack container, ItemStack resource) {
		return Nutrients.getRepairValue(resource);
	}

	int getLivingToolActionCost(ItemStack livingTool, ItemAbility itemAbility);

	default void appendLivingToolTooltip(ItemStack stack, List<Component> tooltip) {
		DecimalFormat df = FormatUtil.getIntegerFormatter();
		tooltip.add(TextComponentUtil.getTooltipText("nutrients_fuel").withStyle(TextStyles.GRAY));
		tooltip.add(ComponentUtil.literal(" %s/%s".formatted(df.format(getNutrients(stack)), df.format(getMaxNutrients(stack)))).withStyle(TextStyles.NUTRIENTS));
	}

}
