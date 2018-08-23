package com.consultoraestrategia.messengeracademico.contactList;

import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;

/**
 * Created by Steve on 7/03/2017.
 */

public interface ContactListView {
    void showContacts(List<Contact> contacts);
    void onContactSelected(Contact contact);

    void reloadContacts();
}
