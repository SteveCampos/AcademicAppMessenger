package com.consultoraestrategia.messengeracademico.verification.sms;

/**
 * Created by Steve on 21/02/2017.
 */

public interface SmsListener {
    void onMessageReceived(String sender, String messageText);
}
