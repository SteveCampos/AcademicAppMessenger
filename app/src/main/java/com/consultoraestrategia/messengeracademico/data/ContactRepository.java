package com.consultoraestrategia.messengeracademico.data;

import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Created by @stevecampos on 16/05/2017.
 */

public class ContactRepository implements ContactDataSource {
    public ContactRepository() {

    }

    @Override
    public void getContacts(LoadContactsCallback callback) {

    }

    @Override
    public void getContact(String phoneNumber, GetContactCallback callback) {
        Contact contact = SQLite.select()
                .from(Contact.class)
                .where(Contact_Table.phoneNumber.eq(phoneNumber))
                .and(Contact_Table.userKey.isNotNull())
                .querySingle();

        if (contact != null) {
            callback.onContactLoaded(contact);
        } else {
            callback.onDataNotAvailable();
        }
    }
}
