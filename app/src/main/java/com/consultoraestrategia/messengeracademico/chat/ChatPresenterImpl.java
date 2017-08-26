package com.consultoraestrategia.messengeracademico.chat;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.consultoraestrategia.messengeracademico.BaseView;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.GenerateMessageKey;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ImageCompression;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ListenSingleMessage;
import com.consultoraestrategia.messengeracademico.chat.events.ChatEvent;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatView;
import com.consultoraestrategia.messengeracademico.entities.Action;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.MediaFile;
import com.consultoraestrategia.messengeracademico.entities.OfficialMessage;
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
import com.consultoraestrategia.messengeracademico.prueba.domain.usecase.UploadImage;
import com.consultoraestrategia.messengeracademico.storage.DefaultSharedPreferencesHelper;
import com.google.firebase.auth.FirebaseUser;
import com.zhihu.matisse.Matisse;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Date;
import java.util.List;

import static com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity.EXTRA_RECEPTOR_PHONENUMBER;

/**
 * Created by @stevecampos on 10/03/2017.
 */
public class ChatPresenterImpl implements ChatPresenter {
    private static final String TAG = ChatPresenterImpl.class.getSimpleName();
    private ChatView view;

    private final UseCaseHandler useCaseHandler;

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
    private final GenerateMessageKey generateMessageKey;
    private final File cacheDir;
    private final UploadImage uploadImage;
    private final ContentResolver contentResolver;
    private final Resources resources;
    private final ListenSingleMessage useCaseListenSingleMessage;


    private FirebaseUser mainUser;
    private Contact emisor;
    private Contact receptor;
    private Chat chat;

    public ChatPresenterImpl(FirebaseUser mainUser, UseCaseHandler useCaseHandler, LoadMessages useCaseLoadMessages, GetContact useCaseGetContact, GetChat useCaseGetChat, SendMessage useCaseSendMessage, ReadMessage useCaseReadMessage, ChangeStateWriting useCaseChangeStateWriting, ListenReceptorConnection useCaseListenReceptorConnection, ListenReceptorAction useCaseListenReceptorAction, EventBus eventBus, ConnectionInteractor connectionInteractor, GenerateMessageKey generateMessageKey, File cacheDir, UploadImage uploadImage, ContentResolver contentResolver, Resources resources, ListenSingleMessage listenSingleMessage) {
        this.mainUser = mainUser;
        this.useCaseHandler = useCaseHandler;
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
        this.generateMessageKey = generateMessageKey;
        this.cacheDir = cacheDir;
        this.uploadImage = uploadImage;
        this.contentResolver = contentResolver;
        this.resources = resources;
        this.useCaseListenSingleMessage = listenSingleMessage;
    }

    @Override
    public void attachView(BaseView view) {
        this.view = (ChatView) view;

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        eventBus.register(this);
        loadEmisor();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
    }

