package com.consultoraestrategia.messengeracademico;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.consultoraestrategia.messengeracademico.domain.FirebaseUser;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.google.firebase.FirebaseApp;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;

/**
 * Created by Steve on 16/02/2017.
 */

public class MessengerAcademicoApp extends Application {
    private static Context context = null;
    @Override
    public void onCreate() {
        context = this;
        super.onCreate();
        initFirebase();
        initDb();
    }

    private void initDb() {
        FlowManager.init(new FlowConfig.Builder(this)
                .openDatabasesOnInit(true).build());
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
    }

    public static Context getContext() {
        return context;
    }
}
