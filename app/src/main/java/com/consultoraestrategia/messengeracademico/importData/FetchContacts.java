package com.consultoraestrategia.messengeracademico.importData;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by @stevecampos on 1/03/2017.
 */


public class FetchContacts extends AsyncTask<ContentResolver, Void, ArrayList<Contact>> {

    private static final String TAG = FetchContacts.class.getSimpleName();

    private ContactListener listener;
    private String errorMessage = null;

    public FetchContacts(ContactListener listener) {
        this.listener = listener;
    }

    public interface ContactListener {
        void onContactsAdded(List<Contact> contacts);

        void onError(String message);
    }


    @Override
    protected ArrayList<Contact> doInBackground(ContentResolver... params) {
        try {
            Log.d(TAG, "doInBackground");
            ArrayList<Contact> contacts = new ArrayList<>();

            ContentResolver cr = params[0];
            Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    // get the contact's information
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex((ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                    Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String phoneNumber = cursor.getString(cursor.getColumnIndex((ContactsContract.CommonDataKinds.Phone.NUMBER)));

                    Log.d(TAG, "id: " + id);
                    Log.d(TAG, "name: " + name);
                    Log.d(TAG, "hasPhone: " + hasPhone);
                    Log.d(TAG, "phoneNumber: " + phoneNumber);

                    if (!TextUtils.isEmpty(phoneNumber)) {
                        Contact contact = new Contact();
                        contact.setName(name);
                        contact.setPhoneNumber(phoneNumber);
                        contacts.add(contact);

                    }

                } while (cursor.moveToNext());

                Log.d(TAG, "cursor getCount: " + cursor.getCount());

                // clean up cursor
                cursor.close();
            }

            //Clear Duplicated
            Set<Contact> hs = new HashSet<>(contacts);
            contacts.clear();
            contacts.addAll(hs);

            return contacts;
        } catch (Exception ex) {
            //ex.printStackTrace();
            Log.d(TAG, "ex: " + ex);
            errorMessage = ex.getMessage();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Contact> contacts) {
        if (listener != null) {
            if (errorMessage == null) {
                listener.onContactsAdded(contacts);
            } else {
                listener.onError(errorMessage);
            }

        }
    }
}
