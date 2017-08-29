package com.consultoraestrategia.messengeracademico.notification;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.MessengerAcademicoApp;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity;
import com.consultoraestrategia.messengeracademico.entities.NotificationInbox;
import com.consultoraestrategia.messengeracademico.main.ui.MainActivity;
import com.consultoraestrategia.messengeracademico.notification.di.FirebaseMessagingComponent;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import static com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity.EXTRA_RECEPTOR_PHONENUMBER;

/**
 * Created by jairc on 17/04/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService implements FirebaseMessagingView {

    private static final String TAG = "MyFMService";
    public static final int MY_NOTIFICATION_ID = 2348;

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
        //super.onMessageReceived(remoteMessage);
        if (presenter != null) {
            presenter.onMessageReceived(remoteMessage);
        }
    }

    public static final String DEFAULT_CHANNEL = "default_channel";

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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DEFAULT_CHANNEL);
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

        int requestCode = 100;
        // cancel intent
        Intent cancelIntent = new Intent(this, CancelNotificationBroadcast.class);

        cancelIntent.putExtra("notification_id", requestCode);

        Intent backIntent = new Intent(this, MainActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_RECEPTOR_PHONENUMBER, action);
        intent.setAction(action);

        PendingIntent pendingIntent = PendingIntent.getActivities(this, requestCode /* Request code */, new Intent[]{backIntent, intent},
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
        int id = action.hashCode();
        Log.d(TAG, "id: " + id);
        NotificationManagerCompat.from(this).notify(action.hashCode(), notification1);
    }
}