    private boolean forwardToAnotherActivity;
    private boolean isLaunchedFromAnotherApp = false;

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        forwardToAnotherActivity = false;
        connectionInteractor.setOnline();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        if (!forwardToAnotherActivity || isLaunchedFromAnotherApp) {
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
        this.view = null;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        eventBus.unregister(this);
        forwardToAnotherActivity = true;
        if (view != null) {
            view.finishActivity();
        }
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
        //emisor = preferencesHelper.getContact();
        this.emisor = new Contact();
        this.emisor.setUid(mainUser.getUid());
        this.emisor.setPhoneNumber(mainUser.getPhoneNumber());
        this.emisor.setName(mainUser.getDisplayName());
        this.emisor.setPhotoUrl(mainUser.getPhotoUrl() != null ? mainUser.getPhotoUrl().toString() : "");
        this.emisor.setEmail(mainUser.getEmail());
        this.emisor.setDisplayName(mainUser.getDisplayName());

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
                        onFatalError("El contacto no tiene instalado el app Messenger Académico.");
                    }
                }
        );
    }

    @Override
    public void manageIntent(Intent intent) {
        String receptorPhoneNumber = intent.getStringExtra(EXTRA_RECEPTOR_PHONENUMBER);
        Log.d(TAG, "receptorPhoneNumber: " + receptorPhoneNumber);
        getAcademicInformation(intent);
        loadReceptor(receptorPhoneNumber);
    }

    private void getAcademicInformation(Intent intent) {
        String phoneNumber = "-";
        String nombre = "-";
        String padre = "-";
        String madre = "-";
        String nivel = "-";
        String anoAcademico = "-";
        String curso = "-";
        String seccion = "-";
        String rol = "-";

        if (intent.hasExtra(EXTRA_RECEPTOR_PHONENUMBER)) {
            phoneNumber = intent.getStringExtra(EXTRA_RECEPTOR_PHONENUMBER);
        }
        if (intent.hasExtra("NOMBRE")) {
            nombre = intent.getStringExtra("NOMBRE");
        }
        if (intent.hasExtra("PADRE")) {
            padre = intent.getStringExtra("PADRE");
        }
        if (intent.hasExtra("MADRE")) {
            madre = intent.getStringExtra("MADRE");
        }
        if (intent.hasExtra("NIVEL")) {
            nivel = intent.getStringExtra("NIVEL");
        }
        if (intent.hasExtra("AÑO")) {
            anoAcademico = intent.getStringExtra("AÑO");
        }
        if (intent.hasExtra("CURSO")) {
            curso = intent.getStringExtra("CURSO");
        }
        if (intent.hasExtra("SECCION")) {
            seccion = intent.getStringExtra("SECCION");
        }
        if (intent.hasExtra("ROL")) {
            rol = intent.getStringExtra("ROL");
            isLaunchedFromAnotherApp = true;
        }

        String textFormatted = null;

        switch (rol) {
            case "ALUMNO":
                textFormatted = String.format(resources.getString(R.string.chat_message_academic_information), rol, nivel, anoAcademico, curso, seccion);
                break;
            case "PADRE":
                textFormatted = String.format(resources.getString(R.string.chat_message_rol_information), rol);
                nombre = padre;
                break;
            case "MADRE":
                textFormatted = String.format(resources.getString(R.string.chat_message_rol_information), rol);
                nombre = madre;
                break;
        }

        if (view != null) {
            view.showReceptor(new Contact(phoneNumber, phoneNumber, nombre, ""));
            if (textFormatted != null) {
                view.showAcademicInformation(textFormatted);
            }
        }

    }


    @Override
    public void sendMessageText(String text) {
        Log.d(TAG, "sendMessageText");
        text = text.trim();
        if (!text.isEmpty()) {

            ChatMessage message = new ChatMessage();
            message.setEmisor(emisor);
            message.setReceptor(receptor);
            message.setMessageText(text);
            message.setMessageStatus(ChatMessage.STATUS_WRITED);
            message.setMessageType(ChatMessage.TYPE_TEXT);
            message.setMessageUri("");
            message.setTimestamp(new Date().getTime());
            message.setChatKey(chat.getChatKey());
            sendMessage(message);
        }

    }


    private void sendMessage(ChatMessage message) {

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

    private void sendReadMessage(ChatMessage message) {
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

    @Override
    public void readMessage(ChatMessage message) {
        Log.d(TAG, "readMessage");
        message.setReadTimestamp(new Date().getTime());
        sendReadMessage(message);
    }

    private boolean isWriting = false;

    @Override
    public void changeWritingState(String text) {
        Log.d(TAG, "changeWritingState");
        if (text.length() == 0) {
            showVoiceIcon();
            saveNoWritingState();
        } else {
            showSendIcon();
            saveWritingState();
        }
    }

    private void showVoiceIcon() {
        if (view != null) {
            view.showVoiceIcon();
        }
    }

    private void showSendIcon() {
        if (view != null) {
            view.showSendIcon();
        }
    }


    @Override
    public void listenReceptorConnection() {
        if (chat != null) {
            useCaseHandler.execute(
                    useCaseListenReceptorConnection,
                    new ListenReceptorConnection.RequestValues(receptor),
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

    @Override
    public void pickImage() {
        if (view != null) {
            view.startPickImageActivity();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode: " + requestCode + ", resultCode: " + resultCode + ", data: " + data);
        if (requestCode == ChatActivity.REQUEST_CODE_CHOOSE_IMAGE && data != null) {
            List<Uri> uris = Matisse.obtainResult(data);
            //Log.d(TAG, "uri's size: " + uris.size());
            for (Uri uri : uris) {
                Log.d(TAG, "uri: " + uri);
                if (!uri.equals(Uri.EMPTY)) {
                    beginToUploadMessageImage(uri);
                    break;
                }
            }
        }
    }

    @Override
    public void onOfficialMessageActionClicked(ChatMessage message, View view) {
        Log.d(TAG, "onOfficialMessageActionClicked");
        int id = view.getId();
        if (id == R.id.btn_accept) {
            showDialogToConfirm(message);
            return;
        }
        if (id == R.id.btn_deny) {
            showDialogToEnsureDeny(message);
        }
    }

    @Override
    public void officialMessageConfirmed(ChatMessage message) {
        OfficialMessage officialMessage = message.getOfficialMessage();
        officialMessage.setState(OfficialMessage.STATE_CONFIRM);
        officialMessage.setActionTimestamp(new Date().getTime());
        message.setOfficialMessage(officialMessage);
        sendReadMessage(message);
    }

    @Override
    public void officialMessageDenied(ChatMessage message) {
        OfficialMessage officialMessage = message.getOfficialMessage();
        officialMessage.setState(OfficialMessage.STATE_DENY);
        officialMessage.setActionTimestamp(new Date().getTime());
        message.setOfficialMessage(officialMessage);
        sendReadMessage(message);
    }

    private void showDialogToConfirm(ChatMessage message) {
        if (view != null) {
            view.showDialogToConfirmOfficialMessage(message);
        }
    }

    private void showDialogToEnsureDeny(ChatMessage message) {
        if (view != null) {
            view.showDialogToEnsureDenyOfficialMessage(message);
        }
    }


    private void showErrorPickingImage(Exception error) {
        if (view != null) {
            view.showError(error.getLocalizedMessage());
        }
    }

    private void beginToUploadMessageImage(final Uri imageUri) {
        Log.d(TAG, "beginToUploadMessageImage");
        useCaseHandler.execute(
                generateMessageKey,
                new GenerateMessageKey.RequestValues(emisor, receptor),
                new UseCase.UseCaseCallback<GenerateMessageKey.ResponseValue>() {
                    @Override
                    public void onSuccess(GenerateMessageKey.ResponseValue response) {
                        final String keyMessage = response.getKey();
                        Log.d(TAG, "generateMessageKey onSuccess keyMessage:" + keyMessage);

                        ImageCompression imageCompression = new ImageCompression(cacheDir, contentResolver) {
                            @Override
                            protected void onPostExecute(MediaFile compressedFile) {
                                Log.d(TAG, "imageCompression path: " + compressedFile.getLocalUri().getPath());
                                // image here is compressed & ready to be sent to the server
                                ChatMessage message = composeMessageImage(keyMessage, compressedFile);
                                onMessageChanged(message);
                                uploadImage(message);
                            }
                        };
                        imageCompression.execute(imageUri);// imagePath as a string

                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void uploadImage(ChatMessage message) {
        if (view != null) {
            useCaseHandler.execute(
                    uploadImage,
                    new UploadImage.RequestValues(message),
                    new UseCase.UseCaseCallback<UploadImage.ResponseValue>() {
                        @Override
                        public void onSuccess(UploadImage.ResponseValue response) {
                            int type = response.getType();
                            Log.d(TAG, "type: " + type);
                            switch (type) {
                                case UploadImage.SUCCESS:
                                    onImageUploadSucess(response.getMessage());
                                    break;
                                case UploadImage.PROGRESS:
                                    Log.d(TAG, "UploadImage.PROGRESS : " + response.getPercent());
                                    //onImageUploadProgress(response.getPercent());
                                    break;
                                case UploadImage.ERROR:
                                    onImageUploadFailure(response.getMessage(), response.getError());
                                    break;

                            }
                        }

                        @Override
                        public void onError() {
                            showError(new Exception("UseCaseHandler error!"));
                        }
                    }
            );
        }
    }

    private void onImageUploadFailure(ChatMessage message, String error) {
        Log.d(TAG, "onImageUploadFailure");
    }

    private void onImageUploadSucess(ChatMessage message) {
        Log.d(TAG, "onImageUploadSuccess");
        message.getMediaFile().setDownloadUri(message.getMessageUri());
        sendMessage(message);
    }

    private void showError(Exception ex) {
        if (view != null) {
            view.showError(ex.getLocalizedMessage());
        }
    }


    private ChatMessage composeMessageImage(String keyMessage, MediaFile mediaFile) {
        ChatMessage message = new ChatMessage();
        message.setKeyMessage(keyMessage);
        message.setEmisor(emisor);
        message.setReceptor(receptor);
        message.setMessageText("");
        message.setMessageStatus(ChatMessage.STATUS_WRITED);
        message.setMessageType(ChatMessage.TYPE_IMAGE);
        message.setMessageUri(mediaFile.getLocalUri().toString());
        message.setMediaFile(mediaFile);
        message.setTimestamp(new Date().getTime());
        message.setChatKey(chat.getChatKey());

        boolean online = false;
        if (lastConnection != null) {
            online = lastConnection.isOnline();
        }
        return message;
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
        }
    }

    private void onFatalError(String error) {
        Log.d(TAG, "onFatalError");
        if (view != null) {
            view.showFatalError(error);
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
