package com.github.elenterius.biomancy.util.colors;

import net.minecraft.util.FastColor;

public final class ColorSpaces {

	private static double cube(double x) {
		return x * x * x;
	}

	private static double cubeRoot(double value) {
		return Math.cbrt(value);
	}

	private static double clamp(double value, double min, double max) {
		return Math.min(max, Math.max(value, min));
	}

	private static int clamp(int value, int min, int max) {
		return Math.min(max, Math.max(value, min));
	}

	private static int floor(double value) {
		int i = (int) value;
		return value < (double) i ? i - 1 : i;
	}

	/**
	 * constrains angle in degrees to [0..360]
	 */
	public static double constrainAngleDeg(double angleDegrees) {
		return ((angleDegrees % 360.0) + 360.0) % 360.0;
	}

	private static final class HSL {
		private HSL() {
		}

		/**
		 * @return s and l in the range of [0.0, 1.0]
		 */
		public static double[] fromARGB32(int rgb) {
			double r = FastColor.ARGB32.red(rgb) / 255.0;
			double g = FastColor.ARGB32.green(rgb) / 255.0;
			double b = FastColor.ARGB32.blue(rgb) / 255.0;

			double min = Math.min(r, Math.min(g, b));
			double max = Math.max(r, Math.max(g, b));
			double diff = max - min;

			double hue;
			double saturation;
			double luminance = (max + min) / 2.0;

			if (diff == 0.0) {
				hue = 0.0;
				saturation = 0.0;
			}
			else {
				if (max == r) hue = (60.0 * (g - b) / diff + 360) % 360.0;
				else if (max == g) hue = 60.0 * (b - r) / diff + 120.0;
				else hue = 60.0 * (r - g) / diff + 240.0;

				saturation = luminance <= 0.5 ? (max - min) / (max + min) : (max - min) / (2 - max - min);
			}

			return new double[]{hue, saturation, luminance};
		}

		/**
		 * expects s and l in the range of [0.0, 1.0]
		 */
		public static int toARGB32(double[] hsl) {
			return toARGB32(hsl[0], hsl[1], hsl[2]);
		}

		/**
		 * expects s and l in the range of [0.0, 1.0]
		 */
		public static int toARGB32(double h, double s, double l) {
			h = (h % 360.0) / 360.0;

			double r;
			double g;
			double b;

			if (s == 0.0) {
				r = l;
				g = l;
				b = l;
			}
			else {
				double q = l < 0.5 ? l * (1.0 + s) : l + s - l * s;
				double p = 2.0 * l - q;

				r = hueToRgb(p, q, h + 1.0 / 3.0);
				g = hueToRgb(p, q, h);
				b = hueToRgb(p, q, h - 1.0 / 3.0);
			}

			return FastColor.ARGB32.color(
					255,
					clamp(floor(r * 255.0), 0, 255),
					clamp(floor(g * 255.0), 0, 255),
					clamp(floor(b * 255.0), 0, 255)
			);
		}

		private static double hueToRgb(double p, double q, double h) {
			if (h < 0.0) h += 1.0;
			if (h > 1.0) h -= 1.0;

			if (h < 1.0 / 6.0) return p + (q - p) * 6.0 * h;
			if (h < 1.0 / 2.0) return q;
			if (h < 2.0 / 3.0) return p + (q - p) * 6.0 * (2.0 / 3.0 - h);

			return p;
		}

	}

	public static final class sRGB {

		private sRGB() {
		}

		/**
		 * Test if sRGB values are in range [0.0, 1.0]
		 */
		public static boolean isInGamut(double[] linearRGB) {
			double r = linearRGB[0];
			double g = linearRGB[1];
			double b = linearRGB[2];

			return r >= 0.0 && r <= 1.0
					&& g >= 0.0 && g <= 1.0
					&& b >= 0.0 && b <= 1.0;
		}

