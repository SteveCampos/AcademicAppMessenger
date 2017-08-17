package com.consultoraestrategia.messengeracademico.notification;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.GetContact;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.NotificationInbox;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.notification.domain.usecase.GetBitmap;
import com.consultoraestrategia.messengeracademico.notification.domain.usecase.GetMessagesNoReaded;
import com.consultoraestrategia.messengeracademico.notification.domain.usecase.SaveMessageOnLocal;
import com.consultoraestrategia.messengeracademico.utils.MapperHelper;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by @stevecampos on 30/06/2017.
 */

public class FirebaseMessagingImpl implements FirebaseMessagingPresenter {
    private static final String TAG = FirebaseMessagingImpl.class.getSimpleName();

    private final Resources resources;
    private final FirebaseMessagingView view;
    private final UseCaseHandler useCaseHandler;
    private final EventBus eventBus;
    private final SaveMessageOnLocal saveMessageOnLocal;
    private final GetContact getContact;
    private final GetMessagesNoReaded getMessagesNoReaded;
    private final GetBitmap getBitmap;
    private Context context;

    public FirebaseMessagingImpl(Resources resources, FirebaseMessagingView view, UseCaseHandler useCaseHandler, EventBus eventBus, SaveMessageOnLocal saveMessageOnLocal, GetContact getContact, GetMessagesNoReaded getMessagesNoReaded, GetBitmap getBitmap, Context context) {
        this.resources = resources;
        this.view = view;
        this.useCaseHandler = useCaseHandler;
        this.eventBus = eventBus;
        this.saveMessageOnLocal = saveMessageOnLocal;
        this.getContact = getContact;
        this.getMessagesNoReaded = getMessagesNoReaded;
        this.getBitmap = getBitmap;
        this.context = context;
    }

    private ChatMessage message;
    private Contact emisor;
    private List<ChatMessage> messagesNoReaded;
    private Bitmap bitmap;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived");
        MapperHelper mapperHelper = new MapperHelper();
        message = mapperHelper.mapToObject(remoteMessage.getData(), ChatMessage.class);
        onMessageReceived(message);
    }

    @Override
    public void onMessageReceived(final ChatMessage message) {
        Log.d(TAG, "onMessageReceived: " + message);
        this.emisor = null;
        this.messagesNoReaded = null;
        this.bitmap = null;
        this.message = message;

        useCaseHandler.execute(
                saveMessageOnLocal,
                new SaveMessageOnLocal.RequestValues(message),
                new UseCase.UseCaseCallback<SaveMessageOnLocal.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveMessageOnLocal.ResponseValue response) {
                        Log.d(TAG, "SaveMessageOnLocal onSuccess");
                        ChatMessage mssgSaved = response.getMessage();
                        constructNotification();
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "SaveMessageOnLocal onError");
                    }
                }
        );

        useCaseHandler.execute(
                getContact,
                new GetContact.RequestValues(message.getEmisor().getPhoneNumber()),
                new UseCase.UseCaseCallback<GetContact.ResponseValue>() {
                    @Override
                    public void onSuccess(GetContact.ResponseValue response) {
                        Log.d(TAG, "GetContact onSuccess");
                        emisor = response.getContact();
                        useCaseHandler.execute(
                                getBitmap,
                                new GetBitmap.RequestValues(emisor.getPhotoUrl(), context),
                                new UseCase.UseCaseCallback<GetBitmap.ResponseValue>() {
                                    @Override
                                    public void onSuccess(GetBitmap.ResponseValue response) {
                                        bitmap = response.getBitmap();
                                        constructNotification();
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d(TAG, "GetContact error!");
                                        emisor = message.getEmisor();
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "GetContact onError");
                    }
                }
        );
        useCaseHandler.execute(
                getMessagesNoReaded,
                new GetMessagesNoReaded.RequestValues(message.getChatKey()),
                new UseCase.UseCaseCallback<GetMessagesNoReaded.ResponseValue>() {
                    @Override
                    public void onSuccess(GetMessagesNoReaded.ResponseValue response) {
                        Log.d(TAG, "GetMessagesNoReaded onSuccess");
                        messagesNoReaded = response.getMessages();
                        Log.d(TAG, "size: " + messagesNoReaded.size());
                        constructNotification();
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "GetMessagesNoReaded onError");
                    }
                }
        );


        //GetContact
        //GetChat
        //GetMessagesNoReaded
    }

    private String getTextFromMessage(ChatMessage message) {
        String messageText = "";
        switch (message.getMessageType()) {
            case ChatMessage.TYPE_TEXT:
                messageText = message.getMessageText();
                break;
            case ChatMessage.TYPE_IMAGE:
                messageText = resources.getString(R.string.txt_global_photo);
                break;
        }
        return messageText;
    }

    private void constructNotification() {
        Log.d(TAG, "constructNotification");
        if (message == null || emisor == null || messagesNoReaded == null) {
            Log.e(TAG, "message == null || emisor == null || messagesNoReaded == null");
            return;
        }

        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_users);
        }


        String messageText = getTextFromMessage(message);

        if (!messagesNoReaded.contains(message)) {
            messagesNoReaded.add(message);
        }
        List<String> lines = new ArrayList<>();
        for (ChatMessage mssg :
                messagesNoReaded) {
            lines.add(getTextFromMessage(mssg));
        }

        String emisorName = emisor.getName();

        NotificationInbox notificationInbox = new NotificationInbox();
        notificationInbox.setLines(lines);
        notificationInbox.setSummaryText(String.format(resources.getString(R.string.notification_summary_newmessages), lines.size()));
        notificationInbox.setBigContentTitle(emisorName);


        notificationInbox.setLargeIcon(bitmap);
        notificationInbox.setLargeIconUri(emisor.getPhotoUrl() != null ? emisor.getPhotoUrl() : null);
        notificationInbox.setSmallIcon(R.drawable.ic_twitter_white);
        notificationInbox.setContentText(messageText);
        notificationInbox.setContentTitle(emisorName);
        notificationInbox.setAction(emisor.getPhoneNumber());

        createNotification(notificationInbox);

    }

    private void createNotification(NotificationInbox notificationInbox) {
        Log.d(TAG, "createNotification");
        if (view != null) {
            view.createNotification(notificationInbox);
        }
    }
}
