package com.consultoraestrategia.messengeracademico.utils;

/**
 * Created by Steve on 21/02/2017.
 */

public class StringUtils {
    /**
     * Get text between two strings. Passed limiting strings are not
     * included into result.
     *
     * @param input     Text to search in.
     * @param textFrom Text to start cutting from (exclusive).
     * @param textTo   Text to stop cuutting at (exclusive).
     */
    public static String getCodeBetweenStrings(
            String input,
            String textFrom,
            String textTo) {
        return input.toLowerCase().substring(input.lastIndexOf(textFrom)+ textFrom.length(), input.lastIndexOf(textTo)).replaceAll("\\s","").replaceAll("-","");
    }
}
