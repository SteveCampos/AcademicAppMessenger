package com.consultoraestrategia.messengeracademico.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by @stevecampos on 29/08/2017.
 */

public class CancelNotificationBroadcast extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra("notification_id", 0);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
}
