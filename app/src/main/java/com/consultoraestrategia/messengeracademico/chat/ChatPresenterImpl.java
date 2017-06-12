package com.consultoraestrategia.messengeracademico.chat;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.BaseView;
import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.chat.events.ChatEvent;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatView;
import com.consultoraestrategia.messengeracademico.entities.Action;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
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
import com.consultoraestrategia.messengeracademico.storage.DefaultSharedPreferencesHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;

/**
 * Created by @stevecampos on 10/03/2017.
 */
public class ChatPresenterImpl implements ChatPresenter {
    private static final String TAG = ChatPresenterImpl.class.getSimpleName();
    private ChatView view;

    private final UseCaseHandler useCaseHandler;
    private final DefaultSharedPreferencesHelper preferencesHelper;

    private final LoadMessages useCaseLoadMessages;
    private final GetContact useCaseGetContact;
    private final GetChat useCaseGetChat;
    private final SendMessage useCaseSendMessage;
    private final ReadMessage useCaseReadMessage;
    private final ChangeStateWriting useCaseChangeStateWriting;
    private final ListenReceptorConnection useCaseListenReceptorConnection;
    private final ListenReceptorAction useCaseListenReceptorAction;
    private final EventBus eventBus;
    private final ConnectionInteractor connectionInteractor;


    private Contact emisor;
    private Contact receptor;
    private Chat chat;


    public ChatPresenterImpl(UseCaseHandler useCaseHandler, DefaultSharedPreferencesHelper preferencesHelper, LoadMessages useCaseLoadMessages, GetContact useCaseGetContact, GetChat useCaseGetChat, SendMessage useCaseSendMessage, ReadMessage useCaseReadMessage, ChangeStateWriting useCaseChangeStateWriting, ListenReceptorConnection useCaseListenReceptorConnection, ListenReceptorAction useCaseListenReceptorAction, EventBus eventBus, ConnectionInteractor connectionInteractor) {
        this.useCaseHandler = useCaseHandler;
        this.preferencesHelper = preferencesHelper;
        this.useCaseLoadMessages = useCaseLoadMessages;
        this.useCaseGetContact = useCaseGetContact;
        this.useCaseGetChat = useCaseGetChat;
        this.useCaseSendMessage = useCaseSendMessage;
        this.useCaseReadMessage = useCaseReadMessage;
        this.useCaseChangeStateWriting = useCaseChangeStateWriting;
        this.useCaseListenReceptorConnection = useCaseListenReceptorConnection;
        this.useCaseListenReceptorAction = useCaseListenReceptorAction;
        this.eventBus = eventBus;
        this.connectionInteractor = connectionInteractor;
    }

    @Override
    public void attachView(BaseView view) {
        this.view = (ChatView) view;

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        loadEmisor();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
    }

