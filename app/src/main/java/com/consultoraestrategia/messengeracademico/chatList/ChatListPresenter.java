package com.consultoraestrategia.messengeracademico.chatList;

import android.content.SharedPreferences;

import com.consultoraestrategia.messengeracademico.chatList.event.ChatListEvent;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;

/**
 * Created by jairc on 24/03/2017.
 */

public interface ChatListPresenter {

    void onCreateView();

    void onDestroy();

    void onPause();

    void onStop();

    void onResume();

    void getPhoneNumber(SharedPreferences preferences);

    void getContact(String phoneNumber);

    void getChats(Contact contact);

    void onEventMainThread(ChatListEvent event);

    void onPhoneNumberRetreived(String phoneNumber);

    void onContactRetreived(Contact contact);

    void onChatsRetreived(List<Chat> chats);

    void onChatAdded(Chat chat);

    void onChatChanged(Chat chat);

    void onChatDeleted(Chat chat);

    void onChatClicked(Chat chat);
}
