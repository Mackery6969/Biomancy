package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.tooltip.EmptyLineTooltipComponent;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.tooltip.TooltipContents;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.client.ClientHooks;
import org.jspecify.annotations.Nullable;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class ComponentUtil {

	public static final Component SPACE = CommonComponents.SPACE;
	public static final Component EMPTY = CommonComponents.EMPTY;
	public static final Component NEW_LINE = CommonComponents.NEW_LINE;
	public static final Component EMPTY_LINE = TooltipHacks.EMPTY_LINE_COMPONENT;
	public static final Component HORIZONTAL_LINE = TooltipHacks.wrap(new HrTooltipComponent(ColorStyles.WHITE_ARGB));
	public static final Component ELLIPSIS = CommonComponents.ELLIPSIS;
	public static final Component TEXT_SEPARATOR = Component.literal(", ");

	private ComponentUtil() {}

	public static Component nullToEmpty(@Nullable String text) {
		return Component.nullToEmpty(text);
	}

	public static MutableComponent literal(String text) {
		return Component.literal(text);
	}

	public static MutableComponent translatable(String translationKey) {
		return Component.translatable(translationKey);
	}

	public static MutableComponent translatable(String translationKey, Object... args) {
		return Component.translatable(translationKey, args);
	}

	public static MutableComponent mutable() {
		return Component.empty();
	}

	/**
	 * whitespace
	 */
	public static MutableComponent space() {
		return Component.literal(" ");
	}

	/**
	 * horizontal line in item tooltips
	 */
	public static Component horizontalLine(int colorARGB) {
		return TooltipHacks.wrap(new HrTooltipComponent(colorARGB));
	}

	/**
	 * client sided due to KeyMapping
	 */
	public static MutableComponent keybind(KeyMapping keyMapping) {
		return Component.keybind(keyMapping.getName());
	}

	public static MutableComponent keybind(String keyName) {
		return Component.keybind(keyName);
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static MutableComponent nbt(String nbtPathPattern, boolean interpreting, Optional<Component> separator, DataSource dataSource) {
		return Component.nbt(nbtPathPattern, interpreting, separator, dataSource);
	}

	public static MutableComponent score(String name, String objective) {
		return Component.score(name, objective);
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static MutableComponent selector(String pattern, Optional<Component> separator) {
		return MutableComponent.create(new SelectorContents(pattern, separator));
	}

	public static Component wrap(TooltipComponent component) {
		return TooltipHacks.wrap(component);
	}

	private static final class TooltipHacks {

		/**
		 * This is a component for {@link CommonComponents#EMPTY}
		 * <br><br>
		 * When tooltip text is too wide it is wrapped around by neoforge ({@link ClientHooks#gatherTooltipComponents}) to the next line and {@link CommonComponents#EMPTY}
		 * components (empty strings) are discarded by minecraft's {@link net.minecraft.client.StringSplitter#splitLines StringSplitter#splitLines} method.<br>
		 */
		static final Component EMPTY_LINE_COMPONENT = wrap(new EmptyLineTooltipComponent());

		private TooltipHacks() {}

		private static Component wrap(TooltipComponent component) {
			return MutableComponent.create(new TooltipContents(component));
		}

	}

	public static List<Component> splitLines(Locale locale, String text, int maxLength, Style style) {
		BreakIterator breakIterator = BreakIterator.getLineInstance(locale);
		breakIterator.setText(text);

		List<Component> lines = new ArrayList<>();
		StringBuilder currentLine = new StringBuilder();

		int start = breakIterator.first();
		int end = breakIterator.next();

		while (end != BreakIterator.DONE) {
			String word = text.substring(start, end);

			if (currentLine.length() + word.length() >= maxLength) {
				lines.add(ComponentUtil.literal(currentLine.toString()).withStyle(style));
				currentLine = new StringBuilder();
			}

			if (word.equals("\n")) {
				lines.add(EMPTY_LINE);
			}
			else if (word.contains("\n")) {
				word = word.replace("\n", "");
				currentLine.append(word);
				lines.add(ComponentUtil.literal(currentLine.toString()).withStyle(style));
				currentLine = new StringBuilder();
			}
			else {
				currentLine.append(word);
			}

			start = end;
			end = breakIterator.next();
		}

		if (!currentLine.isEmpty()) {
			lines.add(ComponentUtil.literal(currentLine.toString()).withStyle(style));
		}

		return lines;
	}

	public static List<Component> splitLines(Locale locale, String text, Style style) {
		BreakIterator breakIterator = BreakIterator.getLineInstance(locale);
		breakIterator.setText(text);

		List<Component> lines = new ArrayList<>();
		StringBuilder currentLine = new StringBuilder();

		int start = breakIterator.first();
		int end = breakIterator.next();

		while (end != BreakIterator.DONE) {
			String word = text.substring(start, end);

			if (word.equals("\n")) {
				lines.add(EMPTY_LINE);
			}
			else if (word.contains("\n")) {
				word = word.replace("\n", "");
				currentLine.append(word);
				lines.add(ComponentUtil.literal(currentLine.toString()).withStyle(style));
				currentLine = new StringBuilder();
			}
			else {
				currentLine.append(word);
			}

			start = end;
			end = breakIterator.next();
		}

		if (!currentLine.isEmpty()) {
			lines.add(ComponentUtil.literal(currentLine.toString()).withStyle(style));
		}

		return lines;
	}

	public static <T extends Component> T setStyles(T component, Style style) {
		if (component instanceof MutableComponent mutable) {
			mutable.withStyle(style);
		}

		component.getSiblings().forEach(sibling -> setStyles(sibling, style));

		return component;
	}

}
