package com.consultoraestrategia.messengeracademico.data.source.local;

import android.support.annotation.NonNull;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage_Table;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.storage.ChatStorage;
import com.consultoraestrategia.messengeracademico.utils.StringUtils;
import com.google.firebase.auth.FirebaseUser;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

/**
 * Created by @stevecampos on 18/05/2017.
 */

public class ChatLocalDataSource implements ChatDataSource {

    private static final String TAG = ChatLocalDataSource.class.getSimpleName();
    private ChatStorage chatStorage;
    private FirebaseUser mainUser;

    public ChatLocalDataSource(ChatStorage chatStorage, FirebaseUser mainUser) {
        this.chatStorage = chatStorage;
        this.mainUser = mainUser;
    }

    @Override
    public void getChat(final Contact from, final Contact to, final GetChatCallback callback) {
        String[] sort = StringUtils.sortAlphabetical(from.getUid(), to.getUid());
        final String chatKey = sort[0] + "_" + sort[1];
        getChat(chatKey, new GetChatCallback() {
            @Override
            public void onChatLoaded(Chat chat) {
                callback.onChatLoaded(chat);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "onDataNotAvailable building chat...");
                Chat chat = new Chat();
                chat.setChatKey(chatKey);
                chat.setEmisor(from);
                chat.setReceptor(to);
                callback.onChatLoaded(chat);
            }
        });
    }


    @Override
    public void getChat(String keyChat, GetChatCallback callback) {

        Chat chat = new Chat();
        chat.setChatKey(keyChat);
        chat.load();

        Contact emisor = chat.getEmisor();
        Contact receptor = chat.getReceptor();

        if (emisor != null && receptor != null) {
            emisor.load();
            receptor.load();
        } else {
            callback.onDataNotAvailable();
            return;
        }

        chat.setEmisor(emisor);
        chat.setReceptor(receptor);
        callback.onChatLoaded(chat);

    }

    @Override
    public void getChat(final ChatMessage message, final GetChatCallback callback) {
        Log.d(TAG, "getChat from message");
        Chat chat = new Chat();
        chat.setChatKey(message.getChatKey());
        chat.setEmisor(message.getEmisor());
        chat.setReceptor(message.getReceptor());
        chat.setTimestamp(message.getTimestamp());
        callback.onChatLoaded(chat);
    }

    @Override
    public void getMessages(Chat chat, final GetMessageCallback callback) {
        Log.d(TAG, "getMessages");
        if (chat != null) {
            SQLite.select()
                    .from(ChatMessage.class)
                    .where(ChatMessage_Table.chatKey.eq(chat.getChatKey()))
                    //.and(ChatMessage_Table.timestamp.greaterThanOrEq(chat.getTimestamp()))
                    .orderBy(ChatMessage_Table.timestamp, false)
                    .limit(100)
                    .async()
                    .queryResultCallback(new QueryTransaction.QueryResultCallback<ChatMessage>() {
                        @Override
                        public void onQueryResult(QueryTransaction<ChatMessage> transaction, @NonNull CursorResult<ChatMessage> tResult) {
                            callback.onMessagesLoaded(tResult.toList());
                        }
                    }).execute();
        }
    }

    @Override
    public void getMessagesNoReaded(Chat chat, GetMessageCallback callback) {
        Log.d(TAG, "getMessagesNoReaded");
        if (chat != null) {
            callback.onMessagesLoaded(chat.getMessageNoReadedList(mainUser.getUid()));
        }
    }

    @Override
    public void saveMessage(final ChatMessage message, Chat chat, final ListenMessagesCallback callback) {
        Log.d(TAG, "saveMessage");
        chatStorage.save(
                message,
                chat,
                new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        Log.d(TAG, "message : " + message.getMessageText() + " saved successfully.");
                        callback.onMessageChanged(message);
                    }
                },
                new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        Log.d(TAG, "onError: " + error.getLocalizedMessage());
                        callback.onError(error.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void saveMessageWithStatusWrited(ChatMessage message, boolean receptorOnline, Chat chat, ListenMessagesCallback callback) {
        saveMessage(message, chat, callback);
    }

    @Override
    public void saveMessageWithStatusReaded(ChatMessage message, Chat chat, ListenMessagesCallback callback) {
        saveMessage(message, chat, callback);
    }


    @Override
    public void listenForAllUserMessages(String messageKey, ListenMessagesCallback callback) {

    }

    @Override
    public void stopListenMessages() {

    }

    @Override
    public void changeStateWriting(Chat chat, boolean writing) {
        Log.d(TAG, "changeStateWriting");
    }

    @Override
    public void listenConnection(Contact contact, ListenConnectionCallback callback) {

    }

    @Override
    public void listenReceptorAction(Chat chat, ListenReceptorActionCallback callback) {

    }

    @Override
    public void saveMessageOnLocal(ChatMessage message, Chat chat, ListenMessagesCallback callback) {

    }

    @Override
    public void getLastMessage(ListenMessagesCallback callback) {
        ChatMessage message = SQLite.select()
                .from(ChatMessage.class)
                .orderBy(ChatMessage_Table.timestamp, false)
                .querySingle();

        if (message != null) {
            Log.d(TAG, "message: " + message.toString());
            callback.onMessageChanged(message);
        } else {
            callback.onError("error retrieving last message!");
        }
    }

    @Override
    public void listenSingleMessage(ChatMessage message, ListenMessagesCallback callback) {

    }
}