    private boolean forwardToAnotherActivity;
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
        this.view = null;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        forwardToAnotherActivity = true;
    }

    @Override
    public void loadMessages(Chat chat) {
        Log.d(TAG, "loadMessages");
        final LoadMessages.RequestValues requestValue = new LoadMessages.RequestValues(chat);
        useCaseHandler.execute(
                useCaseLoadMessages,
                requestValue,
                new UseCase.UseCaseCallback<LoadMessages.ResponseValue>() {
                    @Override
                    public void onSuccess(LoadMessages.ResponseValue response) {
                        onMessagesLoaded(response.getMessages());
                    }

                    @Override
                    public void onError() {
                        onLoadingFailed();
                    }
                });
    }

    @Override
    public void loadEmisor() {
        Log.d(TAG, "loadEmisor");
        emisor = preferencesHelper.getContact();
    }

    private boolean contactsAreLoaded = false;

    private void hideProgress() {
        if (view != null) {
            if (contactsAreLoaded) {
                view.hideProgress();
            }
        }
    }

    private void onContactsLoaded() {
        if (emisor != null && receptor != null) {
            contactsAreLoaded = true;
            hideProgress();

            GetChat.RequestValues requestValues = new GetChat.RequestValues(emisor, receptor);
            useCaseHandler.execute(
                    useCaseGetChat,
                    requestValues,
                    new UseCase.UseCaseCallback<GetChat.ResponseValue>() {
                        @Override
                        public void onSuccess(GetChat.ResponseValue response) {
                            Log.d(TAG, "useCaseGetChat onSuccess");
                            chat = response.getChat();
                            listenReceptorConnection();
                            listenReceptorAction();
                            loadMessages(chat);
                        }

                        @Override
                        public void onError() {
                            Log.d(TAG, "useCaseGetChat onError");
                            onLoadingFailed();
                        }
                    }
            );
        }
    }


    @Override
    public void loadReceptor(String phoneNumber) {
        Log.d(TAG, "loadReceptor");
        final GetContact.RequestValues requestValue = new GetContact.RequestValues(phoneNumber);
        useCaseHandler.execute(
                useCaseGetContact,
                requestValue,
                new UseCase.UseCaseCallback<GetContact.ResponseValue>() {
                    @Override
                    public void onSuccess(GetContact.ResponseValue response) {
                        onReceptorLoaded(response.getContact());
                    }

                    @Override
                    public void onError() {
                        onLoadingFailed();
                    }
                }
        );
    }

    @Override
    public void sendMessageText(String text) {
        Log.d(TAG, "sendMessageText");

        ChatMessage message = new ChatMessage();
        message.setEmisor(emisor);
        message.setReceptor(receptor);
        message.setMessageText(text);
        message.setMessageStatus(ChatMessage.STATUS_WRITED);
        message.setMessageType(ChatMessage.TYPE_TEXT);
        message.setMessageUri("");
        message.setTimestamp(new Date().getTime());
        message.setChatKey(chat.getChatKey());

        boolean online = false;
        if (lastConnection != null) {
            online = lastConnection.isOnline();
        }

        useCaseHandler.execute(
                useCaseSendMessage,
                new SendMessage.RequestValues(message, online),
                new UseCase.UseCaseCallback<SendMessage.ResponseValue>() {
                    @Override
                    public void onSuccess(SendMessage.ResponseValue response) {
                        Log.d(TAG, "useCaseSendMessage onSuccess");
                        onMessageChanged(response.getMessage());
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "useCaseSendMessage onError");
                    }
                }
        );
    }

    @Override
    public void readMessage(ChatMessage message) {
        Log.d(TAG, "readMessage");
        useCaseHandler.execute(
                useCaseReadMessage,
                new ReadMessage.RequestValues(message),
                new UseCase.UseCaseCallback<ReadMessage.ResponseValue>() {
                    @Override
                    public void onSuccess(ReadMessage.ResponseValue response) {
                        Log.d(TAG, "readMessage onSuccess");
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "readMessage onError");
                    }
                });
    }

    private boolean isWriting = false;

    @Override
    public void changeWritingState(String text) {
        Log.d(TAG, "changeWritingState");
        if (text.length() == 0) {
            saveNoWritingState();
        } else {
            saveWritingState();
        }
    }

    @Override
    public void listenReceptorConnection() {
        if (chat != null) {
            useCaseHandler.execute(
                    useCaseListenReceptorConnection,
                    new ListenReceptorConnection.RequestValues(chat),
                    new UseCase.UseCaseCallback<ListenReceptorConnection.ResponseValue>() {
                        @Override
                        public void onSuccess(ListenReceptorConnection.ResponseValue response) {
                            onReceptorConnectionChanged(response.getConnection());
                        }

                        @Override
                        public void onError() {
                            Log.d(TAG, "listenReceptorConnection onError");
                        }
                    });
        }
    }

    private Connection lastConnection;

    private void onReceptorConnectionChanged(Connection connection) {
        Log.d(TAG, "onReceptorConnectionChanged");
        this.lastConnection = connection;
        if (view != null && connection != null) {
            view.showConnection(connection);
        }
    }

    @Override
    public void listenReceptorAction() {
        if (chat != null) {
            useCaseHandler.execute(
                    useCaseListenReceptorAction,
                    new ListenReceptorAction.RequestValues(chat),
                    new UseCase.UseCaseCallback<ListenReceptorAction.ResponseValue>() {
                        @Override
                        public void onSuccess(ListenReceptorAction.ResponseValue response) {
                            String action = response.getAction();
                            if (action != null) {
                                onReceptorActionChanged(action);
                            }
                        }

                        @Override
                        public void onError() {
                            Log.d(TAG, "listenReceptorAction onError");
                        }
                    });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onEventMainThread(ChatEvent event) {
        Log.d(TAG, "onEventMainThread");
        switch (event.getType()) {
            case ChatEvent.TYPE_MESSAGE:
                onMessageChanged(event.getMessage());
                break;
        }
    }

    private void onReceptorActionChanged(String action) {
        Log.d(TAG, "onReceptorActionChanged");
        if (view != null && receptor != null) {
            switch (action) {
                case Action.ACTION_NO_ACTION:
                    onReceptorConnectionChanged(lastConnection);
                    break;
                case Action.ACTION_WRITING:
                    view.showReceptorAction(receptor, action);
                    break;
            }
        }
    }


    private void saveNoWritingState() {
        isWriting = false;
        changeWritingState(false);
    }

    private void saveWritingState() {
        if (!isWriting) {
            isWriting = true;
            changeWritingState(true);
        }
    }

    private void changeWritingState(boolean writing) {
        useCaseHandler.execute(
                useCaseChangeStateWriting,
                new ChangeStateWriting.RequestValues(chat, writing),
                new UseCase.UseCaseCallback<ChangeStateWriting.ResponseValue>() {
                    @Override
                    public void onSuccess(ChangeStateWriting.ResponseValue response) {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void onMessagesLoaded(List<ChatMessage> messages) {
        Log.d(TAG, "onMessagesLoaded");
        if (view != null) {
            if (messages != null) {
                Log.d(TAG, "count messages: " + messages.size());
                view.addMessages(messages);
            }
        }
    }

    private void onMessageChanged(ChatMessage message) {
        Log.d(TAG, "onMessageChanged");
        if (view != null) {
            if (chat != null && chat.getChatKey().equals(message.getChatKey())) {
                view.addMessage(message);
            }

        } else {
            Log.d(TAG, "view = null !!!");
        }
    }


    private void onLoadingFailed() {
        Log.d(TAG, "onError");
        if (view != null) {
            //view.showError("Error loading Messages!");
        }
    }

    private void onReceptorLoaded(Contact receptor) {
        this.receptor = receptor;
        onContactsLoaded();
        if (view != null) {
            view.showReceptor(receptor);
        }
    }
}
