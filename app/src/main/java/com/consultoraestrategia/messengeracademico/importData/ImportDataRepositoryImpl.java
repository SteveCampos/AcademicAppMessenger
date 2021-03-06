package com.consultoraestrategia.messengeracademico.importData;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.consultoraestrategia.messengeracademico.domain.FirebaseContactsHelper;
import com.consultoraestrategia.messengeracademico.domain.FirebaseHelper;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.importData.events.ImportDataEvent;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.List;

/**
 * Created by Steve on 24/02/2017.
 */
public class ImportDataRepositoryImpl implements ImportDataRepository, FetchContacts.ContactListener, ExistsPhonenumbersAsyncTask.ContactListener {

    private static final String TAG = ImportDataRepositoryImpl.class.getSimpleName();
    private ContentResolver resolver;
    private Context context;
    private int countContacts = 0;
    private int countContactsSaved = 0;

    public ImportDataRepositoryImpl(Context context) {
        this.context = context;
        this.resolver = context.getContentResolver();
    }

    @Override
    public void getData() {
        Log.d(TAG, "ImportDataRepositoryImpl getData");
        new FetchContacts(this).execute(resolver);
    }

    private void post(int eventType) {
        ImportDataEvent event = new ImportDataEvent();
        event.setType(eventType);
        EventBus eventBus = GreenRobotEventBus.getInstance();
        eventBus.post(event);
    }

    private void saveContact(final Contact c) {
        DatabaseDefinition database = FlowManager.getDatabase(MessengerAcademicoDatabase.class);
        Transaction transaction = database.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                c.setType(Contact.TYPE_ADDED_AND_VISIBLE);
                c.save();
            }
        }).success(new Transaction.Success() {
            @Override
            public void onSuccess(Transaction transaction) {
                Log.d(TAG, "onSuccess");
                countContactsSaved++;
                //NOT EXIST -> NEW CONTACT!
                if (c.getUid() != null) {
                    post(ImportDataEvent.OnContactImported);
                }

                if (countContactsSaved >= countContacts) {
                    onFinish();
                }

                /*
                //EXIST -> UPDATED
                if (exist && c.getUid() != null) {
                    post(ImportDataEvent.OnContactImported);
                    Log.d(TAG, "ALREADY EXIST!");
                }*/
            }
        }).error(new Transaction.Error() {
            @Override
            public void onError(Transaction transaction, Throwable error) {
                Log.d(TAG, "error: " + error.getMessage());
                post(ImportDataEvent.OnContactFailedToImport);
            }
        }).build();
        transaction.execute(); // execute

    }


    @Override
    public void onContactsAdded(final List<Contact> contacts) {
        Log.d(TAG, "onContactsAdded");
        if (contacts != null && !contacts.isEmpty()) {
            FirebaseContactsHelper fireContacts = FirebaseContactsHelper.getInstance();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser == null) {
                post(ImportDataEvent.OnContactFailedToImport);
                return;
            }
            fireContacts.saveContacsFromUser(
                    firebaseUser.getUid()
                    ,
                    contacts,
                    new FirebaseHelper.Listener() {
                        @Override
                        public void onSuccess() {
                            addContactsIfExists(contacts);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.d(TAG, "saveContactsFromUser onFailure: " + e);
                            post(ImportDataEvent.OnContactFailedToImport);
                        }
                    }
            );

        } else {
            onImportFailed();
        }
    }

    private void addContactsIfExists(List<Contact> contacts) {
        onFinish();
        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        new ExistsPhonenumbersAsyncTask(phoneNumber, context, this).execute(contacts);
    }

    @Override
    public void onError(String message) {
        post(ImportDataEvent.OnImportError);
    }


    @Override
    public void onContactInstalled(Contact contact) {
        Log.d(TAG, "onContactInstalled");
        countContacts++;
        saveContact(contact);
    }

    @Override
    public void onFinish() {
        Log.d(TAG, "onFinish");
        post(ImportDataEvent.OnImportFinish);
    }

    public void onImportFailed() {
        post(ImportDataEvent.OnImportError);
    }
}
