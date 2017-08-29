package com.consultoraestrategia.messengeracademico.data.source.remote;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.domain.FirebaseChat;
import com.consultoraestrategia.messengeracademico.domain.FirebaseUser;
import com.consultoraestrategia.messengeracademico.entities.Action;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by @stevecampos on 18/05/2017.
 */

public class ChatRemoteDataSource implements ChatDataSource {

    private static final String TAG = ChatRemoteDataSource.class.getSimpleName();
    private FirebaseChat firebaseChat;
    private FirebaseUser firebaseUser;
    private com.google.firebase.auth.FirebaseUser mainUser;


    public ChatRemoteDataSource(FirebaseChat firebaseChat, FirebaseUser firebaseUser, com.google.firebase.auth.FirebaseUser mainUser) {
        this.firebaseChat = firebaseChat;
        this.firebaseUser = firebaseUser;
        this.mainUser = mainUser;
    }

    @Override
    public void getChat(Contact emisor, Contact receptor, GetChatCallback callback) {

    }

    @Override
    public void getChat(String keyChat, GetChatCallback callback) {

    }

    @Override
    public void getChat(ChatMessage message, GetChatCallback callback) {

    }

    @Override
    public void getMessages(Chat chat, GetMessageCallback callback) {

    }

    @Override
    public void getMessagesNoReaded(Chat chat, GetMessageCallback callback) {

    }

    @Override
    public void saveMessage(final ChatMessage message, Chat chat, final ListenMessagesCallback callback) {
        Log.d(TAG, "saveMessage");
        firebaseChat.saveMessage(true, message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "saveMessage databaseError: " + databaseError);
                    callback.onError(databaseError.getMessage());
                } else {
                    Log.d(TAG, "saveMessage success");
                    callback.onMessageChanged(message);
                }
            }
        });
    }

    @Override
    public void saveMessageWithStatusWrited(final ChatMessage message, boolean receptorOnline, Chat chat, final ListenMessagesCallback callback) {
        Log.d(TAG, "saveMessageWithStatusWrited}");
        firebaseChat.saveMessage(receptorOnline, message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "saveMessageWithStatusWrited databaseError: " + databaseError);
                    callback.onError(databaseError.getMessage());
                } else {
                    Log.d(TAG, "saveMessageWithStatusWrited SUCCESS");
                    message.setMessageStatus(ChatMessage.STATUS_SEND);
                    callback.onMessageChanged(message);
                }
            }
        });
    }

    @Override
    public void saveMessageWithStatusReaded(ChatMessage message, Chat chat, ListenMessagesCallback callback) {
        saveMessage(message, chat, callback);
    }

    @Override
    public void listenForAllUserMessages(String lastMessageKey, final ListenMessagesCallback callback) {
        Log.d(TAG, "listenForAllUserMessages");
        listenMessages(lastMessageKey, callback);

    }

    private void listenMessages(String lastMessageKey, final ListenMessagesCallback callback){
        firebaseUser.listenForAllUserMessages(lastMessageKey, new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded");
                parseAndFire(callback, dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged");
                parseAndFire(callback, dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "listenMessages databaseError: " + databaseError);
            }
        });
    }

    @Override
    public void stopListenMessages() {
        Log.d(TAG, "stopListenMessages");
        firebaseChat.removeMessagesListener();
    }

    @Override
    public void changeStateWriting(Chat chat, boolean writing) {
        Log.d(TAG, "changeStateWriting");
        if (chat != null) {
            firebaseChat.changeAction(chat.getChatKey(), writing ? Action.ACTION_WRITING : Action.ACTION_NO_ACTION);
        }
    }

    @Override
    public void listenConnection(Contact contact, final ListenConnectionCallback callback) {
        Log.d(TAG, "listenConnection");
        firebaseChat.listenConnection(contact, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "listenConnection onDataChange: " + dataSnapshot);
                if (dataSnapshot != null) {
                    Connection connection = dataSnapshot.getValue(Connection.class);
                    if (connection != null) {
                        callback.onConnectionChanged(connection);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) {
                    callback.onError(databaseError.getMessage());
                }
            }
        });
    }

    @Override
    public void listenReceptorAction(final Chat chat, final ListenReceptorActionCallback callback) {
        Log.d(TAG, "listenReceptorAction");
        firebaseChat.listenReceptorAction(chat, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String action = dataSnapshot.getValue(String.class);
                    callback.onReceptorActionChanged(chat.getReceptor(), action);
                } catch (Exception ex) {
                    Log.e(TAG, "listenReceptorAction Exception: " + ex);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void saveMessageOnLocal(ChatMessage message, Chat chat, ListenMessagesCallback callback) {

    }

    @Override
    public void getLastMessage(final ListenMessagesCallback callback) {
        firebaseUser.listenLastMessage(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                parseAndFire(callback, dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                parseAndFire(callback, dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    @Override
    public void listenSingleMessage(final ChatMessage message, final ListenMessagesCallback callback) {
        Log.d(TAG, "listenSingleMessage message: " + message.toString());
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot: " + dataSnapshot);
                if (dataSnapshot != null && dataSnapshot.getValue() != null){
                    ChatMessage message1 = dataSnapshot.getValue(ChatMessage.class);
                    if (message1 != null){
                        callback.onMessageChanged(message1);
                    }else{
                        callback.onError("message null");
                    }
                }else{
                    callback.onError("snapshot null");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        };
        firebaseUser.listenSingleMessage(message, listener);
    }

    private void parseAndFire(ListenMessagesCallback callback, DataSnapshot dataSnapshot) {
        ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
        if (message != null) {
            callback.onMessageChanged(message);
        }else{
            callback.onError("message is null!");
        }
    }

}
