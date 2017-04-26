package com.consultoraestrategia.messengeracademico.chat;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.chat.events.ChatEvent;
import com.consultoraestrategia.messengeracademico.domain.FirebaseChat;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.main.ConnectionRepositoryImpl;
import com.consultoraestrategia.messengeracademico.messageRepository.MessageRepository;
import com.consultoraestrategia.messengeracademico.messageRepository.MessageRepositoryImpl;
import com.consultoraestrategia.messengeracademico.storage.ChatDbFlowStorage;
import com.consultoraestrategia.messengeracademico.storage.ChatStorage;
import com.consultoraestrategia.messengeracademico.storage.ChatStorageImpl;
import com.consultoraestrategia.messengeracademico.utils.StringUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import java.util.List;

/**
 * Created by @stevecampos on 10/03/2017.
 */

public class ChatRepositoryImpl implements ChatRepository {

    private static final String TAG = ChatRepositoryImpl.class.getSimpleName();
    private EventBus eventBus;
    private FirebaseChat firebaseChat;
    public static final int FROM_CHAT = 100;
    public static final int FROM_USER_PATH = 101;
    public static final int FROM_NOTIFICATION = 102;

    private boolean online = false;


    private Chat chat;
    private Contact from;
    private Contact to;
    private ChatStorage chatStorage;
    private MessageRepository messageRepository;

    public ChatRepositoryImpl() {
        this.eventBus = GreenRobotEventBus.getInstance();
        this.firebaseChat = new FirebaseChat();
        this.chatStorage = new ChatStorageImpl(new ChatDbFlowStorage());
        this.messageRepository = new MessageRepositoryImpl();
    }

    private void manageSnapshot(DataSnapshot dataSnapshot) {
        Log.d(TAG, "manageSnapshot");
        if (dataSnapshot != null) {
            ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
            if (message != null) {
                messageRepository.manageIncomingMessage(message, from, FROM_CHAT);
            }
        }
    }

    @Override
    public void listenMessages(final Contact from, final Contact to) {
        Log.d(TAG, "listenMessages");
        this.from = from;
        this.to = to;
        this.chat = getChat(from, to);

        List<ChatMessage> messages = chat.getMessageList();

        if (messages != null && !messages.isEmpty()) {
            post(ChatEvent.TYPE_MESSAGE_LIST, messages);
        }

        firebaseChat.listenMessages(chat, new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: " + dataSnapshot);
                manageSnapshot(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged: " + dataSnapshot);
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
        Log.d(TAG, "changeAction");
        firebaseChat.changeAction(from, to, action);
    }

    @Override
    public void changeConnection(Contact from, Connection connection) {
        Log.d(TAG, "changeConnection");
        firebaseChat.changeConnection(from, connection);
    }

    private void saveMessageAndPost(final int source, final Contact userIncomingMessage, final ChatMessage message) {
        Log.d(TAG, "saveMessageAndPost");
        messageRepository.saveMessage(message, source);
    }


    private void saveMessageAndSend(final boolean online, final Contact from, final Contact to, ChatMessage chatMessage) {
        Log.d(TAG, "saveMessageAndSend");
        Chat chat = getChat(chatMessage);

        chatMessage.setEmisor(chat.getEmisor());
        chatMessage.setReceptor(chat.getReceptor());
        chatMessage.setChatKey(chat.getChatKey());

        String keyMessage = firebaseChat.getKeyMessage(chat.getEmisor(), chat.getReceptor());

        if (!keyMessage.isEmpty()) {
            chatMessage.setKeyMessage(keyMessage);
            messageRepository.saveMessage(chatMessage, FROM_CHAT);
            sendMessageToFirebase(online, from, to, chatMessage);

        } else {
            Log.e(TAG, "ERROR: keyMessage can't be empty!");
        }
    }

    private void sendMessageToFirebase(boolean online, final Contact from, final Contact to, final ChatMessage chatMessage) {
        Log.d(TAG, "sendMessageToFirebase");
        FirebaseChat firebaseChat = new FirebaseChat();
        firebaseChat.sendMessage(online, from, to, chatMessage, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "sendMessageToFirebase databaseError: " + databaseError);
                } else {
                    Log.d(TAG, "sendMessageToFirebase SUCCESS");
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

        messageRepository.saveMessage(message, FROM_CHAT);
        firebaseChat.setStatusReaded(online, chatMessage, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "databaseError: " + databaseError);
                } else {
                    messageRepository.saveMessage(chatMessage, FROM_CHAT);
                }
            }
        });

        /*
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
                });*/

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

    private Chat getChat(Contact from, Contact to) {
        String[] sort = StringUtils.sortAlphabetical(from.getUserKey(), to.getUserKey());
        Chat chat = new Chat();

        chat.setChatKey(sort[0] + "_" + sort[1]);
        chat.load();

        Contact emisor = chat.getEmisor();
        Contact receptor = chat.getReceptor();

        if (emisor != null && receptor != null) {
            emisor.load();
            receptor.load();
        } else {
            chat.setEmisor(from);
            chat.setReceptor(to);

            return chat;
        }


        chat.setEmisor(emisor);
        chat.setReceptor(receptor);

        return chat;
    }

    private Chat getChat(ChatMessage message) {
        Contact emisor = message.getEmisor();
        Contact receptor = message.getReceptor();


        emisor.load();
        receptor.load();


        String[] sort = StringUtils.sortAlphabetical(emisor.getUserKey(), receptor.getUserKey());
        String chatKey = sort[0] + "_" + sort[1];


        Chat chat = new Chat();
        chat.setChatKey(chatKey);
        chat.setEmisor(emisor);
        chat.setReceptor(receptor);
        chat.setStateTimestamp(message.getTimestamp());
        return chat;
    }


}
