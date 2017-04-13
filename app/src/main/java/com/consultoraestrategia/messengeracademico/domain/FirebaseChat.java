package com.consultoraestrategia.messengeracademico.domain;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.utils.StringUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.consultoraestrategia.messengeracademico.domain.FirebaseUser.CHILD_CONNECTION;
import static com.consultoraestrategia.messengeracademico.domain.FirebaseUser.CHILD_USER;

/**
 * Created by Steve on 10/03/2017.
 */
public class FirebaseChat extends FirebaseHelper {

    private static final String TAG = FirebaseChat.class.getSimpleName();

    private static final String CHILD_CHATS = "CHATS";
    //private static final String CHILD_GROUPS = "GROUP_CHAT";
    private static final String CHILD_ONE_TO_ONE = "ONE_TO_ONE";


    private static final String PATH_CHATS = "/" + CHILD_CHATS + "/" + CHILD_ONE_TO_ONE + "/";
    //private static final String PATH_GROUPS =  "/" + CHILD_CHATS + "/" + CHILD_GROUPS + "/" ;
    private static final String CHILD_MESSAGES = "MESSAGES";
    private static final String CHILD_ACTIONS = "ACTIONS";

    private DatabaseReference chatsRef;

    public FirebaseChat() {
        super();
        chatsRef = getDatabase().getReference(PATH_CHATS);
    }

    public void listenMessages(Chat chat, ChildEventListener listener) {
        Contact from = chat.getEmisor();
        Contact to = chat.getReceptor();
        long lastMessageTimestamp = chat.getStateTimestamp();

        String[] sort = StringUtils.sortAlphabetical(from.getUserKey(), to.getUserKey());
        String key1 = sort[0];
        String key2 = sort[1];
        chatsRef.child(key1 + "_" + key2).child(CHILD_MESSAGES).orderByChild("timestamp").startAt(lastMessageTimestamp).addChildEventListener(listener);
    }


    public void listenConnection(Contact to, ValueEventListener listener) {
        if (to != null && to.getUserKey() != null) {
            getDatabase().getReference()
                    .child(CHILD_USER)
                    .child(to.getUserKey())
                    .child(CHILD_CONNECTION)
                    .addValueEventListener(listener);
        }
    }

    public void listenChatAction(Contact from, Contact to, ValueEventListener listener) {
        String[] sort = StringUtils.sortAlphabetical(from.getUserKey(), to.getUserKey());
        String key1 = sort[0];
        String key2 = sort[1];

        getDatabase().getReference()
                .child(CHILD_CHATS)
                .child(CHILD_ONE_TO_ONE)
                .child(key1 + "_" + key2)
                .child(CHILD_ACTIONS)
                .addValueEventListener(listener);
    }

    public String getKeyMessage(Contact from, Contact to) {

        String emisorKey = from.getUserKey();
        String receptorKey = to.getUserKey();
        String[] sortKeys = StringUtils.sortAlphabetical(emisorKey, receptorKey);
        String keyChat = sortKeys[0] + "_" + sortKeys[1];

        return chatsRef.child(keyChat).child(CHILD_MESSAGES).push().getKey();
    }

    public void sendMessage(boolean online, Contact from, Contact to, ChatMessage message, DatabaseReference.CompletionListener listener) {
        message.setMessageStatus(ChatMessage.STATUS_SEND);
        changeStatus(online, ChatMessage.STATUS_SEND, message, listener);
        /*
        String emisorKey = from.getUserKey();
        String receptorKey = to.getUserKey();
        String[] sortKeys = StringUtils.sortAlphabetical(emisorKey, receptorKey);
        String keyChat = sortKeys[0] + "_" + sortKeys[1];

        String keyMessage = message.getKeyMessage();

        Map<String, Object> map = new HashMap<>();
        map.put(PATH_CHATS + keyChat + "/" + CHILD_MESSAGES + "/" + keyMessage, message.toMap());
        map.put(FirebaseUser.CHILD_USER + "/" + receptorKey + "/" + FirebaseUser.CHILD_INCOMING_MESSAGES + "/" + keyMessage, message.toMap());
        getDatabase().getReference().updateChildren(map, listener);*/
    }

    private void changeStatus(boolean online, int messageStatus, ChatMessage message, DatabaseReference.CompletionListener listener) {
        Contact from = message.getEmisor();
        Contact to = message.getReceptor();

        String emisorKey = from.getUserKey();
        String receptorKey = to.getUserKey();
        String[] sortKeys = StringUtils.sortAlphabetical(emisorKey, receptorKey);
        String keyChat = sortKeys[0] + "_" + sortKeys[1];


        String keyMessage = message.getKeyMessage();
        message.setMessageStatus(messageStatus);

        Map<String, Object> map = new HashMap<>();
        map.put(PATH_CHATS + keyChat + "/" + CHILD_MESSAGES + "/" + keyMessage, message.toMap());


        String userKey = null;

        int status = message.getMessageStatus();
        switch (status) {
            case ChatMessage.STATUS_SEND:
                userKey = receptorKey;
                break;
            case ChatMessage.STATUS_DELIVERED:
                userKey = emisorKey;
                break;
            case ChatMessage.STATUS_READED:
                userKey = emisorKey;
                break;
            default:
                userKey = receptorKey;
                break;
        }

        if (online) {
            map.put(FirebaseUser.CHILD_USER + "/" + userKey + "/" + FirebaseUser.CHILD_INCOMING_MESSAGES + "/" + keyMessage, message.toMap());
        } else {
            map.put(FirebaseUser.CHILD_NOTIFICATIONS + "/" + keyMessage, message.toMap());
        }


        getDatabase().getReference().updateChildren(map, listener);
    }

    public void setStatusReaded(boolean online, ChatMessage message, DatabaseReference.CompletionListener listener) {
        changeStatus(online, ChatMessage.STATUS_READED, message, listener);
    }

    public void setStatusDelivered(boolean online, ChatMessage message, DatabaseReference.CompletionListener listener) {
        changeStatus(online, ChatMessage.STATUS_DELIVERED, message, listener);
    }

    public void setStatusSend(boolean online, ChatMessage message, DatabaseReference.CompletionListener listener) {
        changeStatus(online, ChatMessage.STATUS_SEND, message, listener);
    }

    public void changeConnection(Contact from, Connection connection) {
        FirebaseUser firebaseUser = new FirebaseUser();
        firebaseUser.changeConnection(from, connection);
    }

    public void changeAction(Contact from, Contact to, String action) {
        String sort[] = StringUtils.sortAlphabetical(from.getUserKey(), to.getUserKey());
        String key1 = sort[0];
        String key2 = sort[1];

        Map<String, Object> map = new HashMap<>();
        map.put(from.getUserKey(), action);

        getDatabase().getReference()
                .child(CHILD_CHATS)
                .child(CHILD_ONE_TO_ONE)
                .child(key1 + "_" + key2)
                .child(CHILD_ACTIONS)
                .updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Log.d(TAG, "changeAction databaseReference: " + databaseReference);
                        Log.d(TAG, "changeAction databaseError: " + databaseError);
                    }
                });
    }
}
