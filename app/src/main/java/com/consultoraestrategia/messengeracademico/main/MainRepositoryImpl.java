package com.consultoraestrategia.messengeracademico.main;

import android.content.SharedPreferences;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.domain.FirebaseUser;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.main.event.MainEvent;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import static com.consultoraestrategia.messengeracademico.main.event.MainEvent.TYPE_CONTACT;
import static com.consultoraestrategia.messengeracademico.main.event.MainEvent.TYPE_PHONENUMBER;

/**
 * Created by @stevecampos on 27/03/2017.
 */

class MainRepositoryImpl implements MainRepository {

    private static final String TAG = MainRepositoryImpl.class.getSimpleName();
    private EventBus eventBus;
    private FirebaseUser firebaseUser;
    private int counter = 0;
    private Contact me;

    public MainRepositoryImpl() {
        this.eventBus = GreenRobotEventBus.getInstance();
        this.firebaseUser = new FirebaseUser();
    }

    @Override
    public void getPhoneNumber(SharedPreferences preferences) {
        String phoneNumber = preferences.getString(VerificationActivity.PREF_PHONENUMBER, null);
        counter++;
        if (phoneNumber != null) {
            post(phoneNumber);
        } else {
            if (counter < 10) {
                getPhoneNumber(preferences);
            } else {
                //Show error Retreiving phoneNumber!
                Log.e(TAG, "Error getting phonenumber from preferences!");
            }
        }
    }

    @Override
    public void getContact(String phoneNumber) {
        Log.d(TAG, "getContact");
        Contact contact = SQLite.select()
                .from(Contact.class)
                .where(Contact_Table.phoneNumber.eq(phoneNumber))
                .querySingle();
        this.me = contact;
        post(contact);
    }

    private void manageSnapshot(DataSnapshot dataSnapshot) {
        Log.d(TAG, "manageSnapshot dataSnapshot: " + dataSnapshot);
    }

    @Override
    public void listenForIncomingMessages(final Contact contact) {
        Log.d(TAG, "listenForIncomingMessages");

        firebaseUser.listenForAllUserMessages(contact, new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                manageSnapshot(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                manageSnapshot(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void post(int type, String phoneNumber, Contact contact) {
        MainEvent event = new MainEvent();
        event.setType(type);
        if (phoneNumber != null) {
            event.setPhoneNumber(phoneNumber);
        }
        if (contact != null) {
            event.setContact(contact);
        }
        eventBus.post(event);
    }

    private void post(String phoneNuber) {
        post(TYPE_PHONENUMBER, phoneNuber, null);
    }

    private void post(Contact contact) {
        post(TYPE_CONTACT, null, contact);
    }


}
