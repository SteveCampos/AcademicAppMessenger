package com.consultoraestrategia.messengeracademico.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

import java.util.Calendar;

public class MessageUtils {
    public static Drawable getDrawableFromMessageStatus(int status, Context context) {
        int drawableResStatus = R.drawable.ic_access_time;
        switch (status) {
            case ChatMessage.STATUS_WRITED:
                drawableResStatus = R.drawable.ic_access_time;
                break;
            case ChatMessage.STATUS_SEND:
                drawableResStatus = R.drawable.ic_single_check;
                break;
            case ChatMessage.STATUS_DELIVERED:
                drawableResStatus = R.drawable.ic_double_check;
                break;
            case ChatMessage.STATUS_READED:
                drawableResStatus = R.drawable.ic_double_check_colored;
                break;
        }
        return ContextCompat.getDrawable(context, drawableResStatus);
    }



    public static void setTimeTitleVisibility(long ts1, long ts2, TextView timeText, Resources resources) {

        if (ts2 == 0) {
            timeText.setVisibility(View.VISIBLE);
            timeText.setText(TimeUtils.formatDate(ts1, resources));
        } else {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTimeInMillis(ts1);
            cal2.setTimeInMillis(ts2);

            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

            if (sameDay) {
                timeText.setVisibility(View.GONE);
                timeText.setText("");
            } else {
                timeText.setVisibility(View.VISIBLE);
                timeText.setText(TimeUtils.formatDate(ts1, resources));
            }

        }
    }
}
