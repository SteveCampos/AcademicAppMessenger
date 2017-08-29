package com.consultoraestrategia.messengeracademico.chat.listener;

import android.view.View;

import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

/**
 * Created by @stevecampos on 9/03/2017.
 */
public interface ChatMessageListener {
    void onMessageReaded(ChatMessage message);

    void onImageClick(ChatMessage message, View view);

    void onOfficialMesageListener(ChatMessage message, View view);

    void onMessageNotReaded(ChatMessage message);

    void onNewMessageAddedToTheBottom();
}
