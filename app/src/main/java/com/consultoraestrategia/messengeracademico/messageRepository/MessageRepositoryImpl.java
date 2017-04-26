package com.consultoraestrategia.messengeracademico.messageRepository;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.domain.FirebaseChat;
import com.consultoraestrategia.messengeracademico.domain.FirebaseUser;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.postEvent.ChatListPostEvent;
import com.consultoraestrategia.messengeracademico.postEvent.ChatListPostEventImpl;
import com.consultoraestrategia.messengeracademico.postEvent.ChatPostEvent;
import com.consultoraestrategia.messengeracademico.postEvent.ChatPostEventImpl;
import com.consultoraestrategia.messengeracademico.storage.ChatDbFlowStorage;
import com.consultoraestrategia.messengeracademico.storage.ChatStorage;
import com.consultoraestrategia.messengeracademico.storage.ChatStorageImpl;
import com.consultoraestrategia.messengeracademico.utils.StringUtils;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import static com.consultoraestrategia.messengeracademico.chat.ChatRepositoryImpl.FROM_CHAT;

/**
 * Created by @stevecampos on 24/04/2017.
 */

public class MessageRepositoryImpl implements MessageRepository {
    private static final String TAG = MessageRepositoryImpl.class.getSimpleName();

    private ChatStorage chatStorage;
    private ChatPostEvent chatPostEvent;
    private ChatListPostEvent chatListPostEvent;

    public MessageRepositoryImpl() {
        this.chatStorage = new ChatStorageImpl(new ChatDbFlowStorage());
        this.chatPostEvent = new ChatPostEventImpl();
        this.chatListPostEvent = new ChatListPostEventImpl();
    }

    @Override
    public void manageIncomingMessage(ChatMessage message, Contact me, int source) {
        Log.d(TAG, "manageSnapshotMessage");
        if (me == null || message == null) {
            return;
        }

        int messageStatus = message.getMessageStatus();


        Log.d(TAG, "source: " + source);
        Log.d(TAG, "messageStatus: " + messageStatus);

        if (message.getEmisor().equals(me)) {
            Log.d(TAG, "SOY EL EMISOR DEL MENSAJE");

            switch (messageStatus) {
                case ChatMessage.STATUS_WRITED:
                    //DO NOTHING!
                    break;
                case ChatMessage.STATUS_SEND:
                    //DO NOTHING!
                    break;
                case ChatMessage.STATUS_DELIVERED:
                    //SAVE MESSAGE AND POST!
                    saveMessage(message, source);
                    break;
                case ChatMessage.STATUS_READED:
                    //SAVE MESSAGE AND POST!
                    saveMessage(message, source);
                    break;

            }
        } else if (message.getReceptor().equals(me)) {

            //SOY EL RECEPTOR DEL MENSAJE

            Log.d(TAG, "SOY EL RECEPTOR DEL MENSAJE");

            switch (messageStatus) {
                case ChatMessage.STATUS_WRITED:
                    //SAVE MESSAGE WITH OTHER STATUS, POST, AND UPLOAD TO FIREBASE.
                    removeIncomingMessage(source, message);

                    message.setMessageStatus(ChatMessage.STATUS_DELIVERED);
                    saveMessage(message, source);
                    setMessageDelivered(source, message);

                    break;
                case ChatMessage.STATUS_SEND:
                    break;
                case ChatMessage.STATUS_DELIVERED:
                    //DO NOTHING!
                    break;
                case ChatMessage.STATUS_READED:
                    //DO NOTHING!
                    removeIncomingMessage(source, message);
                    break;
            }
        }

    }

    @Override
    public void saveMessage(final ChatMessage message, final int source) {

        final Chat chat = buildChatFromMessage(message);

        message.setEmisor(chat.getEmisor());
        message.setReceptor(chat.getReceptor());
        message.setChatKey(chat.getChatKey());


        chatStorage.save(
                message,
                chat,
                new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        Log.d(TAG, "saveMessage onSuccess");

                        chatPostEvent.post(message);
                        chatListPostEvent.post(chat);

                        removeIncomingMessage(source, message);

                    }
                }, new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        Log.d(TAG, "onError: " + error);
                    }
                });
    }

    private Contact getUserToRemoveMessage(ChatMessage message) {
        Contact user;
        switch (message.getMessageStatus()) {
            case ChatMessage.STATUS_WRITED:
                user = message.getReceptor();
                break;
            case ChatMessage.STATUS_DELIVERED:
                user = message.getEmisor();
                break;
            case ChatMessage.STATUS_READED:
                user = message.getEmisor();
                break;
            default:
                user = message.getReceptor();
        }
        return user;

    }


    @Override
    public void removeIncomingMessage(int source, ChatMessage message) {
        if (source != FROM_CHAT) {
            Contact user = getUserToRemoveMessage(message);
            removeMessageFromUser(source, user, message);
        }
    }

    private void removeMessageFromUser(int source, Contact user, ChatMessage message) {
        FirebaseUser firebaseUser = new FirebaseUser();
        firebaseUser.removeMessage(source, user, message);
    }

    private Chat buildChatFromMessage(ChatMessage message) {
        Contact emisor = message.getEmisor();
        Contact receptor = message.getReceptor();


        emisor.load();
        receptor.load();

        emisor.save();
        receptor.save();

        String[] sort = StringUtils.sortAlphabetical(emisor.getUserKey(), receptor.getUserKey());
        String chatKey = sort[0] + "_" + sort[1];

        Chat chat = new Chat();
        chat.setChatKey(chatKey);
        chat.setEmisor(emisor);
        chat.setReceptor(receptor);
        chat.setStateTimestamp(message.getTimestamp());
        return chat;
    }

    private void setMessageDelivered(int type, ChatMessage message) {
        FirebaseChat firebaseChat = new FirebaseChat();
        firebaseChat.setStatusDelivered(true, message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "firebaseChat.setStatusDelivered ERROR: " + databaseError.getMessage());
                } else {
                    Log.d(TAG, "firebaseChat.setStatusDelivered SUCESS");
                }
            }
        });
    }
}
