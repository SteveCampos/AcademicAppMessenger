package com.consultoraestrategia.messengeracademico.chatList;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.chatList.event.ChatListEvent;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Chat_Table;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.main.event.MainEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jairc on 24/03/2017.
 */

public class ChatListRepositoryImpl implements ChatListRepository {

    private static final String TAG = ChatListRepositoryImpl.class.getSimpleName();
    private EventBus eventBus;
    private FirebaseUser mainUser;

    public ChatListRepositoryImpl() {
        this.eventBus = GreenRobotEventBus.getInstance();
        this.mainUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void getChats() {
        Log.d(TAG, "getChats");
        List<Chat> chats = SQLite.select()
                .from(Chat.class)
                .where(Chat_Table.timestamp.isNotNull())
                .queryList();
        post(chats);
    }

    @Override
    public void onChatClicked(Chat chat) {
        Log.d(TAG, "onChatClicked");
        Contact emisor = chat.getEmisor();
        Contact receptor = chat.getReceptor();
        if (mainUser == null || emisor == null || receptor == null) {
            return;
        }

        if (mainUser.getUid().equals(emisor.getUid())) {
            postToMain(chat.getReceptor());
        } else if (mainUser.getUid().equals(receptor.getUid())) {
            postToMain(chat.getEmisor());
        }
    }

    private void postToMain(Contact contact) {
        Log.d(TAG, "postToMain");
        EventBus eventBus = GreenRobotEventBus.getInstance();
        MainEvent event = new MainEvent();
        event.setType(MainEvent.TYPE_LAUNCH_CHAT);
        event.setContact(contact);
        eventBus.post(event);
    }

    private static void post(EventBus eventBus, int type, Contact contact, List<Chat> chats, String phoneNumber, Chat chat) {
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

    private void post(Contact contact) {
        post(eventBus, ChatListEvent.TYPE_CONTACT, contact, null, null, null);
    }

    private void post(List<Chat> chats) {
        post(eventBus, ChatListEvent.TYPE_CHATLIST, null, chats, null, null);
    }

    private void post(String phoneNumber) {
        post(eventBus, ChatListEvent.TYPE_PHONENUMBER, null, null, phoneNumber, null);
    }

    public static void post(Chat chat) {
        post(GreenRobotEventBus.getInstance(), ChatListEvent.TYPE_CHAT, null, null, null, chat);
    }


}
