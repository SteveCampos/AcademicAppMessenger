package com.consultoraestrategia.messengeracademico.chatList;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.chatList.event.ChatListEvent;
import com.consultoraestrategia.messengeracademico.chatList.ui.ChatListView;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        Log.d(TAG, "onCreateView");
        eventBus.register(this);
        getChats();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        eventBus.unregister(this);
        view = null;
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
    }

    @Override
    public void getChats() {
        interactor.getChats();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onEventMainThread(ChatListEvent event) {
        Log.d(TAG, "onEventMainThread event type: " + event.getType());
        switch (event.getType()) {
            case ChatListEvent.TYPE_CHATLIST:
                onChatsRetreived(event.getChats());
                break;
            case ChatListEvent.TYPE_CONTACT:
                //onContactRetreived(event.getContact());
                break;
            case ChatListEvent.TYPE_PHONENUMBER:
                //onPhoneNumberRetreived(event.getPhoneNumber());
                break;
            case ChatListEvent.TYPE_CHAT:
                onChatChanged(event.getChat());
                break;
        }
    }

    public void onChatsRetreived(List<Chat> chats) {
        if (view != null) {
            view.updateChatList(chats);
        }
    }

    public void onChatAdded(Chat chat) {
        if (view != null) {
            view.addChat(chat);
        }
    }

    public void onChatChanged(Chat chat) {
        if (view != null) {
            view.updateChatAndUp(chat);
        }
    }

    public void onChatDeleted(Chat chat) {
        if (view != null) {
            view.removeChat(chat);
        }
    }

    public void onChatClicked(Chat chat) {
        Log.d(TAG, "onChatClicked");
        interactor.onChatClicked(chat);
    }
}
