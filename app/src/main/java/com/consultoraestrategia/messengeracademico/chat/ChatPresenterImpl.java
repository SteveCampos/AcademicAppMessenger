package com.consultoraestrategia.messengeracademico.chat;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.consultoraestrategia.messengeracademico.InjectorUtils;
import com.consultoraestrategia.messengeracademico.base.BasePresenter;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.base.actionMode.BasePresenterActionModeImpl;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.DeleteMessage;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.GenerateMessageKey;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ImageCompression;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ListenSingleMessage;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.LoadMoreMessages;
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
import com.google.firebase.auth.FirebaseUser;
import com.zhihu.matisse.Matisse;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity.EXTRA_RECEPTOR_PHONENUMBER;

/**
 * Created by @stevecampos on 10/03/2017.
 */
public class ChatPresenterImpl extends BasePresenterActionModeImpl<ChatView, ChatMessage> implements ChatPresenter {
    private static final String TAG = ChatPresenterImpl.class.getSimpleName();

    private final LoadMessages useCaseLoadMessages;
    private final GetContact useCaseGetContact;
    private final GetChat useCaseGetChat;
    private final SendMessage useCaseSendMessage;
    private final ReadMessage useCaseReadMessage;
    private final ChangeStateWriting useCaseChangeStateWriting;
    private final ListenReceptorConnection useCaseListenReceptorConnection;
    private final ListenReceptorAction useCaseListenReceptorAction;
    //private final EventBus eventBus;
    private final ConnectionInteractor connectionInteractor;
    private final GenerateMessageKey generateMessageKey;
    private final File cacheDir;
    private final UploadImage uploadImage;
    private final ContentResolver contentResolver;
    private final ListenSingleMessage useCaseListenSingleMessage;
    private final LoadMoreMessages useCaseLoadMoreMessages;


    private FirebaseUser mainUser;
    private Contact emisor;
    private Contact receptor;
    private Chat chat;

