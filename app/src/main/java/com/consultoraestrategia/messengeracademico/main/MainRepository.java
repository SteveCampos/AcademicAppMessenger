package com.consultoraestrategia.messengeracademico.main;

import android.content.SharedPreferences;

import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by jairc on 27/03/2017.
 */

public interface MainRepository {

    void getPhoneNumber(SharedPreferences preferences);

    void getContact(String phoneNumber);

    void listenForIncomingMessages(Contact contact);
}
