package com.consultoraestrategia.messengeracademico.contactList;

import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by Steve on 7/03/2017.
 */
public class ContactListInteractorImpl implements ContactListInteractor {

    private ContactListRepository repository;

    public ContactListInteractorImpl() {
        this.repository = new ContactListRepositoryImpl();
    }

    @Override
    public void getContactsFromPhone() {
        repository.getContactsFromPhone();
    }

    @Override
    public void onContactSelected(Contact contact) {
        repository.onContactSelected(contact);
    }
}
