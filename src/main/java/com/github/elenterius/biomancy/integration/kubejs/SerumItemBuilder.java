package com.github.elenterius.biomancy.integration.kubejs;

import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.api.serum.SerumContainer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SerumItemBuilder extends ItemBuilder {

	public transient ResourceLocation serumId;

	protected SerumItemBuilder(ResourceLocation i) {
		super(i);
		maxStackSize(16);
	}

	@Info("Set the serum of this item.")
	public SerumItemBuilder serum(ResourceLocation serumId) {
		this.serumId = serumId;
		return this;
	}

	@Override
	public Item createObject() {
		return new SerumItemKJS(createItemProperties(), serumId);
	}

	public static class SerumItemKJS extends Item implements SerumContainer, ItemTooltipStyleProvider {

		private final ResourceLocation serumId;
		private Serum serum = null;

		protected SerumItemKJS(Properties properties, ResourceLocation serumId) {
			super(properties);
			this.serumId = serumId;
		}

		@Override
		public Serum getSerum(ItemStack stack) {
			if (serum == null) {
				serum = ModSerums.REGISTRY.get().getValue(serumId);
			}

			if (serum == null) {
				serum = ModSerums.EMPTY.get();
			}

			return serum;
		}

		@Override
		public int getSerumColor(ItemStack stack) {
			return getSerum(stack).getColor(Serum.getDataTag(stack));
		}

		@Override
		public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
			tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
		}

		@Override
		public String getTooltipKey(ItemStack stack) {
			return getSerum(stack).getDescriptionTranslationKey();
		}

	}

}
