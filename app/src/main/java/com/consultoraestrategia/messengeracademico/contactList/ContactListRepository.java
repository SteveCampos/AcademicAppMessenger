package com.consultoraestrategia.messengeracademico.contactList;

import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by Steve on 7/03/2017.
 */

public interface ContactListRepository {
    void getContactsFromPhone();

    void onContactSelected(Contact contact);
}
