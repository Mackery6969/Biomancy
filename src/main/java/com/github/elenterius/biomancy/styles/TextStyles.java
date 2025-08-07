package com.github.elenterius.biomancy.styles;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;

public final class TextStyles {

	public static final Style LIGHT_GRAY = Style.EMPTY.withColor(0xff_e5e4e2);
	public static final Style GRAY = Style.EMPTY.withColor(ChatFormatting.GRAY);
	public static final Style DARK_GRAY = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);
	public static final Style LIME = Style.EMPTY.withColor(ChatFormatting.GREEN);
	public static final Style RED = Style.EMPTY.withColor(0xff_bb0b3f);
	public static final Style PURPLE = Style.EMPTY.withColor(0xff_a870e1);
	public static final Style MUTED_PURPLE = Style.EMPTY.withColor(0xff_8655b9);

	public static final Style ITALIC_GRAY = GRAY.withItalic(true);
	public static final Style LORE = DARK_GRAY.withItalic(true);
	public static final Style ERROR = Style.EMPTY.withColor(ColorStyles.TEXT_ERROR);
	public static final Style KEYBOARD_INPUT = Style.EMPTY.withColor(ColorStyles.TEXT_MUTED_AQUA);

	public static final Style NUTRIENTS = Style.EMPTY.withColor(ColorStyles.TEXT_NUTRIENTS);
	public static final Style NUTRIENTS_CONSUMPTION = Style.EMPTY.withColor(ColorStyles.TEXT_NUTRIENTS_CONSUMPTION);

	public static final Style PRIMORDIAL_RUNES_LORE = LORE.withFont(Fonts.PrimordialRunes.getId());
	public static final Style PRIMORDIAL_RUNES = Style.EMPTY.withFont(Fonts.PrimordialRunes.getId());
	public static final Style PRIMORDIAL_RUNES_GRAY = GRAY.withFont(Fonts.PrimordialRunes.getId());
	public static final Style PRIMORDIAL_RUNES_LIGHT_GRAY = LIGHT_GRAY.withFont(Fonts.PrimordialRunes.getId());
	public static final Style PRIMORDIAL_RUNES_RED = RED.withFont(Fonts.PrimordialRunes.getId());
	public static final Style PRIMORDIAL_RUNES_PURPLE = PURPLE.withFont(Fonts.PrimordialRunes.getId());
	public static final Style PRIMORDIAL_RUNES_MUTED_PURPLE = MUTED_PURPLE.withFont(Fonts.PrimordialRunes.getId());

	private TextStyles() {}

}
