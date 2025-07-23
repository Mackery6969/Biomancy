package com.github.elenterius.biomancy.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class FormatUtil {

	private static Locale prevLocale = null;
	private static DecimalFormat integerFormatter = null;
	private static DecimalFormat doubleFormatter = null;

	private FormatUtil() {}

	public static String format(String template, Object... objects) {
		return String.format(getLocale(), template, objects);
	}

	public static String formatDuration(long elapsedNanos) {
		return formatDuration(Duration.ofNanos(elapsedNanos));
	}

	public static String formatDuration(Duration duration) {
		long daysPart = duration.toDaysPart();
		int hoursPart = duration.toHoursPart();
		int minutesPart = duration.toMinutesPart();
		int secondsPart = duration.toSecondsPart();
		int millisPart = duration.toMillisPart();
		int nanosPart = duration.toNanosPart() % 1_000_000;

		List<String> parts = new ArrayList<>();

		if (daysPart > 0)
			parts.add(daysPart + " day" + (daysPart > 1 ? "s" : ""));
		if (hoursPart > 0)
			parts.add(hoursPart + " hr" + ((hoursPart > 1 ? "s" : "")));
		if (minutesPart > 0)
			parts.add(minutesPart + " min");
		if (secondsPart > 0)
			parts.add(secondsPart + " sec");
		if (millisPart > 0)
			parts.add(millisPart + " ms");
		if (nanosPart > 0)
			parts.add(nanosPart + " ns");

		return String.join(", ", parts);
	}

	public static String formatInteger(double value) {
		return getIntegerFormatter().format(value);
	}

	public static DecimalFormat getIntegerFormatter() {
		updateDecimalFormat();
		return integerFormatter;
	}

	public static String formatDouble(double value) {
		return getDoubleFormatter().format(value);
	}

	public static DecimalFormat getDoubleFormatter() {
		updateDecimalFormat();
		return doubleFormatter;
	}

	public static Locale getLocale() {
		return FMLEnvironment.dist.isClient() ? getClientLocale() : getServerLocale();
	}

	private static Locale getClientLocale() {
		return Minecraft.getInstance().getLocale();
	}

	private static Locale getServerLocale() {
		return Locale.US; // we provide the US locale because the server uses the default language en_us
	}

	private static void updateDecimalFormat() {
		Locale locale = getLocale();
		if (locale.equals(prevLocale)) return;

		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
		integerFormatter = new DecimalFormat("#,###,###", symbols);
		doubleFormatter = new DecimalFormat("#.###", symbols);
		prevLocale = locale;
	}

}
