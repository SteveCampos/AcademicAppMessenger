package com.consultoraestrategia.messengeracademico.chat.ui;

import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;

/**
 * Created by @stevecampos on 9/03/2017.
 */

public interface ChatView {

    void showProgress();

    void hideProgress();

    void setEmisor(Contact emisor);

    void setReceptor(Contact receptor);

    void onMessagAdded(ChatMessage message);

    void onConnectionChanged(Connection connection);

    void onUserAction(Contact contact, String action);

    void sendMessage();

    void onMessageListAdded(List<ChatMessage> messages);
}
