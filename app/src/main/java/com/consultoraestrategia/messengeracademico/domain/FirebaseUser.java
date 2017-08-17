package com.consultoraestrategia.messengeracademico.domain;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;


/**
 * Created by @stevecampos on 16/03/2017.
 */

public class FirebaseUser extends FirebaseHelper {

    public static final String CHILD_NOTIFICATIONS = "notifications";
    public static final String CHILD_USERS = "users";
    public static final String CHILD_USERS_MESSAGES = "users-messages";
    public static final String CHILD_PROFILE = "profile";
    public static final String CHILD_LASTCONNECTION = "lastConnection";
    private static final String CHILD_DEVICES = "devices";
    public static final String CHILD_MESSAGES = "messages";
    public static final String CHILD_USER_MESSAGES = "users-messages";

    private static final String TAG = FirebaseUser.class.getSimpleName();
    private static FirebaseUser instance;

    private static FirebaseUser INSTANCE = null;
    private com.google.firebase.auth.FirebaseUser mainUser;

    public FirebaseUser() {
        super();
        mainUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    public static FirebaseUser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FirebaseUser();
        }
        return INSTANCE;
    }


    public void changeConnection(Connection connection) {
        Log.d(TAG, "changeConnection");
        getDatabase().getReference()
                .child(CHILD_USERS)
                .child(mainUser.getUid())
                .child(CHILD_LASTCONNECTION)
                .updateChildren(connection.toMap());
    }

    public void onDisconnect() {
        Log.d(TAG, "onDisconnect");
        getDatabase().getReference()
                .child(CHILD_USERS)
                .child(mainUser.getUid())
                .child(CHILD_LASTCONNECTION)
                .child("online")
                .onDisconnect()
                .setValue(false);
    }

    private ChildEventListener listenerAllMessages;

    public void listenForAllUserMessages(com.google.firebase.auth.FirebaseUser mainUser, ChildEventListener listener) {
        Log.d(TAG, "listenForAllUserMessages");
        this.listenerAllMessages = listener;
        getDatabase().getReference()
                .child(CHILD_USERS_MESSAGES)
                .child(mainUser.getUid())
                .limitToLast(100)
                .addChildEventListener(listener);
    }


    public void removeListenerAllMessages() {
        Log.d(TAG, "removeListenerAllMessages");
        if (listenerAllMessages != null) {
            getDatabase().getReference()
                    .child(CHILD_USERS_MESSAGES)
                    .child(mainUser.getUid())
                    .removeEventListener(listenerAllMessages);
        }
    }
}
