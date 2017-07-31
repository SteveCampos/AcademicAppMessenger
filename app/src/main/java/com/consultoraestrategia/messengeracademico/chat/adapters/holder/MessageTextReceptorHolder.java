package com.consultoraestrategia.messengeracademico.chat.adapters.holder;


import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by @stevecampos on 9/03/2017.
 */
public class MessageTextReceptorHolder extends RecyclerView.ViewHolder {

    private static final String TAG = MessageTextReceptorHolder.class.getSimpleName();
    @BindView(R.id.txt_message)
    public EmojiTextView txtMessage;
    @BindView(R.id.txt_time)
    public TextView txtTime;
    @BindView(R.id.txt_day_group)
    public TextView txtTimeTitle;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    public MessageTextReceptorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }


    public void bind(ChatMessage message, ChatMessage previousMessage, ChatMessageListener listener, Resources resources) {
        Log.d(TAG, "MessageTextReceptorHolder bind");
        txtMessage.setText(message.getMessageText());
        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));

        if (listener != null && message.getMessageStatus() != ChatMessage.STATUS_READED) {
            listener.onMessageReaded(message);
        }

        MessageTextEmisorHolder.
                setTimeTitleVisibility(message.getTimestamp(), previousMessage == null ? 0 : previousMessage.getTimestamp(), txtTimeTitle, resources);
    }


}
