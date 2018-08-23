package com.consultoraestrategia.messengeracademico.domain;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


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

    public void getUser(String uid, final CompletionListener<Contact> listener) {
        Log.d(TAG, "getUser: " + uid);
        getDatabase().getReference()
                .child(CHILD_USERS)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: ");
                        if (dataSnapshot.exists()) {
                            Contact contact = dataSnapshot.getValue(Contact.class);
                            listener.onSuccess(contact);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "getUser onCancelled: ");
                        listener.onFailure(new Exception(databaseError.getMessage()));
                    }
                });
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

    public void listenForAllUserMessages(String lastKey, ChildEventListener listener) {
        Log.d(TAG, "listenForAllUserMessages lastKey: " + lastKey);
        this.listenerAllMessages = listener;
        getDatabase().getReference()
                .child(CHILD_USERS_MESSAGES)
                .child(mainUser.getUid())
                .orderByKey()
                .startAt(lastKey)
                //.limitToLast(100)
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

    public void listenLastMessage(ChildEventListener listener) {
        Log.d(TAG, "listenLastMessage");
        getDatabase().getReference()
                .child(CHILD_USERS_MESSAGES)
                .child(mainUser.getUid())
                .limitToLast(1)
                .addChildEventListener(listener);
    }

    public void listenSingleMessage(ChatMessage message, ValueEventListener listener) {
        Log.d(TAG, "listenLastMessage");
        getDatabase().getReference()
                .child(CHILD_USERS_MESSAGES)
                .child(mainUser.getUid())
                .child(message.getKeyMessage())
                .addListenerForSingleValueEvent(listener);
    }

    public void removeListenerSingleMessage(ChatMessage message, ValueEventListener listener) {
        Log.d(TAG, "listenLastMessage");
        getDatabase().getReference()
                .child(CHILD_USERS_MESSAGES)
                .child(mainUser.getUid())
                .child(message.getKeyMessage())
                .removeEventListener(listener);
    }

    public interface UpdateListener {
        void onSuccess();

        void onFailure(Exception e);
    }

    public void updateUser(User user, final UpdateListener listener) {
        if (user == null || TextUtils.isEmpty(user.getUid())) {
            listener.onFailure(new Exception("user null or uid empty!!!"));
            return;
        }
        Map<String, Object> map = new HashMap<>();
        String uid = user.getUid();
        String displayName = user.getDisplayName();
        String email = user.getEmail();
        String phoneNumber = user.getPhoneNumber();
        Uri photoUri = user.getPhotoUri();

        map.put("/users/" + uid + "/uid", uid);

        if (!TextUtils.isEmpty(displayName)) {
            map.put("/users/" + uid + "/displayName", displayName);
        }
        if (!TextUtils.isEmpty(email)) {
            map.put("/users/" + uid + "/email", email);
        }
        if (photoUri != null && !Uri.EMPTY.equals(photoUri)) {
            map.put("/users/" + uid + "/photoUrl", photoUri.toString());
        }

        if (!TextUtils.isEmpty(phoneNumber)) {
            map.put("/users/" + uid + "/phoneNumber", phoneNumber);
            map.put("/phoneNumbers/" + phoneNumber, uid);
        }

        getDatabase().getReference().updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Log.d(TAG, "onComplete: ");
                if (databaseError != null) {
                    listener.onFailure(new Exception(databaseError.getMessage()));
                    return;
                }
                listener.onSuccess();
            }
        });
    }
}
