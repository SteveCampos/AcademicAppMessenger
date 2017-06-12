package com.consultoraestrategia.messengeracademico.data;

import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;

/**
 * Created by @stevecampos on 16/05/2017.
 */

public interface ContactDataSource {
    interface LoadContactsCallback{
        void onContactsLoaded(List<Contact> contacts);
        void onDataNotAvailable();
    }
    interface GetContactCallback{
        void onContactLoaded(Contact contact);
        void onDataNotAvailable();
    }

    void getContacts(LoadContactsCallback callback);
    void getContact(String phoneNumber, GetContactCallback callback);

}
