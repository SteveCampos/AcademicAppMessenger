package com.consultoraestrategia.messengeracademico.chat;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.chat.events.ChatEvent;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatView;
import com.consultoraestrategia.messengeracademico.entities.Action;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.main.ConnectionInteractor;
import com.consultoraestrategia.messengeracademico.main.ConnectionInteractorImpl;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by @stevecampos on 10/03/2017.
 */
public class ChatPresenterImpl implements ChatPresenter {

    private static final String TAG = ChatPresenterImpl.class.getSimpleName();
    private ChatView view;
    private ChatInteractor interactor;
    private EventBus eventBus;
    private ConnectionInteractor connectionInteractor;

    private boolean online = false;
    private boolean forwardToAnotherActivity = false;

    public ChatPresenterImpl(ChatView view) {
        this.view = view;
        this.interactor = new ChatInteractorImpl();
        this.eventBus = GreenRobotEventBus.getInstance();
        this.connectionInteractor = new ConnectionInteractorImpl();
    }

    @Override
    public void onCreate() {
        eventBus.register(this);
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
        view = null;
        eventBus.unregister(this);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        forwardToAnotherActivity = true;
    }

    @Override
    public void listenMessages(Contact from, Contact to) {
        if (from != null && to != null) {
            interactor.listenMessages(from, to);
            listenConnection(from, to);
            listenChatAction(from, to);
            if (view != null) {
                view.hideProgress();
            }
        }

    }

    @Override
    public void listenConnection(Contact from, Contact to) {
        interactor.listenConnection(from, to);
    }

    @Override
    public void listenChatAction(Contact from, Contact to) {
        interactor.listenChatAction(from, to);
    }

    @Override
    public void sendMessage(Contact from, Contact to, ChatMessage message) {
        interactor.sendMessage(online, from, to, message);
    }

    private boolean isWriting = false;

    @Override
    public void afterTextChanged(String message) {
        Log.d(TAG, "afterTextChanged");
        if (message.length() == 0) {
            saveNoWritingState();
        } else {
            saveWritingState();
        }
    }


    private void saveNoWritingState() {
        isWriting = false;
        interactor.changeAction(emisor, receptor, Action.ACTION_NO_ACTION);
    }

    private void saveWritingState() {
        if (!isWriting) {
            isWriting = true;
            interactor.changeAction(emisor, receptor, Action.ACTION_WRITING);
        }
    }


    @Override
    public void setMessageStatusReaded(ChatMessage message) {
        Log.d(TAG, "setMessageStatusReaded");
        interactor.setMessageStatusReaded(online, emisor, receptor, message);
    }

    @Subscribe
    @Override
    public void onEventMainThread(ChatEvent event) {
        Log.d(TAG, "onEventMainThread");
        switch (event.getType()) {
            case ChatEvent.TYPE_CONTACT_EMISOR:
                onEmisorRetrieved(event.getContact());
                break;
            case ChatEvent.TYPE_CONTACT_RECEPTOR:
                onReceptorRetrieved(event.getContact());
                break;
            case ChatEvent.TYPE_MESSAGE:
                Log.d(TAG, "TYPE_MESSAGE");
                ChatMessage message = event.getMessage();
                Log.d(TAG, "messate text: " + message.getMessageText());
                Log.d(TAG, "messate getMessageStatus: " + message.getMessageStatus());
                onMessagedAdded(event.getMessage());
                break;
            case ChatEvent.TYPE_CONNECTION:
                onConnectionChanged(event.getConnection());
                break;
            case ChatEvent.TYPE_ACTION:
                onActionChanged(event.getContact(), event.getAction());
                break;
            case ChatEvent.TYPE_INCOMING_MESSAGE:
                Log.d(TAG, "TYPE_INCOMING_MESSAGE");
                ChatMessage message1 = event.getMessage();
                Log.d(TAG, "messate text: " + message1.getMessageText());
                Log.d(TAG, "messate getMessageStatus: " + message1.getMessageStatus());
                onIncomingMessage(event.getContact(), event.getReceptor(), event.getMessage());
                break;
            case ChatEvent.TYPE_MESSAGE_LIST:
                Log.d(TAG, "TYPE_MESSAGE_LIST");
                onMessageListAdded(event.getMessages());
                break;
        }

    }

    @Override
    public void onMessagedAdded(ChatMessage message) {
        if (view != null) {
            view.onMessagAdded(message);
        }
    }

    private Connection lastConnection;

    @Override
    public void onConnectionChanged(Connection connection) {
        this.lastConnection = connection;
        this.online = connection.isOnline();
        if (view != null) {
            view.onConnectionChanged(connection);
        }
    }

    @Override
    public void onActionChanged(Contact contact, String action) {
        Log.d(TAG, "onActionChanged");
        if (view != null) {
            switch (action) {
                case Action.ACTION_NO_ACTION:
                    onConnectionChanged(lastConnection);
                    break;
                default:
                    if (receptor.equals(contact))
                        view.onUserAction(contact, action);
                    break;
            }
        }
    }

    @Override
    public void onIncomingMessage(Contact from, Contact to, ChatMessage message) {
        Log.d(TAG, "onIncomingMessage");
        if (view != null) {
            /*
            Chat chat = ChatDbHelper.getChat(emisor, receptor);
            Chat incomingChat = ChatDbHelper.getChat(from, to);
            if (chat.equals(incomingChat)) {
                Log.d(TAG, "receptor.equals(contact");
                onMessagedAdded(message);
            }*/
        }
    }

    @Override
    public void onMessageListAdded(List<ChatMessage> messages) {
        Log.d(TAG, "onMessageListAdded");
        if (view != null) {
            view.onMessageListAdded(messages);
        }
    }

    private Contact receptor;

    @Override
    public void onReceptorRetrieved(Contact receptor) {
        if (view != null) {
            view.setReceptor(receptor);
            this.receptor = receptor;
        }
        listenMessages(emisor, receptor);
    }


    private Contact emisor;

    @Override
    public void onEmisorRetrieved(Contact emisor) {
        if (view != null) {
            view.setEmisor(emisor);
            this.emisor = emisor;
        }
        listenMessages(emisor, receptor);
    }

    @Override
    public void getContactEmisor(String id) {
        interactor.getContactEmisor(id);
    }

    @Override
    public void getContactReceptor(String id) {
        interactor.getContactReceptor(id);
    }
}
