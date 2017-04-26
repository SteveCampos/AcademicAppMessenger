package com.consultoraestrategia.messengeracademico.postEvent;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.chatList.event.ChatListEvent;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;

import java.util.List;

/**
 * Created by jairc on 24/04/2017.
 */

public class ChatListPostEventImpl implements ChatListPostEvent {

    private static final String TAG = ChatListPostEventImpl.class.getSimpleName();
    private EventBus eventBus;

    public ChatListPostEventImpl() {
        this.eventBus = GreenRobotEventBus.getInstance();
    }

    @Override
    public void post(Chat chat) {
        post(ChatListEvent.TYPE_CHAT, null, null, null, chat);
    }

    private void post(int type, Contact contact, List<Chat> chats, String phoneNumber, Chat chat) {
        Log.d(TAG, "post");
        ChatListEvent event = new ChatListEvent();
        event.setType(type);
        if (contact != null) {
            event.setContact(contact);
        }
        if (chats != null && !chats.isEmpty()) {
            event.setChats(chats);
        }
        if (phoneNumber != null) {
            event.setPhoneNumber(phoneNumber);
        }
        if (chat != null) {
            event.setChat(chat);
        }

        eventBus.post(event);
    }
}
