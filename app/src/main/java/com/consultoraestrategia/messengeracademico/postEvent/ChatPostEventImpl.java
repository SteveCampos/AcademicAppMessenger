package com.consultoraestrategia.messengeracademico.postEvent;

import com.consultoraestrategia.messengeracademico.chat.events.ChatEvent;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;

import java.util.List;

/**
 * Created by jairc on 24/04/2017.
 */

public class ChatPostEventImpl implements ChatPostEvent {
    private EventBus eventBus;

    public ChatPostEventImpl() {
        this.eventBus = GreenRobotEventBus.getInstance();
    }

    @Override
    public void post(ChatMessage message) {
        post(ChatEvent.TYPE_MESSAGE, null, message, null, null, null);
    }

    private void post(int type, Contact contact, ChatMessage message, Connection connection, String action, List<ChatMessage> messages) {
        ChatEvent event = new ChatEvent();
        event.setType(type);
        if (contact != null) {
            event.setContact(contact);
        }
        if (message != null) {
            event.setMessage(message);
        }
        if (connection != null) {
            event.setConnection(connection);
        }
        if (action != null) {
            event.setAction(action);
        }
        if (messages != null) {
            event.setMessages(messages);
        }
        eventBus.post(event);
    }

}
