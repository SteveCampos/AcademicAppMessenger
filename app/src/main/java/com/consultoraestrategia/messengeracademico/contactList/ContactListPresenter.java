package com.consultoraestrategia.messengeracademico.contactList;

import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;

/**
 * Created by Steve on 7/03/2017.
 */

public interface ContactListPresenter {
    void onCreate();
    void onDestroy();

    void getContacts();
    void onContactsRetreived(List<Contact> contacts);
    void onError(String error);

    void onEventMainThread(List<Contact> contacts);

    void onContactSelected(Contact contact);


    /*...*/
}
