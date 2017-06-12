package com.consultoraestrategia.messengeracademico;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.data.source.local.ChatLocalDataSource;
import com.consultoraestrategia.messengeracademico.data.source.remote.ChatRemoteDataSource;
import com.consultoraestrategia.messengeracademico.domain.FirebaseChat;
import com.consultoraestrategia.messengeracademico.domain.FirebaseUser;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.main.ConnectionInteractor;
import com.consultoraestrategia.messengeracademico.main.ConnectionInteractorImpl;
import com.consultoraestrategia.messengeracademico.main.ConnectionRepository;
import com.consultoraestrategia.messengeracademico.main.ConnectionRepositoryImpl;
import com.consultoraestrategia.messengeracademico.postEvent.ChatListPostEvent;
import com.consultoraestrategia.messengeracademico.postEvent.ChatListPostEventImpl;
import com.consultoraestrategia.messengeracademico.postEvent.ChatPostEvent;
import com.consultoraestrategia.messengeracademico.postEvent.ChatPostEventImpl;
import com.consultoraestrategia.messengeracademico.storage.ChatDbFlowStorage;
import com.consultoraestrategia.messengeracademico.storage.DefaultSharedPreferencesHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by @stevecampos on 17/05/2017.
 */

@Module
public class MessengerAcademicoAppModule {

    private static final String TAG = MessengerAcademicoAppModule.class.getSimpleName();
    Context context;
    SharedPreferences sharedPreferences;

    public MessengerAcademicoAppModule(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }


    @Provides
    @Singleton
    UseCaseHandler provideUseCaseHandler(UseCaseScheduler useCaseScheduler) {
        return new UseCaseHandler(useCaseScheduler);
    }

    @Provides
    @Singleton
    UseCaseScheduler provideUseCaseScheduler() {
        return new UseCaseThreadPoolScheduler();
    }

    @Provides
    @Singleton
    Context provideContext() {
        return this.context;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return this.sharedPreferences;
    }

    @Provides
    @Singleton
    DefaultSharedPreferencesHelper provideDefaultSharedPreferencesHelper() {
        return new DefaultSharedPreferencesHelper(sharedPreferences);
    }

    @Provides
    @Singleton
    String provideMainPhoneNumber(DefaultSharedPreferencesHelper preferencesHelper) {
        return preferencesHelper.getDefaultPhoneNumber();
    }

    @Provides
    @Singleton
    Contact provideMainContact(DefaultSharedPreferencesHelper preferencesHelper) {
        Contact contact = preferencesHelper.getContact();
        if (contact == null) {
            contact = new Contact();
            Log.d(TAG, "contact = null!!!");
        }
        return contact;
    }

    @Provides
    @Singleton
    Resources provideResources(Context context) {
        return context.getResources();
    }


    @Provides
    @Singleton
    ChatRepository provideChatRepository(ChatLocalDataSource localDataSource, ChatRemoteDataSource remoteDataSource, ChatListPostEvent chatListPostEvent, ChatPostEvent chatPostEvent, Contact mainContact) {
        return ChatRepository.getInstance(localDataSource, remoteDataSource, chatListPostEvent, chatPostEvent, mainContact);
    }

    @Provides
    @Singleton
    ChatPostEvent provideChatPostEvent() {
        return new ChatPostEventImpl();
    }

    @Provides
    @Singleton
    ChatListPostEvent provideChatListPostEvent() {
        return new ChatListPostEventImpl();
    }

    @Provides
    @Singleton
    ChatLocalDataSource provideChatLocalDataSource(ChatDbFlowStorage chatDbFlowStorage) {
        return new ChatLocalDataSource(chatDbFlowStorage);
    }

    @Provides
    @Singleton
    ChatDbFlowStorage provideChatDbFlowStorage() {
        return new ChatDbFlowStorage();
    }

    @Provides
    @Singleton
    ChatRemoteDataSource provideChatRemoteDataSource(FirebaseChat firebaseChat, FirebaseUser firebaseUser) {
        return new ChatRemoteDataSource(firebaseChat, firebaseUser);
    }

    @Provides
    @Singleton
    FirebaseChat provideFirebaseChat() {
        return FirebaseChat.getInstance();
    }

    @Provides
    @Singleton
    FirebaseUser provideFirebaseUser() {
        return FirebaseUser.getInstance();
    }


    @Provides
    @Singleton
    EventBus provideEventBus() {
        return GreenRobotEventBus.getInstance();
    }

    @Provides
    @Singleton
    ConnectionInteractor provideConnectionInteractor(ConnectionRepository connectionRepository) {
        return new ConnectionInteractorImpl(connectionRepository);
    }

    @Provides
    @Singleton
    ConnectionRepository provideConnectionRepository(FirebaseUser firebaseUser, Contact mainContact) {
        return new ConnectionRepositoryImpl(firebaseUser, mainContact);
    }

}
