package com.consultoraestrategia.messengeracademico.contactList;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by Steve on 7/03/2017.
 */

public class ContactListPresenterImpl implements ContactListPresenter {

    private static final String TAG = ContactListPresenterImpl.class.getSimpleName();
    private ContactListView view;
    private ContactListInteractor interactor;
    private EventBus eventBus;

    public ContactListPresenterImpl(ContactListView view) {
        this.view = view;
        this.interactor = new ContactListInteractorImpl();
        this.eventBus = GreenRobotEventBus.getInstance();
    }

    @Override
    public void onCreate() {
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        view = null;
        eventBus.unregister(this);
    }

    @Override
    public void getContacts() {
        interactor.getContactsFromPhone();
    }

    @Override
    public void onContactsRetreived(List<Contact> contacts) {
        if (view != null){
            view.showContacts(contacts);
        }
    }

    @Override
    public void onError(String error) {
        if (view != null){
            Log.d(TAG, "showMessage");
        }
    }

    @Subscribe
    @Override
    public void onEventMainThread(List<Contact> contacts) {
        onContactsRetreived(contacts);
    }

    @Override
    public void onContactSelected(Contact contact) {
        interactor.onContactSelected(contact);
    }
}
