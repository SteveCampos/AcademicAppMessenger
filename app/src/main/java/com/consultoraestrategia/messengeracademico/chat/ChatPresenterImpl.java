package com.consultoraestrategia.messengeracademico.chat;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.chat.events.ChatEvent;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatView;
import com.consultoraestrategia.messengeracademico.domain.ChatDbHelper;
import com.consultoraestrategia.messengeracademico.entities.Action;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by Steve on 10/03/2017.
 */
public class ChatPresenterImpl implements ChatPresenter {

    private static final String TAG = ChatPresenterImpl.class.getSimpleName();
    private ChatView view;
    private ChatInteractor interactor;
    private EventBus eventBus;

    public ChatPresenterImpl(ChatView view) {
        this.view = view;
        this.interactor = new ChatInteractorImpl();
        this.eventBus = GreenRobotEventBus.getInstance();
    }

    @Override
    public void onCreate() {
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        view = null;
        eventBus.unregister(this);
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
        interactor.sendMessage(from, to, message);
    }

    @Override
    public void changeAction(Contact from, Contact to, String action) {
        interactor.changeAction(from, to, action);
    }

    @Override
    public void setMessageStatusReaded(ChatMessage message) {
        Log.d(TAG, "setMessageStatusReaded");
        interactor.setMessageStatusReaded(emisor, receptor, message);
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
                onMessagedAdded(event.getMessage());
                break;
            case ChatEvent.TYPE_CONNECTION:
                onConnectionChanged(event.getConnection());
                break;
            case ChatEvent.TYPE_ACTION:
                onActionChanged(event.getContact(), event.getAction());
                break;
            case ChatEvent.TYPE_INCOMING_MESSAGE:
                onIncomingMessage(event.getContact(), event.getReceptor(), event.getMessage());
                break;
            case ChatEvent.TYPE_MESSAGE_LIST:
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
                    view.onUserAction(contact, action);
                    break;
            }
        }
    }

    @Override
    public void onIncomingMessage(Contact from, Contact to, ChatMessage message) {
        Log.d(TAG, "onIncomingMessage");
        if (view != null) {
            Chat chat = ChatDbHelper.getChat(emisor, receptor);
            Chat incomingChat = ChatDbHelper.getChat(from, to);
            Log.d(TAG, "chat id: " + chat.getId());
            Log.d(TAG, "incomingChat id: " + incomingChat.getId());
            if (chat.equals(incomingChat)) {
                Log.d(TAG, "receptor.equals(contact");
                onMessagedAdded(message);
            }
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
