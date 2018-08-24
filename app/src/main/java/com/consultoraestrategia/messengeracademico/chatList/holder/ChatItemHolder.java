package com.consultoraestrategia.messengeracademico.chatList.holder;

import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.base.SelectableViewHolder;
import com.consultoraestrategia.messengeracademico.chatList.listener.ChatListListener;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser;
import com.consultoraestrategia.messengeracademico.utils.TimeUtils;
import com.google.firebase.auth.FirebaseUser;
import com.vanniktech.emoji.EmojiTextView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jairc on 22/03/2017.
 */

public class ChatItemHolder extends SelectableViewHolder<Chat> {


    private static final String TAG = ChatItemHolder.class.getSimpleName();
    @BindView(R.id.img_profile)
    CircleImageView imgProfile;
    @BindView(R.id.txt_name)
    AppCompatTextView txtName;
    @BindView(R.id.txt_time)
    AppCompatTextView txtTime;
    @BindView(R.id.img_status_message)
    AppCompatImageView imgStatusMessage;
    @BindView(R.id.txt_message)
    EmojiTextView txtMessage;
    @BindView(R.id.txt_counter)
    AppCompatTextView txtCounter;
    @BindView(R.id.txt_rol_verificado)
    AppCompatTextView txtRolVerificado;


    @Override
    protected View getBgRegion() {
        return itemView;
    }

    public ChatItemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }


    public void bind(FirebaseUser mainUser, final Chat chat, final ChatListListener listener) {
        Log.d(TAG, "bind");
        super.bind(chat, listener);

        Contact receptor = chat.getReceptor();
        Contact emisor = chat.getEmisor();


        if (emisor != null && receptor != null) {

            emisor.load();
            receptor.load();

            Contact temp = null;

            if (!emisor.getUid().equals(mainUser.getUid())) {
                temp = receptor;
                receptor = emisor;
                emisor = temp;
            }

            setReceptorProfile(receptor, listener);


            ChatMessage lastMessage = chat.getLastMessage();
            setLastMessage(mainUser, lastMessage);

            Log.d(TAG, "chatKey: " + chat.getChatKey());

            long countMessagesNoReaded = chat.countMessagesNoReaded(mainUser.getUid());
            setCountMessages(txtCounter, countMessagesNoReaded);
            /*setNotificationState(imgNotificationState);*/
        }
    }


    private void setReceptorProfile(final Contact contact, final ChatListListener listener) {
        String title = null;
        String uri = null;
        String verified = null;
        if (contact != null) {
            String name = contact.getName();
            String displayName = contact.getDisplayName();
            String phoneNumber = contact.getPhoneNumber();
            String uriProfile = contact.getPhotoUrl();


            title = displayName;
            if (TextUtils.isEmpty(title))
                title = !TextUtils.isEmpty(name) ? name : phoneNumber;


            uri = !TextUtils.isEmpty(uriProfile) ? uriProfile : "";
            verified = contact.getInfoVerified();
            CrmeUser crmeUser = CrmeUser.getCrmeUser(phoneNumber);
            if (crmeUser != null) {
                txtName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_verify,
                        0,
                        0,
                        0
                );

                title = crmeUser.getName();
                String rol = crmeUser.getDisplayName();
                txtRolVerificado.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(rol))
                    txtRolVerificado.setText(rol);
            } else {
                txtRolVerificado.setVisibility(View.GONE);
            }
        }

        if (!TextUtils.isEmpty(title))
            txtName.setText(title);
        if (!TextUtils.isEmpty(verified)) {
            /*txtName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_verify,
                    0
            );
            txtRolVerificado.setVisibility(View.VISIBLE);
            String rolVerificado = String.format(itemView.getResources().getString(R.string.global_rol_verified), verified);
            txtRolVerificado.setText(rolVerificado);*/
        } else {
            //txtRolVerificado.setVisibility(View.GONE);
        }
        Glide
                .with(itemView.getContext())
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_users)
                .into(imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChatProfileClick(contact);
            }
        });
    }

    private void setLastMessage(FirebaseUser mainUser, ChatMessage lastMessage) {
        Resources res = itemView.getResources();
        if (lastMessage != null) {
            Contact messageSender = lastMessage.getEmisor();
            messageSender.load();

            String message = null;
            switch (lastMessage.getMessageType()) {
                default:
                    message = lastMessage.getMessageText();
                    break;
                case ChatMessage.TYPE_IMAGE:
                    message = res.getString(R.string.txt_global_photo);
                    break;
                case ChatMessage.TYPE_AUDIO:
                    message = res.getString(R.string.txt_global_audio);
                    break;
                case ChatMessage.TYPE_VIDEO:
                    message = res.getString(R.string.txt_global_video);
                    break;

            }

            txtMessage.setText(message);
            int statusMessage = lastMessage.getMessageStatus();
            setStatusMessage(mainUser, messageSender, imgStatusMessage, statusMessage);
            long timeStamp = lastMessage.getTimestamp();
            setTime(txtTime, timeStamp, itemView.getResources());
        } else {
            txtMessage.setText(R.string.activity_chat_messages_deleted);
        }

    }

    private void setStatusMessage(FirebaseUser mainUser, Contact messageSender, AppCompatImageView imgStatusMessage, int statusMessage) {
        if (messageSender != null && mainUser != null && messageSender.getUid() != null) {
            if (messageSender.getUid().equals(mainUser.getUid())) {
                showStatusMessage(imgStatusMessage, statusMessage);
            } else {
                hideImageView(imgStatusMessage);
            }
        }
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


    private void showStatusMessage(AppCompatImageView imgStatusMessage, int statusMessage) {
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
        imgStatusMessage.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), id));
    }


}
