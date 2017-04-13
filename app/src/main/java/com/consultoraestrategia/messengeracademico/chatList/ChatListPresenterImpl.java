package com.consultoraestrategia.messengeracademico.chatList;

import android.content.SharedPreferences;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.chatList.event.ChatListEvent;
import com.consultoraestrategia.messengeracademico.chatList.ui.ChatListView;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by jairc on 24/03/2017.
 */

public class ChatListPresenterImpl implements ChatListPresenter {

    private static final String TAG = ChatListPresenterImpl.class.getSimpleName();
    private ChatListView view;
    private EventBus eventBus;
    private ChatListInteractor interactor;

    public ChatListPresenterImpl(ChatListView view) {
        this.view = view;
        this.eventBus = GreenRobotEventBus.getInstance();
        this.interactor = new ChatListInteractorImpl();
    }

    @Override
    public void onCreateView() {
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void getPhoneNumber(SharedPreferences preferences) {
        interactor.getPhoneNumber(preferences);
    }

    @Override
    public void getContact(String phoneNumber) {
        Log.d(TAG, "getContact");
        interactor.getContact(phoneNumber);
    }

    @Override
    public void getChats(Contact contact) {
        interactor.getChats(contact);
    }

    @Subscribe
    @Override
    public void onEventMainThread(ChatListEvent event) {
        Log.d(TAG, "onEventMainThread event type: " + event.getType());
        switch (event.getType()) {
            case ChatListEvent.TYPE_CHATLIST:
                onChatsRetreived(event.getChats());
                break;
            case ChatListEvent.TYPE_CONTACT:
                onContactRetreived(event.getContact());
                break;
            case ChatListEvent.TYPE_PHONENUMBER:
                onPhoneNumberRetreived(event.getPhoneNumber());
                break;
            case ChatListEvent.TYPE_CHAT:
                onChatChanged(event.getChat());
                break;
        }
    }

    @Override
    public void onPhoneNumberRetreived(String phoneNumber) {
        Log.d(TAG, "onPhoneNumberRetreived");
        if (view != null) {
            view.setPhoneNumber(phoneNumber);
        }
    }

    @Override
    public void onContactRetreived(Contact contact) {
        if (view != null) {
            view.setContact(contact);
        }
    }

    @Override
    public void onChatsRetreived(List<Chat> chats) {
        if (view != null) {
            view.onChatsChanged(chats);
        }
    }

    @Override
    public void onChatAdded(Chat chat) {
        if (view != null) {
            view.onChatAdded(chat);
        }
    }

    @Override
    public void onChatChanged(Chat chat) {
        if (view != null) {
            view.onChatChanged(chat);
        }
    }

    @Override
    public void onChatDeleted(Chat chat) {
        if (view != null) {
            view.onChatRemoved(chat);
        }
    }

    @Override
    public void onChatClicked(Chat chat) {
        interactor.onChatClicked(chat);
    }
}
