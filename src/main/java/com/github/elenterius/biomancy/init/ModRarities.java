package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public final class ModRarities {

	public static final Rarity COMMON = EnumParams.COMMON_PROXY.getValue();
	public static final Rarity UNCOMMON = EnumParams.UNCOMMON_PROXY.getValue();
	public static final Rarity RARE = EnumParams.RARE_PROXY.getValue();
	public static final Rarity VERY_RARE = EnumParams.VERY_RARE_PROXY.getValue();
	public static final Rarity ULTRA_RARE = EnumParams.ULTRA_RARE_PROXY.getValue();

	private ModRarities() {}

	public static int getRGBColor(ItemStack stack) {
		TextColor color = stack.getRarity().getStyleModifier().apply(Style.EMPTY).getColor();
		return color != null ? color.getValue() : 0xFF_FF_FF;
	}

	public static int getARGBColor(ItemStack stack) {
		return getRGBColor(stack) | 0xFF_00_00_00;
	}

	public static final class EnumParams {

		public static final EnumProxy<Rarity> COMMON_PROXY = createProxy("common", 0xA58369);
		public static final EnumProxy<Rarity> UNCOMMON_PROXY = createProxy("uncommon", 0xB19748);
		public static final EnumProxy<Rarity> RARE_PROXY = createProxy("rare", 0x2E9E3E);
		public static final EnumProxy<Rarity> VERY_RARE_PROXY = createProxy("very_rare", 0xA870E1);
		public static final EnumProxy<Rarity> ULTRA_RARE_PROXY = createProxy("ultra_rare", 0xFF3D51);

		private EnumParams() {}

		private static EnumProxy<Rarity> createProxy(String name, int rgbColor) {
			return new EnumProxy<>(Rarity.class, -1, BiomancyMod.MOD_ID + ":" + name, (UnaryOperator<Style>) style -> style.withColor(rgbColor));
		}
	}

}
