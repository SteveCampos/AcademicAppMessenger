package com.consultoraestrategia.messengeracademico.main;


import android.util.Log;

import com.consultoraestrategia.messengeracademico.BaseView;
import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.main.domain.usecase.ListenForUserMessages;
import com.consultoraestrategia.messengeracademico.main.event.MainEvent;
import com.consultoraestrategia.messengeracademico.main.ui.MainView;
import com.consultoraestrategia.messengeracademico.storage.DefaultSharedPreferencesHelper;
import org.greenrobot.eventbus.Subscribe;

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

    private boolean forwardToAnotherActivity = false;

    public MainPresenterImpl(UseCaseHandler useCaseHandler, DefaultSharedPreferencesHelper preferencesHelper, ListenForUserMessages listenForUserMessages, EventBus eventBus, ConnectionInteractor connectionInteractor) {
        this.useCaseHandler = useCaseHandler;
        this.preferencesHelper = preferencesHelper;
        this.listenForUserMessages = listenForUserMessages;
        this.eventBus = eventBus;
        this.connectionInteractor = connectionInteractor;
    }

    @Override
    public void attachView(BaseView view) {
        this.view = (MainView) view;
        listenForMessages();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        forwardToAnotherActivity = false;
        eventBus.register(this);
        connectionInteractor.setOnline();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        eventBus.unregister(this);
        if (!forwardToAnotherActivity) {
            connectionInteractor.setOffline();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
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
    }

    @Subscribe
    public void onEventMainThread(MainEvent event) {
        Log.d(TAG, "onEventMainThread");
        switch (event.getType()) {
            case MainEvent.TYPE_LAUNCH_CHAT:
                launchChat(event.getContact());
                break;
        }
    }

    private void launchChat(Contact contact) {
        Log.d(TAG, "launchChat");
        if (view != null) {
            forwardToAnotherActivity = true;
            view.startChat(contact);
        }
    }
    /*
    private static final String TAG = MainPresenterImpl.class.getSimpleName();
    private EventBus eventBus;
    private MainView view;
    private MainInteractor interactor;
    private ConnectionInteractor connectionInteractor;

    private boolean forwardToAnotherActivity = false;

    public MainPresenterImpl(MainView view, SharedPreferences preferences) {
        this.view = view;
        this.eventBus = GreenRobotEventBus.getInstance();
        this.interactor = new MainInteractorImpl();
        this.connectionInteractor = new ConnectionInteractorImpl(preferences);
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        eventBus.register(this);
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
    public void onResume() {
        Log.d(TAG, "onResume");
        forwardToAnotherActivity = false;
        connectionInteractor.setOnline();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        eventBus.unregister(this);
    }

    @Override
    public void getPhoneNumber(SharedPreferences preferences) {
        Log.d(TAG, "getPhoneNumber");
        interactor.getPhoneNumber(preferences);
    }

    @Override
    public void getContact(String phoneNumber) {
        Log.d(TAG, "getContact");
        interactor.getContact(phoneNumber);
    }

    @Subscribe
    @Override
    public void onEventMainThread(MainEvent event) {
        Log.d(TAG, "onEventMainThread");
        switch (event.getType()) {
            case MainEvent.TYPE_CONTACT:
                listenForIncomingMessages(event.getContact());
                break;
            case MainEvent.TYPE_PHONENUMBER:
                getContact(event.getPhoneNumber());
                break;
            case MainEvent.TYPE_LAUNCH_CHAT:
                launchChat(event.getContact());
                break;
        }
    }

    @Override
    public void listenForIncomingMessages(Contact contact) {
        Log.d(TAG, "listenForIncomingMessages");
        interactor.listenForIncomingMessages(contact);
    }

    @Override
    public void launchChat(Contact contact) {
        forwardToAnotherActivity = true;
        if (view != null) {
            view.startChat(contact);
        }
    }*/
}
