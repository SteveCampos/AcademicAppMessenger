package com.consultoraestrategia.messengeracademico.chatList.ui;

import com.consultoraestrategia.messengeracademico.chatList.listener.ChatListListener;
import com.consultoraestrategia.messengeracademico.entities.Chat;

import java.util.List;

/**
 * Created by jairc on 22/03/2017.
 */

public interface ChatListView extends ChatListListener {

    void addChat(Chat chat);

    void updateChat(Chat chat);

    void removeChat(Chat chat);

    void updateChatList(List<Chat> chats);

    void updateChatAndUp(Chat chat);
}
