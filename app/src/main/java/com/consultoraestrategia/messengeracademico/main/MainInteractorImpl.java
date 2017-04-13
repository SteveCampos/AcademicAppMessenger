package com.consultoraestrategia.messengeracademico.main;

import android.content.SharedPreferences;

import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by jairc on 27/03/2017.
 */

class MainInteractorImpl implements MainInteractor {

    private MainRepository repository;

    public MainInteractorImpl() {
        this.repository = new MainRepositoryImpl();
    }

    @Override
    public void getPhoneNumber(SharedPreferences preferences) {
        repository.getPhoneNumber(preferences);
    }

    @Override
    public void getContact(String phoneNumber) {
        repository.getContact(phoneNumber);
    }

    @Override
    public void listenForIncomingMessages(Contact contact) {
        repository.listenForIncomingMessages(contact);
    }
}
