package com.consultoraestrategia.messengeracademico.notification;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.MessengerAcademicoApp;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.UseCaseThreadPoolScheduler;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.GetContact;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Notification;
import com.consultoraestrategia.messengeracademico.entities.NotificationInbox;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.notification.di.FirebaseMessagingComponent;
import com.consultoraestrategia.messengeracademico.notification.domain.usecase.GetMessagesNoReaded;
import com.consultoraestrategia.messengeracademico.notification.domain.usecase.SaveMessageOnLocal;
import com.consultoraestrategia.messengeracademico.storage.DefaultSharedPreferencesHelper;
import com.consultoraestrategia.messengeracademico.utils.MapperHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import static com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity.EXTRA_RECEPTOR_PHONENUMBER;

/**
 * Created by jairc on 17/04/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService implements FirebaseMessagingView {

    private static final String TAG = "MyFMService";
    private static final int MY_NOTIFICATION_ID = 2348;

    private FirebaseMessagingPresenter presenter;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        setupInjection();
    }

    private void setupInjection() {
        MessengerAcademicoApp app = (MessengerAcademicoApp) getApplication();
        FirebaseMessagingComponent component = app.getFirebaseMessagingComponent(
                this,
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        presenter = component.getPresenter();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived");
        if (presenter != null) {
            presenter.onMessageReceived(remoteMessage);
        }
        //super.onMessageReceived(remoteMessage);
    }

    @Override
    public void createNotification(NotificationInbox notification) {
        Log.d(TAG, "createNotification");

        String action = notification.getAction();
        String bigContentTitle = notification.getBigContentTitle();
        String summaryText = notification.getSummaryText();
        String largeIconUri = notification.getLargeIconUri();
        int smallIcon = notification.getSmallIcon();
        Bitmap largeIcon = notification.getLargeIcon();
        List<String> lines = notification.getLines();

        Log.d(TAG, "bigContentTitle: " + bigContentTitle);
        Log.d(TAG, "largeIconUri: " + largeIconUri);
        Log.d(TAG, "action: " + action);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(bigContentTitle);
        builder.setContentText(summaryText);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();


        if (bigContentTitle != null) {
            inboxStyle.setBigContentTitle(bigContentTitle);
        }
        if (summaryText != null) {
            inboxStyle.setSummaryText(summaryText);
        }
        if (lines != null && !lines.isEmpty()) {
            for (String line : lines) {
                inboxStyle.addLine(line);
            }
        }

        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra(EXTRA_RECEPTOR_PHONENUMBER, action);

        intent.setAction(action);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (smallIcon > 0) {
            builder.setSmallIcon(smallIcon);
        }

        if (largeIcon != null) {
            builder.setLargeIcon(largeIcon);
        }

        builder.setSound(defaultSoundUri);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setStyle(inboxStyle);

        android.app.Notification notification1 = builder.build();
        NotificationManagerCompat.from(this).notify(MY_NOTIFICATION_ID, notification1);
    }
}
