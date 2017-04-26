package com.consultoraestrategia.messengeracademico.messageRepository;

import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by jairc on 24/04/2017.
 */

public interface MessageRepository {

    void manageIncomingMessage(ChatMessage message, Contact me, int source);
    void saveMessage(ChatMessage message, int from);
    void removeIncomingMessage(int source, ChatMessage message);

}
