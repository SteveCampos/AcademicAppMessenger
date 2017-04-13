package com.consultoraestrategia.messengeracademico.contactList;

import android.support.annotation.NonNull;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.main.event.MainEvent;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.List;

/**
 * Created by Steve on 7/03/2017.
 */
public class ContactListRepositoryImpl implements ContactListRepository {

    private static final String TAG = ContactListRepositoryImpl.class.getSimpleName();

    @Override
    public void getContactsFromPhone() {
        SQLite
                .select()
                .from(Contact.class)
                .where(Contact_Table.userKey.isNotNull())
                .orderBy(Contact_Table.name, true)
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<Contact>() {
                    @Override
                    public void onQueryResult(QueryTransaction<Contact> transaction, @NonNull CursorResult<Contact> tResult) {
                        // called when query returns on UI thread
                        List<Contact> contacts = tResult.toListClose();
                        post(contacts);
                    }
                })
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        Log.d(TAG, "error: " + error.getMessage());
                        //post(error.getMessage());
                    }
                }).execute();
    }

    @Override
    public void onContactSelected(Contact contact) {
        EventBus eventBus = GreenRobotEventBus.getInstance();
        MainEvent event = new MainEvent();
        event.setType(MainEvent.TYPE_LAUNCH_CHAT);
        event.setContact(contact);
        eventBus.post(event);
    }

    private void post(String message) {
        EventBus eventBus = GreenRobotEventBus.getInstance();
        eventBus.post(message);
    }

    private void post(List<Contact> contacts) {
        EventBus eventBus = GreenRobotEventBus.getInstance();
        eventBus.post(contacts);
    }
}
