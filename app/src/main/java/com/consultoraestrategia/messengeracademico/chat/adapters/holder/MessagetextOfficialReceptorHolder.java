package com.consultoraestrategia.messengeracademico.chat.adapters.holder;

import android.content.res.Resources;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.OfficialMessage;
import com.consultoraestrategia.messengeracademico.utils.MessageUtils;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.consultoraestrategia.messengeracademico.chat.adapters.holder.MessageTextOfficialEmisorHolder.showOfficialMessage;

/**
 * Created by @stevecampos on 16/08/2017.
 */

public class MessagetextOfficialReceptorHolder extends RecyclerView.ViewHolder {

    public static final String TAG = MessagetextOfficialReceptorHolder.class.getSimpleName();
    @BindView(R.id.txt_day_group)
    TextView txtDayGroup;
    @BindView(R.id.txt_subject)
    AppCompatTextView txtSubject;
    @BindView(R.id.txt_official_title)
    AppCompatTextView txtTitle;
    @BindView(R.id.txt_additional1)
    AppCompatTextView txtAdditional1;
    @BindView(R.id.txt_additional2)
    AppCompatTextView txtAdditional2;
    @BindView(R.id.txt_important_reference)
    AppCompatTextView txtImportantReference;
    @BindView(R.id.cardview)
    CardView cardview;
    @BindView(R.id.message_text)
    TextView messageText;
    @BindView(R.id.txt_official_emisor)
    TextView txtOfficialEmisor;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.btn_deny)
    AppCompatButton btnDeny;
    @BindView(R.id.btn_accept)
    AppCompatButton btnAccept;
    @BindView(R.id.txt_action_respone)
    TextView txtActionRespone;
    @BindView(R.id.container_action_buttons)
    LinearLayout containerActionButtons;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    public MessagetextOfficialReceptorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }


    public void bind(final ChatMessage message, ChatMessage previousMessage, final ChatMessageListener listener) {
        Log.d(TAG, "MessageTextReceptorHolder bind");
        messageText.setText(message.getMessageText());
        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));

        showOfficialMessage(
                message.getOfficialMessage(),
                itemView.getResources(),
                txtSubject,
                txtTitle,
                txtAdditional1,
                txtAdditional2,
                txtImportantReference,
                txtOfficialEmisor,
                txtActionRespone,
                btnAccept,
                btnDeny);

        MessageUtils.
                setTimeTitleVisibility(message.getTimestamp(), previousMessage == null ? 0 : previousMessage.getTimestamp(), txtDayGroup, itemView.getResources());


        if (listener != null && message.getMessageStatus() != ChatMessage.STATUS_READED) {
            listener.onMessageReaded(message);
        }

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onOfficialMesageListener(message, view);
                }
            }
        });
        btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onOfficialMesageListener(message, view);
                }
            }
        });
    }


}
