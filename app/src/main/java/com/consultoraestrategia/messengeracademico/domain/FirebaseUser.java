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

import static com.consultoraestrategia.messengeracademico.chat.ChatRepositoryImpl.FROM_USER_PATH;

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
    private static final String TAG = FirebaseUser.class.getSimpleName();

    public FirebaseUser() {
        super();
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

    private ChildEventListener listenerIncomingMessages;

    public void listenToIncomingMessages(Contact contact, ChildEventListener listener) {
        Log.d(TAG, "listenToIncomingMessages");
        this.listenerIncomingMessages = listener;
        String keyUser = contact.getUserKey();
        getDatabase().getReference()
                .child(CHILD_USER)
                .child(keyUser)
                .child(CHILD_INCOMING_MESSAGES)
                .addChildEventListener(listener);
    }

    public void removeMessage(int type, Contact user, ChatMessage message) {
        Log.d(TAG, "removeMessage");
        String userKey = user.getUserKey();
        String keyMessage = message.getKeyMessage();

        Map<String, Object> map = new HashMap<>();
        map.put(keyMessage, null);

        String path = null;
        if (type == FROM_USER_PATH) {
            path = CHILD_USER + "/" + userKey + "/" + CHILD_INCOMING_MESSAGES;
        } else {
            path = CHILD_NOTIFICATIONS;
        }

        getDatabase().getReference()
                .child(path)
                .updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.d(TAG, "removeMessage databaseError: " + databaseError);
                        } else {
                            Log.d(TAG, "removeMessage success");
                        }
                    }
                });
    }

    public void removeListenerToIncomingMessages(Contact contact) {
        Log.d(TAG, "removeListenerToIncomingMessages");
        if (listenerIncomingMessages != null) {
            String keyUser = contact.getUserKey();
            getDatabase().getReference()
                    .child(CHILD_USER)
                    .child(keyUser)
                    .child(CHILD_INCOMING_MESSAGES)
                    .removeEventListener(listenerIncomingMessages);
        }
    }
}
