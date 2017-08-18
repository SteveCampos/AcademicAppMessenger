package com.consultoraestrategia.messengeracademico.domain;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.data.ContactDataSource;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.OfficialMessage;
import com.consultoraestrategia.messengeracademico.utils.StringUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.consultoraestrategia.messengeracademico.domain.FirebaseUser.CHILD_LASTCONNECTION;
import static com.consultoraestrategia.messengeracademico.domain.FirebaseUser.CHILD_USERS;

/**
 * Created by Steve on 10/03/2017.
 */
public class FirebaseChat extends FirebaseHelper {

    private static final String TAG = FirebaseChat.class.getSimpleName();

    private static final String CHILD_CHATS = "chats";
    private static final String CHILD_GROUPS = "GROUP_CHAT";
    private static final String CHILD_ONE_TO_ONE = "ONE_TO_ONE";


    //private static final String PATH_GROUPS =  "/" + CHILD_CHATS + "/" + CHILD_GROUPS + "/" ;
    private static final String CHILD_MESSAGES = "messages";
    private static final String CHILD_ACTIONS = "actions";

    private static final String CHILD_CHAT_MESSAGES = "chats-messages";
    private static final String CHILD_CHAT_ACTIONS = "chats-actions";

    private static final String PATH_CHAT_MESSAGES = "/" + CHILD_CHAT_MESSAGES + "/";

    private static FirebaseChat INSTANCE = null;
    private com.google.firebase.auth.FirebaseUser mainUser;

