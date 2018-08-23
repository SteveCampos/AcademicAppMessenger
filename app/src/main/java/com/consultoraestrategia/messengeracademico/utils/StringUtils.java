package com.consultoraestrategia.messengeracademico.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Steve on 21/02/2017.
 */

public class StringUtils {
    private static final String TAG = StringUtils.class.getSimpleName();

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

    public static String getCode(String input) {
        String[] split = input.split("is your verification code.");
        String code = split[0].replaceAll("\\s+", "");
        Log.d(TAG, "code: " + code);
        return code;
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

    public static boolean isSuccessful(JSONObject jsonObject) {
        boolean s = false;
        try {
            s = jsonObject.getBoolean("Successful");
            Log.d("JSON RESULT", "" + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String getJsonObResult(JSONObject jsonObject) {
        String s = "";
        try {
            s = jsonObject.getJSONObject("Value").toString();
            Log.d("JSON RESULT", "" + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String removeInvalidCharacterFirebase(String x) {
        return x.replaceAll("[/#$.’\\[\\]]", "");
    }
}
