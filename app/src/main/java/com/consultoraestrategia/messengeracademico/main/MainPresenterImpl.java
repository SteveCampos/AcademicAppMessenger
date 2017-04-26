package com.consultoraestrategia.messengeracademico.main;

import android.content.SharedPreferences;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.main.event.MainEvent;
import com.consultoraestrategia.messengeracademico.main.ui.MainView;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by jairc on 27/03/2017.
 */

public class MainPresenterImpl implements MainPresenter {

    private static final String TAG = MainPresenterImpl.class.getSimpleName();
    private EventBus eventBus;
    private MainView view;
    private MainInteractor interactor;
    private ConnectionInteractor connectionInteractor;

    private boolean forwardToAnotherActivity = false;

    public MainPresenterImpl(MainView view) {
        this.view = view;
        this.eventBus = GreenRobotEventBus.getInstance();
        this.interactor = new MainInteractorImpl();
        this.connectionInteractor = new ConnectionInteractorImpl();
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
    }
}
