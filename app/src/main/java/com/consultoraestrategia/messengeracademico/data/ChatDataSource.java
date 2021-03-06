package com.consultoraestrategia.messengeracademico.data;

import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Created by @stevecampos on 16/05/2017.
 */

public interface ChatDataSource {

    void deleteChat(Chat chat, SuccessCallback<Chat> successCallback);

    interface SuccessCallback<T> {
        void onSucess(T data, boolean success);

        void onFailure(Exception e);
    }

    interface LoadChatsCallback {
        void onChatsLoaded(List<Chat> chats);

        void onDataNotAvailable();
    }

    interface GetChatCallback {
        void onChatLoaded(Chat chat);

        void onDataNotAvailable();
    }

    interface ListenMessagesCallback {
        void onMessageChanged(ChatMessage message);

        void onError(String error);
    }

    interface GetMessageCallback {
        void onMessagesLoaded(List<ChatMessage> messages);

        void onDataNotAvailable();
    }

    interface ListenConnectionCallback {
        void onConnectionChanged(Connection connection);

        void onError(String error);
    }

    interface ListenReceptorActionCallback {
        void onReceptorActionChanged(Contact receptor, String action);
    }


    void getChat(Contact emisor, Contact receptor, GetChatCallback callback);

    void getChat(String keyChat, GetChatCallback callback);

    void getChat(ChatMessage message, GetChatCallback callback);

    void getMessages(Chat chat, GetMessageCallback callback);

    void getMoreMessages(ChatMessage message, GetMessageCallback callback);

    void getMessagesNoReaded(Chat chat, GetMessageCallback callback);

    void saveMessage(ChatMessage message, Chat chat, ListenMessagesCallback callback);

    void saveMessageWithStatusWrited(ChatMessage message, boolean receptorOnline, Chat chat, ListenMessagesCallback callback);

    void saveMessageWithStatusReaded(ChatMessage message, Chat chat, ListenMessagesCallback callback);

    void sendMessageNotReaded(ChatMessage message, boolean isReceptorOnline, Chat chat, ListenMessagesCallback callback);


    void listenForAllUserMessages(String lastMessageKey, ListenMessagesCallback callback);

    void stopListenMessages();

    void changeStateWriting(Chat chat, boolean writing);

    void listenConnection(Contact contact, ListenConnectionCallback callback);

    void listenReceptorAction(Chat chat, ListenReceptorActionCallback callback);


    void saveMessageOnLocal(ChatMessage message, Chat chat, ListenMessagesCallback callback);


    void getLastMessage(ListenMessagesCallback callback);


    void listenSingleMessage(ChatMessage message, ListenMessagesCallback callback);

    void deleteMessage(ChatMessage message, SuccessCallback<ChatMessage> listener);

}
