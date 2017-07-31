package com.consultoraestrategia.messengeracademico.data;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.domain.FirebaseContactsHelper;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.importData.ExistsPhonenumbersAsyncTask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Created by @stevecampos on 16/05/2017.
 */

public class ContactRepository implements ContactDataSource {

    private static final String TAG = ContactRepository.class.getSimpleName();
    private FirebaseContactsHelper firebaseContactsHelper;

    public ContactRepository() {
        firebaseContactsHelper = new FirebaseContactsHelper();
    }

    @Override
    public void getContacts(LoadContactsCallback callback) {

    }

    @Override
    public void getContact(String phoneNumber, GetContactCallback callback) {
        Contact contact = getContactFromLocal(phoneNumber);

        if (contact != null) {
            callback.onContactLoaded(contact);
        } else {
            getContactFromRemote(phoneNumber, callback);
        }
    }

    private Contact getContactFromLocal(String phoneNumber) {
        return SQLite.select()
                .from(Contact.class)
                .where(Contact_Table.phoneNumber.eq(phoneNumber))
                .and(Contact_Table.userKey.isNotNull())
                .querySingle();
    }

    private void getContactFromRemote(String phoneNuber, final GetContactCallback callback) {
        firebaseContactsHelper.existPhoneNumber(phoneNuber, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "getContactFromRemote dataSnapshot: " + dataSnapshot);
                String userKey = null;
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    userKey = dataSnapshot.getValue().toString();
                    if (userKey != null && !userKey.isEmpty()) {
                        listenUserProfile(userKey, callback);
                    } else {
                        onContactNotAvaliable(callback);
                    }
                } else {
                    onContactNotAvaliable(callback);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "databaseError: " + databaseError);
                onContactNotAvaliable(callback);
            }
        });
    }

    private void listenUserProfile(final String userKey, final GetContactCallback callback) {
        firebaseContactsHelper.listenUserProfile(userKey, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    Contact contactProfile = dataSnapshot.getValue(Contact.class);
                    callback.onContactLoaded(contactProfile);
                } else {
                    onContactNotAvaliable(callback);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "databaseError: " + databaseError.getMessage());
                onContactNotAvaliable(callback);
            }
        });
    }

    private void onContactNotAvaliable(GetContactCallback callback) {
        if (callback != null) {
            callback.onDataNotAvailable();
        }
    }
}
