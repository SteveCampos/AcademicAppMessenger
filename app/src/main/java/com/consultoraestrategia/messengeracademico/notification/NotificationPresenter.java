package com.consultoraestrategia.messengeracademico.notification;

import com.consultoraestrategia.messengeracademico.entities.NotificationInbox;
import com.consultoraestrategia.messengeracademico.notification.event.NotificationEvent;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by @stevecampos on 19/04/2017.
 */

public interface NotificationPresenter {

    void onMessageReceived(RemoteMessage remoteMessage);
    void onEventMainThread(NotificationEvent event);

    void fireNotification(NotificationInbox notificationInbox, String phoneNumber);
}
