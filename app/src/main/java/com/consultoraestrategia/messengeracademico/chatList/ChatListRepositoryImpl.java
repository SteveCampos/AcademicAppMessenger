package com.consultoraestrategia.messengeracademico.chatList;

import android.content.SharedPreferences;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.chatList.event.ChatListEvent;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.main.event.MainEvent;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Created by jairc on 24/03/2017.
 */

public class ChatListRepositoryImpl implements ChatListRepository {

    private static final String TAG = ChatListRepositoryImpl.class.getSimpleName();
    private EventBus eventBus;
    private Contact contact;

    public ChatListRepositoryImpl() {
        this.eventBus = GreenRobotEventBus.getInstance();
    }

    private int counter = 0;

    @Override
    public void getPhoneNumber(SharedPreferences preferences) {
        Log.d(TAG, "getPhoneNumber");
        String phoneNumber = preferences.getString(VerificationActivity.PREF_PHONENUMBER, null);
        counter++;
        if (phoneNumber != null) {
            post(phoneNumber);
        } else {
            if (counter < 10) {
                getPhoneNumber(preferences);
            } else {
                //Show error Retreiving phoneNumber!
                Log.d(TAG, "Error getting phonenumber from preferences!");
            }
        }

    }

    @Override
    public void getContact(String phoneNumber) {
        Log.d(TAG, "getContact");
        Contact contact = SQLite.select()
                .from(Contact.class)
                .where(Contact_Table.phoneNumber.eq(phoneNumber))
                .and(Contact_Table.userKey.isNotNull())
                .querySingle();
        this.contact = contact;
        post(contact);
    }

    @Override
    public void getChats(Contact contact) {
        Log.d(TAG, "getChats");
        if (contact != null) {
            List<Chat> chats = SQLite.select()
                    .from(Chat.class)
                    .queryList();
            post(chats);
        }
    }

    @Override
    public void onChatClicked(Chat chat) {
        String userKey1 = chat.getEmisor().getUserKey();
        //String userKey2 = chat.getReceptor().getUserKey();
        if (contact.getUserKey().equals(userKey1)) {
            postToMain(chat.getReceptor());
        } else {
            postToMain(chat.getEmisor());
        }

    }

    private void postToMain(Contact contact) {
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
