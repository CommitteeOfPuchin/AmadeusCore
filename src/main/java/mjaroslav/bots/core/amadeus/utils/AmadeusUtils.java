package mjaroslav.bots.core.amadeus.utils;

import java.util.ArrayList;
import java.util.HashMap;

import mjaroslav.bots.core.amadeus.AmadeusCore;

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

    public static HashMap<String, String> parseArgsToMap(String args) throws Exception {
        HashMap<String, String> result = new HashMap<String, String>();
        String argsString = args;
        int index = -1;
        while (argsString.length() > 0) {
            index = argsString.indexOf("=");
            String key = "", value = "";
            if (index != -1) {
                key = argsString.substring(0, index);
                value = "";
                argsString = argsString.substring(index + 1);
                index = -1;
                if (argsString.startsWith("\"")) {
                    argsString = argsString.substring(1);
                    index = argsString.indexOf("\"");
                    if (index != -1) {
                        value = argsString.substring(0, index);
                        argsString = argsString.substring(index + 1);
                    }
                } else {
                    index = argsString.indexOf(" ");
                    if (index != -1) {
                        value = argsString.substring(0, index);
                        argsString = argsString.substring(index);
                    } else {
                        value = argsString;
                        argsString = "";
                    }
                }
                argsString = argsString.trim();
                index = -1;
                result.put(key, value);
            } else
                throw new Exception("Error on args parsing >" + argsString);
        }
        return result;
    }

    public static ArrayList<String> parseArgsToArray(String args) throws Exception {
        ArrayList<String> result = new ArrayList<String>();
        String argsString = args;
        int index = 0;
        while (argsString.length() > 0) {
            if (index != -1) {
                if (argsString.startsWith("\"")) {
                    argsString = argsString.substring(1);
                    index = argsString.indexOf("\"");
                    if (index != -1) {
                        result.add(argsString.substring(0, index));
                        argsString = argsString.substring(index + 1);
                    }
                } else if (argsString.startsWith(" "))
                    argsString = argsString.substring(1);
                else {
                    index = argsString.indexOf(" ");
                    if (index != -1) {
                        result.add(argsString.substring(0, index));
                        argsString = argsString.substring(index + 1);
                    } else {
                        result.add(argsString);
                        argsString = "";
                    }
                }
            } else
                throw new Exception("Error on args parsing >" + argsString);
        }
        return result;
    }

    public static String removePreifx(String text, AmadeusCore core, Iterable<String> prefixes) {
        for (String prefix : prefixes) {
            String replaced = prefix.replaceFirst("<bot>", "<@" + core.client.getOurUser().getLongID() + ">");
            if (text.toLowerCase().startsWith(replaced))
                return text.substring(replaced.length()).trim();
        }
        return text;
    }
}
