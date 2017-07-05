package com.consultoraestrategia.messengeracademico.notification.di;

import android.content.Context;
import android.content.res.Resources;

import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.GetContact;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.data.ContactRepository;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.notification.FirebaseMessagingImpl;
import com.consultoraestrategia.messengeracademico.notification.FirebaseMessagingPresenter;
import com.consultoraestrategia.messengeracademico.notification.FirebaseMessagingView;
import com.consultoraestrategia.messengeracademico.notification.domain.usecase.GetBitmap;
import com.consultoraestrategia.messengeracademico.notification.domain.usecase.GetMessagesNoReaded;
import com.consultoraestrategia.messengeracademico.notification.domain.usecase.SaveMessageOnLocal;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by @stevecampos on 30/06/2017.
 */

@Module
public class FirebaseMessagingModule {
    private FirebaseMessagingView view;

    public FirebaseMessagingModule(FirebaseMessagingView view) {
        this.view = view;
    }


    @Provides
    @Singleton
    FirebaseMessagingView provideView() {
        return this.view;
    }

    @Provides
    FirebaseMessagingPresenter provideFirebaseMessagingPresenterImpl(Resources resources, FirebaseMessagingView view, UseCaseHandler useCaseHandler, EventBus eventBus, SaveMessageOnLocal saveMessageOnLocal, GetContact getContact, GetMessagesNoReaded getMessagesNoReaded, GetBitmap getBitmap, Context context) {
        return new FirebaseMessagingImpl(resources, view, useCaseHandler, eventBus, saveMessageOnLocal, getContact, getMessagesNoReaded, getBitmap, context);
    }

    @Provides
    @Singleton
    GetBitmap provideGetBitmap() {
        return new GetBitmap();
    }

    @Provides
    @Singleton
    SaveMessageOnLocal provideSaveMessageOnLocal(ChatRepository repository) {
        return new SaveMessageOnLocal(repository);
    }

    @Provides
    @Singleton
    GetContact provideGetContact(ContactRepository repository) {
        return new GetContact(repository);
    }

    @Provides
    @Singleton
    ContactRepository provideContactRepository() {
        return new ContactRepository();
    }

    @Provides
    @Singleton
    GetMessagesNoReaded provideGetMessagesNoReaded(ChatRepository repository) {
        return new GetMessagesNoReaded(repository);
    }

}
