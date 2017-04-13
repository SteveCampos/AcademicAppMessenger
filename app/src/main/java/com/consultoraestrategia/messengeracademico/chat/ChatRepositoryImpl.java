package com.consultoraestrategia.messengeracademico.chat;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.chat.events.ChatEvent;
import com.consultoraestrategia.messengeracademico.chatList.ChatListRepositoryImpl;
import com.consultoraestrategia.messengeracademico.domain.ChatDbHelper;
import com.consultoraestrategia.messengeracademico.domain.FirebaseChat;
import com.consultoraestrategia.messengeracademico.domain.FirebaseUser;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.main.ConnectionRepositoryImpl;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.List;

import static com.consultoraestrategia.messengeracademico.domain.ChatDbHelper.getChat;

/**
 * Created by Steve on 10/03/2017.
 */
public class ChatRepositoryImpl implements ChatRepository {

    private static final String TAG = ChatRepositoryImpl.class.getSimpleName();
    private EventBus eventBus;
    private FirebaseChat firebaseChat;
    private static final int FROM_CHAT = 100;
    public static final int FROM_USER_PATH = 101;
    public static final int FROM_NOTIFICATION = 102;

    private boolean online = false;


    private Chat chat;
    private Contact from;
    private Contact to;

    public ChatRepositoryImpl() {
        this.eventBus = GreenRobotEventBus.getInstance();
        this.firebaseChat = new FirebaseChat();
    }

