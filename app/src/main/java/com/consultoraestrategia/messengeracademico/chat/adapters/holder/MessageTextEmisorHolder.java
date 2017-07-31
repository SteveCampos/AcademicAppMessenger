package com.consultoraestrategia.messengeracademico.chat.adapters.holder;

import android.content.Context;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.utils.TimeUtils;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by @stevecampos on 9/03/2017.
 */
public class MessageTextEmisorHolder extends RecyclerView.ViewHolder {

    private static final String TAG = MessageTextEmisorHolder.class.getSimpleName();
    @BindView(R.id.img_status)
    public ImageView imgStatus;
    @BindView(R.id.txt_time)
    public TextView txtTime;
    @BindView(R.id.message_text)
    public EmojiTextView messageText;
    @BindView(R.id.layout_bubble)
    public RelativeLayout layoutBubble;
    @BindView(R.id.txt_day_group)
    public TextView txtTimeTitle;


    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    public MessageTextEmisorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(ChatMessage message, ChatMessage previousMessage, Drawable drawable, Resources resources) {
        messageText.setText(message.getMessageText());
        imgStatus.setImageDrawable(drawable);
        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));
        setTimeTitleVisibility(message.getTimestamp(), previousMessage == null ? 0 : previousMessage.getTimestamp(), txtTimeTitle, resources);
    }

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
