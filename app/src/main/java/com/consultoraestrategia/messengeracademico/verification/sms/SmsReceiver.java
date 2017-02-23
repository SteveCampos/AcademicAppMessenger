package com.consultoraestrategia.messengeracademico.verification.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by Steve on 21/02/2017.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");
        String format = data.getString("format");

        if (pdus != null) {
            for (Object pdu : pdus) {

                SmsMessage smsMessage = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
                } else {
                    smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                }
                String sender = smsMessage.getDisplayOriginatingAddress();
                //You must check here if the sender is your provider and not another one with same text.
                String messageBody = smsMessage.getMessageBody();

                //Pass on the text to our listener.
                if (mListener != null) {
                    mListener.onMessageReceived(sender, messageBody);
                }
            }
        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
