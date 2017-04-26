package com.consultoraestrategia.messengeracademico.notification;

import com.consultoraestrategia.messengeracademico.entities.NotificationInbox;

/**
 * Created by @stevecampos on 19/04/2017.
 */

public interface NotificationService {
    void createNotification(NotificationInbox notification, String phoneNumberTo);
}
