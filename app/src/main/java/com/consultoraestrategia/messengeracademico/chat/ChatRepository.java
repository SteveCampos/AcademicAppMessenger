package com.consultoraestrategia.messengeracademico.chat;

import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by @stevecampos on 9/03/2017.
 */

public interface ChatRepository {
    void listenMessages(Contact from, Contact to);

    void listenConnection(Contact from, Contact to);

    void listenChatAction(Contact from, Contact to);

    void changeAction(Contact from, Contact to, String action);

    void changeConnection(Contact from, Connection connection);

    void sendMessage(boolean online, Contact from, Contact to, ChatMessage message);

    void setMessageStatusReaded(Contact from, Contact to, ChatMessage message, boolean online);

    void setMessageStatusDelivered(Contact from, Contact to, ChatMessage message, boolean online);

    void setMessageStatusSended(Contact from, Contact to, ChatMessage message, boolean online);

    void getContactEmisor(String id);

    void getContactReceptor(String id);

}