    public ChatPresenterImpl(FirebaseUser mainUser, UseCaseHandler useCaseHandler, LoadMessages useCaseLoadMessages, GetContact useCaseGetContact, GetChat useCaseGetChat, SendMessage useCaseSendMessage, ReadMessage useCaseReadMessage, ChangeStateWriting useCaseChangeStateWriting, ListenReceptorConnection useCaseListenReceptorConnection, ListenReceptorAction useCaseListenReceptorAction, EventBus eventBus, ConnectionInteractor connectionInteractor, GenerateMessageKey generateMessageKey, File cacheDir, UploadImage uploadImage, ContentResolver contentResolver, Resources resources, ListenSingleMessage listenSingleMessage, LoadMoreMessages useCaseLoadMoreMessages) {
        super(useCaseHandler, resources, eventBus);
        this.mainUser = mainUser;
        this.useCaseLoadMessages = useCaseLoadMessages;
        this.useCaseGetContact = useCaseGetContact;
        this.useCaseGetChat = useCaseGetChat;
        this.useCaseSendMessage = useCaseSendMessage;
        this.useCaseReadMessage = useCaseReadMessage;
        this.useCaseChangeStateWriting = useCaseChangeStateWriting;
        this.useCaseListenReceptorConnection = useCaseListenReceptorConnection;
        this.useCaseListenReceptorAction = useCaseListenReceptorAction;
        //this.eventBus = eventBus;
        this.connectionInteractor = connectionInteractor;
        this.generateMessageKey = generateMessageKey;
        this.cacheDir = cacheDir;
        this.uploadImage = uploadImage;
        this.contentResolver = contentResolver;
        this.useCaseListenSingleMessage = listenSingleMessage;
        this.useCaseLoadMoreMessages = useCaseLoadMoreMessages;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected BasePresenter<ChatView> getPresenterImpl() {
        return this;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        loadEmisor();
    }

    @Override
    public void setExtras(Bundle extras) {
        Log.d(TAG, "setExtras: ");
        super.setExtras(extras);
        manageExtraReceptor();
    }

    private boolean forwardToAnotherActivity;
    private boolean isLaunchedFromAnotherApp = false;

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        forwardToAnotherActivity = false;
        connectionInteractor.setOnline();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if (!forwardToAnotherActivity || isLaunchedFromAnotherApp) {
            connectionInteractor.setOffline();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        super.onBackPressed();
        forwardToAnotherActivity = true;

        if (isInSelectedMode()) {
            clearItems();
        }

        if (view != null) {
            view.finishActivity();
        }
    }

    @Override
    public void loadMessages(Chat chat) {
        Log.d(TAG, "loadMessages");
        final LoadMessages.RequestValues requestValue = new LoadMessages.RequestValues(chat);
        handler.execute(
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

    public void hideProgress() {
        if (view != null) {
            if (contactsAreLoaded) {
                super.hideProgress();
            }
        }
    }

    private void onContactsLoaded() {
        if (emisor != null && receptor != null) {
            contactsAreLoaded = true;
            hideProgress();

            GetChat.RequestValues requestValues = new GetChat.RequestValues(emisor, receptor);
            handler.execute(
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
        handler.execute(
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
        dismissNotification(receptorPhoneNumber);
        getAcademicInformation(extras);
        loadReceptor(receptorPhoneNumber);
    }

    private void manageExtraReceptor() {
        Log.d(TAG, "manageExtraReceptor: ");
        String receptorPhoneNumber = extras.getString(EXTRA_RECEPTOR_PHONENUMBER);
        Log.d(TAG, "receptorPhoneNumber: " + receptorPhoneNumber);
        dismissNotification(receptorPhoneNumber);
        getAcademicInformation(extras);
        loadReceptor(receptorPhoneNumber);
    }

    private void dismissNotification(String receptorPhoneNumber) {
        if (view != null) {
            view.dismissNotification(receptorPhoneNumber.hashCode());
        }
    }

    private void getAcademicInformation(Bundle extras) {
        String phoneNumber = "-";
        String nombre = "-";
        String padre = "-";
        String madre = "-";
        String nivel = "-";
        String anoAcademico = "-";
        String curso = "-";
        String seccion = "-";
        String rol = "-";

        phoneNumber = extras.getString(EXTRA_RECEPTOR_PHONENUMBER);
        nombre = extras.getString("NOMBRE");
        padre = extras.getString("PADRE");
        madre = extras.getString("MADRE");
        nivel = extras.getString("NIVEL");
        anoAcademico = extras.getString("AÑO");
        curso = extras.getString("CURSO");
        seccion = extras.getString("SECCION");
        rol = extras.getString("ROL");
        if (!TextUtils.isEmpty(rol)) {
            isLaunchedFromAnotherApp = true;
        } else {
            return;
        }

        String textFormatted = null;

        switch (rol) {
            case "ALUMNO":
                textFormatted = String.format(res.getString(R.string.chat_message_academic_information), rol, nivel, anoAcademico, curso, seccion);
                break;
            case "PADRE":
                textFormatted = String.format(res.getString(R.string.chat_message_rol_information), rol);
                nombre = padre;
                break;
            case "MADRE":
                textFormatted = String.format(res.getString(R.string.chat_message_rol_information), rol);
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

        handler.execute(
                useCaseSendMessage,
                new SendMessage.RequestValues(message, online),
                new UseCase.UseCaseCallback<SendMessage.ResponseValue>() {
                    @Override
                    public void onSuccess(SendMessage.ResponseValue response) {
                        Log.d(TAG, "useCaseSendMessage onSuccess");
                        addMessage(response.getMessage());
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "useCaseSendMessage onError");
                        showError(new Exception(res.getString(R.string.error_sending_message)));
                    }
                }
        );
    }

    private void sendReadMessage(ChatMessage message) {
        handler.execute(
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
            handler.execute(
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
            handler.execute(
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
                updateMessage(event.getMessage());
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
                if (uri != null && !uri.equals(Uri.EMPTY)) {
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


    private Map<String, Boolean> messagesNotReadedMap = new LinkedHashMap<>();

    @Override
    public void onMessageNotReaded(ChatMessage message) {
        Log.d(TAG, "onMessageNotReaded message: " + message);
        String messageKey = message.getKeyMessage();
        if (!messagesNotReadedMap.containsKey(messageKey)) {
            messagesNotReadedMap.put(messageKey, Boolean.TRUE);
            handler.execute(
                    useCaseListenSingleMessage,
                    new ListenSingleMessage.RequestValues(message),
                    new UseCase.UseCaseCallback<ListenSingleMessage.ResponseValue>() {
                        @Override
                        public void onSuccess(ListenSingleMessage.ResponseValue response) {
                            ChatMessage chatMessage = response.getMessage();
                            Log.d(TAG, "useCaseListenSingleMessage onSuccess message: " + chatMessage);
                        }

                        @Override
                        public void onError() {
                            Log.d(TAG, "onMessageNotReaded onError");
                        }
                    }

            );
        }
    }

    @Override
    public void onMessageNotSended(ChatMessage message) {
        if (message.getMessageType().equals(ChatMessage.TYPE_TEXT)) {
            message.getEmisor().load();
            message.getReceptor().load();
            sendMessage(message);
        }
    }

    Map<String, Boolean> mCachedOlderMessages = new LinkedHashMap<>();

    @Override
    public void loadMoreMessages(ChatMessage olderMessage) {
        Log.d(TAG, "loadMoreMessages");
        if (olderMessage != null) {
            if (!mCachedOlderMessages.containsKey(olderMessage.getId())) {
                mCachedOlderMessages.put(olderMessage.getId(), Boolean.TRUE);
                handler.execute(
                        useCaseLoadMoreMessages,
                        new LoadMoreMessages.RequestValues(olderMessage),
                        new UseCase.UseCaseCallback<LoadMoreMessages.ResponseValue>() {
                            @Override
                            public void onSuccess(LoadMoreMessages.ResponseValue response) {
                                Log.d(TAG, "loadMoreMessages onSucess");
                                onMoreMessagesLoaded(response.getMessages());
                            }

                            @Override
                            public void onError() {
                                Log.d(TAG, "loadMoreMessages onError");
                            }
                        }
                );
            }

        }
    }

    @Override
    public void onActionViewContactSelected() {
        if (view != null && receptor != null) {
            view.showContactInPhone(receptor.getPhoneNumber());
        }
    }

    @Override
    public void actionDelete() {
        Log.d(TAG, "actionDelete: ");
        deleteMessages(getItemsSelected());
    }

    private void deleteMessages(List<ChatMessage> itemsSelected) {
        for (ChatMessage messageSelected :
                itemsSelected) {

            handler.execute(
                    new DeleteMessage(InjectorUtils.getChatRepository()),
                    new DeleteMessage.RequestValues(messageSelected),
                    new UseCase.UseCaseCallback<DeleteMessage.ResponseValue>() {
                        @Override
                        public void onSuccess(DeleteMessage.ResponseValue response) {
                            Log.d(TAG, "onSuccess: ");
                            ChatMessage message = response.getMessage();
                            removeMessage(message);
                            removeItemFromSelectedMode(message);
                        }

                        @Override
                        public void onError() {
                            Log.d(TAG, "onError: ");
                            showMessage(R.string.error_removing_message);
                        }
                    }
            );
        }


    }

    private void removeMessage(ChatMessage message) {
        Log.d(TAG, "removeMessage: ");
        if (view != null) view.removeMessage(message);
    }

    @Override
    public void actionCopy() {
        Log.d(TAG, "actionCopy: ");
        copyMessages();
    }

    @Override
    public void onImageClick(ChatMessage message) {
        if (view != null) view.showFullScreenImg(message.getMessageUri());
    }

    @Override
    public void attachVideoClicked() {
        showMessage(R.string.global_error_not_implemented);
    }

    @Override
    public void attachAudioClicked() {
        showMessage(R.string.global_error_not_implemented);
    }

    @Override
    public void attachLocationClicked() {
        showMessage(R.string.global_error_not_implemented);
    }

    @Override
    public void attachContactClicked() {
        showMessage(R.string.global_error_not_implemented);
    }

    @Override
    public void onSelectRubro(ChatMessage message) {
        showMessage(R.string.global_error_not_implemented);
        if(view!=null)view.showInfoRubro(message.getMessageText());
    }

    private String getTextCopied(List<ChatMessage> messages) {
        StringBuilder textCopied = new StringBuilder();
        for (int i = 0; i < messages.size(); i++) {
            int position = i + 1;
            textCopied.append("(").append(position).append(") ").append(messages.get(i).getMessageText());
        }
        return textCopied.toString();
    }

    private void copyMessages() {

        List<ChatMessage> messagesCopied = getItemsSelected();

        String message = String.format(res.getString(R.string.activity_chat_messages_copied), messagesCopied.size());
        showMessage(message);

        copyText(getTextCopied(messagesCopied));
        clearItems();
    }

    private void copyText(String textToCopy) {
        if (view != null) view.copyText(textToCopy);
    }


    private void onMoreMessagesLoaded(List<ChatMessage> messages) {
        if (view != null) {
            view.addMoreMessages(messages);
        }
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
        handler.execute(
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
                                updateMessage(message);
                                uploadImage(message);
                            }
                        };
                        imageCompression.execute(imageUri);// imagePath as a string

                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "GenerateMessageKey onError: ");
                        showError(new Exception(res.getString(R.string.error_uploading_image)));
                    }
                });
    }

    private void uploadImage(ChatMessage message) {
        if (view != null) {
            handler.execute(
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
                            Log.d(TAG, "uploadImage onError: ");
                            showError(new Exception(res.getString(R.string.error_uploading_image)));
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
        handler.execute(
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

    private void updateMessage(ChatMessage message) {
        Log.d(TAG, "updateItem");
        if (view != null) {
            if (chat != null && chat.getChatKey().equals(message.getChatKey())) {
                view.updateMessage(message);
            }
        }
    }

    private void addMessage(ChatMessage message) {
        if (view != null) {
            view.addMessage(message);
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
            //view.showMessage("Error loading Messages!");
        }
    }

    private void onReceptorLoaded(Contact receptor) {
        this.receptor = receptor;
        onContactsLoaded();
        if (view != null) {
            view.showReceptor(receptor);
        }
    }


    @Override
    protected void updateItem(ChatMessage item) {
        updateMessage(item);
    }

    @Override
    protected @MenuRes
    int getMenuActionMode() {
        return R.menu.menu_chat_action_mode;
    }


}