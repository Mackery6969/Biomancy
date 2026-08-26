package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.api.serum.SerumContainer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class SerumItem extends Item implements SerumContainer, ItemTooltipStyleProvider {

	private final Supplier<? extends Serum> serumSupplier;

	public SerumItem(Properties properties, Supplier<? extends Serum> serumSupplier) {
		super(properties);
		this.serumSupplier = serumSupplier;
	}

	@Override
	public Serum getSerum(ItemStack stack) {
		return serumSupplier.get();
	}

	@Override
	public int getSerumColor(ItemStack stack) {
		return serumSupplier.get().getColor(Serum.getDataTag(stack));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
		if (ClientTextUtil.showItemInfo(tooltip)) {
			getSerum(stack).appendTooltip(Serum.getDataTag(stack), context.level(), tooltip, isAdvanced);
		}
	}

	@Override
	public String getTooltipKey(ItemStack stack) {
		return getSerum(stack).getDescriptionTranslationKey();
	}

}
