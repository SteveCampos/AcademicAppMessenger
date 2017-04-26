package com.consultoraestrategia.messengeracademico.chat.adapters.holder;

import android.content.Context;

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

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by @stevecampos on 9/03/2017.
 */
public class MessageTextEmisorHolder extends RecyclerView.ViewHolder {

    private static final String TAG = MessageTextEmisorHolder.class.getSimpleName();
    @BindView(R.id.img_status)
    ImageView imgStatus;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.message_text)
    TextView messageText;
    @BindView(R.id.layout_bubble)
    RelativeLayout layoutBubble;


    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    public MessageTextEmisorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(ChatMessage message, Context context, ChatMessageListener listener) {
        messageText.setText(message.getMessageText());

        int drawableResStatus = R.drawable.ic_access_time;
        int status = message.getMessageStatus();
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
        imgStatus.setImageDrawable(ContextCompat.getDrawable(context, drawableResStatus));
        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));
    }
}
