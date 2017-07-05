package com.consultoraestrategia.messengeracademico.notification;

import com.consultoraestrategia.messengeracademico.entities.NotificationInbox;

/**
 * Created by @stevecampos on 30/06/2017.
 */

public interface FirebaseMessagingView {
    void createNotification(NotificationInbox notification);
}