    public static FirebaseChat getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FirebaseChat();
        }
        return INSTANCE;
    }

    private FirebaseChat() {
        super();
        this.mainUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    private ChildEventListener messagesListener;

    public void removeMessagesListener() {
        Log.d(TAG, "removeMessagesListener");
        if (messagesListener != null) {
            getDatabase().getReference(CHILD_CHAT_MESSAGES).child("{uidChat}").removeEventListener(messagesListener);
        }
    }


    private ValueEventListener receptorConnectionListener;

    public void listenConnection(Contact to, ValueEventListener listener) {
        this.receptorConnectionListener = listener;
        if (to != null && to.getUid() != null) {
            getDatabase().getReference()
                    .child(CHILD_USERS)
                    .child(to.getUid())
                    .child(CHILD_LASTCONNECTION)
                    .addValueEventListener(listener);
        }
    }

    public void removeReceptorConnectionListener() {

    }


    public void listenReceptorAction(Chat chat, ValueEventListener listener) {
        Contact receptor = chat.getReceptor();
        if (mainUser.getUid().equals(receptor.getUid())) {
            receptor = chat.getEmisor();
        }
        listenChatAction(chat.getChatKey(), receptor.getUid(), listener);
    }

    public void listenChatAction(String keyChat, String receptorUid, ValueEventListener valueEventListener) {
        getDatabase().getReference()
                .child(CHILD_CHAT_ACTIONS)
                .child(keyChat)
                .child(receptorUid)
                .addValueEventListener(valueEventListener);
        /*getDatabase().getReference()
                .child(CHILD_CHATS)
                .child(CHILD_ONE_TO_ONE)
                .child(keyChat)
                .child(CHILD_ACTIONS)
                .child(receptorUid)
                .addValueEventListener(valueEventListener);*/
    }


    public String getKeyMessage(Contact from, Contact to) {
        String emisorUid = from.getUid();
        String receptorUid = to.getUid();
        String[] sortUids = StringUtils.sortAlphabetical(emisorUid, receptorUid);
        String uidChat = sortUids[0] + "_" + sortUids[1];
        return getDatabase().getReference(CHILD_CHAT_MESSAGES).child(uidChat).push().getKey();
    }

    public void saveMessage(boolean online, ChatMessage message, DatabaseReference.CompletionListener listener) {
        changeStatus(online, message.getMessageStatus(), message, listener);
    }

    public void sendMessage(boolean online, ChatMessage message, DatabaseReference.CompletionListener listener) {
        changeStatus(online, ChatMessage.STATUS_WRITED, message, listener);
    }


    private void changeStatus(boolean online, int messageStatus, ChatMessage message, DatabaseReference.CompletionListener listener) {
        Contact from = message.getEmisor();
        Contact to = message.getReceptor();

        String emisorUid = from.getUid();
        String receptorUid = to.getUid();


        String[] sortUids = StringUtils.sortAlphabetical(emisorUid, receptorUid);
        String uidChat = sortUids[0] + "_" + sortUids[1];
        message.setChatKey(uidChat);

        String keyMessage = message.getKeyMessage();
        message.setMessageStatus(messageStatus);

        if (message.getOfficialMessage() != null) {
            OfficialMessage officialMessage = message.getOfficialMessage();
            officialMessage.setId(message.getKeyMessage());
            message.setOfficialMessage(officialMessage);
        }


        Map<String, Object> map;
        if (message.getMessageStatus() == ChatMessage.STATUS_WRITED) {
            //Write all Message
            map = buildMessageMap(message, online);
        } else {
            //Only update timestamps!
            map = buildTimestampMap(message);
        }
        /*
        map.put("/chats-messages/" + uidChat + "/" + keyMessage, message.toMap());
        map.put("/users-messages/" + from.getUid() + "/" + keyMessage, message.toMap());
        map.put("/users-messages/" + to.getUid() + "/" + keyMessage, message.toMap());
        if (!online) {
            map.put("/notifications/" + keyMessage, message.toMap());
        }


        if (message.getMessageType().equals(ChatMessage.TYPE_TEXT_OFFICIAL)) {
            map.put("/official-messages/" + message.getKeyMessage(), message.toMap());
        }*/

        getDatabase().getReference().updateChildren(map, listener);
    }

    private Map<String, Object> buildMessageMap(ChatMessage message, boolean online) {
        String uidChat = message.getChatKey();
        String keyMessage = message.getKeyMessage();
        Map<String, Object> map = new HashMap<>();
        map.put("/chats-messages/" + uidChat + "/" + keyMessage, message.toMap());
        map.put("/users-messages/" + message.getEmisor().getUid() + "/" + keyMessage, message.toMap());
        map.put("/users-messages/" + message.getReceptor().getUid() + "/" + keyMessage, message.toMap());
        if (!online) {
            map.put("/notifications/" + keyMessage, message.toMap());
        }


        if (message.getMessageType().equals(ChatMessage.TYPE_TEXT_OFFICIAL)) {
            map.put("/official-messages/" + keyMessage, message.toMap());
        }
        return map;
    }


    private Map<String, Object> buildTimestampMap(ChatMessage message) {
        Map<String, Object> map = new HashMap<>();
        String uidChat = message.getChatKey();
        String keyMessage = message.getKeyMessage();
        long timestamp = new Date().getTime();
        String statusName = "sendTimestamp";
        int messageStatus = message.getMessageStatus();
        if (messageStatus == ChatMessage.STATUS_DELIVERED) {
            statusName = "deliverTimestamp";
        }
        if (messageStatus == ChatMessage.STATUS_READED) {
            statusName = "readTimestamp";
        }

        map.put("/chats-messages/" + uidChat + "/" + keyMessage + "/" + statusName, timestamp);
        map.put("/chats-messages/" + uidChat + "/" + keyMessage + "/messageStatus", messageStatus);
        map.put("/users-messages/" + message.getEmisor().getUid() + "/" + keyMessage + "/" + statusName, timestamp);
        map.put("/users-messages/" + message.getEmisor().getUid() + "/" + keyMessage + "/messageStatus", messageStatus);
        map.put("/users-messages/" + message.getReceptor().getUid() + "/" + keyMessage + "/" + statusName, timestamp);
        map.put("/users-messages/" + message.getReceptor().getUid() + "/" + keyMessage + "/messageStatus", messageStatus);

        if (message.getMessageType().equals(ChatMessage.TYPE_TEXT_OFFICIAL)) {
            map.put("/official-messages/" + keyMessage + "/" + statusName, timestamp);
            map.put("/official-messages/" + keyMessage + "/messageStatus", messageStatus);
        }
        return map;
    }

    private void sendMessageWithPhoneNumbers(String phoneNumberFrom, final String phoneNumberTo, final String messageText) {
        getContactFromPhoneNumber(phoneNumberFrom, new ContactDataSource.GetContactCallback() {
            @Override
            public void onContactLoaded(final Contact from) {

                getContactFromPhoneNumber(phoneNumberTo, new ContactDataSource.GetContactCallback() {
                    @Override
                    public void onContactLoaded(Contact to) {
                        sendMessage(from, to, messageText);
                    }

                    @Override
                    public void onDataNotAvailable() {

                    }
                });
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
        /*
        getContactFromPhoneNumber(phoneNumberTo, new ContactDataSource.GetContactCallback() {
            @Override
            public void onContactLoaded(Contact contact) {

            }

            @Override
            public void onDataNotAvailable() {

            }
        });*/
    }

    private void sendMessage(Contact from, Contact to, String messageText) {
        Log.d(TAG, "sendMessage");
        String keyMessage = getKeyMessage(from, to);
        final ChatMessage message = new ChatMessage(
                from,
                to,
                messageText,
                ChatMessage.STATUS_WRITED,
                ChatMessage.TYPE_TEXT,
                new Date().getTime(),
                "",
                keyMessage,
                "",
                0
        );

        getDatabase().getReference()
                .child("users")
                .child(to.getUid())
                .child("lastConnection")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            Connection connection = dataSnapshot.getValue(Connection.class);
                            if (connection != null) {
                                changeStatus(connection.isOnline(), message.getMessageStatus(), message, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getContactFromPhoneNumber(String phoneNumber, final ContactDataSource.GetContactCallback callback) {
        getDatabase().getReference("phoneNumbers").child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "getContactFromPhoneNumber onDataChange");
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    String uid = dataSnapshot.getValue(String.class);
                    getContact(uid, callback);
                } else {
                    onContactNotAvaliable(callback);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onContactNotAvaliable(callback);
            }
        });
    }

    private void onContactNotAvaliable(ContactDataSource.GetContactCallback callback) {
        if (callback != null) {
            callback.onDataNotAvailable();
        }
    }

    private void getContact(String uid, final ContactDataSource.GetContactCallback callback) {
        getDatabase()
                .getReference()
                .child("users")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getContact onDataChange");
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                            Contact contact = dataSnapshot.getValue(Contact.class);
                            if (contact != null) {
                                Log.d(TAG, "contact : " + contact.toString());
                                callback.onContactLoaded(contact);
                            }
                        } else {
                            onContactNotAvaliable(callback);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        onContactNotAvaliable(callback);
                    }
                });
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

    public void changeConnection(Connection connection) {
        FirebaseUser firebaseUser = new FirebaseUser();
        firebaseUser.changeConnection(connection);
    }

    public void changeAction(Contact from, Contact to, String action) {
        String sort[] = StringUtils.sortAlphabetical(from.getUid(), to.getUid());
        String key1 = sort[0];
        String key2 = sort[1];
        changeAction(key1 + "_" + key2, action);
    }

    public void changeAction(String uidChat, String action) {
        Map<String, Object> map = new HashMap<>();
        map.put(mainUser.getUid(), action);
        getDatabase().getReference()
                .child(CHILD_CHAT_ACTIONS)
                .child(uidChat)
                .updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Log.d(TAG, "changeAction databaseReference: " + databaseReference);
                        Log.d(TAG, "changeAction databaseError: " + databaseError);
                    }
                });
    }
}
