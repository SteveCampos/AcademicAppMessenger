package com.consultoraestrategia.messengeracademico.notification;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.NotificationInbox;
import com.consultoraestrategia.messengeracademico.messageRepository.MessageRepository;
import com.consultoraestrategia.messengeracademico.messageRepository.MessageRepositoryImpl;
import com.consultoraestrategia.messengeracademico.notification.event.NotificationEvent;
import com.consultoraestrategia.messengeracademico.storage.DefaultSharedPreferencesHelper;
import com.consultoraestrategia.messengeracademico.utils.MapperHelper;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import static com.consultoraestrategia.messengeracademico.chat.ChatRepositoryImpl.FROM_NOTIFICATION;

/**
 * Created by @stevecampos on 19/04/2017.
 */

public class NotificationPresenterImpl implements NotificationPresenter {

    private static final String TAG = NotificationPresenterImpl.class.getSimpleName();
    private NotificationService service;
    private MapperHelper mapperHelper;
    private DefaultSharedPreferencesHelper preferencesHelper;
    private Context context;

    public NotificationPresenterImpl(NotificationService service, MapperHelper mapperHelper, DefaultSharedPreferencesHelper preferencesHelper, Context context) {
        this.service = service;
        this.mapperHelper = mapperHelper;
        this.preferencesHelper = preferencesHelper;
        this.context = context;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        ChatMessage message = mapperHelper.mapToObject(remoteMessage.getData(), ChatMessage.class);
        Contact contact = preferencesHelper.getContact();


        if (message == null || contact == null) {
            Log.d(TAG, "message == null || contact == null");
            return;
        }

        int messageStatus = message.getMessageStatus();

        if (messageStatus == ChatMessage.STATUS_WRITED) {
            //FIRE NOTIFICATION!
            fireNotification(buildNotificationInstanceFromMessage(message, contact, context), message.getTo(contact).getPhoneNumber());
        }

        MessageRepository repository = new MessageRepositoryImpl();
        repository.manageIncomingMessage(message, contact, FROM_NOTIFICATION);
    }

    @Override
    public void onEventMainThread(NotificationEvent event) {

    }


    private NotificationInbox buildNotificationInstanceFromMessage(ChatMessage message, Contact user, Context context) {
        Log.d(TAG, "buildNotificationInstanceFromMessage");
        Contact receptor = message.getTo(user);
        if (receptor == null) {
            Log.e(TAG, "receptor == null");
            return null;
        }
        receptor.load();
        Log.d(TAG, "receptor name: " + receptor.getName());

        List<ChatMessage> messagesNoReaded = message.getMessagesNoReaded();
        if (!messagesNoReaded.contains(message)) {
            messagesNoReaded.add(message);
        }

        List<String> lines = new ArrayList<>();
        int MAX_MESSAGES_NO_READED = 3;

        int countMessages = messagesNoReaded.size();

        for (int i = 0; i < countMessages; i++) {
            ChatMessage actualMessage = messagesNoReaded.get(i);
            if (actualMessage != null) {
                lines.add(actualMessage.getMessageText());
            }
        }
        Log.d(TAG, "countMessages: " + countMessages);


        NotificationInbox notificationInbox = new NotificationInbox();

        notificationInbox.setBigContentTitle(receptor.getName());
        notificationInbox.setSummaryText(String.format(context.getString(R.string.notification_summary_newmessages), countMessages));
        notificationInbox.setLines(lines);

        notificationInbox.setLargeIconUri(receptor.getPhotoUri());

        int smallIcon;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            smallIcon = R.drawable.ic_twitter_white;
        } else {
            smallIcon = R.drawable.ic_twitter;
        }
        notificationInbox.setSmallIcon(smallIcon);


        return notificationInbox;
    }

    @Override
    public void fireNotification(NotificationInbox notificationInbox, String phoneNumber) {
        Log.d(TAG, "fireNotification");
        if (service != null) {
            service.createNotification(notificationInbox, phoneNumber);
        }
    }
}
