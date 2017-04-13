package com.consultoraestrategia.messengeracademico.importData;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.domain.FirebaseContactsHelper;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.List;

/**
 * Created by Steve on 3/03/2017.
 */

public class ExistsPhonenumbersAsyncTask extends AsyncTask<List<Contact>, Void, Void> {

    private static final String TAG = ExistsPhonenumbersAsyncTask.class.getSimpleName();
    private FirebaseContactsHelper firebaseContactsHelper;
    private Context context;
    private ContactListener listener;

    public ExistsPhonenumbersAsyncTask(Context context, ContactListener listener) {
        this.context = context;
        this.firebaseContactsHelper = new FirebaseContactsHelper();
        this.listener = listener;
    }

    public interface ContactListener {
        void onContactInstalled(Contact contact);
        void onError(String message);
    }

    @Override
    protected Void doInBackground(List<Contact>... params) {
        Log.d(TAG, "ExistsPhonenumbersAsyncTask doInBackground");
        List<Contact> contacts = params[0];
        for (Contact contact : contacts) {
            final String phoneNumber = formatPhoneNumber(context, contact.getPhoneNumber());
            final String name = contact.getName();
            if (phoneNumber != null) {
                contact.setPhoneNumber(phoneNumber);

                firebaseContactsHelper.existPhoneNumber(phoneNumber, new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        new ParseDataSnapshot(phoneNumber, name).execute(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "databaseError: " + databaseError);
                        if (listener != null && databaseError != null){
                            listener.onError(databaseError.getMessage());
                        }
                    }
                });
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    //Google's common Java, C++ and JavaScript library for parsing, formatting, and validating international phone numbers.
    //https://github.com/googlei18n/libphonenumber
    private String formatPhoneNumber(Context context, String phoneNumber) {
        String formatNumber = null;
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String usersCountryISOCode = manager.getNetworkCountryIso().toUpperCase();
        Log.d(TAG, "usersCountryISOCode: " + usersCountryISOCode);

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber,
                    usersCountryISOCode);
            if (phoneUtil.isValidNumber(numberProto)) {
                formatNumber = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
                Log.d(TAG, "formatPhoneNumber: " + phoneNumber + " -> " + formatNumber);
            } else {
                Log.d(TAG, "phoneNumber not valid number: " + phoneNumber);
            }
        } catch (Exception e) {
            Log.d(TAG, "phoneNumber: " + phoneNumber + " Exception: " + e.getMessage());
            return null;
        }
        return formatNumber;
    }

    private class ParseDataSnapshot extends AsyncTask<DataSnapshot, Void, Contact> {

        private String phoneNumber;
        private String name;

        public ParseDataSnapshot(String phoneNumber, String name) {
            this.phoneNumber = phoneNumber;
            this.name = name;
        }

        @Override
        protected Contact doInBackground(DataSnapshot... params) {
            DataSnapshot dataSnapshot = params[0];
            Log.d(TAG, "dataSnapshot: " + dataSnapshot);
            String phoneCode = null;
            Contact contact = new Contact();
            contact.setPhoneNumber(phoneNumber);
            contact.setName(name);

            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                phoneCode = dataSnapshot.getValue().toString();
            }
            contact.setUserKey(phoneCode);
            return contact;
        }

        @Override
        protected void onPostExecute(Contact contact) {
            if (listener != null) {
                listener.onContactInstalled(contact);
            }
        }
    }


}
