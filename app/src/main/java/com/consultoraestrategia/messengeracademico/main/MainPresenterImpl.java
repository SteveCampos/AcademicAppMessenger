package com.consultoraestrategia.messengeracademico.main;


import android.util.Log;

import com.consultoraestrategia.messengeracademico.BaseView;
import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.main.domain.usecase.ListenForUserMessages;
import com.consultoraestrategia.messengeracademico.main.event.MainEvent;
import com.consultoraestrategia.messengeracademico.main.ui.MainView;
import com.consultoraestrategia.messengeracademico.storage.DefaultSharedPreferencesHelper;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.Subscribe;

import static com.consultoraestrategia.messengeracademico.notification.MyFirebaseInstanceIdService.USER_TOPIC;

/**
 * Created by jairc on 27/03/2017.
 */

public class MainPresenterImpl implements MainPresenter {

    private static final String TAG = MainPresenterImpl.class.getSimpleName();
    private MainView view;

    private final UseCaseHandler useCaseHandler;
    private final DefaultSharedPreferencesHelper preferencesHelper;
    private final ListenForUserMessages listenForUserMessages;
    private final EventBus eventBus;
    private final ConnectionInteractor connectionInteractor;
    private final Long timestamp;

    private boolean forwardToAnotherActivity = false;

    public MainPresenterImpl(UseCaseHandler useCaseHandler, DefaultSharedPreferencesHelper preferencesHelper, ListenForUserMessages listenForUserMessages, EventBus eventBus, ConnectionInteractor connectionInteractor, Long timestamp) {
        this.useCaseHandler = useCaseHandler;
        this.preferencesHelper = preferencesHelper;
        this.listenForUserMessages = listenForUserMessages;
        this.eventBus = eventBus;
        this.connectionInteractor = connectionInteractor;
        this.timestamp = timestamp;
    }

    @Override
    public void attachView(BaseView view) {
        this.view = (MainView) view;
        listenForMessages();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        eventBus.register(this);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        forwardToAnotherActivity = false;
        connectionInteractor.setOnline();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        if (!forwardToAnotherActivity) {
            connectionInteractor.setOffline();
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        eventBus.unregister(this);
        view = null;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
    }

    @Override
    public void listenForMessages() {
        Log.d(TAG, "listenForAllUserMessages");
        Contact mainContact = preferencesHelper.getContact();
        if (mainContact != null) {
            useCaseHandler.execute(
                    listenForUserMessages,
                    new ListenForUserMessages.RequestValues(mainContact),
                    new UseCase.UseCaseCallback<ListenForUserMessages.ResponseValue>() {
                        @Override
                        public void onSuccess(ListenForUserMessages.ResponseValue response) {
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
        suscribeToNotifications(mainContact);
    }

    @Override
    public void suscribeToNotifications(Contact mainContact) {
        Log.d(TAG, "suscribeToNotifications");
        FirebaseMessaging.getInstance()
                .subscribeToTopic(USER_TOPIC + mainContact.getUserKey());

    }

    @Subscribe
    public void onEventMainThread(MainEvent event) {
        Log.d(TAG, "onEventMainThread");
        switch (event.getType()) {
            case MainEvent.TYPE_LAUNCH_CHAT:
                launchChat(event.getContact());
                break;
            case MainEvent.TYPE_FIRE_NOTIFICATION:
                fireNotification(event.getMessage());
                break;
        }
    }

    private void fireNotification(ChatMessage message) {
        Log.d(TAG, "fireNotification");
        if (view != null) {
            int retval = timestamp.compareTo(message.getTimestamp());
            Log.d(TAG, "retveal: " + retval);
            if (retval <= 0) {
                //The messages timestamp is greather than the timestamp when the presenter was created!
                //then the message needs to convert into a notification fire!
                view.fireNotification(message);
            }
        }

    }

    private void launchChat(Contact contact) {
        Log.d(TAG, "launchChat");
        if (view != null) {
            forwardToAnotherActivity = true;
            view.startChat(contact);
        }
    }
}
