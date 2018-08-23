package com.consultoraestrategia.messengeracademico.chatList.listener;

import com.consultoraestrategia.messengeracademico.base.actionMode.SelectListener;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by jairc on 22/03/2017.
 */

public interface ChatListListener extends SelectListener<Chat> {
    void onChatProfileClick(Contact contact);
}
