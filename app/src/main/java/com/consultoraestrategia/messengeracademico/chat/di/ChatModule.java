package com.consultoraestrategia.messengeracademico.chat.di;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;

import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.chat.ChatPresenter;
import com.consultoraestrategia.messengeracademico.chat.ChatPresenterImpl;
import com.consultoraestrategia.messengeracademico.chat.adapters.ChatMessageAdapter;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.GenerateMessageKey;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ListenSingleMessage;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.data.ContactRepository;
import com.consultoraestrategia.messengeracademico.domain.FirebaseChat;
import com.consultoraestrategia.messengeracademico.domain.FirebaseImageStorage;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.main.ConnectionInteractor;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ChangeStateWriting;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.GetChat;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.GetContact;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ListenReceptorAction;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ListenReceptorConnection;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.LoadMessages;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ReadMessage;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.SendMessage;
import com.consultoraestrategia.messengeracademico.prueba.domain.usecase.UploadImage;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by @stevecampos on 9/06/2017.
 */
@Module
public class ChatModule {
    ChatMessageListener messageListener;
    ChatMessageAdapter.OnBottomReachedListener onBottomReachedListener;

    public ChatModule(ChatMessageListener messageListener, ChatMessageAdapter.OnBottomReachedListener onBottomReachedListener) {
        this.messageListener = messageListener;
        this.onBottomReachedListener = onBottomReachedListener;
    }

    @Provides
    @Singleton
    ChatMessageAdapter provideChatMessageAdapter(FirebaseUser mainUser, List<ChatMessage> messages, ChatMessageListener listener, Context context, ChatMessageAdapter.OnBottomReachedListener onBottomReachedListener) {
        return new ChatMessageAdapter(mainUser, messages, listener, context, onBottomReachedListener);
    }

    @Provides
    @Singleton
    ChatMessageAdapter.OnBottomReachedListener provideOnBottomReachedListener(){
        return this.onBottomReachedListener;
    }

    @Provides
    @Singleton
    List<ChatMessage> providesMessages() {
        return new ArrayList<>();
    }

    @Provides
    @Singleton
    ChatMessageListener provideMessageListener() {
        return this.messageListener;
    }

    @Provides
    @Singleton
    ChatPresenter providePresenter(FirebaseUser mainUser, UseCaseHandler useCaseHandler, LoadMessages useCaseLoadMessages, GetContact useCaseGetContact, GetChat useCaseGetChat, SendMessage useCaseSendMessage, ReadMessage useCaseReadMessage, ChangeStateWriting useCaseChangeStateWriting, ListenReceptorConnection useCaseListenReceptorConnection, ListenReceptorAction useCaseListenReceptorAction, EventBus eventBus, ConnectionInteractor connectionInteractor, GenerateMessageKey generateMessageKey, File cacheDir, UploadImage uploadImage, ContentResolver contentResolver, Resources resources, ListenSingleMessage listenSingleMessage) {
        return new ChatPresenterImpl(mainUser, useCaseHandler, useCaseLoadMessages, useCaseGetContact, useCaseGetChat, useCaseSendMessage, useCaseReadMessage, useCaseChangeStateWriting, useCaseListenReceptorConnection, useCaseListenReceptorAction, eventBus, connectionInteractor, generateMessageKey, cacheDir, uploadImage, contentResolver, resources, listenSingleMessage);
    }

    @Provides
    @Singleton
    ListenSingleMessage provideListenSingleMessage(ChatRepository repository) {
        return new ListenSingleMessage(repository);
    }


    @Provides
    @Singleton
    UploadImage provideUploadImage(FirebaseImageStorage firebaseImageStorage) {
        return new UploadImage(firebaseImageStorage);
    }

    @Provides
    @Singleton
    FirebaseImageStorage provideFirebaseImageStorage() {
        return FirebaseImageStorage.getInstance();
    }


    @Provides
    @Singleton
    GenerateMessageKey provideGenerateMessageKey(FirebaseChat firebaseChat) {
        return new GenerateMessageKey(firebaseChat);
    }

    @Provides
    @Singleton
    ListenReceptorConnection provideListenReceptorConnection(ChatRepository repository) {
        return new ListenReceptorConnection(repository);
    }

    @Provides
    @Singleton
    ListenReceptorAction provideListenReceptorAction(ChatRepository repository) {
        return new ListenReceptorAction(repository);
    }

    @Provides
    @Singleton
    ChangeStateWriting provideChangeStateWriting(ChatRepository repository) {
        return new ChangeStateWriting(repository);
    }

    @Provides
    @Singleton
    ReadMessage provideReadMessage(ChatRepository repository) {
        return new ReadMessage(repository);
    }

    @Provides
    @Singleton
    LoadMessages provideLoadMessges(ChatRepository repository) {
        return new LoadMessages(repository);
    }

    @Provides
    @Singleton
    GetContact provideGetContact(ContactRepository contactRepository) {
        return new GetContact(contactRepository);
    }

    @Provides
    @Singleton
    ContactRepository provideContactRepository() {
        return new ContactRepository();
    }

    @Singleton
    @Provides
    GetChat provideGetChat(ChatRepository repository) {
        return new GetChat(repository);
    }

    @Singleton
    @Provides
    SendMessage provideSendMessage(ChatRepository repository, FirebaseChat firebaseChat) {
        return new SendMessage(repository, firebaseChat);
    }
}
