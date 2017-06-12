package com.consultoraestrategia.messengeracademico.data.source.local;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.storage.ChatDbFlowStorage;
import com.consultoraestrategia.messengeracademico.storage.ChatStorage;
import com.consultoraestrategia.messengeracademico.utils.StringUtils;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.List;

/**
 * Created by @stevecampos on 18/05/2017.
 */

public class ChatLocalDataSource implements ChatDataSource {

    private static final String TAG = ChatLocalDataSource.class.getSimpleName();
    private ChatStorage chatStorage;

    public ChatLocalDataSource(ChatStorage chatStorage) {
        this.chatStorage = chatStorage;
    }

    @Override
    public void getChat(final Contact from, final Contact to, final GetChatCallback callback) {
        String[] sort = StringUtils.sortAlphabetical(from.getUserKey(), to.getUserKey());
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
    public void getMessages(Chat chat, GetMessageCallback callback) {
        Log.d(TAG, "getMessages");
        if (chat != null) {
            callback.onMessagesLoaded(chat.getMessageList());
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
                        Log.d(TAG, "onSuccess");
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
    public void listenForAllUserMessages(Contact mainContact, ListenMessagesCallback callback) {

    }

    @Override
    public void stopListenMessages() {

    }

    @Override
    public void changeStateWriting(Chat chat, boolean writing) {
        Log.d(TAG, "changeStateWriting");
    }

    @Override
    public void listenConnection(Chat chat, ListenConnectionCallback callback) {

    }

    @Override
    public void listenReceptorAction(Chat chat, ListenReceptorActionCallback callback) {

    }
}
