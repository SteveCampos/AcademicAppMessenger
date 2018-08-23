package com.consultoraestrategia.messengeracademico.chat.adapters.holder;


import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.base.SelectableViewHolder;
import com.consultoraestrategia.messengeracademico.base.actionMode.Selectable;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.utils.MessageUtils;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by @stevecampos on 9/03/2017.
 */
public class MessageTextReceptorHolder extends SelectableViewHolder<ChatMessage> {

    private static final String TAG = MessageTextReceptorHolder.class.getSimpleName();

    @BindView(R.id.txt_day_group)
    TextView txtDayGroup;
    @BindView(R.id.txt_message)
    EmojiTextView txtMessage;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.layout_message)
    ConstraintLayout layoutMessage;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    @Override
    protected View getBgRegion() {
        return layoutMessage;
    }

    public MessageTextReceptorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }


    public void bind(ChatMessage message, ChatMessage previousMessage, ChatMessageListener listener) {
        bind(message, listener);
        Log.d(TAG, "MessageTextReceptorHolder bind");
        txtMessage.setText(message.getMessageText());
        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));

        if (listener != null && message.getMessageStatus() != ChatMessage.STATUS_READED) {
            listener.onMessageReaded(message);
        }

        MessageUtils.
                setTimeTitleVisibility(message.getTimestamp(), previousMessage == null ? 0 : previousMessage.getTimestamp(), txtDayGroup, itemView.getResources());
    }


}
