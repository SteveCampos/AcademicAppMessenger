package com.consultoraestrategia.messengeracademico.chat;

import com.consultoraestrategia.messengeracademico.chat.events.ChatEvent;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;

/**
 * Created by @stevecampos on 9/03/2017.
 */

public interface ChatPresenter {

    void onCreate();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onBackPressed();

    void listenMessages(Contact from, Contact to);

    void listenConnection(Contact from, Contact to);

    void listenChatAction(Contact from, Contact to);

    void sendMessage(Contact from, Contact to, ChatMessage message);

    void afterTextChanged(String message);

    void setMessageStatusReaded(ChatMessage message);

    void onEventMainThread(ChatEvent event);


    void getContactEmisor(String id);

    void getContactReceptor(String id);

    void onEmisorRetrieved(Contact emisor);

    void onReceptorRetrieved(Contact receptor);

    void onMessagedAdded(ChatMessage message);

    void onConnectionChanged(Connection connection);

    void onActionChanged(Contact contact, String action);

    void onIncomingMessage(Contact from, Contact to, ChatMessage message);

    void onMessageListAdded(List<ChatMessage> messages);

}
