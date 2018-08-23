package com.consultoraestrategia.messengeracademico.chat.adapters.holder;

import android.content.Context;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.base.SelectableViewHolder;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.utils.MessageUtils;
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
public class MessageTextEmisorHolder extends SelectableViewHolder<ChatMessage> {

    private static final String TAG = MessageTextEmisorHolder.class.getSimpleName();
    @BindView(R.id.txt_day_group)
    TextView txtDayGroup;
    @BindView(R.id.img_status)
    ImageView imgStatus;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.txt_message)
    EmojiTextView txtMessage;
    @BindView(R.id.layout_message)
    ConstraintLayout layoutMessage;


    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    @Override
    protected ViewGroup getBgRegion() {
        return layoutMessage;
    }

    public MessageTextEmisorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(final ChatMessage message, ChatMessage previousMessage, final ChatMessageListener listener) {
        super.bind(message, listener);

        Drawable imgDrawable = MessageUtils.getDrawableFromMessageStatus(message.getMessageStatus(), itemView.getContext());

        txtMessage.setText(message.getMessageText());
        imgStatus.setImageDrawable(imgDrawable);

        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));

        MessageUtils.setTimeTitleVisibility(message.getTimestamp(), previousMessage == null ? 0 : previousMessage.getTimestamp(), txtDayGroup, itemView.getResources());
        checkStatusAndFire(message, listener);
    }


    public static void checkStatusAndFire(ChatMessage message, ChatMessageListener listener) {
        if (message.getMessageStatus() != ChatMessage.STATUS_READED) {
            if (listener != null) {
                listener.onMessageNotReaded(message);
            }
        }

        if (message.getMessageStatus() == ChatMessage.STATUS_WRITED) {
            if (listener != null) {
                listener.onMessageNotSended(message);
            }
        }
    }

}
