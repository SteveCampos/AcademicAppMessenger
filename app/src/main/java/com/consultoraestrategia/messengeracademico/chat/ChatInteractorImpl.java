package com.consultoraestrategia.messengeracademico.chat;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by Steve on 10/03/2017.
 */
public class ChatInteractorImpl implements ChatInteractor {
    private static final String TAG = ChatInteractorImpl.class.getSimpleName();
    private ChatRepository repository;

    public ChatInteractorImpl() {
        this.repository = new ChatRepositoryImpl();
    }

    @Override
    public void listenMessages(Contact from, Contact to) {
        repository.listenMessages(from, to);
    }

    @Override
    public void listenConnection(Contact from, Contact to) {
        repository.listenConnection(from, to);
    }

    @Override
    public void listenChatAction(Contact from, Contact to) {
        repository.listenChatAction(from, to);
    }

    @Override
    public void changeAction(Contact from, Contact to, String action) {
        repository.changeAction(from, to, action);
    }

    @Override
    public void changeConnection(Contact from, Connection connection) {
        repository.changeConnection(from, connection);
    }

    @Override
    public void setMessageStatusReaded(boolean online, Contact from, Contact to, ChatMessage message) {
        Log.d(TAG, "setMessageStatusReaded");
        repository.setMessageStatusReaded(from, to, message, online);
    }

    @Override
    public void sendMessage(boolean online, Contact from, Contact to, ChatMessage message) {
        repository.sendMessage(online, from, to, message);
    }

    @Override
    public void getContactEmisor(String id) {
        repository.getContactEmisor(id);
    }

    @Override
    public void getContactReceptor(String id) {
        repository.getContactReceptor(id);
    }
}
