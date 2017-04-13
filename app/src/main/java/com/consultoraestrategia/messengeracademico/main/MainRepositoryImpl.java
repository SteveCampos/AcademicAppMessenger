package com.consultoraestrategia.messengeracademico.main;

import android.content.SharedPreferences;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.chat.ChatRepositoryImpl;
import com.consultoraestrategia.messengeracademico.domain.ChatDbHelper;
import com.consultoraestrategia.messengeracademico.domain.FirebaseChat;
import com.consultoraestrategia.messengeracademico.domain.FirebaseUser;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.main.event.MainEvent;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import static com.consultoraestrategia.messengeracademico.main.event.MainEvent.TYPE_CONTACT;
import static com.consultoraestrategia.messengeracademico.main.event.MainEvent.TYPE_PHONENUMBER;

/**
 * Created by jairc on 27/03/2017.
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
                Log.d(TAG, "Error getting phonenumber from preferences!");
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

    @Override
    public void listenForIncomingMessages(final Contact contact) {

        Log.d(TAG, "listenForIncomingMessages");

        firebaseUser.listenToIncomingMessages(contact, new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatRepositoryImpl.manageSnapshotMessage(true, ChatRepositoryImpl.FROM_USER_PATH, me, dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ChatRepositoryImpl.manageSnapshotMessage(true, ChatRepositoryImpl.FROM_USER_PATH, me, dataSnapshot);
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

    /*
    private void manageSnapshot(final Contact emisor, DataSnapshot dataSnapshot) {
        Log.d(TAG, "manageSnapshot: " + dataSnapshot);
        if (dataSnapshot != null) {
            final ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
            String keyMessage = dataSnapshot.getKey();
            message.setKeyMessage(keyMessage);

            Contact receptor = ChatDbHelper.getContact(message.getEmisor().getUserKey());
            Log.d(TAG, "getMessageStatus: " + message.getMessageStatus());



            switch (message.getMessageStatus()) {
                case ChatMessage.STATUS_SEND:
                    saveMessageWithStatusSend(emisor, receptor, message);
                    break;
                case ChatMessage.STATUS_DELIVERED:
                    saveMessageWithStatusDelivered(emisor, receptor, message);
                    break;
                case ChatMessage.STATUS_READED:
                    saveMessageWithStatusReaded(emisor, receptor, message);
                    break;
            }
        }
    }

    private void saveMessageWithStatusReaded(final Contact emisor, final Contact receptor, final ChatMessage message) {
        ChatDbHelper.saveMessage(emisor, receptor, message,
                new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        ChatDbHelper.post(eventBus, emisor, receptor, message);
                        firebaseUser.removeIncomingMessage(emisor, message);
                    }
                }, new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        Log.d(TAG, "saveMessageWithStatusDelivered onError: " + error.getMessage());
                    }
                });
    }

    private void saveMessageWithStatusDelivered(final Contact emisor, final Contact receptor, final ChatMessage message) {
        ChatDbHelper.saveMessage(emisor, receptor, message,
                new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        ChatDbHelper.post(eventBus, emisor, receptor, message);
                        firebaseUser.removeIncomingMessage(emisor, message);
                    }
                }, new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        Log.d(TAG, "saveMessageWithStatusDelivered onError: " + error.getMessage());
                    }
                });
    }


    private void saveMessageWithStatusSend(final Contact emisor, final Contact receptor, ChatMessage message) {
        message.setMessageStatus(ChatMessage.STATUS_DELIVERED);
        final ChatMessage chatMessage = message;
        ChatDbHelper.saveMessage(emisor, receptor, message,
                new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        setStatusDelivered(emisor, receptor, chatMessage);
                        firebaseUser.removeIncomingMessage(emisor, chatMessage);
                    }
                }, new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        Log.d(TAG, "saveMessageWithStatusSend onError: " + error.getMessage());
                    }
                });
    }

    private void setStatusDelivered(final Contact emisor, final Contact receptor, final ChatMessage message) {
        FirebaseChat firebaseChat = new FirebaseChat();
        firebaseChat.setStatusDelivered(message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "setStatusDelivered databaseError: " + databaseError.getMessage());
                } else {
                    Log.d(TAG, "setStatusDelivered");
                    ChatDbHelper.post(eventBus, emisor, receptor, message);
                    firebaseUser.removeIncomingMessage(emisor, message);
                }
            }
        });
    }*/


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
