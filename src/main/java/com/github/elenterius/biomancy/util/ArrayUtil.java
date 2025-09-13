package com.github.elenterius.biomancy.util;

import net.minecraft.util.RandomSource;

import java.util.Arrays;

public final class ArrayUtil {

	private ArrayUtil() {}

	/**
	 * shuffle the array in place using Durstenfeld / Fisher-Yates
	 */
	public static void shuffle(Object[] array, RandomSource random) {
		for (int i = array.length - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);
			Object temp = array[j];
			array[j] = array[i];
			array[i] = temp;
		}
	}

	/**
	 * shuffle the array in place using Durstenfeld / Fisher-Yates
	 */
	public static void shuffle(int[] array, RandomSource random) {
		for (int i = array.length - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);
			int temp = array[j];
			array[j] = array[i];
			array[i] = temp;
		}
	}

	/**
	 * shuffle the array in place using Durstenfeld / Fisher-Yates
	 */
	public static void shuffle(float[] array, RandomSource random) {
		for (int i = array.length - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);
			float temp = array[j];
			array[j] = array[i];
			array[i] = temp;
		}
	}

	/**
	 * shuffle the array in place using Durstenfeld / Fisher-Yates
	 */
	public static void shuffle(double[] array, RandomSource random) {
		for (int i = array.length - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);
			double temp = array[j];
			array[j] = array[i];
			array[i] = temp;
		}
	}

	public static int sum(int[] array) {
		int sum = 0;
		for (int i : array) sum += i;
		return sum;
	}

	public static <T> T[] copyOf(T[] array) {
		return Arrays.copyOf(array, array.length);
	}

	public static int[] copyOf(int[] array) {
		return Arrays.copyOf(array, array.length);
	}

}
