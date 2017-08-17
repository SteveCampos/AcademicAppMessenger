package com.consultoraestrategia.messengeracademico.main;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.domain.FirebaseUser;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.Date;

/**
 * Created by jairc on 12/04/2017.
 */

public class ConnectionRepositoryImpl implements ConnectionRepository {

    private static final String TAG = ConnectionRepositoryImpl.class.getSimpleName();
    private final FirebaseUser firebaseUser;
    private final com.google.firebase.auth.FirebaseUser mainUser;


    public ConnectionRepositoryImpl(FirebaseUser firebaseUser, com.google.firebase.auth.FirebaseUser mainUser) {
        this.firebaseUser = firebaseUser;
        this.mainUser = mainUser;
        onDisconnect();
    }

    private void onDisconnect() {
        Log.d(TAG, "onDisconnect");
        firebaseUser.onDisconnect();
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
        firebaseUser.changeConnection(connection);
    }
}
