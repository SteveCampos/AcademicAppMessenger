package com.consultoraestrategia.messengeracademico.storage;

import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by @stevecampos on 19/04/2017.
 */

public interface SharedPreferencesHelper {

    void savePhoneNumber(String phoneNumber);

    String getDefaultPhoneNumber();

    void saveContact(Contact contact);

    Contact getContact();
}
