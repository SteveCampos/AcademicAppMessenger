package com.consultoraestrategia.messengeracademico.chatList;

import android.content.SharedPreferences;

import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by jairc on 24/03/2017.
 */

class ChatListInteractorImpl implements ChatListInteractor {

    private ChatListRepository repository;

    public ChatListInteractorImpl() {
        this.repository = new ChatListRepositoryImpl();
    }
/*
    @Ov erride
    public void getPhoneNumber(SharedPreferences preferences) {
        repository.getPhoneNumber(preferences);
    }

    @Override
    public void getContact(String phoneNumber) {
        repository.getContact(phoneNumber);
    }*/

    @Override
    public void getChats() {
        repository.getChats();
    }

    @Override
    public void onChatClicked(Chat chat) {
        repository.onChatClicked(chat);
    }
}
