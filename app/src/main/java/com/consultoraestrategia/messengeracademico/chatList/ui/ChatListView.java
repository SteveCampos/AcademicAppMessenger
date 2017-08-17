package com.consultoraestrategia.messengeracademico.chatList.ui;

import com.consultoraestrategia.messengeracademico.entities.Chat;

import java.util.List;

/**
 * Created by jairc on 22/03/2017.
 */

public interface ChatListView {

    void onChatAdded(Chat chat);

    void onChatChanged(Chat chat);

    void onChatRemoved(Chat chat);

    void onChatsChanged(List<Chat> chats);

    /*
    void setContact(Contact contact);

    void setPhoneNumber(String phoneNumber);*/


}
