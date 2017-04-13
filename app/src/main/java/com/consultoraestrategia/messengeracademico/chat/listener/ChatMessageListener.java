package com.consultoraestrategia.messengeracademico.chat.listener;

import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

/**
 * Created by Steve on 9/03/2017.
 */
public interface ChatMessageListener {
    void onMessageReaded(ChatMessage message);
}
