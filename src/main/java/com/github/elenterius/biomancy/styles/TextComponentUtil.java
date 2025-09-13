package com.github.elenterius.biomancy.styles;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class TextComponentUtil {

	private TextComponentUtil() {}

	private static String key(String prefix, String suffix) {
		return BiomancyMod.translationKey(prefix, suffix);
	}

	public static String getItemTooltipKey(Item item) {
		return item.getDescriptionId() + ".tooltip";
	}

	public static String getItemTooltipKey(Block block) {
		return block.getDescriptionId() + ".tooltip";
	}

	public static MutableComponent getItemTooltip(Item item) {
		return ComponentUtil.translatable(getItemTooltipKey(item));
	}

	public static MutableComponent getTooltipText(String tooltipKey) {
		return ComponentUtil.translatable(key("tooltip", tooltipKey));
	}

	public static MutableComponent getTooltipText(String tooltipKey, Object... formatArgs) {
		return ComponentUtil.translatable(key("tooltip", tooltipKey), formatArgs);
	}

	public static MutableComponent getMsgText(String msgKey) {
		return ComponentUtil.translatable(key("msg", msgKey));
	}

	public static MutableComponent getMsgText(String msgKey, Object... formatArgs) {
		return ComponentUtil.translatable(key("msg", msgKey), formatArgs);
	}

	public static MutableComponent getFailureMsgText(String msgKey) {
		return getMsgText(msgKey).withStyle(TextStyles.ERROR);
	}

	public static MutableComponent getFailureMsgText(String msgKey, Object... formatArgs) {
		return getMsgText(msgKey, formatArgs).withStyle(TextStyles.ERROR);
	}

	public static MutableComponent getAbilityText(String key) {
		return ComponentUtil.translatable(key("ability", key));
	}

	public static MutableComponent getAbilityText(String key, Object... args) {
		return ComponentUtil.translatable(key("ability", key), args);
	}

	public static MutableComponent getActionText(String key) {
		return ComponentUtil.translatable(key("tooltip", "action." + key));
	}

}
