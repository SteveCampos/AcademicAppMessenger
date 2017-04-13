package com.consultoraestrategia.messengeracademico.main;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.MessengerAcademicoApp;
import com.consultoraestrategia.messengeracademico.domain.FirebaseUser;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;

/**
 * Created by jairc on 12/04/2017.
 */

public class ConnectionRepositoryImpl implements ConnectionRepository {

    private static final String TAG = ConnectionRepositoryImpl.class.getSimpleName();

    public ConnectionRepositoryImpl() {
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
        FirebaseUser firebaseUser = new FirebaseUser();
        firebaseUser.changeConnection(getMainContact(), connection);
    }

    private String getPhoneNumberFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MessengerAcademicoApp.getContext());
        return preferences.getString(VerificationActivity.PREF_PHONENUMBER, null);
    }

    public Contact getMainContact() {
        String phoneNumber = getPhoneNumberFromPreferences();
        if (phoneNumber != null) {
            return SQLite.select()
                    .from(Contact.class)
                    .where(Contact_Table.phoneNumber.eq(phoneNumber))
                    .querySingle();
        }
        return null;
    }
}