		/**
		 * Convert in-gamut sRGB values in range [0.0, 1.0] to linear light form
		 *
		 * @return linear light sRGB
		 */
		public static double toLinearLight(double colorValue) {
			double sign = colorValue < 0.0 ? -1.0 : 1.0;
			double abs = Math.abs(colorValue);

			if (abs <= 0.04045) {
				return colorValue / 12.92;
			}

			return sign * Math.pow((abs + 0.055) / 1.055, 2.4);
		}

		/**
		 * @return linear light sRGB
		 */
		public static double[] toLinearLight(double[] rgb) {
			return new double[]{
					toLinearLight(rgb[0]),
					toLinearLight(rgb[1]),
					toLinearLight(rgb[2]),
			};
		}

		/**
		 * Convert linear light sRGB in the range [0.0, 1.0] to gamma corrected form
		 *
		 * @return gamma corrected sRGB
		 * @see <a href="https://en.wikipedia.org/wiki/SRGB">SRGB</a>
		 */
		public static double gammaFromLinear(double colorValue) {
			double sign = colorValue < 0.0 ? -1.0 : 1.0;
			double abs = Math.abs(colorValue);

			if (abs > 0.0031308) {
				return sign * (1.055 * Math.pow(abs, 1.0 / 2.4) - 0.055);
			}

			return 12.92 * colorValue;
		}

		/**
		 * @return gamma corrected sRGB
		 */
		public static double[] gammaFromLinear(double[] linearRGB) {
			return new double[]{
					gammaFromLinear(linearRGB[0]),
					gammaFromLinear(linearRGB[1]),
					gammaFromLinear(linearRGB[2]),
			};
		}

		/**
		 * @return sRGB
		 */
		public static double[] fromARGB32(int argb) {
			return new double[]{
					FastColor.ARGB32.red(argb) / 255.0,
					FastColor.ARGB32.green(argb) / 255.0,
					FastColor.ARGB32.blue(argb) / 255.0
			};
		}

		/**
		 * @return ARGB 32
		 */
		public static int toARGB32(double[] rgb) {
			return FastColor.ARGB32.color(
					255,
					clamp((int) (rgb[0] * 255.0), 0, 255),
					clamp((int) (rgb[1] * 255.0), 0, 255),
					clamp((int) (rgb[2] * 255.0), 0, 255)
			);
		}
	}

	/**
	 * @see <a href="https://bottosson.github.io/posts/oklab">Oklab</a>
	 * @see <a href="https://www.w3.org/TR/css-color-4/#color-conversion-code">W3C Color Module 4 - Color Conversion</a>
	 */
	public static final class OkLab {
		private OkLab() {
		}

		/**
		 * Convert linear light sRGB [0..1] to OKLab
		 *
		 * @return Lab (Lightness, a, b)
		 */
		public static double[] fromLinearSRGB(double rLinear, double gLinear, double bLinear) {
			double cubeRootL = cubeRoot(0.4122214708 * rLinear + 0.5363325363 * gLinear + 0.0514459929 * bLinear);
			double cubeRootM = cubeRoot(0.2119034982 * rLinear + 0.6806995451 * gLinear + 0.1073969566 * bLinear);
			double cubeRootS = cubeRoot(0.0883024619 * rLinear + 0.2817188376 * gLinear + 0.6299787005 * bLinear);

			double L = 0.2104542683093140 * cubeRootL + 0.7936177747023054 * cubeRootM - 0.0040720430116193 * cubeRootS;
			double a = 1.9779985324311684 * cubeRootL - 2.4285922420485799 * cubeRootM + 0.4505937096174110 * cubeRootS;
			double b = 0.0259040424655478 * cubeRootL + 0.7827717124575296 * cubeRootM - 0.8086757549230774 * cubeRootS;

			return new double[]{L, a, b};
		}

		/**
		 * @return Lab (Lightness, a, b)
		 */
		public static double[] fromSRGB(double r, double g, double b) {
			return fromLinearSRGB(sRGB.toLinearLight(r), sRGB.toLinearLight(g), sRGB.toLinearLight(b));
		}

		/**
		 * @return Lab (Lightness, a, b)
		 */
		public static double[] fromSRGB(double[] rgb) {
			return fromSRGB(rgb[0], rgb[1], rgb[2]);
		}

