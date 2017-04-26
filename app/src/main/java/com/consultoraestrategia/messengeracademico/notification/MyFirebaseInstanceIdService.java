package com.consultoraestrategia.messengeracademico.notification;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Created by @stevecampos on 17/04/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {


    private static final String TAG = "MyFirebaseIIDService";
    private static final String USER_TOPIC = "user_";
    private static final String PREF_USERKEY = "PREF_USERKEY";


    /**
     * The Application's current Instance ID token is no longer valid
     * and thus a new one must be requested.
     */

    @Override
    public void onTokenRefresh() {
        // If you need to handle the generation of a token, initially or
        // after a refresh this is where you should do that.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM Token: " + token);

        // Once a token is generated, we subscribe to topic.
        String keyUser = getKey();
        Log.d(TAG, "keyUser: " + keyUser);
        FirebaseMessaging.getInstance()
                .subscribeToTopic(USER_TOPIC + getKey());
    }

    public String getKey() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getString(PREF_USERKEY, null);
    }
}
