package com.consultoraestrategia.messengeracademico.chatList;

import android.content.SharedPreferences;

import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by jairc on 24/03/2017.
 */

public interface ChatListInteractor {

    void getPhoneNumber(SharedPreferences preferences);
    void getContact(String phoneNumber);
    void getChats(Contact contact);

    void onChatClicked(Chat chat);
}