		/**
		 * @return Lab (Lightness, a, b)
		 */
		public static double[] fromARGB32(int rgb) {
			return fromSRGB(sRGB.fromARGB32(rgb));
		}

		/**
		 * @return linear sRGB (r, g, b)
		 */
		public static double[] toLinearSRGB(double[] Lab) {
			return toLinearSRGB(Lab[0], Lab[1], Lab[2]);
		}

		/**
		 * @param L Lightness from 0.0 to 1.0
		 * @param a from -1.0 to 1.0
		 * @param b from -1.0 to 1.0
		 * @return linear-light sRGB (r, g, b)
		 */
		public static double[] toLinearSRGB(double L, double a, double b) {
			double l = cube(L + 0.3963377773761749 * a + 0.2158037573099136 * b);
			double m = cube(L - 0.1055613458156586 * a - 0.0638541728258133 * b);
			double s = cube(L - 0.0894841775298119 * a - 1.2914855480194092 * b);

			return new double[]{
					4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
					-1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
					-0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s,
			};
		}

		/**
		 * @return sRGB (r, g, b)
		 */
		public static double[] toSRGB(double[] Lab) {
			double[] linearRGB = toLinearSRGB(Lab);
			return sRGB.gammaFromLinear(linearRGB);
		}

		/**
		 * @return ARGB (alpha, r, g, b)
		 */
		public static int toARGB32(double[] Lab) {
			return sRGB.toARGB32(toSRGB(Lab));
		}

		/**
		 * @return LCh (Lightness, Chroma, hue)
		 */
		public static double[] toOkLCh(double[] Lab) {
			double Lightness = Lab[0]; // 0.0 - 1.0
			double a = Lab[1]; // -1.0 - 1.0
			double b = Lab[2]; // -1.0 - 1.0
			//            double Chroma = Math.min(Math.hypot(a, b), 0.37); // 0.0 - 0.37 // hypot = sqrt(a * a + b * b)
			double Chroma = Math.hypot(a, b); // 0.0 - 0.37 // hypot = sqrt(a * a + b * b)
			double hue = constrainAngleDeg(Math.toDegrees(Math.atan2(b, a))); // 0.0 - 360.0

			return new double[]{Lightness, Chroma, hue};
		}

		/**
		 * Calculate difference (i.e. Euclidean distance) between color sample and reference
		 *
		 * @param reference reference OKLab color
		 * @param sample    sample  OKLab color
		 * @return deltaE OK
		 */
		public static double deltaEOK(double[] reference, double[] sample) {
			double dL = reference[0] - sample[0];
			double da = reference[1] - sample[1];
			double db = reference[2] - sample[2];
			return Math.sqrt(dL * dL + da * da + db * db);
		}

		/**
		 * Clip OKLab color to the sRGB gamut.
		 *
		 * @return clipped OKLab color
		 */
		public static double[] clipToSRGBGamut(double[] Lab) {
			double[] linearRGB = toLinearSRGB(Lab);
			return fromLinearSRGB(
					clamp(linearRGB[0], 0.0, 1.0),
					clamp(linearRGB[1], 0.0, 1.0),
					clamp(linearRGB[2], 0.0, 1.0)
			);
		}

	}

	/**
	 * Oklch is the cylindrical representation of Oklab
	 */
	public static final class OkLCh {
		private OkLCh() {
		}

		/**
		 * @return LCh
		 * <br>(
		 * <br> Lightness: 0.0 - 1.0
		 * <br> Chroma: 0.0 - 0.37
		 * <br> hue: 0.0 - 360.0
		 * <br>)
		 */
		public static double[] fromLinearSRGB(double rLinear, double gLinear, double bLinear) {
			return OkLab.toOkLCh(OkLab.fromLinearSRGB(rLinear, gLinear, bLinear));
		}

		/**
		 * @return LCh (Lightness, Chroma, hue)
		 */
		public static double[] fromSRGB(double r, double g, double b) {
			return fromLinearSRGB(sRGB.toLinearLight(r), sRGB.toLinearLight(g), sRGB.toLinearLight(b));
		}

