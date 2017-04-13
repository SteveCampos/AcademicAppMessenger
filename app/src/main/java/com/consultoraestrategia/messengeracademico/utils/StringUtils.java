package com.consultoraestrategia.messengeracademico.utils;

/**
 * Created by Steve on 21/02/2017.
 */

public class StringUtils {
    /**
     * Get text between two strings. Passed limiting strings are not
     * included into result.
     *
     * @param input    Text to search in.
     * @param textFrom Text to start cutting from (exclusive).
     * @param textTo   Text to stop cuutting at (exclusive).
     */
    public static String getCodeBetweenStrings(
            String input,
            String textFrom,
            String textTo) {
        return input.toLowerCase().substring(input.lastIndexOf(textFrom) + textFrom.length(), input.lastIndexOf(textTo)).replaceAll("\\s", "").replaceAll("-", "");
    }

    public static String[] sortAlphabetical(String key1, String key2) {
        String temp = null;
        int compare = key1.compareTo(key2);//Comparing strings by their alphabetical order
        if (compare > 0) {
            temp = key2;
            key2 = key1;
            key1 = temp;
        }
        return new String[]{key1, key2};
    }
}
