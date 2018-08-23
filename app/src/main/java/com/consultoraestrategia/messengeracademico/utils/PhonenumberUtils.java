package com.consultoraestrategia.messengeracademico.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhonenumberUtils {

    private static final String TAG = PhonenumberUtils.class.getSimpleName();

    //Google's common Java, C++ and JavaScript library for parsing, formatting, and validating international phone numbers.
    //https://github.com/googlei18n/libphonenumber
    public static String getCountryCode(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return getCountryCode(manager);
    }

    public static String getCountryCode(TelephonyManager manager) {
        if (manager != null) {
            return manager.getNetworkCountryIso().toUpperCase();
        }
        return null;
    }


    public static String formatPhonenumber(String countryCode, String phonenumber) {
        Log.d(TAG, "formatPhonenumber: ");
        String formatNumber = null;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phonenumber,
                    countryCode);
            if (phoneUtil.isValidNumber(numberProto)) {
                formatNumber = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
                Log.d(TAG, "phonenumber E164: " + formatNumber);
            }
        } catch (Exception e) {
            Log.d(TAG, "EXCEPTION formatPhonenumber: " + e);
            return null;
        }
        return formatNumber;
    }
}
