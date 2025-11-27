package com.github.elenterius.biomancy.client.util;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.client.ModKeyBindings;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.UUID;


public final class ClientTextUtil {

	private static final MutableComponent CTRL_KEY_TEXT = BiomancyMod.translatableFrom("keyboard", "ctrl");
	private static final MutableComponent ALT_KEY_TEXT = BiomancyMod.translatableFrom("keyboard", "alt");
	private static final MutableComponent SHIFT_KEY_TEXT = BiomancyMod.translatableFrom("keyboard", "shift");
	private static final MutableComponent RIGHT_MOUSE_KEY_TEXT = BiomancyMod.translatableFrom("keyboard", "right_mouse");
	private static final MutableComponent SHOW_INFO = BiomancyMod.translatableFrom("tooltip", "action.show_info");

	private ClientTextUtil() {}

	private static MutableComponent getItemTooltip(ItemStack stack) {
		Item item = stack.getItem();

		if (item instanceof ItemTooltipStyleProvider iTooltip) {
			return iTooltip.getTooltipText(stack);
		}

		return TextComponentUtil.getItemTooltip(item);
	}

	public static List<Component> getItemInfoTooltip(ItemStack stack) {
		if (Screen.hasControlDown()) {
			return splitLinesByNewLine(getItemTooltip(stack).withStyle(TextStyles.LORE));
		}

		return List.of(holdButtonTo(CTRL_KEY_TEXT.plainCopy(), SHOW_INFO).withStyle(TextStyles.LORE));
	}

	public static boolean showItemInfo(List<Component> tooltip) {
		boolean flag = Screen.hasControlDown();
		if (!flag) tooltip.add(holdButtonTo(CTRL_KEY_TEXT.plainCopy(), SHOW_INFO).withStyle(TextStyles.LORE));
		return flag;
	}

	public static boolean showExtraInfo(List<Component> tooltip) {
		boolean flag = Screen.hasAltDown();
		if (!flag) tooltip.add(holdButtonTo(ALT_KEY_TEXT.plainCopy(), SHOW_INFO).withStyle(TextStyles.LORE));
		return flag;
	}

	public static MutableComponent holdButtonTo(MutableComponent key, Object action) {
		return ComponentUtil.translatable(BiomancyMod.translationKey("tooltip", "hold_button_to"), key.withStyle(TextStyles.KEYBOARD_INPUT), action);
	}

	public static MutableComponent pressButtonTo(MutableComponent key, Object action) {
		return ComponentUtil.translatable(BiomancyMod.translationKey("tooltip", "press_button_to"), key.withStyle(TextStyles.KEYBOARD_INPUT), action);
	}

	public static MutableComponent getAltKey() {
		return ALT_KEY_TEXT.plainCopy();
	}

	public static MutableComponent getCtrlKey() {
		return CTRL_KEY_TEXT.plainCopy();
	}

	public static MutableComponent getShiftKey() {
		return SHIFT_KEY_TEXT.plainCopy();
	}

	public static MutableComponent getRightMouseKey() {
		return RIGHT_MOUSE_KEY_TEXT.plainCopy();
	}

	public static MutableComponent getDefaultKey() {
		return ComponentUtil.keybind(ModKeyBindings.MAIN_HAND_ITEM_ACTION);
	}

	public static String tryToGetPlayerNameOnClientSide(UUID uuid) {
		if (Minecraft.getInstance().level != null) {
			Player player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
			if (player != null) {
				return player.getGameProfile().getName();
			}
		}
		return uuid.toString();
	}

	public static List<Component> splitLinesByNewLine(Component component) {
		Locale locale = Minecraft.getInstance().getLocale();
		String text = component.getString();
		Style style = component.getStyle();
		return ComponentUtil.splitLines(locale, text, style);
	}

	public static List<Component> splitLinesByNewLine(String text, Style style) {
		Locale locale = Minecraft.getInstance().getLocale();
		return ComponentUtil.splitLines(locale, text, style);
	}

	public static List<Component> splitLines(Component component, int maxLength) {
		Locale locale = Minecraft.getInstance().getLocale();
		String text = component.getString();
		Style style = component.getStyle();
		return ComponentUtil.splitLines(locale, text, maxLength, style);
	}

	public static List<Component> splitLines(String text, int maxLength, Style style) {
		Locale locale = Minecraft.getInstance().getLocale();
		return ComponentUtil.splitLines(locale, text, maxLength, style);
	}

}