    @Override
    public void listenMessages(final Contact from, final Contact to) {
        Log.d(TAG, "listenMessages");
        this.from = from;
        this.to = to;
        this.chat = getChat(from, to);

        List<ChatMessage> messages = chat.getMessageList();

        if (messages != null && !messages.isEmpty()) {
            Log.d(TAG, "message list size: " + messages.size());
            post(ChatEvent.TYPE_MESSAGE_LIST, messages);
        }

        firebaseChat.listenMessages(chat, new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "dataSnapshot: " + dataSnapshot);
                manageSnapshotMessage(online, FROM_CHAT, from, dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                manageSnapshotMessage(online, FROM_CHAT, from, dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) {
                    Log.d(TAG, "listenMessages onCancelled databaseError: " + databaseError);
                }
            }
        });
    }

    @Override
    public void listenConnection(Contact from, Contact to) {
        Log.d(TAG, "listenConnection");
        firebaseChat.listenConnection(to, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "listenConnection onDataChange: " + dataSnapshot);
                if (dataSnapshot != null) {
                    Connection connection = dataSnapshot.getValue(Connection.class);
                    if (connection != null) {
                        online = connection.isOnline();
                        post(ChatEvent.TYPE_CONNECTION, connection);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void listenChatAction(Contact from, Contact to) {
        Log.d(TAG, "listenChatAction");
        firebaseChat.listenChatAction(from, to, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "listenChatAction onDataChange: " + dataSnapshot);
                if (dataSnapshot != null) {
                    for (DataSnapshot children :
                            dataSnapshot.getChildren()) {
                        String userId = children.getKey();
                        String action = children.getValue(String.class);

                        Contact contact =
                                SQLite
                                        .select()
                                        .from(Contact.class)
                                        .where(Contact_Table.userKey.eq(userId))
                                        .querySingle();

                        if (contact != null) {
                            post(ChatEvent.TYPE_ACTION, contact, action);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void changeAction(Contact from, Contact to, String action) {
        firebaseChat.changeAction(from, to, action);
    }

    @Override
    public void changeConnection(Contact from, Connection connection) {
        firebaseChat.changeConnection(from, connection);
    }

    public static void manageSnapshotMessage(boolean online, int type, Contact me, DataSnapshot dataSnapshot) {
        Log.d(TAG, "manageSnapshotMessage");
        if (me == null || dataSnapshot == null) {
            return;
        }
        if (dataSnapshot.getValue() != null && dataSnapshot.getKey() != null) {

            ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
            String keyMessage = dataSnapshot.getKey();

            Log.d(TAG, "keyMessage: " + dataSnapshot.getKey());


            String emisorKey = message.getEmisor().getUserKey();
            String receptorKey = message.getReceptor().getUserKey();
            int messageStatus = message.getMessageStatus();

            Log.d(TAG, "messageStatus: " + message.getMessageStatus());

            message.setKeyMessage(keyMessage);

            if (emisorKey.equals(me.getUserKey())) {
                //SOY EL EMISOR DEL MENSAJE
                Log.d(TAG, "SOY EL EMISOR DEL MENSAJE");
                switch (messageStatus) {
                    case ChatMessage.STATUS_SEND:
                        //DO NOTHING!
                        break;
                    case ChatMessage.STATUS_DELIVERED:
                        //SAVE MESSAGE AND POST!
                        saveMessageAndPost(type, message.getEmisor(), message);
                        break;
                    case ChatMessage.STATUS_READED:
                        //SAVE MESSAGE AND POST!
                        saveMessageAndPost(type, message.getEmisor(), message);
                        break;

                }
            } else if (receptorKey.equals(me.getUserKey())) {
                //SOY EL RECEPTOR DEL MENSAJE
                Log.d(TAG, "SOY EL RECEPTOR DEL MENSAJE");
                switch (messageStatus) {
                    case ChatMessage.STATUS_SEND:
                        //SAVE MESSAGE WITH OTHER STATUS, POST, AND UPLOAD TO FIREBASE.
                        message.setMessageStatus(ChatMessage.STATUS_DELIVERED);
                        saveMessageAndPost(type, message.getReceptor(), message);

                        FirebaseChat firebaseChat = new FirebaseChat();
                        firebaseChat.setStatusDelivered(online, message, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError != null) {
                                    Log.d(TAG, "firebaseChat.setStatusDelivered ERROR: " + databaseError.getMessage());
                                } else {
                                    Log.d(TAG, "firebaseChat.setStatusDelivered SUCESS");
                                }
                            }
                        });
                        break;
                    case ChatMessage.STATUS_DELIVERED:
                        //DO NOTHING!
                        break;
                    case ChatMessage.STATUS_READED:
                        //DO NOTHING!
                        removeMessage(message.getReceptor(), message);
                        break;
                }
            }
        }

    }

    private static void removeMessage(Contact userIncomingMessage, ChatMessage message) {
        FirebaseUser firebaseUser = new FirebaseUser();
        firebaseUser.removeIncomingMessage(userIncomingMessage, message);
    }

    private static void saveMessageAndPost(final int type, final Contact userIncomingMessage, final ChatMessage message) {
        final Chat chat = getChat(message);

        message.setEmisor(chat.getEmisor());
        message.setReceptor(chat.getReceptor());
        message.setChatKey(chat.getChatKey());

        //final ChatMessage chatMessage = message;

        ChatDbHelper.saveMessage(message,
                chat,
                new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        Log.d(TAG, "saveMessageAndPost onSuccess");
                        post(GreenRobotEventBus.getInstance(), ChatEvent.TYPE_MESSAGE, message);
                        ChatListRepositoryImpl.post(chat);
                        if (type != FROM_CHAT) {
                            removeMessage(userIncomingMessage, message);
                        }
                    }
                }, new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        Log.d(TAG, "onError: " + error);
                    }
                });
    }


    private void saveMessageAndSend(final boolean online, final Contact from, final Contact to, ChatMessage chatMessage) {

        Chat chat = getChat(chatMessage);

        chatMessage.setEmisor(chat.getEmisor());
        chatMessage.setReceptor(chat.getReceptor());
        chatMessage.setChatKey(chat.getChatKey());

        String keyMessage = firebaseChat.getKeyMessage(chat.getEmisor(), chat.getReceptor());

        if (!keyMessage.isEmpty()) {
            chatMessage.setKeyMessage(keyMessage);
            final ChatMessage message = chatMessage;

            ChatDbHelper.saveMessage(message,
                    chat,
                    new Transaction.Success() {
                        @Override
                        public void onSuccess(Transaction transaction) {
                            Log.d(TAG, "saveMessageAndPost onSuccess");
                            post(eventBus, ChatEvent.TYPE_MESSAGE, message);
                            sendMessageToFirebase(online, from, to, message);
                        }
                    }, new Transaction.Error() {
                        @Override
                        public void onError(Transaction transaction, Throwable error) {
                            Log.d(TAG, "onError: " + error);
                        }
                    });
        } else {
            Log.e(TAG, "ERROR KEYMESSAGE EMPTY!");
        }
    }

    private void sendMessageToFirebase(boolean online, final Contact from, final Contact to, final ChatMessage chatMessage) {
        chatMessage.setMessageStatus(ChatMessage.STATUS_SEND);
        //final ChatMessage message = chatMessage;
        FirebaseChat firebaseChat = new FirebaseChat();
        firebaseChat.sendMessage(online, from, to, chatMessage, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "sendMessage databaseError: " + databaseError);
                } else {
                    Log.d(TAG, "sendMessage SEND!");
                    chatMessage.setMessageStatus(ChatMessage.STATUS_SEND);
                    saveMessageAndPost(FROM_CHAT, from, chatMessage);
                }
            }
        });
    }


    @Override
    public void sendMessage(boolean online, Contact from, Contact to, ChatMessage message) {
        Log.d(TAG, "sendMessage");
        saveMessageAndSend(online, from, to, message);
    }

    @Override
    public void setMessageStatusReaded(Contact from, Contact to, ChatMessage message, final boolean online) {
        Log.d(TAG, "setMessageStatusReaded");

        Chat chat = getChat(message);

        message.setEmisor(chat.getEmisor());
        message.setReceptor(chat.getReceptor());
        message.setChatKey(chat.getChatKey());
        message.setMessageStatus(ChatMessage.STATUS_READED);

        final ChatMessage chatMessage = message;

        ChatDbHelper.saveMessage(message,
                chat,
                new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        Log.d(TAG, "saveMessageAndPost onSuccess");

                        firebaseChat.setStatusReaded(online, chatMessage, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.d(TAG, "databaseError: " + databaseError);
                                } else {
                                    post(eventBus, ChatEvent.TYPE_MESSAGE, chatMessage);
                                }
                            }
                        });
                    }
                }, new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        Log.d(TAG, "onError: " + error);
                    }
                });

    }

    @Override
    public void setMessageStatusDelivered(Contact from, Contact to, ChatMessage message, boolean online) {
        Log.d(TAG, "setMessageStatusDelivered");
        firebaseChat.setStatusDelivered(online, message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "setMessageStatusSended databaseError: " + databaseError);
                }
            }
        });
    }

    @Override
    public void setMessageStatusSended(Contact from, Contact to, ChatMessage message, boolean online) {
        Log.d(TAG, "setMessageStatusSended");
        firebaseChat.setStatusSend(online, message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "setMessageStatusSended databaseError: " + databaseError);
                }
            }
        });
    }

    @Override
    public void getContactEmisor(String phoneNumber) {
        /*Contact contact = SQLite.select()
                .from(Contact.class)
                .where(Contact_Table.phoneNumber.eq(phoneNumber))
                .and(Contact_Table.userKey.isNotNull())
                .querySingle();*/
        ConnectionRepositoryImpl repository = new ConnectionRepositoryImpl();
        post(ChatEvent.TYPE_CONTACT_EMISOR, repository.getMainContact());
    }

    @Override
    public void getContactReceptor(String phoneNumber) {
        Contact contact = SQLite.select()
                .from(Contact.class)
                .where(Contact_Table.phoneNumber.eq(phoneNumber))
                .and(Contact_Table.userKey.isNotNull())
                .querySingle();
        post(ChatEvent.TYPE_CONTACT_RECEPTOR, contact);
    }

    private static void post(EventBus eventBus, int type, Contact contact, ChatMessage message, Connection connection, String action, List<ChatMessage> messages) {
        ChatEvent event = new ChatEvent();
        event.setType(type);
        if (contact != null) {
            event.setContact(contact);
        }
        if (message != null) {
            event.setMessage(message);
        }
        if (connection != null) {
            event.setConnection(connection);
        }
        if (action != null) {
            event.setAction(action);
        }
        if (messages != null) {
            event.setMessages(messages);
        }
        eventBus.post(event);
    }

    private void post(int type, Contact contact) {
        post(eventBus, type, contact, null, null, null, null);
    }

    private static void post(EventBus eventBus, int type, ChatMessage message) {
        post(eventBus, type, null, message, null, null, null);
    }

    private void post(int type, Connection connection) {
        post(eventBus, type, null, null, connection, null, null);
    }

    private void post(int type, Contact contact, String action) {
        post(eventBus, type, contact, null, null, action, null);
    }

    private void post(int type, List<ChatMessage> messages) {
        post(eventBus, type, null, null, null, null, messages);
    }


}
