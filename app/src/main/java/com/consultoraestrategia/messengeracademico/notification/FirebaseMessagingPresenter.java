package com.consultoraestrategia.messengeracademico.notification;

import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by @stevecampos on 30/06/2017.
 */

public interface FirebaseMessagingPresenter {
    void onMessageReceived(RemoteMessage remoteMessage);
    void onMessageReceived(ChatMessage message);
    void onMessageReceived(String keyMessage);
}
