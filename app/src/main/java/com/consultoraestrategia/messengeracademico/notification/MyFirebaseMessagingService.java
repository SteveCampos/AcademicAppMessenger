package com.consultoraestrategia.messengeracademico.notification;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.notification.jobService.NewMessageJobService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by jairc on 17/04/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    private static final String TAG = "MyFMService";
    public static final int MY_NOTIFICATION_ID = 2348;
    public static final String USER_TOPIC = "user_";
    private static final String PREF_USERKEY = "PREF_USERKEY";


    public String getKey() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getString(PREF_USERKEY, null);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived");
        String keyMessage = remoteMessage.getData().get("keyMessage");
        if (!TextUtils.isEmpty(keyMessage)) {
            scheduleJob(keyMessage);
        }
    }

    private void scheduleJob(String keyMessage) {
        Log.d(TAG, "scheduleNewMessageJob: " + keyMessage);
        // Create a new dispatcher using the Google Play driver.
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        Bundle extras = new Bundle();
        extras.putString(NewMessageJobService.EXTRA_KEY_MESSAGE, keyMessage);

        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(NewMessageJobService.class)
                // uniquely identifies the job
                .setTag(keyMessage)
                // one-off job
                .setRecurring(false)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.NOW)
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        Constraint.ON_ANY_NETWORK
                )
                .setExtras(extras)
                .build();

        dispatcher.mustSchedule(myJob);
    }
}
