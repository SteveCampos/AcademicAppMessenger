package com.consultoraestrategia.messengeracademico.chatList.listener;

import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by jairc on 22/03/2017.
 */

public interface ChatListener {
    void onChatClickedListener(Chat chat);
    void onImageClickdListener(Contact contact);
}
