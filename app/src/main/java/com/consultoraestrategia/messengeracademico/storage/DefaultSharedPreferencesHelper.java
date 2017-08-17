package com.consultoraestrategia.messengeracademico.storage;

import android.content.SharedPreferences;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Created by @stevecampos on 19/04/2017.
 */

public class DefaultSharedPreferencesHelper implements SharedPreferencesHelper {

    private static final String TAG = DefaultSharedPreferencesHelper.class.getSimpleName();
    private final String MY_SHARED_PREFERENCES = "com.consultoraestrategia.messengeracademico_sharedpreferences";
    //private final String KEY_PHONENUMBER = ".session.phonenumber";
    private final String KEY_PHONENUMBER = VerificationActivity.PREF_PHONENUMBER;

    private final SharedPreferences sharedPreferences;

    public DefaultSharedPreferencesHelper(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    /*
    @Override
    public void savePhoneNumber(String phoneNumber) {
        SharedPreferences.Editor editor = editor();
        editor.putString(KEY_PHONENUMBER, phoneNumber);
        editor.apply();
    }

    @Override
    public String getDefaultPhoneNumber() {
        return sharedPreferences.getString(KEY_PHONENUMBER, null);
    }

    @Override
    public void saveContact(Contact contact) {

    }

    @Override
    public Contact getContact() {
        String phoneNumber = getDefaultPhoneNumber();
        if (phoneNumber != null) {
            return SQLite.select()
                    .from(Contact.class)
                    .where(Contact_Table.phoneNumber.eq(phoneNumber))
                    .and(Contact_Table.uid.isNotNull())
                    .querySingle();
        }
        Log.d(TAG, "getContact = null");
        return null;
    }*/

    private SharedPreferences.Editor editor() {
        return sharedPreferences.edit();
    }
}
