package com.consultoraestrategia.messengeracademico.contactList.listeners;

import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by Steve on 8/03/2017.
 */

public interface ContactListener {
    void onContactSelected(Contact contact);
    void onImageClickdListener(Contact contact);
}
