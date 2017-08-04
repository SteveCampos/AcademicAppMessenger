package com.consultoraestrategia.messengeracademico.main;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.MessengerAcademicoApp;
import com.consultoraestrategia.messengeracademico.domain.FirebaseUser;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.storage.DefaultSharedPreferencesHelper;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;

/**
 * Created by jairc on 12/04/2017.
 */

public class ConnectionRepositoryImpl implements ConnectionRepository {

    private static final String TAG = ConnectionRepositoryImpl.class.getSimpleName();
    private final FirebaseUser firebaseUser;
    private final Contact mainContact;


    public ConnectionRepositoryImpl(FirebaseUser firebaseUser, Contact mainContact) {
        this.firebaseUser = firebaseUser;
        this.mainContact = mainContact;
        onDisconnect();
    }

    private void onDisconnect() {
        Log.d(TAG, "onDisconnect");
        firebaseUser.onDisconnect(mainContact, new Connection(false, 0));
    }

    @Override
    public void setOffline() {
        Log.d(TAG, "setOffline");
        setConnection(false);
    }

    @Override
    public void setOnline() {
        Log.d(TAG, "setOnline");
        setConnection(true);
    }

    private void setConnection(boolean online) {
        long timeStamp = new Date().getTime();
        Connection connection = new Connection(online, timeStamp);
        firebaseUser.changeConnection(mainContact, connection);
    }
}
