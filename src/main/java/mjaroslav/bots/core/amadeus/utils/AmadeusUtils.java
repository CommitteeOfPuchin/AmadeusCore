package mjaroslav.bots.core.amadeus.utils;

public class AmadeusUtils {
	public static boolean stringIsNotEmpty(String input) {
		return input != null && input.length() > 0;
	}

	public static boolean stringIsEmpty(String input) {
		return !stringIsNotEmpty(input);
	}

	/**
	 * Always return string.
	 *
	 * @param input - String object.
	 * @return Empty string if null.
	 */
	public static String string(String input) {
		return stringIsEmpty(input) ? "" : input;
	}

	public static float[] toFloatArray(double[] input) {
		if (input != null) {
			float[] result = new float[input.length];
			for (int id = 0; id < input.length; id++)
				result[id] = (float) input[id];
			return result;
		}
		return new float[] {};
	}

	public static double[] toDoubleArray(float[] input) {
		if (input != null) {
			double[] result = new double[input.length];
			for (int id = 0; id < input.length; id++)
				result[id] = input[id];
			return result;
		}
		return new double[] {};
	}
}
