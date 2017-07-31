package com.consultoraestrategia.messengeracademico.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jairc on 22/03/2017.
 */

public class TimeUtils {

    private static final String TAG = TimeUtils.class.getSimpleName();


    public static String calculateLastConnection(long startDate, long endDate, Resources resources) {

        String lastConnection = "";
        String period = "";
        long rest = 0;
        //milliseconds
        long different = endDate - startDate;

        if (different < 0) {
            different = -different;
        }

        Log.d(TAG, "startDate : " + startDate);
        Log.d(TAG, "endDate : " + endDate);
        Log.d(TAG, "different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long monthInMilli = daysInMilli * 30;

        if (different <= minutesInMilli) {
            rest = (different / secondsInMilli);
            period = resources.getString(R.string.time_seconds);
        } else if (different <= hoursInMilli) {
            rest = (different / minutesInMilli);
            period = resources.getString(R.string.time_minutes);
        } else if (different <= daysInMilli) {
            rest = (different / hoursInMilli);
            period = resources.getString(R.string.time_hours);
        } else if (different <= monthInMilli) {
            rest = (different / daysInMilli);
            period = resources.getString(R.string.time_days);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            return formatter.format(startDate);
        }
        lastConnection = String.format(Locale.getDefault(), resources.getString(R.string.last_connection), rest, period);
        Log.d(TAG, "lastConnection: " + lastConnection);
        return lastConnection;
    }

    public static String lastMessageTime(long startDate, long endDate, Resources resources) {

        String lastConnection = "";
        String period = "";
        long rest = 0;
        //milliseconds
        long different = endDate - startDate;

        if (different < 0) {
            different = -different;
        }

        Log.d(TAG, "startDate : " + startDate);
        Log.d(TAG, "endDate : " + endDate);
        Log.d(TAG, "different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long monthInMilli = daysInMilli * 30;

        if (different <= daysInMilli) {
            rest = (different / hoursInMilli);
            period = resources.getString(R.string.time_hours);
            SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return SIMPLE_DATE_FORMAT.format(startDate);

        } else if (different <= monthInMilli) {
            rest = (different / daysInMilli);
            period = resources.getString(R.string.time_days);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            return formatter.format(startDate);
        }
        Log.d(TAG, "%d resto: " + rest + ", %s periodo: " + period);

        lastConnection = String.format(Locale.getDefault(), resources.getString(R.string.last_message_time), rest, period);
        return lastConnection;
    }

    public static String formatDate(long timestamp, Resources resources) {

        Calendar dateToFormat = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        dateToFormat.setTimeInMillis(timestamp);
        now.setTimeInMillis(new Date().getTime());

        boolean sameYear = dateToFormat.get(Calendar.YEAR) == now.get(Calendar.YEAR);
        boolean sameMonth = dateToFormat.get(Calendar.MONTH) == now.get(Calendar.MONTH);
        boolean sameWeek = dateToFormat.get(Calendar.WEEK_OF_MONTH) == now.get(Calendar.WEEK_OF_MONTH);
        boolean sameDay = dateToFormat.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH);


        if (sameYear && sameMonth && sameDay) {
            return resources.getString(R.string.global_time_today);
        } else if (sameYear && sameMonth && sameWeek) {
            return new SimpleDateFormat("EEEE", Locale.getDefault()).format(dateToFormat.getTime());
        }
        return new SimpleDateFormat("EEEE, d 'de' MMMM 'del' yyyy", Locale.getDefault()).format(dateToFormat.getTime());
    }

}
