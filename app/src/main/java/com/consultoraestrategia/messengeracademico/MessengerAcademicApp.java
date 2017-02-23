package com.consultoraestrategia.messengeracademico;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * Created by Steve on 16/02/2017.
 */

public class MessengerAcademicApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initFirebase();
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
    }
}
