package com.consultoraestrategia.messengeracademico.chat.adapters.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Steve on 9/03/2017.
 */
public class MessageTextReceptorHolder extends RecyclerView.ViewHolder {
    private static final String TAG = MessageTextReceptorHolder.class.getSimpleName();
    @BindView(R.id.txt_message)
    TextView txtMessage;
    @BindView(R.id.txt_time)
    TextView txtTime;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    public MessageTextReceptorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(ChatMessage message, Context context, ChatMessageListener listener) {
        Log.d(TAG, "MessageTextReceptorHolder bind");
        txtMessage.setText(message.getMessageText());
        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));

        if (listener != null && message.getMessageStatus() != ChatMessage.STATUS_READED) {
            listener.onMessageReaded(message);
        }
    }
}