		/**
		 * @return LCh (Lightness, Chroma, hue)
		 */
		public static double[] fromSRGB(double[] rgb) {
			return fromSRGB(rgb[0], rgb[1], rgb[2]);
		}

		/**
		 * @return LCh (Lightness, Chroma, hue)
		 */
		public static double[] fromARGB32(int rgb) {
			return fromSRGB(sRGB.fromARGB32(rgb));
		}

		/**
		 * @return linear sRGB (r, g, b)
		 */
		public static double[] toLinearSRGB(double[] LCh) {
			return OkLab.toLinearSRGB(toOkLab(LCh));
		}

		/**
		 * @return sRGB (r, g, b)
		 */
		public static double[] toSRGB(double[] LCh) {
			double[] linearRGB = toLinearSRGB(LCh);
			return sRGB.gammaFromLinear(linearRGB);
		}

		/**
		 * @return aRGB (r, g, b)
		 */
		public static int toARGB32(double[] LCh) {
			return sRGB.toARGB32(toSRGB(LCh));
		}

		/**
		 * @return Lab (Lightness, a, b)
		 */
		public static double[] toOkLab(double[] LCh) {
			return toOkLab(LCh[0], LCh[1], LCh[2]);
		}

		/**
		 * @return Lab (Lightness, a, b)
		 */
		public static double[] toOkLab(double L, double C, double h) {
			double hue = Math.toRadians(h);
			double a = C * Math.cos(hue);
			double b = C * Math.sin(hue);

			return new double[]{L, a, b};
		}

		public static int gamutMapToARGB32(double[] LCh) {
			return sRGB.toARGB32(gamutMapToSRGB(LCh));
		}

		/**
		 * Binary Search Gamut Mapping Algorithm with Local MINDE
		 *
		 * @param LCh OKLCh color
		 * @return color mapped to the gamut of sRGB
		 * @see <a href="https://www.w3.org/TR/css-color-4/#css-gamut-mapping">OKLab Gamut Mapping</a>
		 */
		public static double[] gamutMapToSRGB(final double[] LCh) {
			final double L = LCh[0];

			if (L >= 1.0) return OkLab.toSRGB(new double[]{1, 0, 0});
			if (L <= 0.0) return OkLab.toSRGB(new double[]{0, 0, 0});

			double[] linearRGB = toLinearSRGB(LCh);
			if (sRGB.isInGamut(linearRGB)) {
				return sRGB.gammaFromLinear(linearRGB);
			}

			final double JND = 0.02;
			final double epsilon = 1.0E-04;

			double[] currentLab = toOkLab(LCh);
			double[] clippedLab = OkLab.clipToSRGBGamut(currentLab);
			double E = OkLab.deltaEOK(clippedLab, currentLab);

			if (E < JND) return OkLab.toSRGB(clippedLab);

			double min = 0.0;
			double max = LCh[1]; // chroma
			boolean minInGamut = true;

			double hRad = Math.toRadians(LCh[2]);
			final double cosHue = Math.cos(hRad);
			final double sinHue = Math.sin(hRad);

			while (max - min > epsilon) {
				double chroma = (min + max) / 2.0;

				currentLab[1] = chroma * cosHue;
				currentLab[2] = chroma * sinHue;

				if (minInGamut && sRGB.isInGamut(OkLab.toLinearSRGB(currentLab))) {
					min = chroma;
					continue;
				}

				clippedLab = OkLab.clipToSRGBGamut(currentLab);
				E = OkLab.deltaEOK(clippedLab, currentLab);

				if (E < JND) {
					if (JND - E < epsilon) return OkLab.toSRGB(clippedLab);
					else {
						minInGamut = false;
						min = chroma;
					}
				}
				else {
					max = chroma;
				}
			}

			//            clippedLab = OkLab.clipToSRGBGamut(currentLab);
			return OkLab.toSRGB(clippedLab);
		}

	}

}
