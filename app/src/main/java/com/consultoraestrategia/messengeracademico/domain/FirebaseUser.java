package com.consultoraestrategia.messengeracademico.domain;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by @stevecampos on 16/03/2017.
 */

public class FirebaseUser extends FirebaseHelper {

    public static final String CHILD_NOTIFICATIONS = "NOTIFICATIONS";
    public static final String CHILD_USER = "USER";
    public static final String CHILD_PROFILE = "PROFILE";
    public static final String CHILD_CONNECTION = "CONNECTION";
    private static final String CHILD_DEVICE = "DEVICE";
    public static final String CHILD_INCOMING_MESSAGES = "INCOMING_MESSAGES";
    public static final String CHILD_ALL_MESSAGES = "ALL_MESSAGES";
    private static final String TAG = FirebaseUser.class.getSimpleName();
    private static FirebaseUser instance;

    private static FirebaseUser INSTANCE = null;

    public FirebaseUser() {
        super();
    }


    public static FirebaseUser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FirebaseUser();
        }
        return INSTANCE;
    }


    public void changeConnection(Contact contact, Connection connection) {
        Log.d(TAG, "changeConnection");
        if (contact != null) {
            String keyUser = contact.getUserKey();
            getDatabase().getReference()
                    .child(CHILD_USER)
                    .child(keyUser)
                    .child(CHILD_CONNECTION)
                    .updateChildren(connection.toMap());
        }
    }

    public void onDisconnect(Contact contact, Connection connection) {
        Log.d(TAG, "onDisconnect");
        if (contact != null) {
            String keyUser = contact.getUserKey();
            getDatabase().getReference()
                    .child(CHILD_USER)
                    .child(keyUser)
                    .child(CHILD_CONNECTION)
                    .child("online")
                    .onDisconnect()
                    .setValue(false);
        }
    }

    private ChildEventListener listenerAllMessages;

    public void listenForAllUserMessages(Contact contact, ChildEventListener listener) {
        Log.d(TAG, "listenForAllUserMessages");
        this.listenerAllMessages = listener;
        if (contact != null) {
            String keyUser = contact.getUserKey();
            getDatabase().getReference()
                    .child(CHILD_USER)
                    .child(keyUser)
                    .child(CHILD_ALL_MESSAGES)
                    .limitToLast(100)
                    .addChildEventListener(listener);
        }
    }


    public void removeListenerAllMessages(Contact contact) {
        Log.d(TAG, "removeListenerAllMessages");
        if (listenerAllMessages != null) {
            String keyUser = contact.getUserKey();
            getDatabase().getReference()
                    .child(CHILD_USER)
                    .child(keyUser)
                    .child(CHILD_ALL_MESSAGES)
                    .removeEventListener(listenerAllMessages);
        }
    }
}
