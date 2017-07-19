package com.consultoraestrategia.messengeracademico.chatList.holder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chatList.listener.ChatListener;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.utils.TimeUtils;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jairc on 22/03/2017.
 */

public class ChatItemHolder extends RecyclerView.ViewHolder {


    private static final String TAG = ChatItemHolder.class.getSimpleName();
    @BindView(R.id.layout)
    RelativeLayout layout;
    public
    @BindView(R.id.img_profile)
    CircleImageView imgProfile;
    @BindView(R.id.txt_name)
    AppCompatTextView txtName;
    @BindView(R.id.txt_time)
    AppCompatTextView txtTime;
    @BindView(R.id.txt_message)
    EmojiTextView txtMessage;
    @BindView(R.id.txt_counter)
    AppCompatTextView txtCounter;

    @BindView(R.id.img_notification_state)
    AppCompatImageView imgNotificationState;
    @BindView(R.id.img_status_message)
    AppCompatImageView imgStatusMessage;
    private int statusMessage;


    public ChatItemHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    private void setReceptorProfile(final Contact contact, Context context, final ChatListener listener) {
        String title = null;
        String uri = null;
        if (contact != null) {
            String name = contact.getName();
            String phoneNumber = contact.getPhoneNumber();
            String uriProfile = contact.getPhotoUri();

            title = !TextUtils.isEmpty(name) ? name : phoneNumber;
            uri = !TextUtils.isEmpty(uriProfile) ? uriProfile : "";
        }

        txtName.setText(title);
        Glide
                .with(context)
                .load(uri)
                .error(R.drawable.ic_users)
                .into(imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onImageClickdListener(contact);
            }
        });
    }

    private void setLastMessage(Contact receptor, ChatMessage lastMessage, Context context) {
        if (lastMessage != null) {
            Contact messageSender = lastMessage.getEmisor();
            messageSender.load();

            String message = null;
            switch (lastMessage.getMessageType()) {
                case ChatMessage.TYPE_TEXT:
                    message = lastMessage.getMessageText();
                    break;
                case ChatMessage.TYPE_IMAGE:
                    message = context.getString(R.string.txt_global_photo);
                    break;
                case ChatMessage.TYPE_AUDIO:
                    message = context.getString(R.string.txt_global_audio);
                    break;
                case ChatMessage.TYPE_VIDEO:
                    message = context.getString(R.string.txt_global_video);
                    break;

            }
            txtMessage.setText(message);

            int statusMessage = lastMessage.getMessageStatus();
            setStatusMessage(receptor, messageSender, imgStatusMessage, statusMessage, context);
            long timeStamp = lastMessage.getTimestamp();
            setTime(txtTime, timeStamp, context.getResources());

        }

    }

    private void setStatusMessage(Contact me, Contact messageSender, AppCompatImageView imgStatusMessage, int statusMessage, Context context) {
        if (messageSender != null && me != null && messageSender.getPhoneNumber() != null && me.getPhoneNumber() != null) {
            if (messageSender.getPhoneNumber().equals(me.getPhoneNumber())) {
                showStatusMessage(imgStatusMessage, statusMessage, context);
            } else {
                hideImageView(imgStatusMessage);
            }
        }
    }

    public void bind(Contact me, final Chat chat, Context context, final ChatListener listener) {
        Log.d(TAG, "bind");

        Contact receptor = chat.getReceptor();
        Contact emisor = chat.getEmisor();


        if (emisor != null && receptor != null) {

            emisor.load();
            receptor.load();

            Contact temp = null;

            if (!emisor.getUserKey().equals(me.getUserKey())) {
                temp = receptor;
                receptor = emisor;
                emisor = temp;

            }

            setReceptorProfile(receptor, context, listener);


            ChatMessage lastMessage = chat.getLastMessage();
            setLastMessage(me, lastMessage, context);

            Log.d(TAG, "chatKey: " + chat.getChatKey());

            long countMessagesNoReaded = chat.countMessagesNoReaded(me.getUserKey());
            setCountMessages(txtCounter, countMessagesNoReaded);
            /*setNotificationState(imgNotificationState);*/
        }


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChatClickedListener(chat);
            }
        });


    }

    private void setTime(AppCompatTextView txtTime, long timeStamp, Resources resources) {
        txtTime.setText(TimeUtils.lastMessageTime(timeStamp, new Date().getTime(), resources));
    }

    private void setNotificationState(AppCompatImageView imgNotificationState) {
        hideImageView(imgNotificationState);
    }

    private void hideImageView(AppCompatImageView imageView) {
        imageView.setVisibility(View.GONE);
    }

    private void hideTextView(AppCompatTextView textView) {
        textView.setVisibility(View.GONE);
    }


    private void setCountMessages(AppCompatTextView txtCounter, long count) {
        Log.d(TAG, "countMessagesNoReaded: " + count);
        if (count > 0) {
            txtCounter.setVisibility(View.VISIBLE);
            txtCounter.setText(String.valueOf(count));
            return;
        }
        hideTextView(txtCounter);
    }


    private void showStatusMessage(AppCompatImageView imgStatusMessage, int statusMessage, Context context) {
        int id = R.drawable.ic_access_time;

        switch (statusMessage) {
            case ChatMessage.STATUS_WRITED:
                id = R.drawable.ic_access_time;
                break;
            case ChatMessage.STATUS_SEND:
                id = R.drawable.ic_single_check;
                break;
            case ChatMessage.STATUS_DELIVERED:
                id = R.drawable.ic_double_check;
                break;
            case ChatMessage.STATUS_READED:
                id = R.drawable.ic_double_check_colored;
                break;
        }
        imgStatusMessage.setVisibility(View.VISIBLE);
        imgStatusMessage.setImageDrawable(ContextCompat.getDrawable(context, id));
    }


}
